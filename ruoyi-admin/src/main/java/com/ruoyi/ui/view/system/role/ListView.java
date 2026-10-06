package com.ruoyi.ui.view.system.role;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import com.muyun.vaadin.antd.button.Button;
import com.muyun.vaadin.antd.button.enums.ButtonType;
import com.muyun.vaadin.antd.flex.Flex;
import com.muyun.vaadin.antd.icon.Icon;
import com.muyun.vaadin.antd.icon.enums.IconName;
import com.muyun.vaadin.antd.modal.Modal;
import com.muyun.vaadin.antd.modal.props.ModalProps;
import com.muyun.vaadin.antd.notification.Notification;
import com.muyun.vaadin.antd.procomponents.common.render.AnchorGroupRender;
import com.muyun.vaadin.antd.procomponents.field.ProFieldValueType;
import com.muyun.vaadin.antd.procomponents.table.ProTable;
import com.muyun.vaadin.antd.procomponents.table.data.FetchCallback;
import com.muyun.vaadin.antd.procomponents.table.data.FetchResult;
import com.muyun.vaadin.antd.procomponents.table.data.FilterMap;
import com.muyun.vaadin.antd.procomponents.table.props.OptionConfig;
import com.muyun.vaadin.antd.procomponents.table.props.ProColumn;
import com.muyun.vaadin.antd.procomponents.table.props.RowEditableConfig;
import com.muyun.vaadin.antd.procomponents.table.props.TablePaginationConfig;
import com.muyun.vaadin.antd.switche.Switch;
import com.muyun.vaadin.antd.switche.event.SwitchClickEvent;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.service.ISysDeptService;
import com.ruoyi.system.service.ISysDictTypeService;
import com.ruoyi.system.service.ISysMenuService;
import com.ruoyi.system.service.ISysRoleService;
import com.ruoyi.ui.view.MainLayoutView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


/**
 * @author muyun
 */
@Route(value = "system/role", layout = MainLayoutView.class)
@PermitAll
@Slf4j
public class ListView extends Div {

    transient ISysDictTypeService dictTypeService;

    transient ISysRoleService roleService;

    transient ISysMenuService menuService;

    transient ISysDeptService deptService;

    @Autowired
    public ListView(ISysDictTypeService dictTypeService, ISysRoleService roleService, ISysMenuService menuService,
                    ISysDeptService deptService) {
        this.dictTypeService = dictTypeService;
        this.roleService = roleService;
        this.menuService = menuService;
        this.deptService = deptService;

        // 初始化 UI
        initUI();

        // 初始化行为
        initBehavior();

        // 初始化样式
        initStyle();
    }

    // *****************************************************************************************************************
    //                                      结构
    // *****************************************************************************************************************

    ProTable<SysRole> proTable;

    EditModal editModal;

    AssignDataScopeModal assignDataScopeModal;


    void initUI() {
        proTable = new ProTable<>(SysRole.class)
                .setColumns(createColumns())
                .setCardBordered(true)
                .setDateFormatter("string")
                .setHeaderTitle("查询表格")
                .setRowKey(SysRole.Fields.roleId)
                .setEditable(new RowEditableConfig()
                        .setType("multiple"))
                .setOptions(getOptionConfig())
                .setPagination(new TablePaginationConfig().setDefaultPageSize(15))
                .setToolBarRender(createToolBarRender());
        // 配置行选择功能
        proTable.getRowSelection().setEnabled(true);
        // 创建模态框
        editModal = new EditModal(proTable, roleService, dictTypeService, menuService);
        assignDataScopeModal = new AssignDataScopeModal(proTable, roleService, deptService);
        //
        add(proTable, editModal, assignDataScopeModal);
    }


    /**
     * 创建 toolBarRender
     *
     * @return
     */
    ProTable.ToolBarRender createToolBarRender() {
        return (action, selectedRowKeys, selectedRows) -> {
            Flex container = new Flex().setGap("8px");
            {
                Button insertButton = new Button("新增", ButtonType.PRIMARY)
                        .setIcon(Icon.of(IconName.PLUS_OUTLINED))
                        .addClickListener(clickEvent -> handleInsertEvent());
                // 批量删除
                Button batchDeleteButton = new Button("批量删除", ButtonType.PRIMARY)
                        .setIcon(Icon.of(IconName.DELETE_OUTLINED))
                        .setDanger(true)
                        .addClickListener(clickEvent -> handleBatchDeleteEvent(selectedRowKeys));
                // 若没有选中行，则隐藏删除按钮
                boolean isEmpty = CollUtil.isEmpty(selectedRowKeys);
                batchDeleteButton.setHidden(isEmpty);
                // 导出
                Button exportButton = new Button("导出", ButtonType.PRIMARY)
                        .setIcon(Icon.of(IconName.DOWNLOAD_OUTLINED))
                        .addClickListener(clickEvent -> handleExportEvent());
                //
                container.add(insertButton, batchDeleteButton, exportButton);
            }
            return container;
        };
    }


