package cn.cqupt.util;

import java.util.ArrayList;

/**
 * 云打印用到的一些常量
 */
public class CPConstant {

    //创瑞短信平台
    public static final String SMS_URL = "http://web.cr6868.com/asmx/smsservice.aspx?";

    //创瑞账号
    public static final String SMS_USERNAME = "18580741650";

    //创瑞密码
    public static final String SMS_PWD = "3B049B2372AE8952D402C9C33738";

    //签名
    public static final String SMS_SIGN = "重邮云打印";

    //每一页显示多少文件
    public static final int SHOW_NUMBER = 10;

    //三天
    public static final int THREE_DAYS = 3 * 24 * 60 * 60 * 1000;

    //没隔多久进行一次删除扫描
    public static final int INTERVAL_DAY = 30 * 60 * 1000;

    /**
     * 阿里云ACCESS_ID
     */
    public static final String ACCESS_ID = "oGvTjqOt1zPbVlgr";
    /**
     * 阿里云ACCESS_KEY
     */
    public static final String ACCESS_KEY = "SI6vc6P15DSBTX6owwzCNyIekWRcW2";
    /**
     * 阿里云BUCKET_NAME  OSS
     */
    public static final String BUCKET_NAME = "cquptcloudprint";
    /**
     * 阿里云oss地址
     */
    public static final String OSS_URL = "http://cquptcloudprint.oss-cn-hangzhou.aliyuncs.com/";
    /**
     * 阿里云endpoint
     */
    public static final String END_POINT = "http://oss.aliyuncs.com";

    /**
     * 绑定微信的url
     */
    public static final String WEIXIN_BINDING_URL = "https://open.weixin.qq.com/connect/oauth2/authorize";
    /**
     * 开发者ID
     */
    public static final String APP_ID = "wxb3f49727d4bb3509";
    /**
     * 开发者密码
     */
    public static final String APP_SECRET = "b5d90ad5edea780313330bd41fbfbb2f";
    /**
     * 跳转页面
     */
    public static final String BINDING_REDIRECT_URL = "http://cquptprint.cn/cloudprint/user/bindingWeChat";

    /**
     * 二维码中Image路径
     */
    public static final String QR_IMAGE_PATH = "";

    /**
     * 微信通过code获取access_token
     */
    public static final String WEIXIN_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token";

}
