package cn.cqupt.service;

import cn.cqupt.model.PrintFile;
import cn.cqupt.model.User;

import java.util.HashMap;

/**
 * Created by Cbillow on 15/10/28.
 */
public interface PrintFileService {

    HashMap<String, Object> addPrintFile(PrintFile file, User user);

    HashMap<String, Object> deletePrintFile(int uid, int pid);

    HashMap<String, Object> updatePrintFile(PrintFile file);

    HashMap<String, Object> loadPrintFile(int pid);

    HashMap<String, Object> findPrintFiles(int uid, int nextPageNum, int status);

    void timingDelete();

    HashMap<String, Object> print(String openid, String printerId);

}
