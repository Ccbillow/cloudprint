package cn.cqupt.service.impl;

import cn.cqupt.dao.AccountDao;
import cn.cqupt.model.Account;
import cn.cqupt.service.AccountService;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sun.rmi.runtime.Log;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Cbillow on 15/11/27.
 */
@Service("accountService")
public class AccountServiceImpl implements AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    private AccountDao accountDao;

    @Resource(name = "accountDao")
    public void setAccountDao(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public HashMap<String, Object> findAccountsByUid(int uid) {
        HashMap<String, Object> result = Maps.newHashMap();
        logger.info("查找该用户下所有账单， uid:{}", uid);

        List<Account> accounts;
        try {
            accounts = accountDao.findAccounstByUid(uid);
        } catch (Exception e) {
            result.put("status", 1);
            result.put("message", "查看账单失败");
            logger.info("查找账单失败 e:{}", e);
            return result;
        }
        result.put("status", 0);
        result.put("message", "查看账单成功");
        result.put("accounts", accounts);
        logger.info("查找账单成功，得到结果。 result:{}", result);
        return result;
    }

    public HashMap<String, Object> delete(int id) {
        return null;
    }
}
