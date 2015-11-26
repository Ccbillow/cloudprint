package cn.cqupt.task;

import cn.cqupt.model.request.CPClient;
import cn.cqupt.util.CPConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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

    public CPServerTask(){
        isCheckHeartbeat = true;
        /**
         * 对客户端断开做心跳监测
         */
        new Thread(new CheckHeartbeat()).start();
    }

    public void run() {
        ServerSocket serverSocket = null;
        logger.info("server start.");

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
                CPConstant.CLIENTS.put(client.getIp(), client);

                /**
                 * 开始对客户端监听
                 * 每个客户端起一个线程
                 */
                logger.info("client connection success! start listen to the client.");
                new Thread(new CPClientTaskThead(client, this)).start();
            }
        } catch (Exception e) {
            logger.info("server start error:{}", e);
        } finally {
            try {
                serverSocket.close();
                logger.info("server closed.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void destroyClients(){
        logger.info("全部客户端连接清空");
        isCheckHeartbeat = false;
        CPClient client;
        ConcurrentHashMap<String,CPClient> clients = CPConstant.CLIENTS;
        Set<String> ips = clients.keySet();
        for(String ip:ips){
            destroyClient(ip);
        }

    }

    public void destroyClient(CPClient client){
        destroyClient(client.getIp());
    }

    private void destroyClient(String ip){
        CPConstant.CLIENTS.get(ip).close();
        CPConstant.CLIENTS.remove(ip);
    }

    /**
     * 心跳监测
     * 每隔5分钟检测一次，接收不到数据，则断开连接
     */
    class CheckHeartbeat implements Runnable{
        public void run() {
            while(isCheckHeartbeat){
                CPClient client;
                ConcurrentHashMap<String,CPClient> clients = CPConstant.CLIENTS;
                Set<String> ips = clients.keySet();
                for(String ip:ips){
                    client = clients.get(ip);
                    try {
                        client.getOs().write("0".getBytes());
                    } catch (IOException e) {
                        logger.info("客户端断开连接，Disconnect the connection from IP:{}",client.getIp());
                        client.close();
                        clients.remove(client.getIp());
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
}
