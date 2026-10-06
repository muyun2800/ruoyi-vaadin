package com.ruoyi.ui.vo;

import lombok.Data;

/**
 * DashBoardVo
 *
 * @author 何德海
 * @version 2026/04/20
 **/
public class DashBoardVo {

    private DashBoardVo() {
        // nop
    }

    @Data
    public static class DataItem {
        private String x;
        private int y;

        public DataItem(String x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    @Data
    public static class OrderDataItem {
        private String date;
        private String type;
        private Float value;

        public OrderDataItem(String date, String type, Float value) {
            this.date = date;
            this.type = type;
            this.value = value;
        }
    }


    @Data
    public static class WordCloudDataItem {
        private Integer id;
        private String word;
        private Float weight;

        public WordCloudDataItem(Integer id, String word, Float weight) {
            this.id = id;
            this.word = word;
            this.weight = weight;
        }
    }


}