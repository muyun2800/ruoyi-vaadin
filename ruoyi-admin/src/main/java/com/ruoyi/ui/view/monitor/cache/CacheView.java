package com.ruoyi.ui.view.monitor.cache;


import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import com.muyun.vaadin.antd.card.Card;
import com.muyun.vaadin.antd.charts.gauge.Gauge;
import com.muyun.vaadin.antd.charts.gauge.GaugeData;
import com.muyun.vaadin.antd.charts.pie.Pie;
import com.muyun.vaadin.antd.grid.Col;
import com.muyun.vaadin.antd.grid.Row;
import com.muyun.vaadin.antd.table.Table;
import com.muyun.vaadin.antd.table.props.TableColumn;
import com.ruoyi.ui.view.MainLayoutView;
import com.ruoyi.web.controller.monitor.CacheController;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 缓存监控
 *
 * @author muyun
 */
@Route(value = "monitor/cache", layout = MainLayoutView.class)
@PermitAll
public class CacheView extends Div {


    Table infoTable;

    Pie pie;

    Gauge gauge;


    transient CacheController cacheController;

    @Autowired
    public CacheView(CacheController cacheController) {
        this.cacheController = cacheController;

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

    void initUI() {
        // 行： cup, 内存
        Row row1 = new Row(List.of(24, 24));
        {
            Col col1 = new Col(24);
            col1.add(createInfoCard());
            //
            Col col2 = new Col(12);
            col2.add(createCommandCard());
            //
            Col col3 = new Col(12);
            col3.add(createMemoryCard());
            //
            row1.add(col1, col2, col3);
        }
        //
        add(row1);
    }

    /**
     * @return
     */
    private Card createCommandCard() {
        Card card = new Card("命令统计");
        {
            pie = new Pie()
                    .setHeight(320)
                    .setRadius(0.8)
                    .setInnerRadius(0.5)
                    .setAngleField("y")
                    .setColorField("x");
            //.setLegend(false)

            card.add(pie);
        }
        return card;
    }

    /**
     * @return
     */
    private Card createMemoryCard() {
        Card card = new Card("内存信息");
        {
            gauge = new Gauge()
                    .setHeight(320);
            card.add(gauge);
        }
        return card;
    }


    /**
     * @return
     */
    private Card createInfoCard() {
        Card card = new Card("基本信息");
        {
            List<TableColumn> columns = ListUtil.of(
                    new TableColumn("col1", "col1", "col1"),
                    new TableColumn("col2", "col2", "col2"),
                    new TableColumn("col3", "col3", "col3"),
                    new TableColumn("col4", "col4", "col4"),
                    new TableColumn("col5", "col5", "col5"),
                    new TableColumn("col6", "col6", "col6"),
                    new TableColumn("col7", "col7", "col7"),
                    new TableColumn("col8", "col8", "col8")
            );
            infoTable = new Table()
                    .setRowKey("col1")
                    .setPagination(false)
                    .setShowHeader(false)
                    .setColumns(columns)
                    .setLoading(true);

            //
            card.add(infoTable);
        }
        return card;
    }


    // *****************************************************************************************************************
    //                                     行为
    // *****************************************************************************************************************

    void initBehavior() {
        //
        initRedisData();
    }


    void initRedisData() {
        UI ui = UI.getCurrent();
        ThreadUtil.execAsync(() -> {
            try {
                Map<String, Object> dataMap = (Map<String, Object>) cacheController.getInfo().get("data");
                Properties info = (Properties) dataMap.get("info");
                //
                InfoVo vo1 = new InfoVo()
                        .setCol1("Redis版本")
                        .setCol2(info.get("redis_version"))
                        .setCol3("运行模式")
                        .setCol4(ObjectUtil.equals(info.get("redis_mode"), "standalone") ? "单机" : "集群")
                        .setCol5("端口")
                        .setCol6(info.get("tcp_port"))
                        .setCol7("客户端数")
                        .setCol8(info.get("connected_clients"));
                //
                InfoVo vo2 = new InfoVo()
                        .setCol1("运行时间(天)")
                        .setCol2(info.get("uptime_in_days"))
                        .setCol3("使用内存")
                        .setCol4(info.get("used_memory_human"))
                        .setCol5("使用CPU")
                        .setCol6(NumberUtil.round(Double.parseDouble(info.getProperty("used_cpu_user_children")), 2) + " %")
                        .setCol7("内存配置")
                        .setCol8(info.get("maxmemory_human"));
                //if


                double kps = 0;
                double outputKps = Double.parseDouble(info.getProperty("instantaneous_output_kbps"));
                double inputKps = Double.parseDouble(info.getProperty("instantaneous_input_kbps"));
                if (outputKps != 0) {
                    kps = inputKps / outputKps;
                }
                //
                InfoVo vo3 = new InfoVo()
                        .setCol1("AOF是否开启")
                        .setCol2(ObjectUtil.equals(info.get("aof_enabled"), "0") ? "否" : "是")
                        .setCol3("RDB是否成功")
                        .setCol4(info.get("rdb_last_bgsave_status"))
                        .setCol5("Key数量")
                        .setCol6(info.get("dbSize"))
                        .setCol7("网络入口/出口")
                        .setCol8(NumberUtil.round(kps, 2) + " kps");

                List<InfoVo> voList = ListUtil.of(vo1, vo2, vo3);


                ui.access(() -> {
                    // 基本信息
                    infoTable.setDataSource(voList)
                             .setLoading(false);
                    // 命令统计
                    List<Map<String, String>> commandStats = (List<Map<String, String>>) dataMap.get("commandStats");
                    List<PieVo> pieData = commandStats.stream().map(p -> {
                        PieVo pieVo = new PieVo()
                                .setX(p.get("name"))
                                .setY(Integer.valueOf(p.get("value")));
                        return pieVo;
                    }).toList();
                    pie.setData(pieData);

                    //  内存使用率
                    double memUsage = NumberUtil.round(Double.parseDouble(info.getProperty("used_memory")) * 100 /
                                                       Double.parseDouble(info.getProperty("total_system_memory")), 2).doubleValue();
                    gauge.setData(new GaugeData()
                            .setTarget(memUsage)
                            .setTotal(100)
                            .setName("score")
                    );
                });
            } catch (Exception e) {
                e.printStackTrace();
                // todo 获取失败时的处理
            }
        });
    }


    // *****************************************************************************************************************
    //                                    样式
    // *****************************************************************************************************************

    void initStyle() {
        // 样式
        this.getStyle().setPaddingLeft("12px").setPaddingRight("12px");
    }


    @Data
    @Accessors(chain = true)
    class InfoVo {
        private Object col1;
        private Object col2;
        private Object col3;
        private Object col4;
        private Object col5;
        private Object col6;
        private Object col7;
        private Object col8;
    }


    @Data
    @Accessors(chain = true)
    class PieVo {
        private String x;
        private int y;

    }


}
