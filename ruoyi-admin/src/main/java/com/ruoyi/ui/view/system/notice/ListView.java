package com.ruoyi.ui.view.system.notice;


import cn.hutool.core.collection.CollUtil;
import com.muyun.vaadin.antd.button.Button;
import com.muyun.vaadin.antd.button.enums.ButtonType;
import com.muyun.vaadin.antd.flex.Flex;
import com.muyun.vaadin.antd.modal.Modal;
import com.muyun.vaadin.antd.modal.props.ModalProps;
import com.muyun.vaadin.antd.notification.Notification;
import com.muyun.vaadin.antd.procomponents.common.render.AnchorGroupRender;
import com.muyun.vaadin.antd.procomponents.common.render.TagRender;
import com.muyun.vaadin.antd.procomponents.field.ProFieldValueType;
import com.muyun.vaadin.antd.procomponents.table.ProTable;
import com.muyun.vaadin.antd.procomponents.table.data.FetchCallback;
import com.muyun.vaadin.antd.procomponents.table.data.FetchResult;
import com.muyun.vaadin.antd.procomponents.table.data.FilterMap;
import com.muyun.vaadin.antd.procomponents.table.props.OptionConfig;
import com.muyun.vaadin.antd.procomponents.table.props.ProColumn;
import com.muyun.vaadin.antd.procomponents.table.props.RowEditableConfig;
import com.muyun.vaadin.antd.procomponents.table.props.TablePaginationConfig;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.system.domain.SysNotice;
import com.ruoyi.system.service.ISysDictTypeService;
import com.ruoyi.system.service.ISysNoticeService;
import com.ruoyi.ui.view.MainLayoutView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


/**
 * @author muyun
 */
@Route(value = "system/notice", layout = MainLayoutView.class)
@PermitAll
@Slf4j
@SuppressWarnings("java:S1199")
public class ListView extends Div {


    transient ISysDictTypeService dictTypeService;


    transient ISysNoticeService noticeService;

    @Autowired
    public ListView(ISysDictTypeService dictTypeService, ISysNoticeService noticeService) {
        this.dictTypeService = dictTypeService;
        this.noticeService = noticeService;

        // 初始化 UI
        initUI();

        // 初始化行为
        initBehavior();

        // 初始化样式
        initStyle();
    }

    // *****************************************************************************************************************
    //                                      结构
    // *****************************************************************************************************************

    ProTable<SysNotice> proTable;

    EditModal editModal;


    void initUI() {
        proTable = new ProTable<>(SysNotice.class)
                .setColumns(createColumns())
                .setCardBordered(true)
                .setDateFormatter("string")
                .setHeaderTitle("查询表格")
                .setRowKey(SysNotice.Fields.noticeId)
                .setEditable(new RowEditableConfig()
                        .setType("multiple"))
                .setOptions(getOptionConfig())
                .setPagination(new TablePaginationConfig().setDefaultPageSize(15))
                .setToolBarRender(createToolBarRender());
        // 配置行选择功能
        proTable.getRowSelection().setEnabled(true);

        //
        editModal = new EditModal(proTable, noticeService, dictTypeService);
        //
        add(proTable, editModal);
    }


    /**
     * 创建 toolBarRender
     *
     * @return
     */
    ProTable.ToolBarRender createToolBarRender() {
        return (action, selectedRowKeys, selectedRows) -> {
            Flex container = new Flex().setGap("8px");
            {
                Button insertButton = new Button("新增", ButtonType.PRIMARY)
                        .addClickListener(clickEvent -> {
                            handleInsertEvent();
                        });
                //
                Button batchDeleteButton = new Button("批量删除", ButtonType.PRIMARY)
                        .setDanger(true)
                        .addClickListener(clickEvent -> {
                            handleBatchDeleteEvent(selectedRowKeys);
                        });
                // 若没有选中行，则隐藏删除按钮
                boolean isEmpty = CollUtil.isEmpty(selectedRowKeys);
                batchDeleteButton.setHidden(isEmpty);
                //
                container.add(insertButton, batchDeleteButton);
            }
            return container;
        };
    }


