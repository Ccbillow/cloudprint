package cn.cqupt.service.impl;

import cn.cqupt.dao.AccountDao;
import cn.cqupt.dao.PrintFileDao;
import cn.cqupt.dao.UserDao;
import cn.cqupt.model.Account;
import cn.cqupt.model.CommonRes;
import cn.cqupt.model.PrintFile;
import cn.cqupt.model.User;
import cn.cqupt.service.PrintFileService;
import cn.cqupt.util.CPConstant;
import cn.cqupt.util.CPHelps;
import cn.cqupt.util.DateUtils;
import cn.cqupt.util.OSSUtils;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
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
        Map<String, Integer> params = Maps.newHashMap();

        logger.info("addPrintFile the file SHA1:{} ", file.getSha1());
        try {
            params.put("uid", loginUser.getId());
            //根据唯一标识查找文件
            PrintFile tempFile = printFileDao.loadPrintFileBySHA1(file.getSha1());
            // 如果没有，则加入文件
            if (tempFile == null) {
                //先添加文件到数据库
                printFileDao.addPrintFile(file);
                PrintFile fileBySHA1 = printFileDao.loadPrintFileBySHA1(file.getSha1());
                params.put("pid", fileBySHA1.getId());
                logger.info("addPrintFile the file is not existed, add the file success");
                //如果有，则多个用户同用一个文件
            } else {
                /**
                 * 如果根据sha1找到了文件，
                 * 就根据登陆用户去找到这个文件的id，如果此id已经存在，则返回，如果不存在则添加关联
                 */
                params.put("pid", tempFile.getId());
                String pidByUid = printFileDao.loadPidByUid(params);
                if (!Strings.isNullOrEmpty(pidByUid) && (Integer.parseInt(pidByUid) == tempFile.getId())) {
                    result.put("status", 1);
                    result.put("message", "不能重复添加文件");
                    logger.error("addPrintFile, with the same openId, file is not allowed to repeated");
                    return result;
                }
                logger.info("addPrintFile the file is existed, only add the Relationship");
            }
            //向中间表添加关联
            printFileDao.addTUP(params);
            logger.info("addPrintFile the Relationship:{}", params);
        } catch (Exception e) {
            result.put("status", 1);
            result.put("message", "添加文件失败，详情请查看日志");
            logger.error("addPrintFile fail : {} ", e);
            return result;
        }
        result.put("status", 0);
        result.put("message", "添加文件成功");
        logger.info("addPrintFile success, The file:{}, the result:{}", file, result);
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
        Map<String, Integer> params = Maps.newHashMap();
        logger.info("deletePrintFile uid:{}, pid:{} ", uid, pid);

        PrintFile printFile = printFileDao.loadPrintFile(pid);
        if (printFile == null) {
            result.put("status", 1);
            result.put("message", "删除文件失败，您要删除的文件不存在");
            logger.info("deletePrintFile fail file is not existed ");
            return result;
        }

        try {
            logger.info("deletePrintFile delete from aliyun file:{}", printFile.getPath().substring(CPConstant.OSS_URL.length()));
            //需要从阿里云上删除文件
            OSSUtils.deleteObject(OSSUtils.getOSSClient(), printFile.getPath().substring(CPConstant.OSS_URL.length()));

            params.put("uid", uid);
            params.put("pid", pid);
            List<String> uids = printFileDao.loadUidsByPid(pid);
            //同一个文件被多人使用，则只删除关联
            if (uids.size() > 1) {
                printFileDao.deleteTUP(params);
                logger.info("deletePrintFile uids:{}, the file is used by not one people, only delete the Relationship");
            } else {
                //当且仅当文件只有一个人，才删除文件
                printFileDao.deleteTUP(params);
                printFileDao.deletePrintFile(pid);
                logger.info("deletePrintFile uids:{}, the file is used by one people, delete file and Relationship");
            }
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

                //在删该文件下所有用户关联
                printFileDao.deleteUidsByPid(pidsBy3Days.get(i));
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
        Account account;
        logger.info("print openid:{}, state:{} ", openid, state);

        try {
            User tuser = userDao.loadUserByOpenId(openid);
            //如果此用户没有绑定，或者微信号不相等，则打印错误
            if (tuser == null) {
                result.put("status", 1);
                result.put("message", "文件传输失败，此微信没有绑定，请用正确的微信号扫描");
                logger.error("print error, error is : weixin is not bindinged");
                return result;
            }

            logger.info(" print checkout the loginUser:{}, isPay:{}", tuser, tuser.getIsPay());
            if (tuser.getIsPay() == 1) {
                result.put("status", 1);
                result.put("message", "用户还没有打印，请支付上一次账单后进行打印");
                logger.error("print error, user has not paid, please pay the last account");
                return result;
            }

            //如果绑定，通过uid查找到所有待打印文件
            params.put("uid", tuser.getId());
            params.put("status", "0");
            params.put("rows", 0);
            params.put("offset", 100);
            logger.info("print params:{}", params);
            List<PrintFile> files = printFileDao.findPrintFiles(params);
            logger.info("print files:{}", files);
            if (files.size() <= 0) {
                result.put("status", 1);
                result.put("message", "没有待打印文件，请确认");
                logger.error("printfiles There are no files being ready to printed");
                return result;
            }

            /**
             * 文件查找成功，将用户设置为未支付
             */
            tuser.setIsPay(1);
            userDao.updateUser(tuser);
            logger.info("printfiles updateUser user:{}", tuser);

            /**
             * 添加账单
             */
            account = new Account();
            account.setUid(tuser.getId());
            account.setTotalPrice(CPHelps.calculateTotalPrice(files));
            account.setDate(DateUtils.getNowTime());
            account.setPrinterID(state);
            accountDao.addAccount(account);
            logger.info("printfiles addAccount account:{}", account);

            //TODO ClientReq，需要将封装的对象序列化传过去
            byte[] bytes = CPHelps.parseObjectToByte(files);
            CommonRes<String> toClient = CPHelps.writeByteToClient(bytes, state);
            logger.info("printfiles The file writeByteToClient success");
            if (!toClient.isSuccess()) {
                result.put("status", 1);
                result.put("message", toClient.getErrorMsg());
                logger.error("printfiles go to client, e:{}", toClient.getErrorMsg());
                return result;
            }
        } catch (Exception e) {
            result.put("status", 1);
            result.put("message", "文件传输失败");
            logger.error("print error:{}", e);
            return result;
        }

        result.put("account", account);
        result.put("status", 0);
        result.put("message", "文件传输成功，请进行支付");
        logger.info("print success!!! result:{}", result);
        return result;
    }


}
