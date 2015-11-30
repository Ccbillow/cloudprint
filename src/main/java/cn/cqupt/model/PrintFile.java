package cn.cqupt.model;

import java.io.Serializable;

/**
 * Created by Cbillow on 15/10/28.
 */
public class PrintFile implements Serializable {

    private int id;
    private String filename;    //文件名
    private int type;           //文件类型  0为word,1为pdf
    private int number;         //打印张数
    private String path;        //文件所存路径
    private String sha1;
    private String overdueTime;   //过期时间
    private int status;         //文件状态，0为待打印，1为已上传暂不打印，2为已打印(默认为0)
    private int isColorful;     //彩印   0为否，1为是(默认为0)
    private int isDelete;       //是否打印完立即删除，0为打印完立即删除，1为保存三天（默认打印完立即删除）
    private String price;       //此文件打印价格
    private int pages;          //页数

    public int getIsColorful() {
        return isColorful;
    }

    public void setIsColorful(int isColorful) {
        this.isColorful = isColorful;
    }

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSha1() {
        return sha1;
    }

    public void setSha1(String sha1) {
        this.sha1 = sha1;
    }

    public String getOverdueTime() {
        return overdueTime;
    }

    public void setOverdueTime(String overdueTime) {
        this.overdueTime = overdueTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    @Override
    public String toString() {
        return "PrintFile{" +
                "filename='" + filename + '\'' +
                ", id=" + id +
                ", type=" + type +
                ", number=" + number +
                ", path='" + path + '\'' +
                ", sha1='" + sha1 + '\'' +
                ", overdueTime='" + overdueTime + '\'' +
                ", status=" + status +
                ", isColorful=" + isColorful +
                ", isDelete=" + isDelete +
                ", price='" + price + '\'' +
                ", pages=" + pages +
                '}';
    }
}
