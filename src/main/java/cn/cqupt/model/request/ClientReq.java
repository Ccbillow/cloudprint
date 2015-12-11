package cn.cqupt.model.request;

import cn.cqupt.model.PrintFile;
import cn.cqupt.model.User;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Cbillow on 15/11/27.
 */
public class ClientReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean isPrint;  // true立即打印 false不打印

    private boolean success;

    private int errCode;    //4为关闭

    private String errMsg;  // "!over" 为关闭

    private List<PrintFile> files;

    private User user;

    private String md5Code;

    private String storeName;

    private int downloadState;

    private int connState;


    public int getConnState() {
        return connState;
    }

    public void setConnState(int connState) {
        this.connState = connState;
    }

    public int getDownloadState() {
        return downloadState;
    }

    public void setDownloadState(int downloadState) {
        this.downloadState = downloadState;
    }

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public List<PrintFile> getFiles() {
        return files;
    }

    public void setFiles(List<PrintFile> files) {
        this.files = files;
    }

    public String getMd5Code() {
        return md5Code;
    }

    public void setMd5Code(String md5Code) {
        this.md5Code = md5Code;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isPrint() {
        return isPrint;
    }

    public void setIsPrint(boolean isPrint) {
        this.isPrint = isPrint;
    }

    @Override
    public String toString() {
        return "ClientReq{" +
                "connState=" + connState +
                ", isPrint=" + isPrint +
                ", success=" + success +
                ", errCode=" + errCode +
                ", errMsg='" + errMsg + '\'' +
                ", files=" + files +
                ", user=" + user +
                ", md5Code='" + md5Code + '\'' +
                ", storeName='" + storeName + '\'' +
                ", downloadState=" + downloadState +
                '}';
    }
}
