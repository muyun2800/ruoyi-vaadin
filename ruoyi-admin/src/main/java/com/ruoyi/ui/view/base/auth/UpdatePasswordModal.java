package com.ruoyi.ui.view.base.auth;

import cn.hutool.core.util.ObjectUtil;
import com.muyun.vaadin.antd.common.binder.ExtBeanValidationBinder;
import com.muyun.vaadin.antd.form.Form;
import com.muyun.vaadin.antd.form.props.FormCol;
import com.muyun.vaadin.antd.modal.Modal;
import com.muyun.vaadin.antd.modal.event.ModalOkEvent;
import com.muyun.vaadin.antd.notification.Notification;
import com.muyun.vaadin.antd.procomponents.form.text.ProFormPassword;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.ui.vo.PasswordVo;
import com.vaadin.flow.data.binder.ValidationException;

import static com.ruoyi.common.utils.SecurityUtils.getUserId;
import static org.reflections.Reflections.log;

/**
 * 修改当前用户的密码
 *
 * @author 何德海
 * @version 2026/05/27
 **/

public class UpdatePasswordModal extends Modal {


    transient ISysUserService userService;



    public UpdatePasswordModal(ISysUserService userService) {
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


    ProFormPassword txtOldPassword;

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
            txtOldPassword = new ProFormPassword()
                    .setName("oldPassword")
                    .setLabel("旧密码");
            txtNewPassword = new ProFormPassword()
                    .setName("newPassword")
                    .setLabel("新密码");

            txtNewPassword2 = new ProFormPassword()
                    .setName("newPassword2")
                    .setLabel("确认新密码");

            //
            form.add(txtOldPassword, txtNewPassword, txtNewPassword2);
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
        binder.forField(txtOldPassword).bind("oldPassword");
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
     * @param
     */
    public void openModal() {
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
            Long userId = getUserId();
            SysUser user = userService.selectUserById(userId);
            String passwordEncrypted  = user.getPassword();
            if (!SecurityUtils.matchesPassword(bean.getOldPassword(), passwordEncrypted))
            {
                Notification.error("修改密码失败，旧密码错误 ");
                return;
            }
            if (SecurityUtils.matchesPassword(bean.getNewPassword(), passwordEncrypted))
            {
                Notification.error("新密码不能与旧密码相同");
                return;
            }
            String newPasswordEncrypted = SecurityUtils.encryptPassword(bean.getNewPassword());
            if (userService.resetUserPwd(userId, newPasswordEncrypted) > 0)
            {
                Notification.success("操作成功");
            }else{
                Notification.error("修改密码异常，请联系管理员");
            }
            // 关闭模态框
            this.setOpen(false);
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

