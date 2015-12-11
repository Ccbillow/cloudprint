package cn.cqupt.service.impl;

import cn.cqupt.dao.AccountDao;
import cn.cqupt.dao.PrintFileDao;
import cn.cqupt.dao.UserDao;
import cn.cqupt.model.CommonRes;
import cn.cqupt.model.PrintFile;
import cn.cqupt.model.User;
import cn.cqupt.model.request.ClientReq;
import cn.cqupt.service.PrintFileService;
import cn.cqupt.task.CPServerHandle;
import cn.cqupt.util.CPConstant;
import cn.cqupt.util.DateUtils;
import cn.cqupt.util.OSSUtils;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Cbillow on 15/10/28.
 */
@Service("printFileService")
public class PrintFileServiceImpl implements PrintFileService {

    private static final Logger logger = LoggerFactory.getLogger(PrintFileServiceImpl.class);

    private PrintFileDao printFileDao;
    private UserDao userDao;
    private AccountDao accountDao;

    @Resource(name = "printFileDao")
    public void setPrintFileDao(PrintFileDao printFileDao) {
        this.printFileDao = printFileDao;
    }

    @Resource(name = "userDao")
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Resource(name = "accountDao")
    public void setAccountDao(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public HashMap<String, Object> addPrintFile(PrintFile file, User loginUser) {
        HashMap<String, Object> result = Maps.newHashMap();
        Map<String, Object> params = Maps.newHashMap();
        Map<String, Object> param = Maps.newHashMap();

        logger.info("addPrintFile the file:{} ", file);
        try {
            params.put("uid", loginUser.getId());
            params.put("filename", file.getFilename());
            logger.info("addPrintFile 根据文件名称和用户查找文件是否存在 uid:{}, filename:{}", loginUser.getId(), file.getFilename());
            PrintFile printFile = printFileDao.loadPrintFileBy(params);
            /**
             * 上传文件
             *
             * 通过文件名称和用户查找
             * 如果不存在 则添加文件-添加关联
             * 如果存在且状态为2 则覆盖掉已打印的文件，将文件重新上传
             * 其他情况，文件不能重复添加
             */
            if (printFile == null) {
                printFileDao.addPrintFile(file);

                Thread.sleep(1000);

                param.put("filename", file.getFilename());
                param.put("sha1", file.getSha1());
                PrintFile temp = printFileDao.loadPrintFileBySha1(param);
                params.put("pid", temp.getId());
                logger.info("addPrintFile 添加文件，需要添加新关联 params:{}", params);
                //向中间表添加关联
                printFileDao.addTUP(params);
            } else if (printFile.getStatus() == 2) {
                file.setId(printFile.getId());
                printFileDao.updatePrintFile(file);
                logger.info("addPrintFile 文件状态为已打印，现在将其更改");
            } else {
                result.put("status", 1);
                result.put("message", "文件不能重复添加，请检查");
                logger.error("添加文件失败，文件不能重复添加，通过用户和文件名判断");
                return result;
            }
        } catch (Exception e) {
            OSSUtils.deleteObject(OSSUtils.getOSSClient(), file.getPath().substring(CPConstant.OSS_URL.length()));
            result.put("status", 1);
            result.put("message", "添加文件失败");
            logger.error("addPrintFile fail : {} ", e);
            return result;
        }
        result.put("status", 0);
        result.put("message", "添加文件成功");
        logger.info("上传文件成功, The file:{}", file);
        return result;
    }

    /**
     * 点击删除
     *
     * @param uid
     * @param pid
     * @return
     */
    public HashMap<String, Object> deletePrintFile(int uid, int pid) {
        HashMap<String, Object> result = Maps.newHashMap();
        logger.info("deletePrintFile uid:{}, pid:{} ", uid, pid);

        PrintFile printFile = printFileDao.loadPrintFile(pid);
        if (printFile == null) {
            result.put("status", 1);
            result.put("message", "删除文件失败，您要删除的文件不存在");
            logger.info("deletePrintFile fail file is not existed ");
            return result;
        }
        try {
            logger.info("deletePrintFile delete from aliyun filePath:{}", printFile.getPath().substring(CPConstant.OSS_URL.length()));
            //需要从阿里云上删除文件
            OSSUtils.deleteObject(OSSUtils.getOSSClient(), printFile.getPath().substring(CPConstant.OSS_URL.length()));
            logger.info("deletePrintFile delete file:{}", printFile);
            printFileDao.deletePrintFile(pid);
        } catch (Exception e) {
            result.put("status", 1);
            result.put("message", "删除文件失败，详情请看日志");
            logger.error("deletePrintFile fail e:{} ", e);
            return result;
        }
        result.put("status", 0);
        result.put("message", "删除文件成功");
        logger.info("deletePrintFile success!!! result:{}", result);
        return result;
    }

    public HashMap<String, Object> updatePrintFile(PrintFile file) {
        HashMap<String, Object> result = Maps.newHashMap();
        try {
            printFileDao.updatePrintFile(file);
        } catch (Exception e) {
            result.put("status", 1);
            result.put("message", "更新文件失败，详情请看日志");
            logger.error("updatePrintFile fail e:{} ", e);
            return result;
        }
        result.put("status", 0);
        result.put("message", "更新文件成功");
        logger.info("updatePrintFile success!!! result:{}", result);
        return result;
    }

    public HashMap<String, Object> loadPrintFile(int pid) {
        HashMap<String, Object> result = Maps.newHashMap();
        logger.info("loadPrintFile pid:{} ", pid);
        PrintFile tempFile = printFileDao.loadPrintFile(pid);
        result.put("file", tempFile);
        result.put("status", 0);
        result.put("message", "加载文件成功");
        logger.info("loadPrintFile success, file:{}", tempFile);
        return result;
    }

    public HashMap<String, Object> findPrintFiles(int uid, int pageNow, int status) {
        HashMap<String, Object> result = Maps.newHashMap();
        HashMap<String, Object> params = Maps.newHashMap();
        logger.info("findAllPrintFile uid:{}, pageNow:{}, status:{}", uid, pageNow, status);

        int pageNum = 0;//总页数
        int offeset = CPConstant.OFFSET;//每页默认显示10条
        List<PrintFile> files = null;
        try {
            params.put("uid", uid);
            params.put("status", status);
            int pageCount = printFileDao.findPrintFilesCount(params);
            pageNum = pageCount % CPConstant.SHOW_NUMBER == 0 ? pageCount / CPConstant.SHOW_NUMBER : pageCount / CPConstant.SHOW_NUMBER + 1;

            int rows = pageNow * CPConstant.SHOW_NUMBER;  //从多少条开始
            params.put("rows", rows);
            params.put("offset", offeset);
            files = printFileDao.findPrintFiles(params);
            logger.info("findPrintFiles the sql of params:{}, pageCount:{}", params, pageCount);
        } catch (Exception e) {
            result.put("status", 1);
            result.put("message", "查找文件失败，详情请查看日志");
            logger.error("findPrintFiles fail e:{} ", e);
        }
        result.put("status", 0);
        result.put("files", files);
        result.put("message", "查找文件成功");
        result.put("totalPage", pageNum);  //总页数
        result.put("nextPageNum", pageNow + 1); //下一页
        logger.info("findPrintFiles success, result:{}", result);
        return result;
    }

    /**
     * 四种情况
     * 1.删除打印完立即删除的文件，不删数据库，不删除关联，只是将path变为空
     * 2.保存三天后删除待打印的文件，要删数据库，删除所有关联
     * 3.保存三天后删除已上传的文件，要删数据库，删除所有关联
     * 4.保存三天后删除已打印中文件，不删数据库，不删除关联，将path变为空
     */
    public void timingDelete() {
        long start = System.currentTimeMillis();
        HashMap<String, Object> param1 = Maps.newHashMap();
        HashMap<String, Object> param2 = Maps.newHashMap();
        HashMap<String, Object> param3 = Maps.newHashMap();
        logger.info("timingDelete start..." + DateUtils.getNowTime());

        //1.删除打印完立即删除的文件，不删数据库，不删除关联，只删除当前关联
        param1.put("status", 2);        //已打印
        param1.put("isDelete", 0);      //打印完立即删除
        List<Integer> pidsPrinted = printFileDao.findPidsPrinted(param1);
        logger.info("timingDelete pidsPrinted:{} ", pidsPrinted);
        if (pidsPrinted.size() > 0) {
            for (int i = 0; i < pidsPrinted.size(); i++) {
                PrintFile temp = printFileDao.loadPrintFile(pidsPrinted.get(i));

                //先删文件，只删除存在阿里云上面的文件
                OSSUtils.deleteObject(OSSUtils.getOSSClient(), temp.getPath().substring(CPConstant.OSS_URL.length()));

                //不删数据，只是将path变为空
                temp.setPath("");
                printFileDao.updatePrintFile(temp);
            }
        }
        logger.info("timingDelete delete pidsPrinted success, the files path is null");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //2.3.删除超过3天的待打印，已上传文件，需要将文件删除
        param2.put("overdueTime", DateUtils.getNowTime());   //超过了三天
        param2.put("status1", "0");  //已上传
        param2.put("status2", "1");  //待打印
        List<Integer> pidsBy3Days = printFileDao.findPidsBy3Days(param2);
        logger.info("timingDelete pidsBy3Days:{} ", pidsBy3Days);
        if (pidsBy3Days.size() > 0) {
            for (int i = 0; i < pidsBy3Days.size(); i++) {
                PrintFile temp = printFileDao.loadPrintFile(pidsBy3Days.get(i));
                //先删除阿里云上的文件
                OSSUtils.deleteObject(OSSUtils.getOSSClient(), temp.getPath().substring(CPConstant.OSS_URL.length()));

                //删数据
                printFileDao.deletePrintFile(pidsBy3Days.get(i));
            }
        }
        logger.info("timingDelete delete pidsBy3Days success, the files is deleted");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //4.保存三天后删除已打印中文件，不删数据库，不删除关联，将path变为空
        param3.put("overdueTime", DateUtils.getNowTime());  //超过了三天
        param3.put("status", "2");  //已打印
        param2.put("isDelete", "1");  //打印完保存三天
        List<Integer> pidsBy3DaysPrinted = printFileDao.findPidsBy3DaysPrinted(param3);
        logger.info("timingDelete pidsBy3DaysPrinted:{} ", pidsBy3DaysPrinted);
        if (pidsBy3DaysPrinted.size() > 0) {
            for (int i = 0; i < pidsBy3DaysPrinted.size(); i++) {
                PrintFile temp = printFileDao.loadPrintFile(pidsBy3DaysPrinted.get(i));
                //先删除阿里云上的文件
                OSSUtils.deleteObject(OSSUtils.getOSSClient(), temp.getPath().substring(CPConstant.OSS_URL.length()));

                //不删数据，只是将path变为空
                temp.setPath("");
                printFileDao.updatePrintFile(temp);
            }
        }
        logger.info("timingDelete delete pidsBy3DaysPrinted success! the files path is null");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        logger.info("timingDelete end... " + DateUtils.getNowTime() + " and it cost " + (System.currentTimeMillis() - start));
    }

    public HashMap<String, Object> print(String openid, String state) {
        HashMap<String, Object> result = Maps.newHashMap();
        HashMap<String, Object> params = Maps.newHashMap();
        List<PrintFile> files;
        CommonRes<String> response;
        logger.info("print 查找文件信息，准备发送给客户端，微信号：openid:{}, state:{} ", openid, state);

        try {
            User tuser = userDao.loadUserByOpenId(openid);
            //如果此用户没有绑定，或者微信号不相等，则打印错误
            if (tuser == null) {
                result.put("status", 1);
                result.put("message", "文件传输失败，请用正确的微信号扫描");
                logger.error("print error, error is : weixin is not bindinged");
                return result;
            }

//            if (tuser.getIsPay() == 1) {
//                result.put("status", 1);
//                result.put("message", "用户还没有打印，请支付上一次账单后进行打印");
//                logger.error("print error, user has not paid, please pay the last account");
//                return result;
//            }

            //如果绑定，通过uid查找到所有待打印文件
            params.put("uid", tuser.getId());
            params.put("status", "0");
            params.put("rows", 0);
            params.put("offset", 100);
            logger.info("发送文件信息，通过用户查找到所有待打印文件 查找参数：params:{}", params);
            files = printFileDao.findPrintFiles(params);
            if (files.size() <= 0) {
                result.put("status", 1);
                result.put("message", "没有待打印文件，请确认");
                logger.error("print There are no files being ready to printed");
                return result;
            }

            /**
             * 文件查找成功，将用户设置为未支付
             */
//            tuser.setIsPay(1);
//            userDao.updateUser(tuser);

            ClientReq clientReq = new ClientReq();
            clientReq.setUser(tuser);
            clientReq.setMd5Code(state);
            clientReq.setFiles(files);
            clientReq.setSuccess(true);
            logger.info("print 文件信息准备传送到客户端. 文件信息：clientReq:{}", clientReq);
            response = CPServerHandle.writeObjectToClient(clientReq, state);
            if (!response.isSuccess()) {
                result.put("status", 1);
                result.put("message", response.getErrorMsg());
                logger.error("print CPServerHandle fail, e:{}", response.getErrorMsg());
                return result;
            }
        } catch (Exception e) {
            result.put("status", 1);
            result.put("message", "文件传输到客户端失败，请检查日志");
            logger.error("print error:{}", e);
            return result;
        }

        logger.info("文件信息已经成功发送给客户端");
        /**
         * 如果打印文件信息传送到客户端成功了
         * 就将文件状态设置成2
         * 文件状态变成已打印
         */
        for (PrintFile pf : files) {
            pf.setStatus(2);
            printFileDao.updatePrintFile(pf);
        }
        result.put("status", 0);
        result.put("message", "文件传输成功，请确认是否立即打印");
        logger.info("print success!!! result:{}", result);
        return result;
    }

    public HashMap<String, Object> confirmPrint(String openid, String md5code) {
        HashMap<String, Object> result = Maps.newHashMap();
        ClientReq request = new ClientReq();
        CommonRes<String> response;
        logger.info("确认打印开始执行，首先通过openid获取到用户：", openid);

        try {
            User user = userDao.loadUserByOpenId(openid);
            if (user == null) {
                result.put("status", 1);
                result.put("message", "确认打印失败，不存在的微信号");
                logger.error("确认打印失败，不存在的微信号");
                return result;
            }

            request.setUser(user);
            request.setMd5Code(md5code);
            logger.info("获得User:{}, 现在向客户端:{} 确认打印请求...", user, md5code);
            response = CPServerHandle.writeObjectToClient(request, md5code);
            if (!response.isSuccess()) {
                result.put("status", 1);
                result.put("message", response.getErrorMsg());
                logger.error("confirmPrint CPServerHandle error:{}", response.getErrorMsg());
                return result;
            }
        } catch (Exception e) {
            result.put("status", 1);
            result.put("message", "向客户端确认打印失败: " + e.getMessage());
            logger.error("confirmPrint error:{}", e);
            return result;
        }

        logger.info("向客户端发送确认打印请求成功");
        result.put("status", 0);
        result.put("message", "发送成功，请到打印机接收你的文件");
        return result;
    }


}
