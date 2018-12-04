package ysn.com.timeview.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author yangsanning
 * @ClassName Time
 * @Description 一句话概括作用
 * @Date 2018/8/15
 * @History 2018/8/15 author: description:
 */
public class Time {

    private String date;
    private float settlement;
    private String code;
    private List<DataBean> data;

    public String getDate() {
        return date;
    }

    public Time setDate(String date) {
        this.date = date;
        return this;
    }

    public float getSettlement() {
        return settlement;
    }

    public Time setSettlement(float settlement) {
        this.settlement = settlement;
        return this;
    }

    public String getCode() {
        return code;
    }

    public Time setCode(String code) {
        this.code = code;
        return this;
    }

    public List<DataBean> getData() {
        return data;
    }

    public Time setData(List<DataBean> data) {
        this.data = data;
        return this;
    }

    public void setDataBeanStringList(List<List<String>> dataBeanStringList) {
        if (data == null) {
            data = new ArrayList<>();
        }
        for (List<String> dataBeanString : dataBeanStringList) {
            data.add(new DataBean(
                    dataBeanString.get(0),
                    Float.valueOf(dataBeanString.get(1)),
                    Float.valueOf(dataBeanString.get(2)),
                    Float.valueOf(dataBeanString.get(3))));
        }
    }

    public static class DataBean {
        /**
         * dateTime : 201808140930
         * trade : 2780.74
         * volume : 2693903
         */

        private String dateTime;
        private float trade;
        private float avgPrice;
        private float volume;

        public DataBean(String dateTime, float trade, float avgPrice, float volume) {
            this.dateTime = dateTime;
            this.trade = trade;
            this.avgPrice = avgPrice;
            this.volume = volume;
        }

        public String getDateTime() {
            return dateTime;
        }

        public void setDateTime(String dateTime) {
            this.dateTime = dateTime;
        }

        public float getTrade() {
            return trade;
        }

        public void setTrade(float trade) {
            this.trade = trade;
        }

        public float getVolume() {
            return volume;
        }

        public void setVolume(float volume) {
            this.volume = volume;
        }

        public float getAvgPrice() {
            return avgPrice;
        }

        public void setAvgPrice(float avgPrice) {
            this.avgPrice = avgPrice;
        }
    }
}
