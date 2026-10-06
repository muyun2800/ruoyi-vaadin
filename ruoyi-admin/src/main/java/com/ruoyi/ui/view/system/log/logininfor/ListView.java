package com.ruoyi.ui.view.system.log.logininfor;


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
import com.ruoyi.system.domain.SysLogininfor;
import com.ruoyi.system.service.ISysDictTypeService;
import com.ruoyi.system.service.ISysLogininforService;
import com.ruoyi.ui.view.MainLayoutView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


/**
 * 在线用户
 *
 * @author muyun
 */
@Route(value = "system/log/logininfor", layout = MainLayoutView.class)
@PermitAll
@SuppressWarnings("java:S1199")
public class ListView extends Div {


     transient ISysLogininforService loginnforService;


     transient ISysDictTypeService dictTypeService;


    @Autowired
    public ListView( ISysDictTypeService dictTypeService, ISysLogininforService loginnforService) {
        this.dictTypeService = dictTypeService;
        this.loginnforService = loginnforService;

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

    ProTable<SysLogininfor> proTable;


    void initUI() {
        proTable = new ProTable<>(SysLogininfor.class)
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
        add(proTable);

    }

    /**
     * @return
     */
    List<ProColumn<SysLogininfor>> createColumns() {
        ProColumn<SysLogininfor> column1 = new ProColumn<SysLogininfor>()
                .setTitle("编号")
                .setDataIndex(SysLogininfor::getInfoId)
                .setHideInSearch(true);
        //
        ProColumn<SysLogininfor> column2 = new ProColumn<SysLogininfor>()
                .setTitle("用户账号")
                .setDataIndex(SysLogininfor::getUserName);


        ProColumn<SysLogininfor> column3 = new ProColumn<SysLogininfor>()
                .setTitle("登录ip")
                .setDataIndex(SysLogininfor::getIpaddr);

        ProColumn<SysLogininfor> column4 = new ProColumn<SysLogininfor>()
                .setTitle("登录地点")
                .setDataIndex(SysLogininfor::getLoginLocation)
                .setHideInSearch(true);
        //
        ProColumn<SysLogininfor> column5 = new ProColumn<SysLogininfor>()
                .setTitle("浏览器")
                .setDataIndex(SysLogininfor::getBrowser)
                .setHideInSearch(true);
        //
        ProColumn<SysLogininfor> column6 = new ProColumn<SysLogininfor>()
                .setTitle("操作系统")
                .setDataIndex(SysLogininfor::getOs)
                .setHideInSearch(true);
        //
        ProColumn<SysLogininfor> column7 = new ProColumn<SysLogininfor>()
                .setTitle("登录状态")
                .setDataIndex(SysLogininfor::getStatus)
                .setValueEnum(dictTypeService.getValueEnum("sys_common_status"))
                .setRender(new TagRender<>(SysLogininfor::getStatus));

        //
        ProColumn<SysLogininfor> column8 = new ProColumn<SysLogininfor>()
                .setTitle("提示消息")
                .setDataIndex(SysLogininfor::getMsg)
                .setHideInSearch(true);
        //
        ProColumn<SysLogininfor> column9 = new ProColumn<SysLogininfor>()
                .setTitle("登录时间")
                .setValueType(ProFieldValueType.DATE_TIME)
                .setDataIndex(SysLogininfor::getLoginTime)
                .setHideInSearch(true);
        //
        return List.of(column1, column2, column3, column4, column5, column6, column7, column8, column9);
    }

    // *****************************************************************************************************************
    //                                     行为
    // *****************************************************************************************************************


    void initBehavior() {
        FetchCallback<SysLogininfor> fetchCallback = fetchParam -> {
            FilterMap filter = fetchParam.getFilter();
            String ipaddr = filter.getString("ipaddr", null);
            String userName = filter.getString("userName", null);
            String status = filter.getString("status", null);
            //
            SysLogininfor logininfor = new SysLogininfor();
            logininfor.setIpaddr(ipaddr);
            logininfor.setUserName(userName);
            logininfor.setStatus(status);
            List<SysLogininfor> list = loginnforService.selectLogininforList(logininfor);
            // 转为结果
            return new FetchResult<SysLogininfor>()
                    .setTotal(PageUtils.getTotal(list))
                    .setRecords(list);
        };
        //
        proTable.setItems(fetchCallback);
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

