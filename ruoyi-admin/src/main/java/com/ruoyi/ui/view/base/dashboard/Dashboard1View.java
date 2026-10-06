package com.ruoyi.ui.view.base.dashboard;


import com.muyun.vaadin.antd.card.Card;
import com.muyun.vaadin.antd.charts.area.Area;
import com.muyun.vaadin.antd.charts.column.Column;
import com.muyun.vaadin.antd.charts.common.option.ChartStyle;
import com.muyun.vaadin.antd.charts.common.option.Slider;
import com.muyun.vaadin.antd.charts.gauge.Gauge;
import com.muyun.vaadin.antd.charts.gauge.GaugeData;
import com.muyun.vaadin.antd.charts.line.Line;
import com.muyun.vaadin.antd.charts.liquid.Liquid;
import com.muyun.vaadin.antd.charts.pie.Pie;
import com.muyun.vaadin.antd.charts.wordcloud.WordCloud;
import com.muyun.vaadin.antd.charts.wordcloud.WordCloudLayout;
import com.muyun.vaadin.antd.grid.Col;
import com.muyun.vaadin.antd.grid.Row;
import com.muyun.vaadin.antd.procomponents.statisticcard.Statistic;
import com.muyun.vaadin.antd.procomponents.statisticcard.StatisticCard;
import com.muyun.vaadin.antd.procomponents.statisticcard.props.StatisticProps;
import com.ruoyi.framework.security.vaadin.VaadinSecurityService;
import com.ruoyi.system.service.ISysMenuService;
import com.ruoyi.ui.view.MainLayoutView;
import com.ruoyi.web.controller.common.DashboardService;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;

import java.util.Collections;
import java.util.List;

/**
 * 最简单的用法。
 *
 * @author muyun
 */
@PageTitle("数据看板")
@Route(value = "dashboard/dashboard1", layout = MainLayoutView.class)
@PermitAll  // 允许登录的用户访问
public class Dashboard1View extends Div {


    // 注意，这个AuthenticationContext 是由 vaadin-spring 提供的
    @Autowired
    transient VaadinSecurityService vaadinSecurityService;


    @Autowired
    transient AuthenticationManager authenticationManager;


    transient DashboardService dashboardService;

    //  @Autowired
    transient ISysMenuService menuService;


    public Dashboard1View(ISysMenuService menuService, DashboardService dashboardService) {
        this.menuService = menuService;
        this.dashboardService = dashboardService;

        Row row = new Row(List.of(24, 24));
        {
            // 大盘趋势
            Col col11 = new Col(8)
                    .add(createStatisticCard1());
            // 财年业绩目标
            Col col12 = new Col(8)
                    .add(createStatisticCard2());
            // 热门词汇
            Col col13 = new Col(8)
                    .add(createHotWordCard());

            // 访问量
            Col col21 = new Col(24)
                    .add(createVisitCard());

            // 销售额类别占比
            Col col31 = new Col(8)
                    .add(createSalesPercentCard());
            // 资源剩余
            Col col32 = new Col(8)
                    .add(createResourceCard());
            //  券核效率
            Col col33 = new Col(8)
                    .add(createGuageCard());


            // 销售额
            Col col41 = new Col(24)
                    .add(createSalesCard2());

            // 付款金额
            Col col51 = new Col(24)
                    .add(createPaymentCard());

            //
            row.add(col11, col12, col13,
                    col21,
                    col31, col32, col33,
                    col41,
                    col51
            );
        }

        //
        add(row);
        //
        this.getStyle().setBackgroundColor("#8f8f8d19").setPadding("10px");
        this.getStyle().setPaddingLeft("12px").setPaddingRight("12px");
    }


    /**
     * 统计卡片1
     *
     * @return
     */
    private StatisticCard createStatisticCard1() {
        StatisticCard statisticCard = new StatisticCard();
        statisticCard.setTitle("大盘趋势");
        {
            StatisticProps statisticParam = new StatisticProps();
            statisticParam.setTitle("实际完成度");
            statisticParam.setValue("82.3%");
            {
                Statistic statistic = new Statistic();
                statistic.setTitle("日同比");
                statistic.setValue("6.47%");
                statistic.setTrend("up");
                statisticParam.setDescription(statistic);
            }
            statisticCard.setStatistic(statisticParam);
        }
        {
            Image image = new Image();
            image.setSrc("https://gw.alipayobjects.com/zos/alicdn/a-LN9RTYq/zhuzhuangtu.svg");
            image.setAlt("柱状图");
            image.setWidth("100%");
            statisticCard.setChart(image);
        }
        return statisticCard;
    }


