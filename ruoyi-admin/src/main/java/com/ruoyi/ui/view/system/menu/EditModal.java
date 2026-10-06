package com.ruoyi.ui.view.system.menu;

import com.muyun.vaadin.antd.common.ReactStyle;
import com.muyun.vaadin.antd.common.binder.ExtBeanValidationBinder;
import com.muyun.vaadin.antd.common.enums.EditType;
import com.muyun.vaadin.antd.form.Form;
import com.muyun.vaadin.antd.form.props.FormCol;
import com.muyun.vaadin.antd.modal.Modal;
import com.muyun.vaadin.antd.modal.event.ModalOkEvent;
import com.muyun.vaadin.antd.notification.Notification;
import com.muyun.vaadin.antd.procomponents.common.schema.ValueEnumType;
import com.muyun.vaadin.antd.procomponents.form.digit.ProFormDigit;
import com.muyun.vaadin.antd.procomponents.form.radio.ProFormRadioGroup;
import com.muyun.vaadin.antd.procomponents.form.text.ProFormText;
import com.muyun.vaadin.antd.procomponents.form.treeselect.ProFormTreeSelect;
import com.muyun.vaadin.antd.procomponents.table.ProTable;
import com.muyun.vaadin.antd.treeselect.props.TreeSelectItem;
import com.ruoyi.common.core.domain.entity.SysMenu;
import com.ruoyi.system.service.ISysDictTypeService;
import com.ruoyi.system.service.ISysMenuService;
import com.vaadin.flow.data.binder.PropertyId;
import com.vaadin.flow.data.binder.ValidationException;

import java.util.*;

import static com.ruoyi.common.utils.SecurityUtils.getUserId;
import static org.reflections.Reflections.log;

/**
 * 修改密码模态框
 *
 * @author 何德海
 * @version 2026/05/27
 **/

public class EditModal extends Modal {
    ProTable<SysMenu> proTable;

    transient EditType editType;


    transient ISysMenuService menuService;

    transient ISysDictTypeService dictTypeService;


    public EditModal(ProTable<SysMenu> proTable, ISysMenuService menuService, ISysDictTypeService dictTypeService) {
        this.proTable = proTable;
        this.menuService = menuService;
        this.dictTypeService = dictTypeService;

        // 初始化 UI
        initUI();

        // 初始化行为
        initBehavior();

        // 初始化样式
        initStyle();
    }


    // *****************************************************************************************************************
    //                                      UI
    // *****************************************************************************************************************


    @PropertyId(SysMenu.Fields.menuId)
    ProFormDigit<Long> menuIdField;


    @PropertyId(SysMenu.Fields.parentId)
    ProFormTreeSelect<Long> parentIdField;


    @PropertyId(SysMenu.Fields.menuType)
    ProFormRadioGroup<String> menuTypeField;


    @PropertyId(SysMenu.Fields.icon)
    ProFormText iconField;

    @PropertyId(SysMenu.Fields.menuName)
    ProFormText menuNameField;


    @PropertyId(SysMenu.Fields.orderNum)
    ProFormDigit<Integer> orderNumField;


    @PropertyId(SysMenu.Fields.isFrame)
    ProFormRadioGroup<String> isFrameField;


    @PropertyId(SysMenu.Fields.path)
    ProFormText pathField;

    @PropertyId(SysMenu.Fields.visible)
    ProFormRadioGroup<String> visibleField;


    @PropertyId(SysMenu.Fields.status)
    ProFormRadioGroup<String> statusField;


    public void initUI() {
        Form form = new Form()
                //.setName("basic")
                .setAutoComplete("off")
                .setLabelCol(new FormCol(8))
                .setWrapperCol(new FormCol(16));
        {
            //
            menuIdField = new ProFormDigit<>(Long.class)
                    .setLabel("ID")
                    .setHidden(true);
            //
            parentIdField = new ProFormTreeSelect<>(Long.class)
                    .setLabel("上级菜单");
            parentIdField.getField()
                         .setTreeData(getTreeData())
                         .setReactStyle(ReactStyle.of("width", "100%"))
                         .setTreeDefaultExpandAll(true)
                         .setTreeIcon(true)
                         .setShowSearch(true)
                         .setFilterTreeNode(true)
                         .setAllowClear(true);

            //
            menuTypeField = new ProFormRadioGroup<>(String.class)
                    .setLabel("菜单类型")
                    .setValueEnum(Map.of(
                            "M", new ValueEnumType("目录"),
                            "C", new ValueEnumType("菜单"),
                            "F", new ValueEnumType("按钮")));
//
            iconField = new ProFormText()
                    .setLabel("菜单图标");
            //
            menuNameField = new ProFormText()
                    .setLabel("菜单名称");
            //
            orderNumField = new ProFormDigit<>(Integer.class)
                    .setLabel("显示顺序");

            isFrameField = new ProFormRadioGroup<>(String.class)
                    .setLabel("是否为外链")
                    .setValueEnum(Map.of(
                            "0", new ValueEnumType("是"),
                            "1", new ValueEnumType("否")
                    ));
            //
            pathField = new ProFormText()
                    .setLabel("路由地址");
            //
            visibleField = new ProFormRadioGroup<>(String.class)
                    .setLabel("显示状态")
                    .setValueEnum(dictTypeService.getValueEnum("sys_show_hide"));
            //
            statusField = new ProFormRadioGroup<>(String.class)
                    .setLabel("状态")
                    .setValueEnum(dictTypeService.getValueEnum("sys_normal_disable"));


            //
            form.add(menuIdField, parentIdField, menuTypeField, iconField, menuNameField, orderNumField,
                    isFrameField, pathField, visibleField, statusField);
        }
        //
        add(form);
        //
    }


