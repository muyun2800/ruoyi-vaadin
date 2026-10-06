package com.ruoyi.ui.view.system.dict.data;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.muyun.vaadin.antd.button.Button;
import com.muyun.vaadin.antd.button.enums.ButtonType;
import com.muyun.vaadin.antd.flex.Flex;
import com.muyun.vaadin.antd.icon.Icon;
import com.muyun.vaadin.antd.icon.enums.IconName;
import com.muyun.vaadin.antd.modal.Modal;
import com.muyun.vaadin.antd.modal.props.ModalProps;
import com.muyun.vaadin.antd.notification.Notification;
import com.muyun.vaadin.antd.procomponents.common.render.AnchorGroupRender;
import com.muyun.vaadin.antd.procomponents.common.render.TagRender;
import com.muyun.vaadin.antd.procomponents.common.schema.ValueEnumType;
import com.muyun.vaadin.antd.procomponents.field.ProFieldValueType;
import com.muyun.vaadin.antd.procomponents.table.ProTable;
import com.muyun.vaadin.antd.procomponents.table.data.FetchCallback;
import com.muyun.vaadin.antd.procomponents.table.data.FetchResult;
import com.muyun.vaadin.antd.procomponents.table.data.FilterMap;
import com.muyun.vaadin.antd.procomponents.table.props.OptionConfig;
import com.muyun.vaadin.antd.procomponents.table.props.ProColumn;
import com.muyun.vaadin.antd.procomponents.table.props.RowEditableConfig;
import com.muyun.vaadin.antd.procomponents.table.props.TablePaginationConfig;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.core.domain.entity.SysDictType;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.service.ISysDictDataService;
import com.ruoyi.system.service.ISysDictTypeService;
import com.ruoyi.ui.view.MainLayoutView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 *
 * @author muyun
 */
@Route(value = "system/dict-data", layout = MainLayoutView.class)
@PageTitle("字典数据")
@PermitAll
@Slf4j
public class ListView extends Div implements BeforeEnterObserver {

    transient ISysDictDataService dictDataService;

    transient ISysDictTypeService dictTypeService;

    public static final String QUERY_NAME_DICT_TYPE = "dictType";


