package cn.cqupt.listener;

import cn.cqupt.task.CPServerTask;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by LiuMian on 2015/11/24.
 * ”√”⁄∆Ù∂ØCPServerTask
 */
public class CPServerListener implements ServletContextListener {

    private CPServerTask serverTask;

    public void contextInitialized(ServletContextEvent sce) {
        serverTask = new CPServerTask();
        serverTask.start();
    }

    public void contextDestroyed(ServletContextEvent sce) {
        serverTask.destroyClients();
    }
}