    /**
     * @return
     */
    List<ProColumn<SysRole>> createColumns() {

        ProColumn<SysRole> column1 = new ProColumn<SysRole>()
                .setTitle("角色编号")
                .setDataIndex(SysRole::getRoleId)
                .setHideInSearch(true);
        //
        ProColumn<SysRole> column2 = new ProColumn<SysRole>()
                .setTitle("角色名称")
                .setDataIndex(SysRole::getRoleName);
        //
        ProColumn<SysRole> column3 = new ProColumn<SysRole>()
                .setTitle("权限字符")
                .setDataIndex(SysRole::getRoleKey);
        //
        ProColumn<SysRole> column4 = new ProColumn<SysRole>()
                .setTitle("显示顺序")
                .setDataIndex(SysRole::getRoleSort)
                .setHideInSearch(true);
        ProColumn<SysRole> column5 = new ProColumn<SysRole>()
                .setTitle("状态")
                .setDataIndex(SysRole::getStatus)
                .setValueEnum(dictTypeService.getValueEnum("sys_normal_disable"))
                .setRender(sysRole -> {
                    boolean status = CharSequenceUtil.equals(sysRole.getStatus(), "0");
                    Switch aSwitch = new Switch()
                            .setCheckedChildren("正常")
                            .setUnCheckedChildren("停用")
                            .setValue(status);
                    aSwitch.addClickListener(event -> handleSwitchEvent(event, sysRole.getRoleId()));
                    return aSwitch;
                });


        //
        ProColumn<SysRole> column6 = new ProColumn<SysRole>()
                .setTitle("备注")
                .setDataIndex(SysRole::getRemark)
                .setHideInSearch(true)
                .setEllipsis(true)
                .setWidth("400px");

        //
        ProColumn<SysRole> column10 = new ProColumn<SysRole>()
                .setTitle("操作")
                .setValueType(ProFieldValueType.OPTION)
                .setHideInSearch(true)
                .setWidth("300px")
                .setRender(new AnchorGroupRender<SysRole>()
                        .add("修改", this::handleUpdateEvent)
                        .addDeleteAnchor(this::handleDeleteEvent)
                        .add("数据权限", this::handleAssignDataScopeEvent)
                );


        return List.of(column1, column2, column3, column4, column5, column6, column10);
    }


    /**
     * @return
     */
    private OptionConfig getOptionConfig() {
        return new OptionConfig()
                .setSetting(new OptionConfig.SettingOptionType()
                        .setListsHeight(400));
    }

    // *****************************************************************************************************************
    //                                     行为
    // *****************************************************************************************************************


    void initBehavior() {
        // 获取数据
        FetchCallback<SysRole> fetchCallback = fetchParam -> {
            FilterMap filter = fetchParam.getFilter();
            SysRole queryObj = new SysRole();
            BeanUtil.copyProperties(filter, queryObj);
            //
            List<SysRole> resultList = roleService.selectRoleList(queryObj);
            // 转为结果
            return new FetchResult<SysRole>()
                    .setTotal(PageUtils.getTotal(resultList))
                    .setRecords(resultList);
        };
        //
        proTable.setItems(fetchCallback);
    }


    /**
     * 处理新增事件
     */
    void handleInsertEvent() {
        editModal.openInsertModal();
    }


    /**
     * 处理修改事件
     *
     * @param dataItem
     */
    void handleUpdateEvent(SysRole dataItem) {
        editModal.openUpdateModal(dataItem.getRoleId());
    }


    /**
     * 处理删除事件
     *
     * @param dataItem
     */
    void handleDeleteEvent(SysRole dataItem) {
        try {
            roleService.deleteRoleByIds(new Long[]{dataItem.getRoleId()});
            Notification.success("删除成功");
            proTable.getActionRef().reload();
        } catch (Exception e) {
            log.error("删除失败", e);
            Notification.error("删除失败");
        }
    }

    /**
     * 处理批量删除事件
     */
    void handleBatchDeleteEvent(List<Object> selectedRowKeys) {
        Modal.confirm(new ModalProps(this)
                .setTitle("是否确认删除所选数据项?")
                .setContent("请谨慎操作")
                .setOkListener(event -> {
                    try {
                        Long[] ids = selectedRowKeys.stream().map(p -> Long.valueOf(p.toString())).toArray(Long[]::new);
                        roleService.deleteRoleByIds(ids);
                        Notification.success("删除成功");
                        proTable.getActionRef().reload();
                        proTable.getRowSelection().setSelectedRowKeys(null);
                    } catch (Exception e) {
                        log.error("删除失败", e);
                        Notification.error("删除失败");
                    }
                    // 关闭模态框
                    event.getSource().setOpen(false);
                })
        );
    }


    /**
     * 处理导出事件
     */
    void handleExportEvent() {
        try {
            List<SysRole> resultList = roleService.selectRoleList(null);
            ExcelUtil<SysRole> util = new ExcelUtil<>(SysRole.class);
            util.exportExcelVaadin(resultList, "参数数据");
            Notification.success("导出成功");
        } catch (Exception e) {
            log.error("导出失败", e);
            Notification.error("导出失败");
        }
    }


    void handleSwitchEvent(SwitchClickEvent event, Long roleId) {
        boolean checked = event.getChecked();
        String statusStr = checked ? "0" : "1";
        //
        SysRole sysRole = new SysRole();
        sysRole.setRoleId(roleId);
        sysRole.setStatus(statusStr);
        roleService.updateRoleStatus(sysRole);
    }

    /**
     * @param dataItem
     */
    void handleAssignDataScopeEvent(SysRole dataItem) {
        assignDataScopeModal.openUpdateModal(dataItem.getRoleId());
    }


    // *****************************************************************************************************************
    //                                    样式
    // *****************************************************************************************************************

    void initStyle() {
        // 样式

    }


}

