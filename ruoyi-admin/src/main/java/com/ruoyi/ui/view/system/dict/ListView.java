package com.ruoyi.ui.view.system.dict;


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
import com.muyun.vaadin.antd.procomponents.field.ProFieldValueType;
import com.muyun.vaadin.antd.procomponents.table.ProTable;
import com.muyun.vaadin.antd.procomponents.table.data.FetchCallback;
import com.muyun.vaadin.antd.procomponents.table.data.FetchResult;
import com.muyun.vaadin.antd.procomponents.table.data.FilterMap;
import com.muyun.vaadin.antd.procomponents.table.props.OptionConfig;
import com.muyun.vaadin.antd.procomponents.table.props.ProColumn;
import com.muyun.vaadin.antd.procomponents.table.props.RowEditableConfig;
import com.muyun.vaadin.antd.procomponents.table.props.TablePaginationConfig;
import com.ruoyi.common.core.domain.entity.SysDictType;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.service.ISysDictTypeService;
import com.ruoyi.ui.view.MainLayoutView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;


/**
 * 参数配置
 *
 * @author muyun
 */
@Route(value = "system/dict", layout = MainLayoutView.class)
@PermitAll
@Slf4j
public class ListView extends Div {

    transient ISysDictTypeService dictTypeService;


    @Autowired
    public ListView(ISysDictTypeService dictTypeService) {
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

    ProTable<SysDictType> proTable;

    EditModal editModal;


    void initUI() {
        proTable = new ProTable<>(SysDictType.class)
                .setColumns(createColumns())
                .setCardBordered(true)
                .setDateFormatter("string")
                .setHeaderTitle("查询表格")
                .setRowKey(SysDictType.Fields.dictId)
                .setEditable(new RowEditableConfig()
                        .setType("multiple"))
                .setOptions(getOptionConfig())
                .setPagination(new TablePaginationConfig().setDefaultPageSize(15))
                .setToolBarRender(createToolBarRender());
        // 配置行选择功能
        proTable.getRowSelection().setEnabled(true);
        // 创建编辑模态框
        editModal = new EditModal(proTable, dictTypeService);

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
    List<ProColumn<SysDictType>> createColumns() {


        ProColumn<SysDictType> column1 = new ProColumn<SysDictType>()
                .setTitle("字典主键")
                .setDataIndex(SysDictType::getDictId)
                .setHideInSearch(true);
        //
        ProColumn<SysDictType> column2 = new ProColumn<SysDictType>()
                .setTitle("字典名称")
                .setDataIndex(SysDictType::getDictName);
        //
        ProColumn<SysDictType> column3 = new ProColumn<SysDictType>()
                .setTitle("字典类型")
                .setDataIndex(SysDictType::getDictType)
                .setRender(item -> {
                    Button button = new Button(item.getDictType(), ButtonType.LINK);
                    button.addClickListener(event -> {
                        UI.getCurrent().navigate("system/dict-data",
                                new QueryParameters(Map.of("dictType", List.of(item.getDictType()))));

                    });
                    return button;
                });
        //

        ProColumn<SysDictType> column4 = new ProColumn<SysDictType>()
                .setTitle("状态")
                .setDataIndex(SysDictType::getStatus)
                .setValueEnum(dictTypeService.getValueEnum("sys_normal_disable"))
                .setRender(new TagRender<>(SysDictType::getStatus));

        //
        ProColumn<SysDictType> column5 = new ProColumn<SysDictType>()
                .setTitle("备注")
                .setDataIndex(SysDictType::getRemark)
                .setHideInSearch(true)
                .setEllipsis(true)
                .setWidth("400px");

        //
        ProColumn<SysDictType> column6 = new ProColumn<SysDictType>()
                .setTitle("创建时间")
                .setDataIndex(SysDictType::getCreateTime)
                .setValueType(ProFieldValueType.DATE_TIME)
                .setHideInSearch(true);

        //
        ProColumn<SysDictType> column10 = new ProColumn<SysDictType>()
                .setTitle("操作")
                .setValueType(ProFieldValueType.OPTION)
                .setHideInSearch(true)
                .setWidth("300px")
                .setRender(new AnchorGroupRender<SysDictType>()
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
        FetchCallback<SysDictType> fetchCallback = fetchParam -> {
            FilterMap filter = fetchParam.getFilter();
            SysDictType queryObj = new SysDictType();
            BeanUtil.copyProperties(filter, queryObj);
            //
            List<SysDictType> resultList = dictTypeService.selectDictTypeList(queryObj);
            // 转为结果
            return new FetchResult<SysDictType>()
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
    void handleUpdateEvent(SysDictType dataItem) {
        editModal.openUpdateModal(dataItem.getDictId());
    }


    /**
     * 处理删除事件
     *
     * @param dataItem
     */
    void handleDeleteEvent(SysDictType dataItem) {
        try {
            dictTypeService.deleteDictTypeByIds(new Long[]{dataItem.getDictId()});
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
                        dictTypeService.deleteDictTypeByIds(ids);
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
            List<SysDictType> resultList = dictTypeService.selectDictTypeList(null);
            ExcelUtil<SysDictType> util = new ExcelUtil<>(SysDictType.class);
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

