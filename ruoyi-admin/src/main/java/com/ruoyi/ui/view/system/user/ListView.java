package com.ruoyi.ui.view.system.user;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import com.muyun.vaadin.antd.button.Button;
import com.muyun.vaadin.antd.button.enums.ButtonSize;
import com.muyun.vaadin.antd.button.enums.ButtonType;
import com.muyun.vaadin.antd.card.Card;
import com.muyun.vaadin.antd.flex.Flex;
import com.muyun.vaadin.antd.grid.Col;
import com.muyun.vaadin.antd.grid.Row;
import com.muyun.vaadin.antd.modal.Modal;
import com.muyun.vaadin.antd.modal.props.ModalProps;
import com.muyun.vaadin.antd.notification.Notification;
import com.muyun.vaadin.antd.popconfirm.Popconfirm;
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
import com.muyun.vaadin.antd.tree.Tree;
import com.muyun.vaadin.antd.tree.event.TreeSelectEvent;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.system.service.*;
import com.ruoyi.ui.view.MainLayoutView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;


/**
 * @author muyun
 */
@Route(value = "system/user", layout = MainLayoutView.class)
@PermitAll
@Slf4j
@SuppressWarnings("java:S1199")
public class ListView extends Div {


    transient ISysDictTypeService dictTypeService;

    transient ISysDeptService deptService;
    transient ISysUserService userService;

    transient ISysPostService postService;

    transient ISysRoleService roleService;

