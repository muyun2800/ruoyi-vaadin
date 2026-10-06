package com.ruoyi.ui.view.system.user;

import com.muyun.vaadin.antd.common.ReactStyle;
import com.muyun.vaadin.antd.common.binder.ExtBeanValidationBinder;
import com.muyun.vaadin.antd.common.enums.EditType;
import com.muyun.vaadin.antd.form.Form;
import com.muyun.vaadin.antd.form.props.FormCol;
import com.muyun.vaadin.antd.modal.Modal;
import com.muyun.vaadin.antd.modal.event.ModalOkEvent;
import com.muyun.vaadin.antd.notification.Notification;
import com.muyun.vaadin.antd.procomponents.form.digit.ProFormDigit;
import com.muyun.vaadin.antd.procomponents.form.radio.ProFormRadioGroup;
import com.muyun.vaadin.antd.procomponents.form.select.ProFormMultiSelect;
import com.muyun.vaadin.antd.procomponents.form.select.ProFormSelect;
import com.muyun.vaadin.antd.procomponents.form.text.ProFormPassword;
import com.muyun.vaadin.antd.procomponents.form.text.ProFormText;
import com.muyun.vaadin.antd.procomponents.form.treeselect.ProFormTreeSelect;
import com.muyun.vaadin.antd.procomponents.table.ProTable;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.service.*;
import com.vaadin.flow.data.binder.PropertyId;
import com.vaadin.flow.data.binder.ValidationException;

import java.util.List;

import static cn.hutool.core.text.CharSequenceUtil.isNotBlank;
import static org.reflections.Reflections.log;

/**
 * 修改密码模态框
 *
 * @author 何德海
 * @version 2026/05/27
 **/

public class EditModal extends Modal {
    ProTable<SysUser> proTable;

    transient EditType editType;


    transient ISysUserService userService;

    transient ISysDeptService deptService;

    transient ISysDictTypeService dictTypeService;


    transient ISysPostService postService;


    transient ISysRoleService roleService;


    public EditModal(ProTable<SysUser> proTable, ISysUserService userService, ISysDictTypeService dictTypeService,
                     ISysDeptService deptService, ISysPostService postService, ISysRoleService roleService) {
        this.proTable = proTable;
        this.userService = userService;
        this.dictTypeService = dictTypeService;
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
    //                                      UI
    // *****************************************************************************************************************

    @PropertyId(SysUser.Fields.userId)
    ProFormDigit<Long> userIdField;

    @PropertyId(SysUser.Fields.nickName)
    ProFormText nickNameField;


    @PropertyId(SysUser.Fields.deptId)
    ProFormTreeSelect<Long> deptIdField;


    @PropertyId(SysUser.Fields.phonenumber)
    ProFormText phonenumberField;


    @PropertyId(SysUser.Fields.email)
    ProFormText emailField;


    @PropertyId(SysUser.Fields.userName)
    ProFormText userNameField;


    @PropertyId(SysUser.Fields.password)
    ProFormPassword passwordField;


    @PropertyId(SysUser.Fields.sex)
    ProFormSelect<String> sexField;

    @PropertyId(SysUser.Fields.status)
    ProFormRadioGroup<String> statusField;

    @PropertyId(SysUser.Fields.postIds)
    ProFormMultiSelect<Long> postIdsField;

    @PropertyId(SysUser.Fields.roleIds)
    ProFormMultiSelect<Long> roleIdsField;


    @PropertyId(BaseEntity.Fields.remark)
    ProFormText remarksField;


    public void initUI() {
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
            nickNameField = new ProFormText()
                    .setLabel("用户昵称");

            //
            deptIdField = new ProFormTreeSelect<>(Long.class)
                    .setLabel("部门");
            deptIdField.getField()
                       .setTreeData(deptService.getDeptTreeData())
                       .setReactStyle(ReactStyle.of("width", "100%"))
                       .setTreeDefaultExpandAll(true)
                       .setTreeIcon(true)
                       .setShowSearch(true)
                       .setFilterTreeNode(true)
                       .setAllowClear(true);

            //
            phonenumberField = new ProFormText()
                    .setLabel("手机号码");
            //
            emailField = new ProFormText()
                    .setLabel("用户邮箱");
            //
            userNameField = new ProFormText()
                    .setLabel("用户账号");
            //
            passwordField = new ProFormPassword()
                    .setLabel("密码");
            //
            sexField = new ProFormSelect<>(String.class)
                    .setLabel("用户性别")
                    .setValueEnum(dictTypeService.getValueEnum("sys_user_sex"));

            //
            statusField = new ProFormRadioGroup<>(String.class)
                    .setLabel("帐号状态")
                    .setValueEnum(dictTypeService.getValueEnum("sys_normal_disable"));
            //
            postIdsField = new ProFormMultiSelect<>(Long.class)
                    .setLabel("岗位")
                    .setValueEnum(postService.getValueEnum());
            //
            roleIdsField = new ProFormMultiSelect<>(Long.class)
                    .setLabel("角色")
                    .setValueEnum(roleService.getValueEnum());
            //
            remarksField = new ProFormText()
                    .setLabel("备注");
            //
            form.add(userIdField, nickNameField,
                    deptIdField,
                    phonenumberField, emailField, userNameField,
                    passwordField, sexField, statusField,
                    postIdsField,
                    roleIdsField,
                    remarksField);
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
        SysUser bean = new SysUser();
        try {
            binder.readBean(bean);
        } catch (Exception exp) {
            log.error("读取bean失败:", exp);
        }
        //
        this.setTitle("新增").setOpen(true);
    }


    /**
     * @param id
     */
    public void openUpdateModal(Long id) {
        editType = EditType.UPDATE;
        SysUser bean = userService.selectUserById(id);
        bean.setPassword("");
        //
        List<Long> postIds = postService.selectPostListByUserId(id);
        bean.setPostIds(postIds);
        //
        List<Long> roleIds = bean.getRoles().stream().map(SysRole::getRoleId).toList();
        bean.setRoleIds(roleIds);
        //
        binder.readBean(bean);
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
            // 保存, 区分插入和修改
            if (editType == EditType.INSERT) {
                String passwordEncrypted = SecurityUtils.encryptPassword(bean.getPassword());
                bean.setPassword(passwordEncrypted);
                userService.insertUser(bean);
            } else {
                // 仅当密码有值时，才修改密码
                if (isNotBlank(bean.getPassword())) {
                    String passwordEncrypted = SecurityUtils.encryptPassword(bean.getPassword());
                    bean.setPassword(passwordEncrypted);
                }
                userService.updateUser(bean);
            }
            // 关闭模态框
            this.setOpen(false);
            proTable.getActionRef().reload();
            Notification.success("操作成功");
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

