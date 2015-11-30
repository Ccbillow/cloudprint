package cn.cqupt.service.impl;

import cn.cqupt.dao.UserDao;
import cn.cqupt.model.User;
import cn.cqupt.service.UserService;
import cn.cqupt.util.CPHelps;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Cbillow on 15/10/27.
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private UserDao userDao;

    @Resource(name = "userDao")
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public HashMap<String, Object> sendSMS(String mobile) {
        HashMap<String, Object> map = Maps.newHashMap();
        logger.info("sendSMS" + mobile);

        //随机生成验证码
        String validateCode = CPHelps.getValidateCode();
        String result;
        try {
            //请求第三方发送验证码短信
            result = CPHelps.sendSMS(mobile, validateCode);
        } catch (IOException e) {
            logger.error("发送验证码失败 e:{}", e);
            map.put("status", 1);
            map.put("message", "发送验证码失败");
            return map;
//            throw new CPException("发送验证码失败");
        }
        map.put("result", result);
        map.put("validateCode", validateCode);
        logger.info("getValidateCode " + result);
        return map;
    }

    public HashMap<String, Object> addUser(User user) {
        HashMap<String, Object> result = Maps.newHashMap();
        logger.info("addUser" + user.toString());

        User temp = userDao.loadUserByMobile(user.getMobile());
        if (temp != null) {
            result.put("status", 1);
            result.put("message", "添加的用户已经存在");
            logger.error("registerUser fail : mobile has already existed");
            return result;
        }
        userDao.addUser(user);
        result.put("status", 0);
        result.put("message", "添加用户成功");
        logger.info("registerUser success");
        return result;
    }

    public HashMap<String, Object> login(String mobile, String password) {
        HashMap<String, Object> result = Maps.newHashMap();
        logger.info("login" + mobile + " " + password);

        User loginUser = userDao.loadUserByMobile(mobile);
        if (loginUser == null) {
            result.put("status", 1);
            result.put("message", "用户不存在");
            logger.error("login fail : user does not exist");
            return result;
        } else if (!password.equalsIgnoreCase(loginUser.getPassword())) {
            result.put("status", 1);
            result.put("message", "密码错误");
            logger.error("login fail : password is wrong");
            return result;
        }
        loginUser.setPassword("");
        result.put("status", 0);
        result.put("loginUser", loginUser);
        result.put("message", "登陆成功");
        logger.info("login success");
        return result;
    }

    public HashMap<String, Object> updateUser(int id, String password, String nickname) {
        HashMap<String, Object> result = Maps.newHashMap();
        logger.info("updateUser id" + id + " password" + password + " nickname" + nickname);

        try {
            User temp = userDao.loadUserById(id);
            if (!Strings.isNullOrEmpty(password)) {
                temp.setPassword(password);
            }
            if (!Strings.isNullOrEmpty(nickname)) {
                temp.setNickname(nickname);
            }
            userDao.updateUser(temp);
        } catch (Exception e) {
            result.put("status", 1);
            result.put("message", "更新用户失败，请查看日志");
            logger.info("updateUser error : {}", e);
            return result;
        }

        result.put("status", 0);
        result.put("message", "更新用户成功");
        logger.info("updateUser success!!! result:{}", result);
        return result;
    }

    public HashMap<String, Object> refundPassword(String mobile, String password) {
        HashMap<String, Object> result = Maps.newHashMap();
        logger.info("refundPassword mobile: " + mobile);

        try {
            User temp = userDao.loadUserByMobile(mobile);
            if (temp == null) {
                result.put("status", 1);
                result.put("message", "重置密码失败，此用户不存在");
                logger.error("refundPassword fail, mobile is not exist");
                return result;
            }
            //密码重置为password
            temp.setPassword(password);
            userDao.updateUser(temp);
        } catch (Exception e) {
            result.put("status", 1);
            result.put("message", "重置密码操作失败，请查看日志");
            logger.error("refundPassword errpr : {}", e);
            return result;
        }
        result.put("status", 0);
        result.put("message", "重置密码成功");
        logger.info("refundPassword success!!!, result:{}", result);
        return result;
    }

    public User loadUserByOpenid(String openid) {
        HashMap<String, Object> result = Maps.newHashMap();
        logger.info("loadUserByOpenid openid:{}", openid);

        User loginUser = userDao.loadUserByOpenId(openid);
        if (loginUser == null) {
            result.put("status", 1);
            result.put("message", "用户不存在");
            logger.error("loadUserByOpenid fail : user does not exist");
            return null;
        }

        logger.info("loadUserByOpenid success");
        return loginUser;
    }

    public HashMap<String, Object> bindingWeChat(String openid, String nickname, String headimgurl) {
        HashMap<String, Object> result = Maps.newHashMap();
        logger.info("bindingWeChat start... the openid:{} ", openid);

        try {
            User temp = userDao.loadUserByOpenId(openid);
            if (temp == null) {
                User user = new User();
                user.setWeixin(openid);
                user.setIsBinding("1");
                user.setNickname(nickname);
                user.setHeadimgurl(headimgurl);
                user.setIsPay(0);   //默认为 已经支付
                userDao.addUser(user);
                result.put("message", "此微信未绑定，已经添加用户，并自动登录");
                result.put("loginUser", user);
                logger.info("bindingWeChat the openId is not binding, and it is binding now");
            } else {
                result.put("message", "此微信已绑定，自动登录");
                result.put("loginUser", temp);
                logger.info("bindingWeChat the openId has already bindinged, and it is logining now！");
            }
        } catch (Exception e) {
            result.put("status", 1);
            result.put("message", "绑定微信操作失败，请查看日志");
            logger.error("bindingWeChat error:{}", e);
            return result;
        }
        result.put("status", 0);
        logger.info("bindingWeChat is success!!! result:{}", result);
        return result;
    }
}
