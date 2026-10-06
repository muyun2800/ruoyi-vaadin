package com.ruoyi.ui.view.system.user;

import cn.hutool.core.util.ObjectUtil;
import com.muyun.vaadin.antd.common.binder.ExtBeanValidationBinder;
import com.muyun.vaadin.antd.form.Form;
import com.muyun.vaadin.antd.form.props.FormCol;
import com.muyun.vaadin.antd.modal.Modal;
import com.muyun.vaadin.antd.modal.event.ModalOkEvent;
import com.muyun.vaadin.antd.notification.Notification;
import com.muyun.vaadin.antd.procomponents.form.text.ProFormPassword;
import com.muyun.vaadin.antd.procomponents.table.ProTable;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.ui.vo.PasswordVo;
import com.vaadin.flow.data.binder.ValidationException;

import static com.ruoyi.common.utils.SecurityUtils.getUsername;
import static org.reflections.Reflections.log;

/**
 * 重置密码模态窗
 *
 * @author 何德海
 * @version 2026/05/27
 **/

public class ResetPasswordModal extends Modal {
    ProTable<SysUser> proTable;

    transient ISysUserService userService;

    Long userId;

    public ResetPasswordModal(ProTable<SysUser> proTable, ISysUserService userService) {
        this.proTable = proTable;
        this.userService = userService;


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


    ProFormPassword txtNewPassword;

    ProFormPassword txtNewPassword2;


    public void initUI() {
        this.setTitle("密码重置");
        Form form = new Form()
                //.setName("basic")
                .setAutoComplete("off")
                .setLabelCol(new FormCol(8))
                .setWrapperCol(new FormCol(16));
        {

            txtNewPassword = new ProFormPassword()
                    .setName("newPassword")
                    .setLabel("新密码");

            txtNewPassword2 = new ProFormPassword()
                    .setName("newPassword2")
                    .setLabel("确认新密码");

            //
            form.add(txtNewPassword, txtNewPassword2);
        }
        //
        add(form);
    }


    // *****************************************************************************************************************
    //                                     行为
    // *****************************************************************************************************************
    ExtBeanValidationBinder<PasswordVo> binder = new ExtBeanValidationBinder<>(PasswordVo.class);


    public void initBehavior() {
        // 绑定验证器
        binder.forField(txtNewPassword).bind("newPassword");
        binder.forField(txtNewPassword2)
              .withValidator(value -> ObjectUtil.equal(value, txtNewPassword.getValue()), "两次输入密码不一致")
              .bind("newPassword2");


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
        this.userId = id;
        this.setOpen(true);
    }


    /**
     *
     */
    void handleOkEvent(ModalOkEvent event) {
        PasswordVo bean = new PasswordVo();
        try {
            // 验证
            binder.writeBean(bean);
            log.debug("bean = {}", bean);
            //
            SysUser sysUser = userService.selectUserById(this.userId);
            //
            userService.checkUserAllowed(sysUser);
            userService.checkUserDataScope(sysUser.getUserId());
            sysUser.setPassword(SecurityUtils.encryptPassword(bean.getNewPassword()));
            sysUser.setUpdateBy(getUsername());
            userService.resetPwd(sysUser);
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

