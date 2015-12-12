package cn.cqupt.controller;

import cn.cqupt.model.PrintFile;
import cn.cqupt.model.PrintType;
import cn.cqupt.model.User;
import cn.cqupt.model.response.WeChatAccessTokenRes;
import cn.cqupt.service.PrintFileService;
import cn.cqupt.util.CPConstant;
import cn.cqupt.util.CPHelps;
import cn.cqupt.util.DateUtils;
import cn.cqupt.util.JacksonUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Cbillow on 15/10/28.
 */
@Controller
@RequestMapping("/printfile")
public class PrintFileWebContriller {

    private static final Logger logger = LoggerFactory.getLogger(PrintFileWebContriller.class);

    private PrintFileService printFileService;

    @Resource(name = "printFileService")
    public void setPrintFileService(PrintFileService printFileService) {
        this.printFileService = printFileService;
    }

    @RequestMapping(value = "/upload", produces = "text/html;charset=UTF-8")
    public void uploadFile(String id, String number, String status, String isColorful, String isDelete,
                           @RequestParam("file") CommonsMultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        logger.info("uploadFile begin... number:{}, states:{}, isColorful:{}, isDelete:{}", number, status, isColorful, isDelete);
        HashMap<String, Object> result = Maps.newHashMap();
        PrintFile pf = new PrintFile();
        result.put("id", id);
        String path;
        String type;

        User loginUser = (User) request.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            result.put("status", 1);
            result.put("message", "请登录后操作");
            logger.error("uploadFile fail, user is not logining");
            returnScript(result, response);
            return;
        }

        try {
            /**
             * 得到文件名，判断其类型
             * 暂时只支持WORD和PDF
             */
            String filename = file.getOriginalFilename();
            if (filename.endsWith(".doc") || filename.endsWith(".docx")
                    || filename.endsWith(".xls") || filename.endsWith(".xlsx")) {
                type = "0";
            } else if (filename.endsWith(".pdf")) {
                type = "1";
            } else {
                result.put("status", 1);
                result.put("message", "暂不支持文件类型");
                logger.error("uploadFile, 暂不支持文件类型");
                returnScript(result, response);
                return;
            }

            pf.setFilename(filename);
            //判断类型为pdf还是word
            if ("0".equalsIgnoreCase(type)) {
                pf.setType(PrintType.WORD.getCode());
            } else if ("1".equalsIgnoreCase(type)) {
                pf.setType(PrintType.PDF.getCode());
            }

            //SHA1生成文件\唯一标识
            pf.setSha1(String.valueOf(loginUser.getId()));
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
                //如果勾选了，则仅上传不打印，放入已上传
            } else if ("on".equalsIgnoreCase(status)) {
                pf.setStatus(1);
            }

            /**
             * 计算打印价格，并设置
             */
            pf.setNumber(Integer.parseInt(number));
//            pf.setPrice(CPHelps.calculatePrice(pf.getNumber(), pf.getIsColorful()));

            logger.info("uploadFile the file:{}", pf);