    @Autowired
    public ListView(ISysDictTypeService dictTypeService, ISysUserService userService, ISysDeptService deptService,
                    ISysPostService postService, ISysRoleService roleService) {
        this.dictTypeService = dictTypeService;
        this.userService = userService;
        this.deptService = deptService;
        this.postService = postService;
        this.roleService = roleService;

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

    ProTable<SysUser> proTable;

    EditModal editModal;

    ResetPasswordModal passwordModal;

    AssignRoleModal assignRoleModal;

    Tree<Long> tree;

    void initUI() {
        // 行
        Row row1 = new Row(List.of(16, 16));
        {
            Col col1 = new Col(6);
            {
                Card card = new Card()
                        .setTitle(false);
                {
                    tree = new Tree<>(Long.class)
                            .setDefaultExpandAll(true)
                            .setTreeData(deptService.getDeptTreeNode())
                            .addSelectListener(this::handleTreeSelectEvent);
                    card.add(tree);

                }
                //
                col1.add(card);
            }
            //
            Col col2 = new Col(18);
            {
                proTable = new ProTable<>(SysUser.class)
                        .setColumns(createColumns())
                        .setCardBordered(true)
                        .setDateFormatter("string")
                        .setHeaderTitle("查询表格")
                        .setRowKey(SysUser.Fields.userId)
                        .setEditable(new RowEditableConfig()
                                .setType("multiple"))
                        .setOptions(getOptionConfig())
                        .setPagination(new TablePaginationConfig().setDefaultPageSize(15))
                        .setToolBarRender(createToolBarRender());
                // 配置行选择功能
                proTable.getRowSelection().setEnabled(true);
                col2.add(proTable);
            }
            //
            row1.add(col1, col2);
        }
        //
        editModal = new EditModal(proTable, userService, dictTypeService, deptService, postService, roleService);
        passwordModal = new ResetPasswordModal(proTable, userService);
        assignRoleModal = new AssignRoleModal(proTable, userService, roleService);

        //
        add(row1, editModal, passwordModal, assignRoleModal);
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
                        .addClickListener(clickEvent -> {
                            handleInsertEvent();
                        });
                //
                Button batchDeleteButton = new Button("批量删除", ButtonType.PRIMARY)
                        .setDanger(true)
                        .addClickListener(clickEvent -> {
                            handleBatchDeleteEvent(selectedRowKeys);
                        });
                // 若没有选中行，则隐藏删除按钮
                boolean isEmpty = CollUtil.isEmpty(selectedRowKeys);
                batchDeleteButton.setHidden(isEmpty);
                //
                container.add(insertButton, batchDeleteButton);
            }
            return container;
        };
    }


    /**
     * @return
     */
    List<ProColumn<SysUser>> createColumns() {
        ProColumn<SysUser> column1 = new ProColumn<SysUser>()
                .setTitle("用户编号")
                .setDataIndex(SysUser::getUserId)
                .setHideInSearch(true);
        //
        ProColumn<SysUser> column2 = new ProColumn<SysUser>()
                .setTitle("用户账号")
                .setDataIndex(SysUser::getUserName);
        //
        ProColumn<SysUser> column3 = new ProColumn<SysUser>()
                .setTitle("用户昵称")
                .setDataIndex(SysUser::getNickName);

        //
        ProColumn<SysUser> column4 = new ProColumn<SysUser>()
                .setTitle("部门")
                .setDataIndex("deptId")
                //.setValueEnum(deptService.getValueEnum())
                .setDataIndex("dept", "deptName")
                .setHideInSearch(true);
        //
        ProColumn<SysUser> column5 = new ProColumn<SysUser>()
                .setTitle("手机号码")
                .setDataIndex(SysUser::getPhonenumber);
        //
        ProColumn<SysUser> column6 = new ProColumn<SysUser>()
                .setTitle("帐号状态")
                .setDataIndex(SysUser::getStatus)
                .setRender(sysUser -> {
                    boolean status = CharSequenceUtil.equals(sysUser.getStatus(), "0");
                    Switch aSwitch = new Switch()
                            .setCheckedChildren("正常")
                            .setUnCheckedChildren("停用")
                            .setValue(status);
                    aSwitch.addClickListener(event -> handleSwitchEvent(event, sysUser.getUserId()));
                    return aSwitch;
                });
        //
        ProColumn<SysUser> column10 = new ProColumn<SysUser>()
                .setTitle("操作")
                .setValueType(ProFieldValueType.OPTION)
                .setHideInSearch(true)
                .setWidth("400px")
                .setRender(this::createOperationCellComponent);


        return List.of(column1, column2, column3, column4, column5, column6, column10);
    }


    /**
     * @param item
     * @return
     */
    private Div createOperationCellComponent(SysUser item) {
        Div container = new Div();
        {
            //
            Button updateButton = new Button("编辑", ButtonType.LINK)
                    .setSize(ButtonSize.SMALL)
                    .addClickListener(event -> handleUpdateEvent(item));
            //
            Popconfirm popconfirm = new Popconfirm()
                    .setTitle("确定要删除吗?")
                    .addConfirmListener(event -> handleDeleteEvent(item))
                    .add(new Button("删除", ButtonType.LINK)
                            .setDanger(true)
                            .setSize(ButtonSize.SMALL));
            //
            Button resetPasswordButton = new Button("密码重置", ButtonType.LINK)
                    .setSize(ButtonSize.SMALL)
                    .addClickListener(event -> handleResetPasswordEvent(item));
            //
            Button assignRoleButton = new Button("分配角色", ButtonType.LINK)
                    .setSize(ButtonSize.SMALL)
                    .addClickListener(event -> handleAssignRoleEvent(item));
            container.add(updateButton, popconfirm, resetPasswordButton, assignRoleButton);
        }
        return container;
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
        FetchCallback<SysUser> fetchCallback = fetchParam -> {
            FilterMap filter = fetchParam.getFilter();
            SysUser queryObj = new SysUser();
            BeanUtil.copyProperties(filter, queryObj);
            //
            List<SysUser> resultList = userService.selectUserList(queryObj);
            // 转为结果
            return new FetchResult<SysUser>()
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
    void handleUpdateEvent(SysUser dataItem) {
        editModal.openUpdateModal(dataItem.getUserId());
    }


    /**
     * 处理删除事件
     *
     * @param dataItem
     */
    void handleDeleteEvent(SysUser dataItem) {
        try {
            userService.deleteUserById(dataItem.getUserId());
            Notification.success("删除成功");
            proTable.getActionRef().reload();
        } catch (Exception e) {
            log.error("刷新失败", e);
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
                        userService.deleteUserByIds(ids);
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
     * @param dataItem
     */
    void handleResetPasswordEvent(SysUser dataItem) {
        passwordModal.openUpdateModal(dataItem.getUserId());
    }


    /**
     * @param dataItem
     */
    void handleAssignRoleEvent(SysUser dataItem) {
        assignRoleModal.openUpdateModal(dataItem.getUserId());
    }


    /**
     * @param event
     */
    void handleTreeSelectEvent(TreeSelectEvent event) {
        if (CollUtil.isEmpty(event.getSelectedKeys())) {
            return;
        }
        List<Long> selectedKeys = event.getSelectedKeys().stream().map(Long::valueOf).toList();
        Long deptId = selectedKeys.get(0);
        proTable.setParams(Map.of("deptId", deptId));
    }


    void handleSwitchEvent(SwitchClickEvent event, Long userId) {
        boolean checked = event.getChecked();
        String statusStr = checked ? "0" : "1";
        //
        SysUser sysUser = new SysUser();
        sysUser.setRoleId(userId);
        sysUser.setStatus(statusStr);
        userService.updateUserStatus(sysUser);
    }


    // *****************************************************************************************************************
    //                                    样式
    // *****************************************************************************************************************

    void initStyle() {
        // 样式

    }


}

