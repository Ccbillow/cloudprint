package cn.cqupt.service;

import cn.cqupt.model.User;

import java.util.HashMap;

/**
 * Created by Cbillow on 15/10/27.
 */
public interface UserService {

    HashMap<String, Object> sendSMS(String mobile);

    HashMap<String, Object> addUser(User user);

    HashMap<String, Object> login(String mobile, String password);

    HashMap<String, Object> updateUser(int id, String password, String nickname);

    HashMap<String, Object> refundPassword(String mobile, String password);

    HashMap<String, Object> bindingWeChat(String openid, String state);

}
