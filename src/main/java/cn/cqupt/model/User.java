package cn.cqupt.model;

import java.io.Serializable;

/**
 * Created by Cbillow on 15/10/27.
 */
public class User implements Serializable{

    private int id;
    private String mobile;
    private String password;
    private String nickname;    //昵称
    private String isBinding; //此账户是否绑定微信   0为未绑定，1为绑定 默认0
    private String weixin;  //绑定的微信账号

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

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", mobile='" + mobile + '\'' +
                ", password='" + password + '\'' +
                ", nickname='" + nickname + '\'' +
                ", isBinding='" + isBinding + '\'' +
                ", weixin='" + weixin + '\'' +
                '}';
    }
}

