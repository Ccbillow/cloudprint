package cn.cqupt.controller;

import cn.cqupt.model.User;
import cn.cqupt.model.response.WeChatAccessTokenRes;
import cn.cqupt.model.response.WeChatUserInfoRes;
import cn.cqupt.service.UserService;
import cn.cqupt.util.CPHelps;
import cn.cqupt.util.JacksonUtil;
import cn.cqupt.util.MD5Util;
import cn.cqupt.util.QRCodeUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by Cbillow on 15/10/27.
 */
@Controller
@RequestMapping("/user")
public class UserWebController {

    private static final Logger logger = LoggerFactory.getLogger(UserWebController.class);

    private UserService userService;

    @Resource(name = "userService")
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/getusermessage", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getUserMessage(HttpServletRequest req, HttpServletResponse res) {
        logger.info("getUserMessage start...");
        HashMap<String, Object> result = Maps.newHashMap();
        ServletContext context = req.getServletContext();
        HttpSession session = req.getSession();

        /**
         * 先从cookie里去拿user
         * 如果存在，则不用扫码，直接登录
         */
        String openid = getUserCookie(req);
        User userByOpenid = userService.loadUserByOpenid(openid);
        if (userByOpenid != null) {
            /**
             * 放置cookie，默认三天
             */
            session.setAttribute("loginUser", userByOpenid);
            setUserCookie(userByOpenid.getWeixin(), res);

            result.put("status", 0);
            result.put("loginUser", userByOpenid);
            result.put("message", "用户已经登录，得到用户信息");
            logger.info("getUserMessage success! 从cookie中拿到openid，通过openid直接登录, result:{}", result);
            return JSON.toJSONString(result);
        }

        /**
         * 从application里面拿
         * 扫码绑定后 通过md5标识
         */
        String md5 = getMD5Cookie(req);
        User loginUser = (User) context.getAttribute(md5);
        if (loginUser == null) {
            /**
             * 从session里面拿
             */
            User userFromSession = (User) session.getAttribute("loginUser");
            if (userFromSession == null) {
                result.put("status", 1);
                result.put("message", "请登录后操作");
                logger.error("用户没有登陆");
                return JSON.toJSONString(result);
            }
            /**
             * 放置cookie，默认三天
             */
            setUserCookie(userFromSession.getWeixin(), res);

            result.put("status", 0);
            result.put("loginUser", userFromSession);
            result.put("message", "用户已经登录，得到用户信息");
            logger.info("getUserMessage success! 从session中拿到登陆用户, result:{}", result);
            return JSON.toJSONString(result);
        }

        /**
         * 将application中loginUser移除，放入到session中
         * 设置cookie，默认三天
         */
        context.removeAttribute("loginUser");
        session.setAttribute("loginUser", loginUser);
        setUserCookie(loginUser.getWeixin(), res);
        removeCookie("md5", req, res);

