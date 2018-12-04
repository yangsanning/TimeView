package ysn.com.timeview.bean;

import java.util.List;

/**
 * @Author yangsanning
 * @ClassName Data
 * @Description 一句话概括作用
 * @Date 2018/12/4
 * @History 2018/12/4 author: description:
 */
public class Data {

    /**
     * code : 000001
     * data : [["201812041413",10.58,10.58,6608],["201812041414",10.56,10.58,545]]
     * date : 20181204
     */

    private String code;
    private String date;
    private List<List<String>> data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<List<String>> getData() {
        return data;
    }

    public void setData(List<List<String>> data) {
        this.data = data;
    }
}