            try {
                //把文件存入阿里云，得到路径
                logger.info("uploadFile loginUser:{}, 开始将文件存入阿里云", loginUser);
                path = CPHelps.uploadFileToOSS(loginUser.getWeixin(), file);
                pf.setPath(URLEncoder.encode(path, "UTF-8"));
                logger.info("uploadFile, 文件存入阿里云结束 path:{}", pf.getPath());
            } catch (IOException e) {
                result.put("id", id);
                result.put("status", 1);
                result.put("message", "将文件存入阿里云出错");
                logger.error("uploadFile, 将文件存入阿里云出错  出错信息 e:{}", e);
                returnScript(result, response);
                return;
            }
            result = printFileService.addPrintFile(pf, loginUser);
        } catch (Exception e) {

            result.put("status", 1);
            result.put("message", "上传文件出错");
            logger.error("uploadFile, 上传文件出错  出错信息 e:{}", e);
            returnScript(result, response);
            return;
        }
        result.put("id", id);
        returnScript(result, response);
    }

    private void returnScript(HashMap<String, Object> result, HttpServletResponse response) {
        response.setContentType("text/html;charset=UTF-8");
        try {
            PrintWriter out = response.getWriter();
            out.println("<script language='javascript'>");
            out.println("top.addReady(" + JSON.toJSONString(result) + ")");
            out.println("</script>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/delete/{pid}", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String deleteFile(@PathVariable String pid, HttpServletRequest request) {
        logger.info("deleteFile pid:{} ", pid);
        HashMap<String, Object> result = Maps.newHashMap();
        User loginUser = (User) request.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            result.put("status", 1);
            result.put("message", "请登录后操作");
            logger.error("deleteFile fail, user is not logining");
            return JSON.toJSONString(result);
        }

        result = printFileService.deletePrintFile(loginUser.getId(), Integer.parseInt(pid));
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "/update/{pid}", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String updateFile(@PathVariable String pid, String number, String isColorful, HttpServletRequest request) {
        logger.error("updateFile start... pid:{}, number:{}, isColorful:{}", pid, number, isColorful);
        HashMap<String, Object> result = Maps.newHashMap();
        User loginUser = (User) request.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            result.put("status", 1);
            result.put("message", "请登录后操作");
            logger.error("deleteFile fail, user is not logining");
            return JSON.toJSONString(result);
        }

        HashMap<String, Object> map = printFileService.loadPrintFile(Integer.parseInt(pid));
        PrintFile file = (PrintFile) map.get("file");
        logger.error("updateFile old file:{}", file);

        /**
         * 需要修改价格
         */
        //默认不彩印，不勾选
        if (Strings.isNullOrEmpty(isColorful)) {
            file.setIsColorful(0);
        } else if ("on".equalsIgnoreCase(isColorful)) {
            //勾选彩印， 则彩印
            file.setIsColorful(1);
        }
        if (!Strings.isNullOrEmpty(number)) {
            file.setNumber(Integer.parseInt(number));
        }
//        file.setPrice(CPHelps.calculatePrice(file.getNumber(), file.getIsColorful()));

        logger.error("updateFile new file:{}", file);
        result = printFileService.updatePrintFile(file);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "/updatestatus/{pid}", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String updateFileStatus(@PathVariable String pid, String status, HttpServletRequest request) {
        logger.error("updateFile start... pid:{}, status:{}", pid, status);
        HashMap<String, Object> result = Maps.newHashMap();
        User loginUser = (User) request.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            result.put("status", 1);
            result.put("message", "请登录后操作");
            logger.error("deleteFile fail, user is not logining");
            return JSON.toJSONString(result);
        }

        HashMap<String, Object> map = printFileService.loadPrintFile(Integer.parseInt(pid));
        PrintFile file = (PrintFile) map.get("file");
        logger.error("updateFile 旧文件:{}", file);

        //默认不勾选，放入待打印
        if (Strings.isNullOrEmpty(status)) {
            file.setStatus(0);
            //如果勾选了，则仅上传不打印
        } else if ("on".equalsIgnoreCase(status)) {
            file.setStatus(1);
        }
        logger.error("updateFile 更新后的文件:{}", file);
        result = printFileService.updatePrintFile(file);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "/load/{pid}", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String previewFile(@PathVariable String pid, HttpServletRequest request) {
        logger.info("load file, pid:{}", pid);
        HashMap<String, Object> result = Maps.newHashMap();
        User loginUser = (User) request.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            result.put("status", 1);
            result.put("message", "请登录后操作");
            logger.error("deleteFile fail,  user is not logining");
            return JSON.toJSONString(result);
        }

        result = printFileService.loadPrintFile(Integer.parseInt(pid));
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "/findbystatus", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String findFilesByStatus(String pageNow, String status, HttpServletRequest request) {
        logger.info("findFilesByStatus pageNow:{}, status:{}", pageNow, status);
        HashMap<String, Object> result = Maps.newHashMap();
        User loginUser = (User) request.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            result.put("status", 1);
            result.put("message", "请登录后操作");
            logger.error("findFilesByStatus fail, user is not logining");
            return JSON.toJSONString(result);
        }
        result = printFileService.findPrintFiles(loginUser.getId(),
                Integer.parseInt(pageNow), Integer.parseInt(status));
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "/timingdelete")
    @ResponseBody
    public String timingDelete() {
        logger.info("timingDelete start......");
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

                //每隔60分钟扫描一次
                Thread.sleep(CPConstant.INTERVAL_DAY * 2);
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
     * @param state MD5CODE
     * @return
     */
    @RequestMapping(value = "/print", produces = "application/json;charset=UTF-8")
    public ModelAndView print(String code, String state) throws UnsupportedEncodingException {
        HashMap<String, Object> result = Maps.newHashMap();
        ModelAndView mav = new ModelAndView("/confirmprint");
        WeChatAccessTokenRes wc = null;
        logger.info("print start... Weixin code:{}, MD5CODE state:{} ", code, state);

        String accessTokenURL = CPHelps.getAccessTokenURL(code);
        logger.info("bindingWeChat getAccessTokenURL:{}", accessTokenURL);
        String content;
        try {
            content = CPHelps.HttpGet(accessTokenURL);
            logger.info("bindingWeChat accessTokenURL return content:{}", content);
            if (content.contains("errcode") && content.contains("errmsg")) {
                result.put("status", 1);
                result.put("message", "打印失败，微信扫码CODE无效");
                mav.addObject(result);
                logger.error("传送打印信息失败，微信扫码 code无效");
                return mav;
            } else if (content.contains("openid")) {
                wc = JacksonUtil.deSerialize(content, WeChatAccessTokenRes.class);
                result = printFileService.print(wc.getOpenid(), state);
            }
        } catch (IOException e) {
            result.put("status", 1);
            result.put("message", "传送打印信息失败，微信获取TOKEN失败");
            mav.addObject(result);
            logger.error("传送打印信息失败，微信获取TOKEN失败, e:", e);
            return mav;
        } catch (Exception ie) {
            result.put("status", 1);
            result.put("message", "传送打印信息失败，请重新扫码");
            mav.addObject(result);
            logger.error("传送打印信息失败, e:{}", ie);
            return mav;
        }

        Integer status = (Integer) result.get("state");
        if (status == 1) {
            mav.addObject(result);
            return mav;
        }
        /**
         * 传送打印信息成功
         *
         * 添加openid和md5code
         * 用于确认立即打印
         */
        result.put("openid", wc.getOpenid());
        result.put("md5code", state);
        mav.addObject(result);
        logger.info("传输文件信息成功，", result);
        return mav;
    }

    @RequestMapping(value = "/confirm", produces = "application/json;charset=UTF-8")
    public String confirmPrint(String openid, String md5code) {
        HashMap<String, Object> result;
        logger.info("用户:{} 向客户端:{} 确认打印", openid, md5code);

        result = printFileService.confirmPrint(openid, md5code);
        logger.info("确认打印结果:{}", result);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "/redirect", method = RequestMethod.GET)
    public ModelAndView redirect(String status, String message, String openid, String md5code) {
        ModelAndView mav = new ModelAndView("/confirmprint");
        HashMap<String, Object> result = Maps.newHashMap();
        result.put("status", status);
        result.put("message", message);
        result.put("openid", openid);
        result.put("md5code", md5code);
        System.out.println(status);
        System.out.println(message);
        mav.addObject(result);
        return mav;
    }

    @RequestMapping(value = "/finalPage", method = RequestMethod.GET)
    public String finalPage(String param) {
        System.out.println(param);
        return "error";
    }
}
