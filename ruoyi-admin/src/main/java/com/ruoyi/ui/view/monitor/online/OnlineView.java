package com.ruoyi.ui.view.monitor.online;


import com.muyun.vaadin.antd.procomponents.common.render.AnchorGroupRender;
import com.muyun.vaadin.antd.procomponents.field.ProFieldValueType;
import com.muyun.vaadin.antd.procomponents.table.ProTable;
import com.muyun.vaadin.antd.procomponents.table.data.FetchCallback;
import com.muyun.vaadin.antd.procomponents.table.data.FetchResult;
import com.muyun.vaadin.antd.procomponents.table.data.FilterMap;
import com.muyun.vaadin.antd.procomponents.table.props.OptionConfig;
import com.muyun.vaadin.antd.procomponents.table.props.ProColumn;
import com.muyun.vaadin.antd.procomponents.table.props.RowEditableConfig;
import com.muyun.vaadin.antd.procomponents.table.props.TablePaginationConfig;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.SysUserOnline;
import com.ruoyi.ui.view.MainLayoutView;
import com.ruoyi.web.controller.monitor.SysUserOnlineController;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;

/**
 * 在线用户
 *
 * @author muyun
 */
@Route(value = "monitor/online", layout = MainLayoutView.class)
@PermitAll
@SuppressWarnings("java:S1199")
public class OnlineView extends Div {

    public static final String STR_OPTION = "option";


    private   transient SysUserOnlineController sysUserOnlineController;


    @Autowired
    public OnlineView(SysUserOnlineController sysUserOnlineController) {
        this.sysUserOnlineController = sysUserOnlineController;


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

    ProTable<SysUserOnline> proTable;


    void initUI() {
        proTable = new ProTable<>(SysUserOnline.class)
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
    List<ProColumn<SysUserOnline>> createColumns() {
        ProColumn<SysUserOnline> column1 = new ProColumn<SysUserOnline>()
                .setTitle("会话编号")
                .setDataIndex(SysUserOnline::getTokenId)
                .setHideInSearch(true);
        //
        ProColumn<SysUserOnline> column2 = new ProColumn<SysUserOnline>()
                .setTitle("用户名")
                .setDataIndex(SysUserOnline::getUserName);

        //
        ProColumn<SysUserOnline> column3 = new ProColumn<SysUserOnline>()
                .setTitle("部门")
                .setDataIndex(SysUserOnline::getDeptName)
                .setHideInSearch(true);
        //
        ProColumn<SysUserOnline> column4 = new ProColumn<SysUserOnline>()
                .setTitle("登录ip")
                .setDataIndex(SysUserOnline::getIpaddr);
        //
        ProColumn<SysUserOnline> column5 = new ProColumn<SysUserOnline>()
                .setTitle("登录地点")
                .setDataIndex(SysUserOnline::getLoginLocation)
                .setHideInSearch(true);
        //
        ProColumn<SysUserOnline> column6 = new ProColumn<SysUserOnline>()
                .setTitle("浏览器")
                .setDataIndex(SysUserOnline::getOs)
                .setHideInSearch(true);
        //
        ProColumn<SysUserOnline> column7 = new ProColumn<SysUserOnline>()
                .setTitle("操作系统")
                .setDataIndex(SysUserOnline::getBrowser)
                .setHideInSearch(true);
        //
        ProColumn<SysUserOnline> column8 = new ProColumn<SysUserOnline>()
                .setTitle("登录时间")
                .setValueType(ProFieldValueType.DATE_TIME)
                .setDataIndex(SysUserOnline::getLoginTime)
                .setHideInSearch(true);

        //
        ProColumn<SysUserOnline> column10 = new ProColumn<SysUserOnline>()
                .setTitle("操作")
                .setValueType(ProFieldValueType.OPTION)
                //.setHideInSearch(true)
                .setRender(new AnchorGroupRender<SysUserOnline>()
                        .add("强退", this::handleForceLogout)
                );
        return List.of(column1, column2, column3, column4, column5, column6, column7, column8, column10);
    }

    // *****************************************************************************************************************
    //                                     行为
    // *****************************************************************************************************************


    void initBehavior() {
        FetchCallback<SysUserOnline> fetchCallback = fetchParam -> {
            FilterMap filter = fetchParam.getFilter();

            String ipaddr = filter.getString("ipaddr", null);
            String userName = filter.getString("userName", null);
            TableDataInfo tableDataInfo = sysUserOnlineController.list(ipaddr, userName);
            // 转为结果
            return new FetchResult<SysUserOnline>()
                    .setTotal(tableDataInfo.getTotal())
                    .setRecords((Collection<SysUserOnline>) tableDataInfo.getRows());
        };
        //
        proTable.setItems(fetchCallback);
    }


    void handleForceLogout(SysUserOnline dataItem) {
        String tokenId = dataItem.getTokenId();
        sysUserOnlineController.forceLogout(tokenId);
        proTable.getActionRef().reload();
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