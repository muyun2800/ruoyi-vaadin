package com.ruoyi.web.controller.common;

import com.ruoyi.ui.vo.DashBoardVo;
import com.ruoyi.ui.vo.DashBoardVo.DataItem;
import com.ruoyi.ui.vo.DashBoardVo.OrderDataItem;
import com.ruoyi.ui.vo.DashBoardVo.WordCloudDataItem;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * DashboardService
 *
 * @author 何德海
 * @version 2026/04/20
 **/
@Service
public class DashboardService {

    /**
     * 获取访问数据
     *
     * @return
     */
    public List<DataItem> getVisitData() {
        return Arrays.asList(
                new DataItem("2026-04-16", 7),
                new DataItem("2026-04-17", 5),
                new DataItem("2026-04-18", 4),
                new DataItem("2026-04-19", 2),
                new DataItem("2026-04-20", 4),
                new DataItem("2026-04-21", 7),
                new DataItem("2026-04-22", 5),
                new DataItem("2026-04-23", 6),
                new DataItem("2026-04-24", 5),
                new DataItem("2026-04-25", 9),
                new DataItem("2026-04-26", 6),
                new DataItem("2026-04-27", 3),
                new DataItem("2026-04-28", 1),
                new DataItem("2026-04-29", 5),
                new DataItem("2026-04-30", 3),
                new DataItem("2026-05-01", 6),
                new DataItem("2026-05-02", 5)
        );
    }


    /**
     * 获取饼图数据
     *
     * @return
     */
    public List<DataItem> getPieData() {
        return Arrays.asList(
                new DataItem("家用电器", 4544),
                new DataItem("食用酒水", 3321),
                new DataItem("个护健康", 3113),
                new DataItem("服饰箱包", 2341),
                new DataItem("母婴产品", 1231),
                new DataItem("其他", 1231)
        );
    }


    /**
     * 获取销售数据
     *
     * @return
     */
    public List<DataItem> getSalesData() {
        return Arrays.asList(
                new DataItem("1月", 485),
                new DataItem("2月", 805),
                new DataItem("3月", 411),
                new DataItem("4月", 336),
                new DataItem("5月", 412),
                new DataItem("6月", 1066),
                new DataItem("7月", 886),
                new DataItem("8月", 678),
                new DataItem("9月", 242),
                new DataItem("10月", 434),
                new DataItem("11月", 641),
                new DataItem("12月", 1050)
        );
    }


    /**
     * 获取订单数据
     *
     * @return
     */
    public List<DashBoardVo.OrderDataItem> getOrderData() {
        return Arrays.asList(
                new OrderDataItem("23:06", "客流量", 107.0f),
                new OrderDataItem("23:06", "支付笔数", 85.0f),
                new OrderDataItem("23:36", "客流量", 101.0f),
                new OrderDataItem("23:36", "支付笔数", 27.0f),
                new OrderDataItem("00:06", "客流量", 99.0f),
                new OrderDataItem("00:06", "支付笔数", 99.0f),
                new OrderDataItem("00:36", "客流量", 100.0f),
                new OrderDataItem("00:36", "支付笔数", 45.0f),
                new OrderDataItem("01:06", "客流量", 79.0f),
                new OrderDataItem("01:06", "支付笔数", 77.0f),
                new OrderDataItem("01:36", "客流量", 94.0f),
                new OrderDataItem("01:36", "支付笔数", 61.0f),
                new OrderDataItem("02:06", "客流量", 22.0f),
                new OrderDataItem("02:06", "支付笔数", 17.0f),
                new OrderDataItem("02:36", "客流量", 88.0f),
                new OrderDataItem("02:36", "支付笔数", 83.0f),
                new OrderDataItem("03:06", "客流量", 92.0f),
                new OrderDataItem("03:06", "支付笔数", 68.0f),
                new OrderDataItem("03:36", "客流量", 35.0f),
                new OrderDataItem("03:36", "支付笔数", 50.0f),
                new OrderDataItem("04:06", "客流量", 74.0f),
                new OrderDataItem("04:06", "支付笔数", 102.0f),
                new OrderDataItem("04:36", "客流量", 26.0f),
                new OrderDataItem("04:36", "支付笔数", 64.0f),
                new OrderDataItem("05:06", "客流量", 70.0f),
                new OrderDataItem("05:06", "支付笔数", 84.0f),
                new OrderDataItem("05:36", "客流量", 78.0f),
                new OrderDataItem("05:36", "支付笔数", 16.0f),
                new OrderDataItem("06:06", "客流量", 56.0f),
                new OrderDataItem("06:06", "支付笔数", 40.0f),
                new OrderDataItem("06:36", "客流量", 78.0f),
                new OrderDataItem("06:36", "支付笔数", 13.0f),
                new OrderDataItem("07:06", "客流量", 106.0f),
                new OrderDataItem("07:06", "支付笔数", 84.0f),
                new OrderDataItem("07:36", "客流量", 95.0f),
                new OrderDataItem("07:36", "支付笔数", 105.0f),
                new OrderDataItem("08:06", "客流量", 71.0f),
                new OrderDataItem("08:06", "支付笔数", 65.0f),
                new OrderDataItem("08:36", "客流量", 54.0f),
                new OrderDataItem("08:36", "支付笔数", 20.0f)
        );
    }


