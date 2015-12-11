package cn.cqupt.util;

import cn.cqupt.model.PrintFile;
import cn.cqupt.model.User;
import com.aliyun.openservices.oss.OSSClient;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Random;

/**
 * 云打印帮助类
 */
public class CPHelps {

    private static final Logger logger = LoggerFactory.getLogger(CPHelps.class);

    /**
     * 生成4位验证码
     *
     * @return
     */
    public static String getValidateCode() {
        Random random = new Random();
        return String.valueOf(random.nextInt(9999 - 1000 + 1) + 1000);
    }

    /**
     * 功能:		web.cr6868.com HTTP接口 发送短信
     * <p/>
     * 说明:		http://web.cr6868.com/asmx/smsservice.aspx?name=登录名&pwd=接口密码&mobile=手机号码&content=内容&sign=签名&stime=发送时间&type=pt&extno=自定义扩展码
     */
    public static String sendSMS(String mobile, String SMSCode) throws IOException {
        StringBuffer sb = new StringBuffer();
        sb.append(CPConstant.SMS_URL).append("name=").append(CPConstant.SMS_USERNAME)
                .append("&pwd=").append(CPConstant.SMS_PWD)
                .append("&mobile=").append(mobile)
                .append("&content=" + URLEncoder.encode("尊敬的用户，您的验证码是" + SMSCode + "，在60s内有效。如非本人操作请忽略本短信。", "UTF-8"))
                .append("&stime=").append("&sign=" + URLEncoder.encode(CPConstant.SMS_SIGN, "UTF-8"))
                .append("&type=pt&extno=");

//        System.out.println("sb:" + sb.toString());

        URL url = new URL(sb.toString());
        // 打开url连接
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        // 设置url请求方式 ‘get’ 或者 ‘post’
        connection.setRequestMethod("POST");
        // 发送
        InputStream is = url.openStream();
        //转换返回值
        String returnStr = CPHelps.convertStreamToString(is);

        // 返回结果为‘0，20140009090990,1，提交成功’ 发送成功   具体见说明文档
//        System.out.println(returnStr);

        // 返回发送结果
        return returnStr;
    }