        result.put("status", 0);
        result.put("loginUser", loginUser);
        result.put("message", "用户已经登录，得到用户信息");
        logger.info("getUserMessage success! 从md5中拿到用户，将openid放入cookie中，将用户放入session, result:{}", result);
        return JSON.toJSONString(result);
    }

    private void setUserCookie(String openid, HttpServletResponse res) {
        Cookie cookie = new Cookie("openid", openid);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 3);
        res.addCookie(cookie);
        logger.info("getUserMessage setUserCookie openid:{}", openid);
    }

    private String getUserCookie(HttpServletRequest req) {
        String openid = "";
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                Cookie temp = cookies[i];
                if (temp.getName().equalsIgnoreCase("openid")) {
                    openid = temp.getValue();
                }
            }
        }
        logger.info("getUserMessage getUserCookie openid:{}", openid);
        return openid;
    }

    @RequestMapping(value = "/getValidateCode", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getValidateCode(String mobile, HttpServletRequest req) {
        logger.info("getValidateCode mobile:{}", mobile);

        HashMap<String, Object> result = userService.sendSMS(mobile);

        String validateCode = (String) result.get("validateCode");
        req.getSession().setAttribute("validateCode", validateCode);
        result.remove("validateCode");
        logger.info("getValidateCode saving valiteCode to session " + validateCode);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "/register", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String registerUser(String mobile, String password, String nickname, String VCode,
                               HttpServletRequest req) {
        HashMap<String, Object> result = Maps.newHashMap();
        logger.info("registerUser mobile:{}, password:{}, nickname:{}, VCode:{}", mobile, password, nickname, VCode);
        String validateCode = (String) req.getSession().getAttribute("validateCode");
        if (Strings.isNullOrEmpty(validateCode) || !validateCode.equalsIgnoreCase(VCode)) {
            result.put("status", 1);
            result.put("message", "验证码输入有误");
            logger.error("UserWebController registerUser fail : validateCode is wrong");
            return JSON.toJSONString(result);
        }

        if (Strings.isNullOrEmpty(mobile) || Strings.isNullOrEmpty(password)) {
            result.put("status", 1);
            result.put("message", "手机号或者密码不能为空");
            logger.error("UserWebController registerUser fail : mobile or password is empty!!");
            return JSON.toJSONString(result);
        }
        User user = new User();
        user.setMobile(mobile);
        user.setPassword(password);
        user.setIsBinding("0");     //默认为没有绑定
        if (Strings.isNullOrEmpty(nickname)) {
            user.setNickname(mobile);
        } else {
            user.setNickname(nickname);
        }
        result = userService.addUser(user);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "/login", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String login(String mobile, String password, HttpServletRequest req) {
        logger.info("login mobile:{}, password:{}", mobile, password);

        HashMap<String, Object> result = userService.login(mobile, password);
        if (result.containsKey("loginUser")) {
            User loginUser = (User) result.get("loginUser");
            req.getSession().setAttribute("loginUser", loginUser);
        }
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "/logout", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String logout(HttpServletRequest req, HttpServletResponse res) {
        HashMap<String, Object> result = Maps.newHashMap();
        try {
            req.getSession().removeAttribute("loginUser");
            removeCookie("openid", req, res);
        } catch (Exception e) {
            result.put("status", 1);
            result.put("message", "注销失败");
            logger.error("UserWebController logout error:{}", e);
            return JSON.toJSONString(result);
        }
        result.put("status", 0);
        result.put("message", "注销成功");
        logger.info("logout success");
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "/update/{uid}", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String update(@PathVariable String id, String password, String nickname, HttpServletRequest req) {
        logger.info("update id:{}, password:{}, nickname:{}", id, password, nickname);

        HashMap<String, Object> result = Maps.newHashMap();
        User loginUser = (User) req.getSession().getAttribute("loginUser");
        if (id.equalsIgnoreCase(String.valueOf(loginUser.getId()))) {
            result = userService.updateUser(Integer.parseInt(id), password, nickname);
        } else {
            result.put("status", 1);
            result.put("message", "登陆用户和被修改用户不一致");
            logger.error("UserWebController update fail : 登陆用户和被修改用户不一致");
            return JSON.toJSONString(result);
        }
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "/refundPassword", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String updatePassword(String password, String VCode, String mobile, HttpServletRequest req) {
        logger.info("refundPassword  VCode:{}, mobile:{} ", VCode, mobile);
        HashMap<String, Object> result = Maps.newHashMap();

        String validateCode = (String) req.getSession().getAttribute("validateCode");
        if (Strings.isNullOrEmpty(validateCode) || !validateCode.equalsIgnoreCase(VCode)) {
            result.put("status", 1);
            result.put("message", "验证码输入有误");
            logger.error("UserWebController updatePassword fail : validateCode is wrong");
            return JSON.toJSONString(result);
        }
        if (!Strings.isNullOrEmpty(mobile)) {
            result = userService.refundPassword(mobile, password);
        }
        return JSON.toJSONString(result);
    }

    private void setMD5Cookie(String md5, HttpServletResponse res) {
        Cookie cookie = new Cookie("md5", md5);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24);
        res.addCookie(cookie);
        logger.info("getqrcode setMD5Cookie md5:{}", md5);
    }

    private String getMD5Cookie(HttpServletRequest req) {
        String md5 = "";
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                Cookie temp = cookies[i];
                if (temp.getName().equalsIgnoreCase("md5")) {
                    md5 = temp.getValue();
                }
            }
        }
        logger.info("getQRCode getMD5Cookie md5:{}", md5);
        return md5;
    }

    private void removeCookie(String key, HttpServletRequest req, HttpServletResponse res) {
        Cookie[] cookies = req.getCookies();
        for (int i = 0; i < cookies.length; i++) {
            Cookie cookie = cookies[i];
            if (cookie.getName().equalsIgnoreCase(key)) {
                cookie.setPath("/");
                cookie.setMaxAge(0);
                res.addCookie(cookie);
                logger.info("logout remove cookie:{}", cookies[i].getName());
            }
        }
        logger.info("getQRCode removeCookie cookie:{}", key);
    }

    @RequestMapping(value = "/getqrcode")
    public void getQRCode(HttpServletResponse response) {
        InputStream is = null;
        String bindingURL;
        BufferedImage image;
        String md5;
        try {
            md5 = MD5Util.string2MD5(String.valueOf(new Random().nextInt()));
            setMD5Cookie(md5, response);
            logger.info("设置md5 cookie，用于验证唯一性 md5:{}", md5);
            bindingURL = CPHelps.getBingdingURL(md5);
            logger.info("得到绑定微信的url:{} ", bindingURL);
            /**
             * 通过微信绑定url生成标准二维码
             *
             * 标准二维码250*250
             */
            image = QRCodeUtil.generate(bindingURL, 250, 250);

            OutputStream out = response.getOutputStream();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", os);
            is = new ByteArrayInputStream(os.toByteArray());
            byte[] b = new byte[is.available()];
            is.read(b);
            out.write(b);
            logger.info("getqrcode image writing success");
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("getqrcode error : {}", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 页面扫码，绑定微信账号
     *
     * @param code 通过code得到access_token,通过access_token得到用户openid
     * @param req
     * @return
     */
    @RequestMapping(value = "/bindingwechat")
    public String bindingWeChat(String code, String state, HttpServletRequest req) {
        logger.info("bindingWeChat start... code:{}", code);

        HashMap<String, Object> result = Maps.newHashMap();
        ServletContext application = req.getServletContext();

        String accessTokenURL = CPHelps.getAccessTokenURL(code);
        logger.info("bindingWeChat getAccessTokenURL:{}", accessTokenURL);
        String content;
        String userinfo;
        String wxUserInfoUrl;
        WeChatUserInfoRes res = null;
        try {
            content = CPHelps.HttpGet(accessTokenURL);
            logger.info("bindingWeChat accessTokenURL return content:{}", content);
            if (content.contains("errcode") && content.contains("errmsg")) {
                result.put("status", 1);
                result.put("message", "微信登陆失败，无法获取到微信号");
                logger.error("UserWebController bindingWeChat code is wrong e:{}", content);
                return "banding_fail";
            } else if (content.contains("openid")) {
                WeChatAccessTokenRes wc = JacksonUtil.deSerialize(content, WeChatAccessTokenRes.class);

                /**
                 * 根据openid获取微信用户信息
                 */
                wxUserInfoUrl = CPHelps.getWXUserInfoUrl(wc.getOpenid(), wc.getAccess_token());
                logger.info("bindingWeChat wxUserInfoUrl return weixin UserInfo:{}", wxUserInfoUrl);
                userinfo = CPHelps.HttpGet(wxUserInfoUrl);
                if (userinfo.contains("errcode") && userinfo.contentEquals("errmsg")) {
                    result.put("status", 1);
                    result.put("message", "微信登陆失败，获取用户信息失败");
                    logger.error("UserWebController bindingWeChat code is wrong e:{}", userinfo);
                    return "banding_fail";
                } else if (userinfo.contains("nickname")) {
                    res = JacksonUtil.deSerialize(userinfo, WeChatUserInfoRes.class);
                }
                result = userService.bindingWeChat(wc.getOpenid(), res.getNickname(), res.getHeadimgurl());
                //application
                User user = (User) result.get("loginUser");
                application.setAttribute(state, user);
            }
        } catch (IOException e) {
            result.put("status", 1);
            result.put("message", "绑定微信失败，无法获取到微信号");
            logger.error("UserWebController bindingWeChat accessTokenURL error:{}", e);
            return "banding_fail";
        } catch (Exception ie) {
            result.put("status", 1);
            result.put("message", "绑定微信失败，详情请查看日志");
            logger.error("UserWebController bindingWeChat error:{}", ie);
            return "banding_fail";
        }
        return "banding_success";
    }
}
