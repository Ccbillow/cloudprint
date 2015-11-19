package cn.cqupt.controller;

import cn.cqupt.model.User;
import cn.cqupt.model.WeChatResponse;
import cn.cqupt.service.UserService;
import cn.cqupt.util.CPHelps;
import cn.cqupt.util.JacksonUtil;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;

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

    @RequestMapping(value = "/getUserMessage", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getUserMessage(HttpServletRequest req) {
        logger.info("UserWebController getUserMessage");

        HashMap<String, Object> result = Maps.newHashMap();
        User loginUser = (User) req.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            result.put("status", 1);
            result.put("message", "请登录后操作");
            logger.error("UserWebController user do not login");
            return JSON.toJSONString(result);
        }

        result.put("status", 0);
        result.put("loginUser", loginUser);
        result.put("message", "用户已经登录，得到用户信息");
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "/getValidateCode", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getValidateCode(String mobile, HttpServletRequest req) {
        logger.info("UserWebController getValidateCode mobile:{}", mobile);

        HashMap<String, Object> result = userService.sendSMS(mobile);

        String validateCode = (String) result.get("validateCode");
        req.getSession().setAttribute("validateCode", validateCode);
        result.remove("validateCode");
        logger.info("UserWebController getValidateCode saving valiteCode to session " + validateCode);
        return JSON.toJSONString(result);
    }


    @RequestMapping(value = "/register", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String registerUser(String mobile, String password, String nickname, String VCode,
                               HttpServletRequest req) {
        HashMap<String, Object> result = Maps.newHashMap();
        logger.info("UserWebController registerUser mobile:{}, password:{}, nickname:{}, VCode:{}", mobile, password, nickname, VCode);
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
        logger.info("UserWebController login mobile:{}, password:{}", mobile, password);

        HashMap<String, Object> result = userService.login(mobile, password);
        if (result.containsKey("loginUser")) {
            User loginUser = (User) result.get("loginUser");
            req.getSession().setAttribute("loginUser", loginUser);
        }
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "/logout", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String logout(HttpServletRequest req) {
        HashMap<String, Object> result = Maps.newHashMap();
        try {
            req.getSession().invalidate();
        } catch (Exception e) {
            result.put("status", 1);
            result.put("message", "注销失败");
            logger.error("UserWebController logout error:{}", e);
            return JSON.toJSONString(result);
        }
        result.put("status", 0);
        result.put("message", "注销成功");
        logger.info("UserWebController logout success");
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "/update/{uid}", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String update(@PathVariable String id, String password, String nickname, HttpServletRequest req) {
        logger.info("UserWebController update id:{}, password:{}, nickname:{}", id, password, nickname);

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
        logger.info("UserWebController refundPassword  VCode:{}, mobile:{} ", VCode, mobile);
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

    @RequestMapping(value = "/getQRCode")
    public void getQRCode(HttpServletResponse response) {
        InputStream is = null;
        String bindingURL;
        BufferedImage image;
        try {
            bindingURL = CPHelps.getBingdingURL();
            logger.info("UserWebController getQRCode binding url : " + bindingURL);
            image = QRCodeUtil.createImage(bindingURL, null, true);

            OutputStream out = response.getOutputStream();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", os);
            is = new ByteArrayInputStream(os.toByteArray());
            byte[] b = new byte[is.available()];
            is.read(b);
            out.write(b);
            logger.info("UserWebController getQRCode image writing success");
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("UserWebController getQRCode error : {}", e);
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
     * @param code  通过code得到access_token,通过access_token得到用户openid
     * @param req
     * @return
     */
    @RequestMapping(value = "/bindingWeChat", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String bindingWeChat(String code, HttpServletRequest req) {
        logger.info("UserWebController bindingWeChat start... code:{}", code);

        HashMap<String, Object> result = Maps.newHashMap();
        HttpSession session = req.getSession();

        String accessTokenURL = CPHelps.getAccessTokenURL(code);
        String content;
        try {
            content = CPHelps.HttpGet(accessTokenURL);
            if (content.contains("errcode") && content.contains("errmsg")) {
                result.put("status", 1);
                result.put("message", "绑定微信失败，Code无效错误");
                logger.error("UserWebController bindingWeChat code is wrong e:{}", content);
                return JSON.toJSONString(result);
            } else if (content.contains("openid")) {
                WeChatResponse wc = JacksonUtil.deSerialize(content, WeChatResponse.class);
                result = userService.bindingWeChat(wc.getOpenid());
                //放入session
                User user = (User) result.get("loginUser");
                session.setAttribute("loginUser", user);
            }
        } catch (IOException e) {
            result.put("status", 1);
            result.put("message", "绑定微信失败，访问" + accessTokenURL + "出错");
            logger.error("UserWebController bindingWeChat accessTokenURL error:{}", e);
            return JSON.toJSONString(result);
        } catch (Exception ie) {
            result.put("status", 1);
            result.put("message", "绑定微信失败，详情请查看日志");
            logger.error("UserWebController bindingWeChat error:{}", ie);
            return JSON.toJSONString(result);
        }
        return JSON.toJSONString(result);
    }

}
