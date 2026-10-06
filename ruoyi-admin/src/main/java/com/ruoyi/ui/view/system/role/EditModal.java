package com.ruoyi.ui.view.system.role;

import com.muyun.vaadin.antd.common.binder.ExtBeanValidationBinder;
import com.muyun.vaadin.antd.common.enums.EditType;
import com.muyun.vaadin.antd.form.Form;
import com.muyun.vaadin.antd.form.FormItem;
import com.muyun.vaadin.antd.form.props.FormCol;
import com.muyun.vaadin.antd.modal.Modal;
import com.muyun.vaadin.antd.modal.event.ModalOkEvent;
import com.muyun.vaadin.antd.notification.Notification;
import com.muyun.vaadin.antd.procomponents.form.digit.ProFormDigit;
import com.muyun.vaadin.antd.procomponents.form.radio.ProFormRadioGroup;
import com.muyun.vaadin.antd.procomponents.form.text.ProFormText;
import com.muyun.vaadin.antd.procomponents.form.text.ProFormTextArea;
import com.muyun.vaadin.antd.procomponents.table.ProTable;
import com.muyun.vaadin.antd.tree.Tree;
import com.muyun.vaadin.antd.tree.props.TreeNode;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.system.service.ISysDictTypeService;
import com.ruoyi.system.service.ISysMenuService;
import com.ruoyi.system.service.ISysRoleService;
import com.vaadin.flow.data.binder.PropertyId;
import com.vaadin.flow.data.binder.ValidationException;

import java.util.Collections;
import java.util.List;

import static com.ruoyi.common.utils.SecurityUtils.getUserId;
import static org.reflections.Reflections.log;

/**
 * 修改密码模态框
 *
 * @author 何德海
 * @version 2026/05/27
 **/

public class EditModal extends Modal {
    ProTable<SysRole> proTable;

    transient EditType editType;


    transient ISysRoleService roleService;

    transient ISysMenuService menuService;


    transient ISysDictTypeService dictTypeService;


    public EditModal(ProTable<SysRole> proTable, ISysRoleService roleService, ISysDictTypeService dictTypeService,
                     ISysMenuService menuService) {
        this.proTable = proTable;
        this.roleService = roleService;
        this.dictTypeService = dictTypeService;
        this.menuService = menuService;

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


    @PropertyId(SysRole.Fields.roleId)
    ProFormDigit<Long> roleIdield;

    @PropertyId(SysRole.Fields.roleName)
    ProFormText roleNameField;


    @PropertyId(SysRole.Fields.roleKey)
    ProFormText roleKeyField;


    @PropertyId(SysRole.Fields.roleSort)
    ProFormText roleSortField;


    @PropertyId(SysRole.Fields.status)
    ProFormRadioGroup<String> statusField;


    Tree<Long> menuTree;


    @PropertyId(BaseEntity.Fields.remark)
    ProFormTextArea remarksField;


    public void initUI() {
        Form form = new Form()
                //.setName("basic")
                .setAutoComplete("off")
                .setLabelCol(new FormCol(8))
                .setWrapperCol(new FormCol(16));

        {
            //
            roleIdield = new ProFormDigit<>(Long.class)
                    .setLabel("ID")
                    .setHidden(true);
            //
            roleNameField = new ProFormText()
                    .setLabel("角色名称");
            //
            roleKeyField = new ProFormText()
                    .setLabel("权限字符");

            roleSortField = new ProFormText()
                    .setLabel("显示顺序");

            //
            statusField = new ProFormRadioGroup<>(String.class)
                    .setLabel("角色状态")
                    .setValueEnum(dictTypeService.getValueEnum("sys_normal_disable"));
            //
            FormItem menuField = createMenuField();
            //
            remarksField = new ProFormTextArea()
                    .setLabel("备注");

            //
            form.add(roleIdield, roleNameField, roleKeyField, roleSortField, statusField, menuField, remarksField);
        }
        //
        add(form);
    }

    private FormItem createMenuField() {

        FormItem formItem = new FormItem()
                // .setName("menuIds")
                .setLabel("菜单权限");
        {
            menuTree = new Tree<>(Long.class)
                    .setCheckable(true)
                    .setCheckStrictly(true)
                    .setMultiple(true)
                    .setDefaultExpandAll(true);
            formItem.add(menuTree);
        }
        return formItem;
    }


    // *****************************************************************************************************************
    //                                     行为
    // *****************************************************************************************************************
    ExtBeanValidationBinder<SysRole> binder = new ExtBeanValidationBinder<>(SysRole.class);


    public void initBehavior() {
        // 绑定表单field
        binder.bindInstanceFields(this);

        // 取消按钮事件
        this.addCancelListener(e -> {
            this.setOpen(false);
        });
        // 确定按钮事件
        this.addOkListener(this::handleOkEvent);
    }


    /**
     *
     */
    public void openInsertModal() {
        editType = EditType.INSERT;
        SysRole bean = new SysRole();
        try {
            binder.readBean(bean);
        } catch (Exception exp) {
            log.error("读取bean失败", exp);
        }
        // 菜单权限树
        List<TreeNode<Long>> menuTreeNode = menuService.getMenuTreeNode(getUserId());
        menuTree.setTreeData(menuTreeNode);
        menuTree.setCheckedKeys(Collections.emptyList());
        //
        this.setTitle("新增").setOpen(true);
    }


    /**
     * @param id
     */
    public void openUpdateModal(Long id) {
        editType = EditType.UPDATE;
        SysRole bean = roleService.selectRoleById(id);
        binder.readBean(bean);
        // 菜单权限树
        List<TreeNode<Long>> menuTreeNode = menuService.getMenuTreeNode(getUserId());
        menuTree.setTreeData(menuTreeNode);
        // 设置checked的菜单
        List<Long> checkedKeys = menuService.selectMenuListByRoleId(id);
        menuTree.setCheckedKeys(checkedKeys);


        this.setTitle("修改").setOpen(true);
    }


    /**
     *
     */
    void handleOkEvent(ModalOkEvent event) {
        SysRole bean = new SysRole();
        try {
            // 验证
            binder.writeBean(bean);
            log.debug("bean = {}", bean);
            // 菜单权限
            bean.setMenuIds(menuTree.getStrictlyCheckedKeys().getChecked());
            // 保存, 区分插入和修改
            if (editType == EditType.INSERT) {
                roleService.insertRole(bean);
            } else {
                roleService.updateRole(bean);
            }
            // 关闭模态框
            this.setOpen(false);
            proTable.getActionRef().reload();
            Notification.success("操作成功");
        } catch (ValidationException exp) {
            log.error("验证失败, {}", exp.getMessage(), exp);
        } catch (Exception exp) {
            log.error("操作失败, {}", exp.getMessage(), exp);
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

