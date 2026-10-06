package com.ruoyi.ui.view.system.log.operlog;


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
import com.ruoyi.system.domain.SysOperLog;
import com.ruoyi.system.service.ISysDictTypeService;
import com.ruoyi.system.service.ISysOperLogService;
import com.ruoyi.ui.view.MainLayoutView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


/**
 *
 *
 * @author muyun
 */
@Route(value = "system/log/operlog", layout = MainLayoutView.class)
@PermitAll
@SuppressWarnings("java:S1199")
public class ListView extends Div {



    private transient ISysOperLogService operLogService;



    private transient ISysDictTypeService dictTypeService;

    @Autowired
    public ListView(ISysOperLogService operLogService, ISysDictTypeService dictTypeService) {
        this.operLogService = operLogService;
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

    ProTable<SysOperLog> proTable;

    DetailModal detailModal;


    void initUI() {
        proTable = new ProTable<>(SysOperLog.class)
                .setColumns(createColumns())
                .setCardBordered(true)
                .setDateFormatter("string")
                .setHeaderTitle("查询表格")
                .setRowKey("id")
                .setEditable(new RowEditableConfig()
                        .setType("multiple"))
                .setOptions(getOptionConfig())
                .setPagination(new TablePaginationConfig().setDefaultPageSize(15));
        //
        detailModal = new DetailModal(dictTypeService);
        //
        add(proTable, detailModal);

    }

    /**
     * @return
     */
    List<ProColumn<SysOperLog>> createColumns() {
        ProColumn<SysOperLog> column1 = new ProColumn<SysOperLog>()
                .setTitle("编号")
                .setDataIndex(SysOperLog::getOperId)
                .setHideInSearch(true);
        //
        ProColumn<SysOperLog> column2 = new ProColumn<SysOperLog>()
                .setTitle("操作日志记录")
                .setDataIndex(SysOperLog::getTitle);

        ProColumn<SysOperLog> column3 = new ProColumn<SysOperLog>()
                .setTitle("业务类型")
                .setDataIndex(SysOperLog::getBusinessType)
                .setValueEnum(dictTypeService.getValueEnum("sys_oper_type"))
                .setRender(new TagRender<>(SysOperLog::getBusinessType));

        ProColumn<SysOperLog> column4 = new ProColumn<SysOperLog>()
                .setTitle("请求方式")
                .setDataIndex(SysOperLog::getRequestMethod)
                .setHideInSearch(true);
        //
        ProColumn<SysOperLog> column5 = new ProColumn<SysOperLog>()
                .setTitle("操作类别")
                .setDataIndex(SysOperLog::getOperatorType)
                .setHideInSearch(true)
                .setValueEnum(dictTypeService.getValueEnum("sys_oper_type"))
                .setRender(new TagRender<>(SysOperLog::getOperatorType));
        //
        ProColumn<SysOperLog> column6 = new ProColumn<SysOperLog>()
                .setTitle("操作人员")
                .setDataIndex(SysOperLog::getOperName)
                .setHideInSearch(true);
        //
        ProColumn<SysOperLog> column7 = new ProColumn<SysOperLog>()
                .setTitle("操作状态")
                .setDataIndex(SysOperLog::getStatus)
                .setHideInSearch(true)
                .setValueEnum(dictTypeService.getValueEnum("sys_common_status"))
                .setRender(new TagRender<>(SysOperLog::getStatus));

        //
        ProColumn<SysOperLog> column8 = new ProColumn<SysOperLog>()
                .setTitle("主机ip")
                .setDataIndex(SysOperLog::getOperIp)
                .setHideInSearch(true);
        //
        ProColumn<SysOperLog> column9 = new ProColumn<SysOperLog>()
                .setTitle("操作时间")
                .setValueType(ProFieldValueType.DATE_TIME)
                .setDataIndex(SysOperLog::getOperTime)
                .setHideInSearch(true);

        //
        ProColumn<SysOperLog> column10 = new ProColumn<SysOperLog>()
                .setTitle("操作")
                .setValueType(ProFieldValueType.OPTION)
                //.setHideInSearch(true)
                .setRender(new AnchorGroupRender<SysOperLog>()
                        .add("详情", this::showDetail)
                );
        return List.of(column1, column2, column3, column4, column5, column6, column7, column8, column9, column10);
    }

    // *****************************************************************************************************************
    //                                     行为
    // *****************************************************************************************************************


    void initBehavior() {
        FetchCallback<SysOperLog> fetchCallback = fetchParam -> {
            FilterMap filter = fetchParam.getFilter();
            String operIp = filter.getString("operIp", null);
            String title = filter.getString("title", null);
            Integer businessType = filter.getInteger("businessType", null);
            Integer status = filter.getInteger("status", null);
            String operName = filter.getString("operName", null);
            //
            SysOperLog operLog = new SysOperLog();
            operLog.setOperIp(operIp);
            operLog.setTitle(title);
            operLog.setBusinessType(businessType);
            operLog.setStatus(status);
            operLog.setOperName(operName);

            //
            List<SysOperLog> list = operLogService.selectOperLogList(operLog);
            // 转为结果
            return new FetchResult<SysOperLog>()
                    .setTotal(PageUtils.getTotal(list))
                    .setRecords(list);

        };
        //
        proTable.setItems(fetchCallback);

        //


    }


    void showDetail(SysOperLog dataItem) {
        detailModal.openModal(dataItem);
    }


    // *****************************************************************************************************************
    //                                    样式
    // *****************************************************************************************************************

    void initStyle() {
        // 样式 -- 页面背景图

    }


    private OptionConfig getOptionConfig() {
        return new OptionConfig()
                .setSetting(new OptionConfig.SettingOptionType()
                        .setListsHeight(400));
    }


}