    @Autowired
    public ListView(ISysDictDataService dictDataService, ISysDictTypeService dictTypeService) {
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
    //                                      结构
    // *****************************************************************************************************************

    ProTable<SysDictData> proTable;

    EditModal editModal;


    String dictType;


    void initUI() {
        proTable = new ProTable<>(SysDictData.class)
                .setColumns(createColumns())
                .setCardBordered(true)
                .setDateFormatter("string")
                .setHeaderTitle("查询表格")
                .setRowKey(SysDictData.Fields.dictCode)
                .setEditable(new RowEditableConfig()
                        .setType("multiple"))
                .setOptions(getOptionConfig())
                .setPagination(new TablePaginationConfig().setDefaultPageSize(15))
                .setToolBarRender(createToolBarRender());
        // 配置行选择功能
        proTable.getRowSelection().setEnabled(true);
        // 创建编辑模态框
        editModal = new EditModal(proTable, dictDataService, dictTypeService);

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
                        .setIcon(Icon.of(IconName.PLUS_OUTLINED))
                        .addClickListener(clickEvent -> handleInsertEvent());
                // 批量删除
                Button batchDeleteButton = new Button("批量删除", ButtonType.PRIMARY)
                        .setIcon(Icon.of(IconName.DELETE_OUTLINED))
                        .setDanger(true)
                        .addClickListener(clickEvent -> handleBatchDeleteEvent(selectedRowKeys));
                // 若没有选中行，则隐藏删除按钮
                boolean isEmpty = CollUtil.isEmpty(selectedRowKeys);
                batchDeleteButton.setHidden(isEmpty);
                // 导出
                Button exportButton = new Button("导出", ButtonType.PRIMARY)
                        .setIcon(Icon.of(IconName.DOWNLOAD_OUTLINED))
                        .addClickListener(clickEvent -> handleExportEvent());
                //
                container.add(insertButton, batchDeleteButton, exportButton);
            }
            return container;
        };
    }


    /**
     * @return
     */
    List<ProColumn<SysDictData>> createColumns() {
        ProColumn<SysDictData> column1 = new ProColumn<SysDictData>()
                .setTitle("字典编码")
                .setDataIndex(SysDictData::getDictCode)
                .setHideInSearch(true);
        //
        ProColumn<SysDictData> column2 = new ProColumn<SysDictData>()
                .setTitle("字典标签")
                .setDataIndex(SysDictData::getDictLabel);
        //
        ProColumn<SysDictData> column3 = new ProColumn<SysDictData>()
                .setTitle("字典类型")
                .setDataIndex(SysDictData::getDictType)
                .setHideInTable(true)
                .setValueEnum(getValueEnumOfDictType());
        //
        ProColumn<SysDictData> column4 = new ProColumn<SysDictData>()
                .setTitle("字典键值")
                .setDataIndex(SysDictData::getDictValue);
        //
        ProColumn<SysDictData> column5 = new ProColumn<SysDictData>()
                .setTitle("字典排序")
                .setHideInTable(true)
                .setDataIndex(SysDictData::getDictSort);
        //
        ProColumn<SysDictData> column6 = new ProColumn<SysDictData>()
                .setTitle("状态")
                .setDataIndex(SysDictData::getStatus)
                .setValueEnum(dictTypeService.getValueEnum("sys_normal_disable"))
                .setRender(new TagRender<>(SysDictData::getStatus));
        //
        ProColumn<SysDictData> column7 = new ProColumn<SysDictData>()
                .setTitle("备注")
                .setDataIndex(SysDictData::getRemark)
                .setHideInSearch(true)
                .setEllipsis(true)
                .setWidth("400px");
        //
        ProColumn<SysDictData> column8 = new ProColumn<SysDictData>()
                .setTitle("创建时间")
                .setDataIndex(SysDictData::getCreateTime)
                .setValueType(ProFieldValueType.DATE_TIME)
                .setHideInSearch(true);

        //
        ProColumn<SysDictData> column10 = new ProColumn<SysDictData>()
                .setTitle("操作")
                .setValueType(ProFieldValueType.OPTION)
                .setHideInSearch(true)
                .setWidth("300px")
                .setRender(new AnchorGroupRender<SysDictData>()
                        .add("修改", this::handleUpdateEvent)
                        .addDeleteAnchor(this::handleDeleteEvent)
                );

        return List.of(column1, column2, column3, column4, column5, column6, column7, column8, column10);
    }


    /**
     * @return
     */
    Map<String, ValueEnumType> getValueEnumOfDictType() {
        List<SysDictType> dictTypes = dictTypeService.selectDictTypeAll();
        Map<String, ValueEnumType> valueEnumTypeMap = new HashMap<>();
        for (SysDictType sysDictType : dictTypes) {
            String key = sysDictType.getDictType();
            String label = sysDictType.getDictName();
            //
            ValueEnumType enumType = new ValueEnumType(label);
            valueEnumTypeMap.put(key, enumType);
        }
        return valueEnumTypeMap;


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
        FetchCallback<SysDictData> fetchCallback = fetchParam -> {
            FilterMap filter = fetchParam.getFilter();
            SysDictData queryObj = new SysDictData();
            BeanUtil.copyProperties(filter, queryObj);
            //
            List<SysDictData> resultList = dictDataService.selectDictDataList(queryObj);
            // 转为结果
            return new FetchResult<SysDictData>()
                    .setTotal(PageUtils.getTotal(resultList))
                    .setRecords(resultList);
        };
        //
        proTable.setItems(fetchCallback);
    }


    /**
     * 这个发生在 initBehavior之后
     *
     * @param event before navigation event with event details
     */
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Map<String, List<String>> parameters = event.getLocation().getQueryParameters().getParameters();
        // 提取dictType来查询表格
        if (CollUtil.isNotEmpty(parameters.get(QUERY_NAME_DICT_TYPE))) {
            this.dictType = parameters.get(QUERY_NAME_DICT_TYPE).get(0);
            //  设置搜索表单，然后提交搜索表单
            proTable.getFormRef().setFieldsValue(Map.of(QUERY_NAME_DICT_TYPE, this.dictType));
            proTable.getFormRef().submit();

        }
    }


    /**
     * 处理新增事件
     */
    void handleInsertEvent() {

        editModal.openInsertModal(this.dictType);

    }


    /**
     * 处理修改事件
     *
     * @param dataItem
     */
    void handleUpdateEvent(SysDictData dataItem) {
        editModal.openUpdateModal(dataItem.getDictCode());
    }


    /**
     * 处理删除事件
     *
     * @param dataItem
     */
    void handleDeleteEvent(SysDictData dataItem) {
        try {
            dictDataService.deleteDictDataByIds(new Long[]{dataItem.getDictCode()});
            Notification.success("删除成功");
            proTable.getActionRef().reload();
        } catch (Exception e) {
            log.error("删除失败", e);
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
                        dictDataService.deleteDictDataByIds(ids);
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


    /**
     * 处理导出事件
     */
    void handleExportEvent() {
        try {
            List<SysDictData> resultList = dictDataService.selectDictDataList(null);
            ExcelUtil<SysDictData> util = new ExcelUtil<>(SysDictData.class);
            util.exportExcelVaadin(resultList, "参数数据");
            Notification.success("导出成功");
        } catch (Exception e) {
            log.error("导出失败", e);
            Notification.error("导出失败");
        }
    }


    // *****************************************************************************************************************
    //                                    样式
    // *****************************************************************************************************************

    void initStyle() {
        // 样式

    }


}

