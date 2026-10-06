package com.ruoyi.ui.view.system.user;

import com.muyun.vaadin.antd.common.binder.ExtBeanValidationBinder;
import com.muyun.vaadin.antd.form.Form;
import com.muyun.vaadin.antd.form.props.FormCol;
import com.muyun.vaadin.antd.modal.Modal;
import com.muyun.vaadin.antd.modal.event.ModalOkEvent;
import com.muyun.vaadin.antd.notification.Notification;
import com.muyun.vaadin.antd.procomponents.form.digit.ProFormDigit;
import com.muyun.vaadin.antd.procomponents.form.select.ProFormMultiSelect;
import com.muyun.vaadin.antd.procomponents.table.ProTable;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.system.service.ISysRoleService;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.ui.vo.AssignRoleVo;
import com.vaadin.flow.data.binder.PropertyId;
import com.vaadin.flow.data.binder.ValidationException;

import java.util.List;

import static org.reflections.Reflections.log;

/**
 * 修改密码模态框
 *
 * @author 何德海
 * @version 2026/05/27
 **/

public class AssignRoleModal extends Modal {
    ProTable<SysUser> proTable;

    transient ISysUserService userService;

    transient ISysRoleService roleService;

    public AssignRoleModal(ProTable<SysUser> proTable, ISysUserService userService, ISysRoleService roleService) {
        this.proTable = proTable;
        this.userService = userService;
        this.roleService = roleService;


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
    @PropertyId(SysUser.Fields.userId)
    ProFormDigit<Long> userIdField;


    @PropertyId(AssignRoleVo.Fields.roleIds)
    ProFormMultiSelect<Long> roleIdsField;


    public void initUI() {
        this.setTitle("分配角色");
        Form form = new Form()
                //.setName("basic")
                .setAutoComplete("off")
                .setLabelCol(new FormCol(8))
                .setWrapperCol(new FormCol(16));
        {
            //
            userIdField = new ProFormDigit<>(Long.class)
                    .setLabel("ID")
                    .setHidden(true);
            //
            roleIdsField = new ProFormMultiSelect<>(Long.class)
                    .setLabel("角色")
                    .setValueEnum(roleService.getValueEnum());
            //
            form.add(userIdField, roleIdsField);
        }
        //
        add(form);
    }


    // *****************************************************************************************************************
    //                                     行为
    // *****************************************************************************************************************
    ExtBeanValidationBinder<AssignRoleVo> binder = new ExtBeanValidationBinder<>(AssignRoleVo.class);


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
        SysUser sysUser = userService.selectUserById(id);
        List<Long> roleIds = sysUser.getRoles().stream().map(SysRole::getRoleId).toList();
        //
        AssignRoleVo bean = new AssignRoleVo();
        bean.setUserId(id);
        bean.setRoleIds(roleIds);
        //
        binder.readBean(bean);
        //
        this.setOpen(true);
    }

    /**
     *
     */
    void handleOkEvent(ModalOkEvent event) {
        AssignRoleVo bean = new AssignRoleVo();
        try {
            // 验证
            binder.writeBean(bean);
            log.debug("bean = {}", bean);
            //
            userService.checkUserDataScope(bean.getUserId());
            roleService.checkRoleDataScope(bean.getRoleIds());
            userService.insertUserAuth(bean.getUserId(), bean.getRoleIds());
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

