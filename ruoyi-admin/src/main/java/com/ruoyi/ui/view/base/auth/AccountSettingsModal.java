package com.ruoyi.ui.view.base.auth;

import cn.hutool.core.collection.CollUtil;
import com.muyun.vaadin.antd.common.binder.ExtBeanValidationBinder;
import com.muyun.vaadin.antd.form.Form;
import com.muyun.vaadin.antd.form.props.FormCol;
import com.muyun.vaadin.antd.icon.Icon;
import com.muyun.vaadin.antd.icon.enums.IconName;
import com.muyun.vaadin.antd.modal.Modal;
import com.muyun.vaadin.antd.modal.event.ModalOkEvent;
import com.muyun.vaadin.antd.notification.Notification;
import com.muyun.vaadin.antd.procomponents.form.digit.ProFormDigit;
import com.muyun.vaadin.antd.procomponents.form.select.ProFormSelect;
import com.muyun.vaadin.antd.procomponents.form.text.ProFormText;
import com.muyun.vaadin.antd.procomponents.form.upload.ProFormUploadButton;
import com.muyun.vaadin.antd.upload.enums.UploadListType;
import com.muyun.vaadin.antd.upload.props.UploadFile;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.service.ISysDictTypeService;
import com.ruoyi.system.service.ISysUserService;
import com.vaadin.flow.data.binder.PropertyId;
import com.vaadin.flow.data.binder.ValidationException;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

import static cn.hutool.core.text.CharSequenceUtil.isNotBlank;
import static com.ruoyi.common.utils.SecurityUtils.getLoginUser;
import static com.ruoyi.common.utils.SecurityUtils.getUserId;
import static org.reflections.Reflections.log;

/**
 * 修改当前用户的密码
 *
 * @author 何德海
 * @version 2026/05/27
 **/

public class AccountSettingsModal extends Modal {


    transient ISysUserService userService;


    transient ISysDictTypeService dictTypeService;

    transient TokenService tokenService;


    public AccountSettingsModal(ISysUserService userService, ISysDictTypeService dictTypeService, TokenService tokenService) {
        this.userService = userService;
        this.dictTypeService = dictTypeService;
        this.tokenService = tokenService;


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

    @PropertyId(SysUser.Fields.nickName)
    ProFormText nickNameField;


    @PropertyId(SysUser.Fields.phonenumber)
    ProFormText phonenumberField;


    @PropertyId(SysUser.Fields.email)
    ProFormText emailField;


    @PropertyId(SysUser.Fields.sex)
    ProFormSelect<String> sexField;

    ProFormUploadButton avatarField;


    public void initUI() {
        this.setTitle("密码重置");
        Form form = new Form()
                //.setName("basic")
                .setAutoComplete("off")
                .setLabelCol(new FormCol(8))
                .setWrapperCol(new FormCol(16));
        {
            avatarField = new ProFormUploadButton()
                    .setLabel("头像")
                    .setName("avatarfile")
                    .setAction("/system/user/profile/avatar2")
                    .setAccept(".png,.jpg")
                    .setMax(1)
                    .setTitle("")
                    .setIcon(Icon.of(IconName.PLUS_OUTLINED));
            avatarField.getField()
                       .setListType(UploadListType.PICTURE_CARD);

            //
            userIdField = new ProFormDigit<>(Long.class)
                    .setLabel("ID")
                    .setHidden(true);
            //
            nickNameField = new ProFormText()
                    .setLabel("用户昵称");
            //
            phonenumberField = new ProFormText()
                    .setLabel("手机号码");
            //
            emailField = new ProFormText()
                    .setLabel("用户邮箱");
            //
            sexField = new ProFormSelect<>(String.class)
                    .setLabel("用户性别")
                    .setValueEnum(dictTypeService.getValueEnum("sys_user_sex"));


            //
            form.add(avatarField, userIdField, nickNameField, phonenumberField, emailField, sexField);
        }
        //
        add(form);
    }


    // *****************************************************************************************************************
    //                                     行为
    // *****************************************************************************************************************
    ExtBeanValidationBinder<SysUser> binder = new ExtBeanValidationBinder<>(SysUser.class);


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
     * @param
     */
    public void openUpdateModal() {
        SysUser bean = userService.selectUserById(getUserId());
        binder.readBean(bean);
        // 头像处理
        if (isNotBlank(bean.getAvatar())) {
            avatarField.setValue(List.of(new UploadFile().setUrl(bean.getAvatar())));
        }
        //
        this.setTitle("修改").setOpen(true);
    }


    /**
     *
     */
    void handleOkEvent(ModalOkEvent event) {
        SysUser bean = new SysUser();
        try {
            // 验证
            binder.writeBean(bean);
            log.debug("bean = {}", bean);
            LoginUser loginUser = getLoginUser();
            // 头像处理
            String avatar = "";
            List<UploadFile> uploadFiles = avatarField.getValue();
            if (CollUtil.isNotEmpty(uploadFiles)) {
                UploadFile uploadFile = uploadFiles.get(0);
                Map<String, Object> response = (Map<String, Object>) uploadFile.getResponse();
                avatar = (String) response.get("imgUrl");
                bean.setAvatar(avatar);
                loginUser.getUser().setAvatar(avatar);
            }

            if (StringUtils.isNotEmpty(bean.getPhonenumber()) && !userService.checkPhoneUnique(bean)) {
                Notification.error("修改用户'" + loginUser.getUsername() + "'失败，手机号码已存在");
                return;
            }
            if (StringUtils.isNotEmpty(bean.getEmail()) && !userService.checkEmailUnique(bean)) {
                Notification.error("修改用户'" + loginUser.getUsername() + "'失败，邮箱账号已存在");
                return;
            }
            if (userService.updateUserProfile(bean) > 0) {
                // 更新缓存
                tokenService.setLoginUser(loginUser);
                Notification.success("操作成功");
                this.setOpen(false);
            } else {
                Notification.error("修改个人信息异常，请联系管理员");
            }
        } catch (ValidationException exp) {
            log.error("验证失败, {}", exp.getMessage(), exp);
            Notification.error("验证失败: " + exp.getMessage());
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

