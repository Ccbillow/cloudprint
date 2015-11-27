package cn.cqupt.dao;

import cn.cqupt.model.Account;

import java.util.List;

/**
 * Created by Cbillow on 15/11/27.
 */
public interface AccountDao {

    void addAccount(Account account);

    List<Account> findAccounstByUid(int uid);

    void deleteAccount(int id);
}
