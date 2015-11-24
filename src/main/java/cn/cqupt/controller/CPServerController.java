package cn.cqupt.controller;

import cn.cqupt.task.CPServerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Cbillow on 15/11/24.
 */
@Controller
@RequestMapping("/server")
public class CPServerController {

    private static final Logger logger = LoggerFactory.getLogger(CPServerController.class);

    private static ExecutorService service;

    @RequestMapping(value = "/start")
    public void startCPServer() {
        logger.info("ServerController startCPServer");

        service = Executors.newFixedThreadPool(1);
        CPServerTask task = new CPServerTask();
        service.submit(task);
    }

}
