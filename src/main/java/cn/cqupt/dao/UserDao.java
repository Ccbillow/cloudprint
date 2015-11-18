package cn.cqupt.dao;

import cn.cqupt.model.User;

/**
 * Created by Cbillow on 15/10/27.
 */
public interface UserDao {

    void addUser(User user);

    User loadUserById(int id);

    User loadUserByMobile(String mobile);

    void updateUser(User user);
}
