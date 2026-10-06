package com.ruoyi.ui.view.system.role;

import cn.hutool.core.text.CharSequenceUtil;
import com.muyun.vaadin.antd.common.binder.ExtBeanValidationBinder;
import com.muyun.vaadin.antd.form.Form;
import com.muyun.vaadin.antd.form.FormItem;
import com.muyun.vaadin.antd.form.props.FormCol;
import com.muyun.vaadin.antd.modal.Modal;
import com.muyun.vaadin.antd.modal.event.ModalOkEvent;
import com.muyun.vaadin.antd.notification.Notification;
import com.muyun.vaadin.antd.procomponents.common.schema.ValueEnumType;
import com.muyun.vaadin.antd.procomponents.form.digit.ProFormDigit;
import com.muyun.vaadin.antd.procomponents.form.select.ProFormSelect;
import com.muyun.vaadin.antd.procomponents.form.text.ProFormText;
import com.muyun.vaadin.antd.procomponents.table.ProTable;
import com.muyun.vaadin.antd.tree.Tree;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.system.service.ISysDeptService;
import com.ruoyi.system.service.ISysRoleService;
import com.vaadin.flow.data.binder.PropertyId;
import com.vaadin.flow.data.binder.ValidationException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;


/**
 * 修改密码模态框
 *
 * @author 何德海
 * @version 2026/05/27
 **/
@Slf4j
public class AssignDataScopeModal extends Modal {
    ProTable<SysRole> proTable;

    transient ISysRoleService roleService;

    transient ISysDeptService deptService;

    public AssignDataScopeModal(ProTable<SysRole> proTable, ISysRoleService roleService, ISysDeptService deptService) {
        this.proTable = proTable;
        this.roleService = roleService;
        this.deptService = deptService;


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
    ProFormDigit<Long> roleIdField;


    @PropertyId(SysRole.Fields.roleName)
    ProFormText roleNameField;


    @PropertyId(SysRole.Fields.dataScope)
    ProFormSelect<String> dataScopeField;


    FormItem deptTreeField;

    Tree<Long> deptTree;

//</Col>
//<Col md={24}>
//
//</Row>
//</ProForm.Item>


    public void initUI() {
        this.setTitle("分配角色");
        Form form = new Form()
                //.setName("basic")
                .setAutoComplete("off")
                .setLabelCol(new FormCol(8))
                .setWrapperCol(new FormCol(16));
        {
            //
            roleIdField = new ProFormDigit<>(Long.class)
                    .setLabel("ID")
                    .setHidden(true);

            //
            roleNameField = new ProFormText()
                    .setLabel("角色名称").setDisabled(true);

            //
            dataScopeField = new ProFormSelect<>(String.class)
                    .setLabel("权限范围")
                    .setValueEnum(Map.of("1", new ValueEnumType("全部数据权限"),
                            "2", new ValueEnumType("自定数据权限"),
                            "3", new ValueEnumType("本部门数据权限"),
                            "4", new ValueEnumType("本部门及以下数据权限"),
                            "5", new ValueEnumType("仅本人数据权限")))
                    .setInitialValue("1");
            // 当 选择自定义权限时，显示部门树
            dataScopeField.getField().addChangeListener(event -> handleDataScopeChangeEvent());
            // 部门树字段
            deptTreeField = createMenuField();
            //
            form.add(roleIdField, roleNameField, dataScopeField, deptTreeField);
        }
        //
        add(form);
    }


    /**
     * @return
     */
    private FormItem createMenuField() {
        FormItem formItem = new FormItem()
                .setLabel("权限范围");
        {
            deptTree = new Tree<>(Long.class)
                    .setCheckable(true)
                    .setDefaultExpandAll(true);
            formItem.add(deptTree);
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
     * @param id
     */
    public void openUpdateModal(Long id) {
        SysRole role = roleService.selectRoleById(id);
        //
        binder.readBean(role);
        // 当选择自定义权限时，显示部门树
        handleDataScopeChangeEvent();

        // 获取部门树
        deptTree.setTreeData(deptService.getDeptTreeNode());
        // 设置checked的菜单
        List<Long> checkedKeys = deptService.selectDeptListByRoleId(id);
        deptTree.setCheckedKeys(checkedKeys);
        //
        this.setOpen(true);
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
            //
            bean.setDeptIds(deptTree.getCheckedKeys());
            //
            roleService.checkRoleAllowed(bean);
            roleService.checkRoleDataScope(bean.getRoleId());
            roleService.authDataScope(bean);

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


    /**
     *
     */
    void handleDataScopeChangeEvent() {
        boolean visible = CharSequenceUtil.equals("2", dataScopeField.getValue());
        deptTreeField.setVisible(visible);

    }

    // *****************************************************************************************************************
    //                                    样式
    // *****************************************************************************************************************

    void initStyle() {
        // 样式
        this.setWidth(650);
    }

}

