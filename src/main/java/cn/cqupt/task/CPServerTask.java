package cn.cqupt.task;

import cn.cqupt.model.request.CPClient;
import cn.cqupt.util.CPConstant;
import cn.cqupt.util.CPHelps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Created by Cbillow on 15/11/24.
 */

public class CPServerTask implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(CPServerTask.class);
    private boolean isCheckHeartbeat;
    ServerSocket serverSocket = null;

    public CPServerTask(){
        isCheckHeartbeat = true;
        /**
         * 对客户端断开做心跳监测
         */
//        new Thread(new CheckHeartbeat()).start();
    }

    public void run() {
        logger.info("服务器启动, 等待客户端连接，监听本机" + CPConstant.PORT + "端口");
        System.out.println("服务器端启动，监听4347端口");

        try {
            serverSocket = new ServerSocket(CPConstant.PORT);

            while (isCheckHeartbeat) {

                /**
                 * 使用循环方式一直等待客户端的连接
                 */
                Socket accept = serverSocket.accept();

                /**
                 * 客户端连接成功
                 */
                CPClient client = new CPClient(accept);
                logger.info("client connection success! IP:{}", client.getIp());
                System.out.println("客户端连接成功! IP : " + client.getIp());

                /**
                 * 开始对客户端监听
                 * 每个客户端起一个线程
                 */
//                logger.info("client connection success! start listen to the client.");
                new Thread(new CPServerHandle(client, this)).start();

            }
        } catch (Exception e) {
            if (e.getMessage().equalsIgnoreCase("Socket closed")) {
                logger.info("服务器已经关闭，请重新开启服务器");
                return;
            }
            logger.info("服务器出现异常，server is e:{}", e);
        } finally {
            try {
                logger.info("服务端等待客户端连接出现异常，将服务关闭");
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void destroyServer() {
        try {
            logger.info("服务端关闭连接");
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void destroyClients(){
        logger.info("全部客户端连接清空：");
        isCheckHeartbeat = false;
        ConcurrentHashMap<String,CPClient> clients = CPConstant.CLIENTS;
        Set<String> md5Codes = clients.keySet();
        for(String md5Code:md5Codes){
            logger.info("依次对每个客户端清空，客户端连接清空 md5Code:{}", md5Code);
            destroyClient(md5Code);
        }

    }

    public void destroyClient(CPClient client){
        destroyClient(client.getMd5Code());
    }

    private void destroyClient(String md5Code){
        CPConstant.CLIENTS.get(md5Code).close();
        CPConstant.CLIENTS.remove(md5Code);
    }

    /**
     * 心跳监测
     * 每隔5分钟检测一次，接收不到数据，则断开连接
     */
    class CheckHeartbeat implements Runnable{
        public void run() {
            while(isCheckHeartbeat){
                CPClient client;
                logger.info("开始对客户端做心跳监测，如果连接的客户端没有接收到数据，则将这个连接断开");
                ConcurrentHashMap<String,CPClient> clients = CPConstant.CLIENTS;
                Set<String> md5Codes = clients.keySet();
                logger.info("心跳监测：依次对以下IP进行检测。 md5Codes:{}", md5Codes);
                for(String md5Code:md5Codes){
                    client = clients.get(md5Code);
                    try {
                        client.getOs().write("0".getBytes());
                    } catch (IOException e) {
                        logger.info("客户端断开连接，Disconnect the connection from IP:{}",client.getIp());
                        destroyClient(client);
                    }
                }
                try {
                    //每五分钟进行一次心跳检测
                    Thread.sleep(5 * 60 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        CPServerTask serverTask = new CPServerTask();
        serverTask.run();
    }
}
