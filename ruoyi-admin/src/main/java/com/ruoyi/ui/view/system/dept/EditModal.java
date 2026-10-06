package com.ruoyi.ui.view.system.dept;

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
import com.muyun.vaadin.antd.procomponents.form.text.ProFormText;
import com.muyun.vaadin.antd.procomponents.form.text.ProFormTextArea;
import com.muyun.vaadin.antd.procomponents.form.treeselect.ProFormTreeSelect;
import com.muyun.vaadin.antd.procomponents.table.ProTable;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.system.service.ISysDeptService;
import com.ruoyi.system.service.ISysDictTypeService;
import com.vaadin.flow.data.binder.PropertyId;
import com.vaadin.flow.data.binder.ValidationException;

import static org.reflections.Reflections.log;

/**
 * 修改密码模态框
 *
 * @author 何德海
 * @version 2026/05/27
 **/

public class EditModal extends Modal {
    ProTable<SysDept> proTable;

    transient EditType editType;


    transient ISysDeptService deptService;

    transient ISysDictTypeService dictTypeService;


    public EditModal(ProTable<SysDept> proTable, ISysDeptService deptService, ISysDictTypeService dictTypeService) {
        this.proTable = proTable;
        this.deptService = deptService;
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



    @PropertyId(SysDept.Fields.deptId)
    ProFormDigit<Long> deptIdField;


    @PropertyId(SysDept.Fields.parentId)
    ProFormTreeSelect<Long> parentIdField;

    @PropertyId(SysDept.Fields.deptName)
    ProFormText deptNameField;


    @PropertyId(SysDept.Fields.orderNum)
    ProFormDigit<Integer> orderNumField;

    @PropertyId(SysDept.Fields.leader)
    ProFormText leaderField;

    @PropertyId(SysDept.Fields.phone)
    ProFormText phoneField;

    @PropertyId(SysDept.Fields.email)
    ProFormText emailField;

    @PropertyId(SysDept.Fields.status)
    ProFormRadioGroup<String> statusField;

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
            deptIdField = new ProFormDigit<>(Long.class)
                    .setLabel("ID")
                    .setHidden(true);
            //
            parentIdField = new ProFormTreeSelect<>(Long.class)
                    .setLabel("上级部门");
            parentIdField.getField()
                         .setTreeData(deptService.getDeptTreeData())
                         .setReactStyle(ReactStyle.of("width", "100%"))
                         .setTreeDefaultExpandAll(true)
                         .setTreeIcon(true)
                         .setShowSearch(true)
                         .setFilterTreeNode(true)
                         .setAllowClear(true);
            //
            deptNameField = new ProFormText()
                    .setLabel("部门名称");
            //
            orderNumField = new ProFormDigit<>(Integer.class)
                    .setLabel("显示顺序");
            //
            leaderField = new ProFormText()
                    .setLabel("负责人");
            //
            phoneField = new ProFormText()
                    .setLabel("联系电话");
            //
            //
            emailField = new ProFormText()
                    .setLabel("邮箱");
            //
            statusField = new ProFormRadioGroup<>(String.class)
                    .setLabel("状态")
                    .setValueEnum(dictTypeService.getValueEnum("sys_normal_disable"));
            //
            remarksField = new ProFormTextArea()
                    .setLabel("备注");

            //
            form.add(deptIdField, parentIdField, deptNameField, orderNumField, leaderField,
                    phoneField, emailField, statusField, remarksField);
        }
        //
        add(form);
        //
    }


    // *****************************************************************************************************************
    //                                     行为
    // *****************************************************************************************************************
    ExtBeanValidationBinder<SysDept> binder = new ExtBeanValidationBinder<>(SysDept.class);


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
     *
     */
    public void openInsertModal() {
        editType = EditType.INSERT;
        SysDept bean = new SysDept();
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
        SysDept bean = deptService.selectDeptById(id);
        binder.readBean(bean);
        //
        this.setTitle("修改").setOpen(true);
    }


    /**
     *
     */
    void handleOkEvent(ModalOkEvent event) {
        SysDept bean = new SysDept();
        try {
            // 验证
            binder.writeBean(bean);
            log.debug("bean = {}", bean);
            // 保存, 区分插入和修改
            if (editType == EditType.INSERT) {
                deptService.insertDept(bean);
            } else {
                deptService.updateDept(bean);
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

