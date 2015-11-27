package cn.cqupt.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by Cbillow on 15/11/27.
 */
public class Account implements Serializable{

    private int id;
    private BigDecimal totalPrice;
    private int uid;
    private String date;    //生成账单日期
    private String printerID;       //打印机id

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getPrinterID() {
        return printerID;
    }

    public void setPrinterID(String printerID) {
        this.printerID = printerID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Account{" +
                "date='" + date + '\'' +
                ", id=" + id +
                ", totalPrice=" + totalPrice +
                ", uid=" + uid +
                ", printerID='" + printerID + '\'' +
                '}';
    }
}
