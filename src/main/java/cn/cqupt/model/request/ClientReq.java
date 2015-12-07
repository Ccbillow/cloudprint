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

    private boolean success;

    private int errCode;

    private String errMsg;

    private List<PrintFile> files;

    private User user;

    private String md5Code;

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

    @Override
    public String toString() {
        return "ClientReq{" +
                "errCode=" + errCode +
                ", success=" + success +
                ", errMsg='" + errMsg + '\'' +
                ", files=" + files +
                ", user=" + user +
                ", md5Code='" + md5Code + '\'' +
                '}';
    }
}