    /**
     * 转换返回值类型为UTF-8格式.
     *
     * @param is
     * @return
     */
    public static String convertStreamToString(InputStream is) {
        StringBuilder sb1 = new StringBuilder();
        byte[] bytes = new byte[4096];
        int size = 0;

        try {
            while ((size = is.read(bytes)) > 0) {
                String str = new String(bytes, 0, size, "UTF-8");
                sb1.append(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb1.toString();
    }

    /**
     * 上传文件到oss，返回objectkey，通过这个可以进行登录客户端下载
     *
     * @param weixin
     * @param file
     * @return
     * @throws IOException
     */
    public static String uploadFileToOSS(String weixin, CommonsMultipartFile file) throws IOException {
        //生成Object路径，文件夹phone区分，文件名
        String objectKey = weixin + "/" + file.getOriginalFilename();
        OSSClient client = OSSUtils.getOSSClient();

        //把Bucket设置成所有人可读
        OSSUtils.setBucketPublicReadable(client, CPConstant.BUCKET_NAME);
        //上传文件
        OSSUtils.uploadFile(client, CPConstant.BUCKET_NAME, objectKey, file);
        //返回链接，可以直接用于下载
        return CPConstant.OSS_URL + objectKey;
    }

    /**
     * 从oss下载文件
     * <p/>
     * 保存在D盘---cloudprint----该用户目录下（可自己修改路径）
     *
     * @param pf
     * @param user
     */
    public static void downloadFileFromOSS(PrintFile pf, User user) {
        String objectKey = user.getWeixin() + "/" + pf.getFilename();
        OSSClient client = OSSUtils.getOSSClient();
        OSSUtils.downloadFile(client, CPConstant.BUCKET_NAME, objectKey, "D:/cloudprint/" + user.getNickname() + "/" + pf.getFilename());
    }

    /**
     * 从阿里云上下载文件
     * <p/>
     * 默认保存在D盘---cloudprint----该用户目录下
     *
     * @param pf
     * @param user
     */
    public void fileDownloadFromOSS(PrintFile pf, User user) {
        HttpClient httpClient = new DefaultHttpClient();
        File file;
        InputStream is = null;
        FileOutputStream fos = null;

        HttpGet get = new HttpGet(pf.getPath());
        get.setHeader("Content-Type", "application/octet-stream");
        get.setHeader("Content-Disposition", "attachment;filename=" + pf.getFilename());
        try {
            HttpResponse response = httpClient.execute(get);
            file = new File("D:/cloudprint/" + user.getNickname() + "/" + pf.getFilename());
            is = response.getEntity().getContent();
            fos = new FileOutputStream(file);

            int ch = 0;
            while ((ch = is.read()) != -1) {
                fos.write(ch);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 得到微信绑定的url生成二维码图片
     *
     * @return
     */
    public static String getBingdingURL(String md5) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        sb.append(CPConstant.WEIXIN_BINDING_URL).append("?appid=")
                .append(CPConstant.APP_ID).append("&redirect_uri=")
                .append(URLEncoder.encode(CPConstant.BINDING_REDIRECT_URL, "UTF-8"))
                .append("&response_type=code&scope=snsapi_userinfo&state=" + md5 + "#wechat_redirect");
        return sb.toString();
    }

    /**
     * 得到微信AccessToken的url
     *
     * @param code
     * @return
     */
    public static String getAccessTokenURL(String code) {
        StringBuilder sb = new StringBuilder();
        sb.append(CPConstant.WEIXIN_ACCESS_TOKEN_URL)
                .append("?appid=").append(CPConstant.APP_ID)
                .append("&secret=").append(CPConstant.APP_SECRET)
                .append("&code=").append(code).append("&grant_type=authorization_code");

        return sb.toString();
    }

    /**
     * 得到微信用户信息的URL
     *
     * @param openId
     * @param accessToken
     * @return
     */
    public static String getWXUserInfoUrl(String openId, String accessToken) {
        StringBuilder sb = new StringBuilder();
        sb.append(CPConstant.WEIXIN_USERINFO_URL)
                .append("?access_token=").append(accessToken)
                .append("&openid=").append(openId).append("&lang=zh_CN");
        return sb.toString();
    }

    /**
     * httpget
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static String HttpGet(String url) throws IOException {
        // 创建HttpClient实例
        HttpClient httpclient = new DefaultHttpClient();
        // 创建Get方法实例
        HttpGet httpgets = new HttpGet(url);
        String str = "";

        try {
            HttpResponse response = httpclient.execute(httpgets);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                InputStream instreams = entity.getContent();
                str = convertStreamToString(instreams);
            }
        } finally {
            httpgets.abort();
        }
        return str;
    }

    /**
     * 计算文件价格
     *
     * @param num
     * @param isColorful
     * @return
     */
    public static String calculatePrice(int num, int isColorful) {
//            , int pages) {
        BigDecimal price = null;
        /**
         * 0为不彩印
         * 1为彩印
         */
        if (isColorful == 0) {
            price = new BigDecimal(CPConstant.NO_COLORFUL_PRICE);
        } else if (isColorful == 1) {
            price = new BigDecimal(CPConstant.COLORFUL_PRICE);
        }
        /**
         *  计算该文件总价
         */
        price = price.multiply(new BigDecimal(num));
        logger.info("file calculatePrice number:{}, iscolorful:{}, price:{}", num, isColorful, price);
        return price.toString();
    }

    /**
     * 计算文件打印总价
     *
     * @param files
     * @return
     */
    public static String calculateTotalPrice(List<PrintFile> files) {
        BigDecimal price = new BigDecimal(BigInteger.ZERO);
        for (PrintFile file : files) {
            price = price.add(new BigDecimal(file.getPrice()));
        }
        return price.toString();
    }
}
