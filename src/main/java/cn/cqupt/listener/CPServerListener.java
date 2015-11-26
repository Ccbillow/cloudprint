package cn.cqupt.listener;

import cn.cqupt.task.CPServerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by LiuMian on 2015/11/24.
 * ��������CPServerTask
 */
public class CPServerListener implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(ServletContextListener.class);

    private static CPServerTask serverTask = new CPServerTask();
    private static ExecutorService service;

    public void contextInitialized(ServletContextEvent sce) {
        logger.info("CPServerListener startCPServer");

        service = Executors.newFixedThreadPool(1);
        service.submit(serverTask);
    }

    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("CPServerListener contextDestroyed");
        serverTask.destroyClients();
    }
}
