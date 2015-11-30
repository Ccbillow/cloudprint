package cn.cqupt.model;

import java.io.Serializable;

/**
 * Created by Cbillow on 15/10/27.
 */
public class User implements Serializable {

    private int id;
    private String mobile;
    private String password;
    private String nickname;    //昵称
    private String isBinding; //此账户是否绑定微信   0为未绑定，1为绑定 默认0
    private String weixin;  //绑定的微信账号
    private String headimgurl; //微信头像url

    /**
     * 是否支付，0为已经支付，1为未支付（默认为0）
     * <p/>
     * 打印之前需要判断是否已经支付
     * 如果为否1，则不能打印
     * 支付完后后需要将isPay设置为0
     */
    private int isPay;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getIsBinding() {
        return isBinding;
    }

    public void setIsBinding(String isBinding) {
        this.isBinding = isBinding;
    }

    public String getWeixin() {
        return weixin;
    }

    public void setWeixin(String weixin) {
        this.weixin = weixin;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public int getIsPay() {
        return isPay;
    }

    public void setIsPay(int isPay) {
        this.isPay = isPay;
    }

    @Override
    public String toString() {
        return "User{" +
                "headimgurl='" + headimgurl + '\'' +
                ", id=" + id +
                ", mobile='" + mobile + '\'' +
                ", password='" + password + '\'' +
                ", nickname='" + nickname + '\'' +
                ", isBinding='" + isBinding + '\'' +
                ", weixin='" + weixin + '\'' +
                ", isPay=" + isPay +
                '}';
    }
}

