package com.ruoyi.ui.view.system.dict.data;

import com.muyun.vaadin.antd.common.binder.ExtBeanValidationBinder;
import com.muyun.vaadin.antd.common.enums.EditType;
import com.muyun.vaadin.antd.form.Form;
import com.muyun.vaadin.antd.form.props.FormCol;
import com.muyun.vaadin.antd.modal.Modal;
import com.muyun.vaadin.antd.modal.event.ModalOkEvent;
import com.muyun.vaadin.antd.notification.Notification;
import com.muyun.vaadin.antd.procomponents.common.schema.ValueEnumType;
import com.muyun.vaadin.antd.procomponents.form.digit.ProFormDigit;
import com.muyun.vaadin.antd.procomponents.form.radio.ProFormRadioGroup;
import com.muyun.vaadin.antd.procomponents.form.select.ProFormSelect;
import com.muyun.vaadin.antd.procomponents.form.text.ProFormText;
import com.muyun.vaadin.antd.procomponents.form.text.ProFormTextArea;
import com.muyun.vaadin.antd.procomponents.table.ProTable;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.system.service.ISysDictDataService;
import com.ruoyi.system.service.ISysDictTypeService;
import com.vaadin.flow.data.binder.PropertyId;
import com.vaadin.flow.data.binder.ValidationException;

import java.util.Map;

import static org.reflections.Reflections.log;

/**
 * 修改密码模态框
 *
 * @author 何德海
 * @version 2026/05/27
 **/

public class EditModal extends Modal {
    ProTable<SysDictData> proTable;

    transient EditType editType;


    transient ISysDictDataService dictDataService;

    transient ISysDictTypeService dictTypeService;


    public EditModal(ProTable<SysDictData> proTable, ISysDictDataService dictDataService, ISysDictTypeService dictTypeService) {
        this.proTable = proTable;
        this.dictDataService = dictDataService;
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

    @PropertyId(SysDictData.Fields.dictCode)
    ProFormDigit<Long> dictCodeField;

    @PropertyId(SysDictData.Fields.dictType)
    ProFormText dictTypeField;


    @PropertyId(SysDictData.Fields.dictLabel)
    ProFormText dictLabelField;

    @PropertyId(SysDictData.Fields.dictValue)
    ProFormText dictValueField;


    @PropertyId(SysDictData.Fields.cssClass)
    ProFormText cssClassField;


    @PropertyId(SysDictData.Fields.listClass)
    ProFormSelect<String> listClassField;


    @PropertyId(SysDictData.Fields.dictSort)
    ProFormText dictSortField;


    @PropertyId(SysDictData.Fields.isDefault)
    ProFormRadioGroup<String> isDefaultField;

    @PropertyId(SysDictData.Fields.status)
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
            dictCodeField = new ProFormDigit<>(Long.class)
                    .setLabel("ID")
                    .setHidden(true);
            // todo .. setDisable api
            dictTypeField = new ProFormText()
                    .setLabel("字典类型")
                    .setDisabled(true);
            //
            dictLabelField = new ProFormText()
                    .setLabel("字典标签");

            //
            dictValueField = new ProFormText()
                    .setLabel("字典键值");
            //
            cssClassField = new ProFormText()
                    .setLabel("回显样式");
            //
            listClassField = new ProFormSelect<>(String.class)
                    .setLabel("请输入回显样式")
                    .setValueEnum(Map.of(
                            "default", new ValueEnumType("默认"),
                            "primary", new ValueEnumType("主要"),
                            "success", new ValueEnumType("成功"),
                            "info", new ValueEnumType("信息"),
                            "warning", new ValueEnumType("警告"),
                            "danger", new ValueEnumType("危险")
                    ));
            //
            dictSortField = new ProFormText()
                    .setLabel("字典排序");

            //
            isDefaultField = new ProFormRadioGroup<>(String.class)
                    .setLabel("是否默认")
                    .setValueEnum(Map.of(
                            "Y", new ValueEnumType("是"),
                            "N", new ValueEnumType("否")
                    ));

            //
            statusField = new ProFormRadioGroup<>(String.class)
                    .setLabel("状态")
                    .setValueEnum(dictTypeService.getValueEnum("sys_normal_disable"));


            //
            remarksField = new ProFormTextArea()
                    .setLabel("备注");
            //
            form.add(dictCodeField, dictTypeField, dictLabelField, dictValueField, cssClassField,
                    listClassField, dictSortField, isDefaultField, statusField, remarksField);
        }
        //
        add(form);
    }

    // *****************************************************************************************************************
    //                                     行为
    // *****************************************************************************************************************
    ExtBeanValidationBinder<SysDictData> binder = new ExtBeanValidationBinder<>(SysDictData.class);


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
    public void openInsertModal(String dictType) {
        editType = EditType.INSERT;
        SysDictData bean = new SysDictData();
        bean.setDictType(dictType);
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
        SysDictData bean = dictDataService.selectDictDataById(id);
        binder.readBean(bean);
        //
        this.setTitle("修改").setOpen(true);
    }


    /**
     *
     */
    void handleOkEvent(ModalOkEvent event) {
        SysDictData bean = new SysDictData();
        try {
            // 验证
            binder.writeBean(bean);
            log.debug("bean = {}", bean);
            // 保存, 区分插入和修改
            if (editType == EditType.INSERT) {
                dictDataService.insertDictData(bean);
            } else {
                dictDataService.updateDictData(bean);
            }
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