    /**
     * @return
     */
    List<ProColumn<SysNotice>> createColumns() {
        ProColumn<SysNotice> column1 = new ProColumn<SysNotice>()
                .setTitle("公告编号")
                .setDataIndex(SysNotice::getNoticeId)
                .setHideInSearch(true);
        //
        ProColumn<SysNotice> column2 = new ProColumn<SysNotice>()
                .setTitle("公告标题")
                .setDataIndex(SysNotice::getNoticeTitle);

        ProColumn<SysNotice> column3 = new ProColumn<SysNotice>()
                .setTitle("公告类型")
                .setDataIndex(SysNotice::getNoticeType)
                .setValueEnum(dictTypeService.getValueEnum("sys_notice_type"))
                .setRender(new TagRender<>(SysNotice::getNoticeType));

        ProColumn<SysNotice> column4 = new ProColumn<SysNotice>()
                .setTitle("公告状态")
                .setDataIndex(SysNotice::getStatus)
                .setValueEnum(dictTypeService.getValueEnum("sys_notice_status"))
                .setHideInSearch(true);
        //
        ProColumn<SysNotice> column5 = new ProColumn<SysNotice>()
                .setTitle("备注")
                .setDataIndex(SysNotice::getRemark)
                .setHideInSearch(true);
        //
        //
        ProColumn<SysNotice> column6 = new ProColumn<SysNotice>()
                .setTitle("创建时间")
                .setValueType(ProFieldValueType.DATE_TIME)
                .setDataIndex(SysNotice::getCreateTime)
                .setHideInSearch(true);

        //
        //
        ProColumn<SysNotice> column10 = new ProColumn<SysNotice>()
                .setTitle("操作")
                .setValueType(ProFieldValueType.OPTION)
                .setHideInSearch(true)
                .setWidth("300px")
                .setRender(new AnchorGroupRender<SysNotice>()
                        .add("修改", this::handleUpdateEvent)
                        .addDeleteAnchor(this::handleDeleteEvent)
                );


        return List.of(column1, column2, column3, column4, column5, column6, column10);
    }


    /**
     * @return
     */
    private OptionConfig getOptionConfig() {
        return new OptionConfig()
                .setSetting(new OptionConfig.SettingOptionType()
                        .setListsHeight(400));
    }

    // *****************************************************************************************************************
    //                                     行为
    // *****************************************************************************************************************


    void initBehavior() {
        // 获取数据
        FetchCallback<SysNotice> fetchCallback = fetchParam -> {
            FilterMap filter = fetchParam.getFilter();
            String noticeTitle = filter.getString("noticeTitle", null);
            String noticeType = filter.getString("noticeType", null);
            String createBy = filter.getString("createBy", null);
            //
            SysNotice queryObj = new SysNotice();
            queryObj.setNoticeTitle(noticeTitle);
            queryObj.setNoticeType(noticeType);
            queryObj.setCreateBy(createBy);
            //
            List<SysNotice> resultList = noticeService.selectNoticeList(queryObj);
            // 转为结果
            return new FetchResult<SysNotice>()
                    .setTotal(PageUtils.getTotal(resultList))
                    .setRecords(resultList);
        };
        //
        proTable.setItems(fetchCallback);
    }


    /**
     * 处理新增事件
     */
    void handleInsertEvent() {
        editModal.openInsertModal();
    }


    /**
     * 处理修改事件
     *
     * @param dataItem
     */
    void handleUpdateEvent(SysNotice dataItem) {
        editModal.openUpdateModal(dataItem.getNoticeId());
    }


    /**
     * 处理删除事件
     *
     * @param dataItem
     */
    void handleDeleteEvent(SysNotice dataItem) {
        try {
            noticeService.deleteNoticeById(dataItem.getNoticeId());
            Notification.success("删除成功");
            proTable.getActionRef().reload();
        } catch (Exception e) {
            log.error("刷新失败", e);
            Notification.error("删除失败");
        }
    }

    /**
     * 处理批量删除事件
     */
    void handleBatchDeleteEvent(List<Object> selectedRowKeys) {
        Modal.confirm(new ModalProps(this)
                .setTitle("是否确认删除所选数据项?")
                .setContent("请谨慎操作")
                .setOkListener(event -> {
                    try {
                        Long[] ids = selectedRowKeys.stream().map(p -> Long.valueOf(p.toString())).toArray(Long[]::new);
                        noticeService.deleteNoticeByIds(ids);
                        Notification.success("删除成功");
                        proTable.getActionRef().reload();
                        proTable.getRowSelection().setSelectedRowKeys(null);
                    } catch (Exception e) {
                        log.error("删除失败", e);
                        Notification.error("删除失败");
                    }
                    // 关闭模态框
                    event.getSource().setOpen(false);
                })
        );
    }


    // *****************************************************************************************************************
    //                                    样式
    // *****************************************************************************************************************

    void initStyle() {
        // 样式

    }


}