    /**
     * 统计卡片2
     *
     * @return
     */
    private StatisticCard createStatisticCard2() {
        StatisticCard statisticCard = new StatisticCard();
        statisticCard.setTitle("财年业绩目标");
        {
            StatisticProps statisticProps = new StatisticProps();
            statisticProps.setTitle("实际完成度");
            statisticProps.setValue("82.6");
            statisticProps.setSuffix("亿");
            {
                Statistic statistic = new Statistic();
                statistic.setTitle("日同比");
                statistic.setValue("6.47%");
                statistic.setTrend("up");
                statisticProps.setDescription(statistic);
            }
            statisticCard.setStatistic(statisticProps);
        }
        {
            Image image = new Image();
            image.setSrc("https://gw.alipayobjects.com/zos/alicdn/PmKfn4qvD/mubiaowancheng-lan.svg");
            image.setAlt("进度条");
            image.setWidth("100%");
            statisticCard.setChart(image);
        }
        // footer
        {
            Div divFooter = new Div();
            {
                Statistic statistic1 = new Statistic("财年业绩完成率", "70.98%");
                Statistic statistic2 = new Statistic("去年同期业绩完成率", "86.98%").setLayout("horizontal");
                Statistic statistic3 = new Statistic("前年同期业绩完成率", "88.98%").setLayout("horizontal");
                divFooter.add(statistic1, statistic2, statistic3);
            }
            statisticCard.setFooter(divFooter);
        }
        return statisticCard;
    }


    /**
     * @return
     */
    private Card createHotWordCard() {
        Card card = new Card().setTitle("热门词汇");
        {
            WordCloud wordCloud = new WordCloud()
                    .setData(dashboardService.getWordCloudData())
                    .setHeight(300)
                    .setWidth(450)
                    .setTextField("word")
                    .setColorField("word")
                    .setLayout(new WordCloudLayout()
                            .setSpiral("rectangular")
                            .setFontSize(new Integer[]{10, 20}));
            card.add(wordCloud);
        }
        return card;
    }


    /**
     * @return
     */
    private Card createVisitCard() {
        Card card = new Card().setTitle("访问量");
        {
            Area area = new Area()
                    .setXField("x")
                    .setYField("y")
                    .setShapeField("smooth")
                    .setHeight(80)
                    .setPadding(-20)
                    .setChartStyle(new ChartStyle()
                            .set("fill", "linear-gradient(-90deg, white 0%, #975FE4 100%)")
                            .set("fillOpacity", 0.6)
                            .set("width", "100%"))
                    .setData(dashboardService.getVisitData());
            card.add(area);
        }
        return card;
    }


    /**
     * @return
     */
    private Card createSalesPercentCard() {
        Card card = new Card().setTitle("销售额类别占比");
        {
            Pie pie = new Pie()
                    .setHeight(200)
                    .setRadius(0.8)
                    .setInnerRadius(0.5)
                    .setAngleField("y")
                    .setColorField("x")
                    .setData(dashboardService.getPieData());
            card.add(pie);
        }
        return card;
    }


    /**
     * @return
     */
    private Card createResourceCard() {
        Card card = new Card().setTitle("资源剩余");
        {
            Liquid liquid = new Liquid()
                    .setHeight(200)
                    .setPercent(0.35);
            card.add(liquid);
        }
        return card;
    }


    /**
     * @return
     */
    private Card createGuageCard() {
        Card card = new Card().setTitle("券核效率");
        {
            Gauge gauge = new Gauge()
                    .setHeight(200)
                    .setData(new GaugeData()
                            .setTarget(120)
                            .setTotal(400)
                            .setName("score")
                    );
            card.add(gauge);
        }
        return card;
    }


    /**
     * @return
     */
    private Card createSalesCard2() {
        Card card = new Card().setTitle("销售额");
        {
            Column column = new Column()
                    .setHeight(300)
                    .setData(dashboardService.getSalesData())
                    .setXField("x")
                    .setYField("y")
                    .setPaddingBottom(12);
            card.add(column);
        }
        return card;
    }


    /**
     * @return
     */
    private Card createPaymentCard() {
        Card card = new Card().setTitle("支付数/客流量");
        {
            Line line = new Line()
                    .setHeight(400)
                    .setData(dashboardService.getOrderData())
                    .setXField("date")
                    .setYField("value")
                    .setColorField("type")
                    .setSlider(Collections.singletonMap("x", new Slider()));
            card.add(line);
        }
        return card;
    }


}