    // *****************************************************************************************************************
    //                                     行为
    // *****************************************************************************************************************
    ExtBeanValidationBinder<SysMenu> binder = new ExtBeanValidationBinder<>(SysMenu.class);


    public void initBehavior() {
        // 绑定表单field
        binder.bindInstanceFields(this);
        //


        // 取消按钮事件
        this.addCancelListener(e -> {
            this.setOpen(false);
        });
        // 确定按钮事件
        this.addOkListener(this::handleOkEvent);
    }


    /**
     * @return
     */
    private List<TreeSelectItem<Long>> getTreeData() {
        List<SysMenu> resultList = menuService.selectMenuList(getUserId());
        return convertToSelectItemList(resultList);
    }


    /**
     * @param deptList
     * @return
     */
    public List<TreeSelectItem<Long>> convertToSelectItemList(List<SysMenu> deptList) {
        if (deptList == null || deptList.isEmpty()) {
            return Collections.emptyList();
        }
        // 1. 构建 id -> TreeSelectItem 的映射，并初始化 children
        Map<Long, TreeSelectItem<Long>> nodeMap = new HashMap<>(deptList.size());
        for (SysMenu dept : deptList) {
            TreeSelectItem<Long> item = new TreeSelectItem<>();
            item.setValue(dept.getMenuId());
            item.setTitle(dept.getMenuName());
            item.setChildren(new ArrayList<>());
            nodeMap.put(dept.getMenuId(), item);
        }
        // 2. 遍历所有节点，将其挂载到父节点下，或作为根节点
        List<TreeSelectItem<Long>> roots = new ArrayList<>();
        for (SysMenu dept : deptList) {
            Long parentId = dept.getParentId();
            TreeSelectItem<Long> node = nodeMap.get(dept.getMenuId());
            if (parentId == null || parentId == 0L) {
                // 根节点（根据业务定义，可能 parentId == null 或 0）
                roots.add(node);
            } else {
                TreeSelectItem<Long> parent = nodeMap.get(parentId);
                if (parent != null) {
                    parent.getChildren().add(node);
                } else {
                    // 如果父节点不存在（数据异常），按根节点处理或抛出异常，此处按根处理
                    roots.add(node);
                }
            }
        }
        return roots;
    }


    /**
     *
     */
    public void openInsertModal() {
        editType = EditType.INSERT;
        SysMenu bean = new SysMenu();
        try {
            binder.readBean(bean);
        } catch (Exception exp) {
            log.error("读取bean失败");
        }
        //
        this.setTitle("新增").setOpen(true);
    }


    /**
     * @param id
     */
    public void openUpdateModal(Long id) {
        editType = EditType.UPDATE;
        SysMenu bean = menuService.selectMenuById(id);
        binder.readBean(bean);
        //
        this.setTitle("修改").setOpen(true);
    }


    /**
     *
     */
    void handleOkEvent(ModalOkEvent event) {
        SysMenu bean = new SysMenu();
        try {
            // 验证
            binder.writeBean(bean);
            log.debug("bean = {}", bean);
            // 保存, 区分插入和修改
            if (editType == EditType.INSERT) {
                menuService.insertMenu(bean);
            } else {
                menuService.updateMenu(bean);
            }
            // 关闭模态框
            this.setOpen(false);
            proTable.getActionRef().reload();
            Notification.success("操作成功");
        } catch (ValidationException exp) {
            log.error("验证失败, {}", exp.getMessage(), exp);
        } catch (Exception exp) {
            log.error("操作失败" + exp.getMessage(), exp);
            Notification.error("操作失败: " + exp.getMessage());
        }
    }


    // *****************************************************************************************************************
    //                                    样式
    // *****************************************************************************************************************

    void initStyle() {
        // 样式
        this.setWidth(650);
    }

}

