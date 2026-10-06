package com.ruoyi.ui.view.system.log.operlog;

import com.muyun.vaadin.antd.modal.Modal;
import com.muyun.vaadin.antd.procomponents.common.schema.ValueEnumType;
import com.muyun.vaadin.antd.procomponents.descriptions.ProDescriptions;
import com.muyun.vaadin.antd.procomponents.descriptions.props.ProDescriptionsItem;
import com.ruoyi.system.domain.SysOperLog;
import com.ruoyi.system.service.ISysDictTypeService;

import java.util.List;
import java.util.Map;

/**
 * @author 何德海
 * @version 2026/05/27
 **/

public class DetailModal extends Modal {

    transient ISysDictTypeService dictTypeService;

    public DetailModal(ISysDictTypeService dictTypeService) {
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


    ProDescriptions descriptions;

    void initUI() {
        setWidth(640);
        //
        descriptions = new ProDescriptions()
                .setColumnCount(24);
        //
        add(descriptions);
    }


    // *****************************************************************************************************************
    //                                     行为
    // *****************************************************************************************************************
    void initBehavior() {
        // 取消按钮事件
        this.addCancelListener(e -> {
            this.setOpen(false);
        });
        // 确定按钮事件
        this.addOkListener(e -> {
            this.setOpen(false);
        });
    }


    public void openModal(SysOperLog operLog) {
        this.setOpen(true);
        //
        List<ProDescriptionsItem> items = getProDescriptionsItems(operLog);
        descriptions.setItems(items);
    }


    private List<ProDescriptionsItem> getProDescriptionsItems(SysOperLog operLog) {
        Map<String, ValueEnumType> operTypeEnum = dictTypeService.getValueEnum("sys_oper_type");
        Map<String, ValueEnumType> commonStatusEnum = dictTypeService.getValueEnum("sys_common_status");

        ProDescriptionsItem item1 = new ProDescriptionsItem()
                .setSpan(12)
                .setLabel("业务类型")
                .setValueEnum(operTypeEnum)
                .setValue(operLog.getBusinessType());
        //
        ProDescriptionsItem item2 = new ProDescriptionsItem()
                .setSpan(12)
                .setLabel("请求方式")
                .setValue(operLog.getRequestMethod());
        //
        ProDescriptionsItem item3 = new ProDescriptionsItem()
                .setSpan(12)
                .setLabel("操作人员")
                .setValue(operLog.getOperName());
        //
        ProDescriptionsItem item4 = new ProDescriptionsItem()
                .setSpan(12)
                .setLabel("操作类别")
                .setValueEnum(operTypeEnum)
                .setValue(operLog.getOperatorType());
        //
        ProDescriptionsItem item5 = new ProDescriptionsItem()
                .setSpan(24)
                .setLabel("方法名称")
                .setValue(operLog.getMethod());
        //
        ProDescriptionsItem item7 = new ProDescriptionsItem()
                .setSpan(24)
                .setLabel("请求URL")
                .setValue(operLog.getOperUrl());

        //
        ProDescriptionsItem item9 = new ProDescriptionsItem()
                .setSpan(24)
                .setLabel("请求参数")
                .setValue(operLog.getOperParam());
        //
        ProDescriptionsItem item11 = new ProDescriptionsItem()
                .setSpan(24)
                .setLabel("返回参数")
                .setValue(operLog.getJsonResult());
        //
        ProDescriptionsItem item13 = new ProDescriptionsItem()
                .setSpan(24)
                .setLabel("错误消息")
                .setValue(operLog.getErrorMsg());
        //
        ProDescriptionsItem item15 = new ProDescriptionsItem()
                .setSpan(12)
                .setValueEnum(commonStatusEnum)
                .setLabel("操作状态")
                .setValue(operLog.getStatus());
        //
        ProDescriptionsItem item16 = new ProDescriptionsItem()
                .setSpan(12)
                .setValueType("dateTime")
                .setLabel("操作时间")
                .setValue(operLog.getOperTime());


        return List.of(item1, item2, item3, item4, item5, item7, item9, item11, item13, item15, item16);
    }
    // *****************************************************************************************************************
    //                                    样式
    // *****************************************************************************************************************

    void initStyle() {
        // 页面样式

    }

}

