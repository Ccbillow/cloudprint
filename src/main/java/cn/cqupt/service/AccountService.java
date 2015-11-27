package cn.cqupt.service;

import java.util.HashMap;

/**
 * Created by Cbillow on 15/11/27.
 */
public interface AccountService {

    HashMap<String, Object> findAccountsByUid(int uid);


    HashMap<String, Object> delete(int id);

}
