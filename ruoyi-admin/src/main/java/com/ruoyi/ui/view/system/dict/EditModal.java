package com.ruoyi.ui.view.system.dict;

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
import com.muyun.vaadin.antd.procomponents.table.ProTable;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.domain.entity.SysDictType;
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
    ProTable<SysDictType> proTable;

    transient EditType editType;


    transient ISysDictTypeService dictTypeService;


    public EditModal(ProTable<SysDictType> proTable, ISysDictTypeService dictTypeService) {
        this.proTable = proTable;
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


    @PropertyId(SysDictType.Fields.dictId)
    ProFormDigit<Long> dictIdField;

    @PropertyId(SysDictType.Fields.dictName)
    ProFormText dictNameField;


    @PropertyId(SysDictType.Fields.dictType)
    ProFormText dictTypeField;


    @PropertyId(SysDictType.Fields.status)
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
            dictIdField = new ProFormDigit<>(Long.class)
                    .setLabel("ID")
                    .setHidden(true);
            //
            dictNameField = new ProFormText()
                    .setLabel("字典名称");
            //
            dictTypeField = new ProFormText()
                    .setLabel("字典类型");
            //
            statusField = new ProFormRadioGroup<>(String.class)
                    .setLabel("状态")
                    .setValueEnum(dictTypeService.getValueEnum("sys_normal_disable"));
            //
            remarksField = new ProFormTextArea()
                    .setLabel("备注");
            //
            form.add(dictIdField, dictNameField, dictTypeField, statusField, remarksField);
        }
        //
        add(form);
    }

    // *****************************************************************************************************************
    //                                     行为
    // *****************************************************************************************************************
    ExtBeanValidationBinder<SysDictType> binder = new ExtBeanValidationBinder<>(SysDictType.class);


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
        SysDictType bean = new SysDictType();
        try {
            binder.readBean(bean);
        } catch (Exception exp) {
            log.error("读取bean失败", exp);
        }
        //
        this.setTitle("新增").setOpen(true);
    }


    /**
     * @param id
     */
    public void openUpdateModal(Long id) {
        editType = EditType.UPDATE;
        SysDictType bean = dictTypeService.selectDictTypeById(id);
        binder.readBean(bean);
        //
        this.setTitle("修改").setOpen(true);
    }


    /**
     *
     */
    void handleOkEvent(ModalOkEvent event) {
        SysDictType bean = new SysDictType();
        try {
            // 验证
            binder.writeBean(bean);
            log.debug("bean = {}", bean);
            // 保存, 区分插入和修改
            if (editType == EditType.INSERT) {
                dictTypeService.insertDictType(bean);
            } else {
                dictTypeService.updateDictType(bean);
            }
            // 关闭模态框
            this.setOpen(false);
            proTable.getActionRef().reload();
            Notification.success("操作成功");
        } catch (ValidationException exp) {
            log.error("验证失败, {}", exp.getMessage(),  exp);
        } catch (Exception exp) {
            log.error("操作失败, {}", exp.getMessage(),  exp);
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

