package cn.cqupt.service.impl;

import cn.cqupt.dao.UserDao;
import cn.cqupt.model.User;
import cn.cqupt.service.UserService;
import cn.cqupt.util.CPHelps;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Cbillow on 15/10/27.
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    private static Logger logger = Logger.getLogger(UserServiceImpl.class);

    private UserDao userDao;

    @Resource(name = "userDao")
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public HashMap<String, Object> sendSMS(String mobile) {
        HashMap<String, Object> map = Maps.newHashMap();
        logger.info("UserServiceImpl sendSMS" + mobile);

        //随机生成验证码
        String validateCode = CPHelps.getValidateCode();
        String result;
        try {
            //请求第三方发送验证码短信
            result = CPHelps.sendSMS(mobile, validateCode);
        } catch (IOException e) {
            logger.error("发送验证码失败 e:{}", e);
            map.put("status", 1);
            map.put("message", "添加的用户已经存在");
            return map;
//            throw new CPException("发送验证码失败");
        }
        map.put("result", result);
        map.put("validateCode", validateCode);
        logger.info("UserServiceImpl getValidateCode " + result);
        return map;
    }

    public HashMap<String, Object> addUser(User user) {
        HashMap<String, Object> result = Maps.newHashMap();
        logger.info("UserServiceImpl addUser" + user.toString());

        User temp = userDao.loadUserByMobile(user.getMobile());
        if (temp != null) {
//            throw new CPException("添加的用户已经存在");
            result.put("status", 1);
            result.put("message", "添加的用户已经存在");
            logger.info("registerUser fail : mobile has already existed");
            return result;
        }
        userDao.addUser(user);
        result.put("status", 0);
        result.put("message", "添加用户成功");
        logger.info("UserServiceImpl registerUser success");
        return result;
    }

    public HashMap<String, Object> login(String mobile, String password) {
        HashMap<String, Object> result = Maps.newHashMap();
        logger.info("UserServiceImpl login" + mobile + " " + password);

        User loginUser = userDao.loadUserByMobile(mobile);
        if (loginUser == null) {
            result.put("status", 1);
            result.put("message", "用户不存在");
            logger.info("UserServiceImpl login fail : user does not exist");
            return result;
        } else if (!password.equalsIgnoreCase(loginUser.getPassword())) {
            result.put("status", 1);
            result.put("message", "密码错误");
            logger.info("UserServiceImpl login fail : password is wrong");
            return result;
        }
        loginUser.setPassword("");
        result.put("status", 0);
        result.put("loginUser", loginUser);
        result.put("message", "登陆成功");
        logger.info("UserServiceImpl login success");
        return result;
    }

    public HashMap<String, Object> updateUser(int id, String password, String nickname) {
        HashMap<String, Object> result = Maps.newHashMap();
        logger.info("UserServiceImpl updateUser id" + id + " password" + password + " nickname" + nickname);

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
            logger.info("UserServiceImpl updateUser error : {}", e);
            return result;
        }

        result.put("status", 0);
        result.put("message", "更新用户成功");
        logger.info("UserServiceImpl updateUser success");
        return result;
    }

    public HashMap<String, Object> refundPassword(String mobile, String password) {
        HashMap<String, Object> result = Maps.newHashMap();
        logger.info("UserServiceImpl refundPassword mobile: " + mobile);

        try {
            User temp = userDao.loadUserByMobile(mobile);
            if (temp == null) {
                result.put("status", 1);
                result.put("message", "重置密码失败，此用户不存在");
                logger.info("UserServiceImpl refundPassword fail, mobile is not exist");
                return result;
            }
            //密码重置为password
            temp.setPassword(password);
            userDao.updateUser(temp);
        } catch (Exception e) {
            result.put("status", 1);
            result.put("message", "重置密码操作失败，请查看日志");
            logger.info("UserServiceImpl refundPassword errpr : {}", e);
            return result;
        }
        result.put("status", 0);
        result.put("message", "重置密码成功");
        logger.info("UserServiceImpl refundPassword success");
        return result;
    }

    public HashMap<String, Object> bindingWeChat(String openid, String state) {
        HashMap<String, Object> result = Maps.newHashMap();
        logger.info("UserServiceImpl bindingWeChat openid: " + openid + " state " + state);

        try {
            User temp = userDao.loadUserByMobile(state);
            //TODO 如果已经绑定，则绑定失败，此版本只允许绑定一次
            if ("1".equalsIgnoreCase(temp.getIsBinding())) {
                result.put("status", 1);
                result.put("message", "绑定微信失败，此用户已经绑定");
                logger.info("UserServiceImpl bindingWeChat fail, the user is already bindinged");
                return result;
                //如果没有绑定，则为这个用户添加微信号，并设置为已绑定
            } else if ("0".equalsIgnoreCase(temp.getIsBinding())) {
                temp.setWeixin(openid);
                temp.setIsBinding("1");
                userDao.updateUser(temp);
            }
        } catch (Exception e) {
            result.put("status", 1);
            result.put("message", "绑定微信操作失败，请查看日志");
            logger.info("UserServiceImpl bindingWeChat error : {}", e);
            return result;
        }
        result.put("status", 0);
        result.put("message", "微信绑定成功");
        logger.info("UserServiceImpl bindingWeChat success");
        return result;
    }
}
