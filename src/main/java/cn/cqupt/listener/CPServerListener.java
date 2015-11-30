package cn.cqupt.listener;

import cn.cqupt.task.CPServerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by LiuMian on 2015/11/24.
 * ��������CPServerTask
 */
public class CPServerListener {
    private static final Logger logger = LoggerFactory.getLogger(ServletContextListener.class);

    private static CPServerTask serverTask = null;
    private static ExecutorService service;

    public void init() {
        logger.info("监听器启动, 服务端启动, contextInitialized");

        if (serverTask == null) {
            synchronized (CPServerListener.class) {
                if (serverTask == null) {
                    serverTask = new CPServerTask();
                    service = Executors.newFixedThreadPool(1);
                    service.submit(serverTask);
                }
            }
        }
    }

    public void close() {
        logger.info("关闭客户端和服务端, contextDestroyed");
        serverTask.destroyClients();
        serverTask.destroyServer();
    }

}
