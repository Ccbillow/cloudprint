package cn.cqupt.controller;

import cn.cqupt.model.PrintFile;
import cn.cqupt.model.PrintType;
import cn.cqupt.model.User;
import cn.cqupt.model.WeChat;
import cn.cqupt.service.PrintFileService;
import cn.cqupt.util.*;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Cbillow on 15/10/28.
 */
@Controller
@RequestMapping("/printFile")
public class PrintFileWebContriller {

    private static final Logger logger = LoggerFactory.getLogger(PrintFileWebContriller.class);

    private PrintFileService printFileService;

    @Resource(name = "printFileService")
    public void setPrintFileService(PrintFileService printFileService) {
        this.printFileService = printFileService;
    }

    @RequestMapping(value = "/upload", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String uploadFile(String number, String status, String isColorful, String isDelete,
                             @RequestParam("file") CommonsMultipartFile file, HttpServletRequest request) {
        logger.info("PrintFileWebContriller uploadFile ");
        HashMap<String, Object> result = Maps.newHashMap();
        PrintFile pf = new PrintFile();
        String path;
        String type;

        User loginUser = (User) request.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            result.put("status", 1);
            result.put("message", "请登录后操作");
            return JSON.toJSONString(result);
        }

        //把文件存入阿里云，得到路径
        logger.info("开始将文件存入阿里云");
        try {
            path = CPHelps.uploadFileToOSS("18580741650"//loginUser.getMobile()
                    , file);
            logger.info("文件存入阿里云结束 path : " + path);
            pf.setPath(path);
        } catch (IOException e) {
            result.put("status", 1);
            result.put("message", "将文件存入阿里云出错");
            logger.error("将文件存入阿里云出错  出错信息：{}", e);
            return JSON.toJSONString(result);
        }

        /**
         * 得到文件名，判断其类型
         * 暂时只支持WORD和PDF
         */
        String filename = file.getOriginalFilename();
        if (filename.endsWith(".doc") || filename.endsWith(".docx")) {
            type = "0";
        } else if (filename.endsWith(".pdf")) {
            type = "1";
        } else {
            result.put("status", 1);
            result.put("message", "暂不支持文件类型");
            logger.error("暂不支持文件类型");
            return JSON.toJSONString(result);
        }
        pf.setFilename(filename);
        //判断类型为pdf还是word
        if ("0".equalsIgnoreCase(type)) {
            pf.setType(PrintType.WORD.getCode());
        } else if ("1".equalsIgnoreCase(type)) {
            pf.setType(PrintType.PDF.getCode());
        }
        pf.setNumber(Integer.parseInt(number));
        //SHA1生成文件\唯一标识
        pf.setSha1(EncoderHandler.encodeBySHA1(file.getBytes()));
        //所有文件保存3天
        pf.setOverdueTime(DateUtils.unixTimestampToDate(new Date().getTime() + CPConstant.THREE_DAYS));

        //默认打印完立即删除，不勾选
        if (Strings.isNullOrEmpty(isDelete)) {
            pf.setIsDelete(0);
            //打印完保存三天
        } else if ("on".equalsIgnoreCase(isDelete)) {
            pf.setIsDelete(1);
        }

        //默认不彩印，不勾选
        if (Strings.isNullOrEmpty(isColorful)) {
            pf.setIsColorful(0);
        } else if ("on".equalsIgnoreCase(isColorful)) {
            //勾选彩印， 则彩印
            pf.setIsColorful(1);
        }

        //默认不勾选，放入待打印
        if (Strings.isNullOrEmpty(status)) {
            pf.setStatus(0);
            //如果勾选了，则仅上传不打印
        } else if ("on".equalsIgnoreCase(status)) {
            pf.setStatus(1);
        }
        logger.info("PrintFileWebContriller uploadFile start... file:{}", pf);
        result = printFileService.addPrintFile(pf, loginUser);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "/delete/{pid}", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String deleteFile(@PathVariable String pid, HttpServletRequest request) {
        logger.info("PrintFileWebContriller deleteFile pid:{} ", pid);
        HashMap<String, Object> result = Maps.newHashMap();
        User loginUser = (User) request.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            result.put("status", 1);
            result.put("message", "请登录后操作");
            logger.error("PrintFileWebContriller deleteFile user is not logining");
            return JSON.toJSONString(result);
        }

        result = printFileService.deletePrintFile(loginUser.getId(), Integer.parseInt(pid));
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "/update/{pid}", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String updateFile(@PathVariable String pid, String number, String isColorful,
                             HttpServletRequest request) {
        logger.error("PrintFileWebContriller updateFile user pid:{}, number:{}, isColorful:{}", pid, number, isColorful);
        HashMap<String, Object> result = Maps.newHashMap();
        User loginUser = (User) request.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            result.put("status", 1);
            result.put("message", "请登录后操作");
            logger.error("PrintFileWebContriller deleteFile user is not logining");
            return JSON.toJSONString(result);
        }
        //空就是不彩印
        if (Strings.isNullOrEmpty(isColorful)) {
            isColorful = "0";
        } else if ("on".equalsIgnoreCase(isColorful)) {
            isColorful = "1";
        }
        if (Strings.isNullOrEmpty(number) || Integer.parseInt(number) <= 0) {
            result.put("status", 1);
            result.put("message", "打印数量不对，请确认，打印数量在1-100之间");
            return JSON.toJSONString(result);
        }
        result = printFileService.updatePrintFile(Integer.parseInt(pid), Integer.parseInt(number), isColorful);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "/load/{pid}", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String previewFile(@PathVariable String pid, HttpServletRequest request) {
        logger.info("PrintFileWebContriller previewFile pid:{}", pid);
        HashMap<String, Object> result = Maps.newHashMap();
        User loginUser = (User) request.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            result.put("status", 1);
            result.put("message", "请登录后操作");
            logger.error("PrintFileWebContriller deleteFile user is not logining");
            return JSON.toJSONString(result);
        }

        result = printFileService.loadPrintFile(Integer.parseInt(pid));
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "/findByStatus", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String findFilesByStatus(String pageNow, String status, HttpServletRequest request) {
        logger.info("PrintFileWebContriller findFilesByStatus pageNow:{}, status:{}", pageNow, status);
        HashMap<String, Object> result = Maps.newHashMap();
        User loginUser = (User) request.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            result.put("status", 1);
            result.put("message", "请登录后操作");
            logger.error("PrintFileWebContriller deleteFile user is not logining");
            return JSON.toJSONString(result);
        }
        result = printFileService.findPrintFiles(loginUser.getId(),
                Integer.parseInt(pageNow), Integer.parseInt(status));
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "/timingDelete", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String timingDelete() {
        while (true) {
            try {

                /**
                 * 没隔30分钟起一个线程去扫描需要被删除的文件
                 */
                new Thread(new Runnable() {
                    public void run() {
                        printFileService.timingDelete();
                    }
                }).start();

                //每隔30分钟扫描一次
                Thread.sleep(CPConstant.INTERVAL_DAY);
            } catch (Exception e) {
                logger.error("TimingDeleteTask exception:{}", e);

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e1) {
                    logger.error("TimingDeleteTask Interrupted exception:{}", e);
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * 客户端扫码，打印
     * 此时state是打印机ip
     *
     * @param code  通过code得到access_token,通过access_token得到用户openid
     * @param state 打印机ip
     * @param req
     * @return
     */
    @RequestMapping(value = "/print", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String print(String code, String state, HttpServletRequest req) {
        logger.info("UserController print start... code:{}, state:{} ", code, state);

        HashMap<String, Object> result = Maps.newHashMap();
        User loginUser = (User) req.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            result.put("status", 1);
            result.put("message", "请登录后操作");
            logger.error("PrintFileWebContriller deleteFile user is not logining");
            return JSON.toJSONString(result);
        }

        String accessTokenURL = CPHelps.getAccessTokenURL(code);
        String content;
        try {
            content = CPHelps.HttpGet(accessTokenURL);
            if (content.contains("errcode") && content.contains("errmsg")) {
                result.put("status", 1);
                result.put("message", "微信扫码，Code无效错误");
                logger.error("UserController print code is wrong");
                return JSON.toJSONString(result);
            } else if (content.contains("openid")) {
                WeChat wc = JacksonUtil.deSerialize(content, WeChat.class);
                result = printFileService.print(loginUser.getId(), wc.getOpenid(), state);
            }
        } catch (IOException e) {
            result.put("status", 1);
            result.put("message", "访问" + accessTokenURL + "出错");
            logger.error("UserController print accessTokenURL error:{}", e);
            return JSON.toJSONString(result);
        } catch (Exception ie) {
            result.put("status", 1);
            result.put("message", "打印失败，详情请查看日志");
            logger.error("UserController print error:{}", ie);
            return JSON.toJSONString(result);
        }
        return JSON.toJSONString(result);
    }
}