    /**
     * 获取词云数据
     *
     * @return
     */
    public   List<WordCloudDataItem> getWordCloudData() {
        return Arrays.asList(
                new WordCloudDataItem(1, "上海市", 20F),
                new WordCloudDataItem(2, "天津市", 2F),
                new WordCloudDataItem(3, "九江市", 3F),
                new WordCloudDataItem(4, "北京市", 30F),
                new WordCloudDataItem(5, "重庆市", 15F),
                new WordCloudDataItem(6, "丹东市", 5F),
                new WordCloudDataItem(7, "黄山市", 8F),
                new WordCloudDataItem(8, "安康市", 18F),
                new WordCloudDataItem(9, "菏泽市", 41F),
                new WordCloudDataItem(10, "福州市", 6F),
                new WordCloudDataItem(11, "贵阳市", 7F),
                new WordCloudDataItem(12, "广州市", 25F),
                new WordCloudDataItem(13, "深圳市", 28F),
                new WordCloudDataItem(14, "杭州市", 22F),
                new WordCloudDataItem(15, "南京市", 19F),
                new WordCloudDataItem(16, "武汉市", 17F),
                new WordCloudDataItem(17, "成都市", 21F),
                new WordCloudDataItem(18, "西安市", 16F),
                new WordCloudDataItem(19, "苏州市", 14F),
                new WordCloudDataItem(20, "青岛市", 12F),
                new WordCloudDataItem(21, "郑州市", 13F),
                new WordCloudDataItem(22, "长沙市", 11F),
                new WordCloudDataItem(23, "昆明市", 9F),
                new WordCloudDataItem(24, "大连市", 10F),
                new WordCloudDataItem(25, "厦门市", 8F),
                new WordCloudDataItem(26, "宁波市", 15F),
                new WordCloudDataItem(27, "合肥市", 12F),
                new WordCloudDataItem(28, "南昌市", 7F),
                new WordCloudDataItem(29, "石家庄市", 6F),
                new WordCloudDataItem(30, "太原市", 5F),
                new WordCloudDataItem(31, "长春市", 9F),
                new WordCloudDataItem(32, "哈尔滨市", 11F),
                new WordCloudDataItem(33, "沈阳市", 10F),
                new WordCloudDataItem(34, "济南市", 14F),
                new WordCloudDataItem(35, "唐山市", 4F),
                new WordCloudDataItem(36, "温州市", 13F),
                new WordCloudDataItem(37, "佛山市", 16F),
                new WordCloudDataItem(38, "东莞市", 18F),
                new WordCloudDataItem(39, "无锡市", 12F),
                new WordCloudDataItem(40, "常州市", 8F)
        );
    }

}