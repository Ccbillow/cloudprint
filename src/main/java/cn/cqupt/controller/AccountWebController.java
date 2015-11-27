package cn.cqupt.controller;

import cn.cqupt.model.User;
import cn.cqupt.service.AccountService;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * Created by Cbillow on 15/11/27.
 */
@Controller
@RequestMapping("/account")
public class AccountWebController {

    private static final Logger logger = LoggerFactory.getLogger(AccountWebController.class);

    private AccountService accountService;

    @Resource(name = "accountService")
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * 当用户isPay为1，未支付的时候需要访问这个url，得到他的账单
     * @param req
     * @return
     */
    @RequestMapping(value = "/getaccount", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getAccount(HttpServletRequest req) {
        HashMap<String, Object> result = Maps.newHashMap();
        User loginUser = (User) req.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            result.put("status", 1);
            result.put("message", "请登录后操作");
            logger.error("findFilesByStatus fail, user is not logining");
            return JSON.toJSONString(result);
        }

        logger.info("查看该用户下账单， loginUser:{}", loginUser);
        result = accountService.findAccountsByUid(loginUser.getId());
        return JSON.toJSONString(result);
    }
}
