package cn.cqupt.dao;

import cn.cqupt.model.PrintFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Cbillow on 15/10/28.
 */
public interface PrintFileDao {

    void addPrintFile(PrintFile file);

    void addTUP(Map<String, Integer> params);      //添加关联

    void deletePrintFile(int pid);   //删除文件

    void deleteTUP(Map<String, Integer> params);   //删除关联

    void deleteUidsByPid(int pid); //根据被删除文件删除该文件下所有用户关联

    void updatePrintFile(PrintFile file);   //修改文件

    PrintFile loadPrintFile(int pid);    //预览文件

    PrintFile loadPrintFileBySHA1(String sha1); //根据sha1查找文件

    List<String> loadUidsByPid(int pid);

    String loadPidByUid(Map<String, Integer> params);  //根据uid和pid查找pid，如果有，则不用添加关联

    List<PrintFile> findPrintFiles(HashMap<String, Object> params);       //根据状态查找所有文件

    int findPrintFilesCount(HashMap<String, Object> params);    //根据状态查找该状态下所有文件

    List<Integer> findPidsPrinted(HashMap<String, Object> params); //找到已打印且立即删除的文件ids

    List<Integer> findPidsBy3Days(HashMap<String, Object> param); //找到三天后待打印，已上传文件ids

    List<Integer> findPidsBy3DaysPrinted(HashMap<String, Object> param); //找到三天后已打印文件ids

}
