package cn.cqupt.task;

import cn.cqupt.model.Client;
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

public class CPServerTask extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(CPServerTask.class);
    private boolean isCheckHeartbeat;
    private ExecutorService executorService;

    public CPServerTask(){
        isCheckHeartbeat = true;
        executorService = Executors.newCachedThreadPool();
        new Thread(new CheckHeartbeat()).start();
    }

    public void run() {
        ServerSocket serverSocket = null;
        logger.info("server start.");

        try {
            serverSocket = new ServerSocket(CPConstant.PORT);
            while (isCheckHeartbeat) {
                //使用循环方式一直等待客户端的连接
                Socket accept = serverSocket.accept();

                Client client = new Client(accept);
                CPConstant.CLIENTS.put(client.getIp(),client);
                executorService.submit(new CPClientTask(client,this));

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
        isCheckHeartbeat = false;
        Client client;
        ConcurrentHashMap<String,Client> clients = CPConstant.CLIENTS;
        Set<String> ips = clients.keySet();
        for(String ip:ips){
            destroyClient(ip);
        }

    }

    public void destroyClient(Client client){
        destroyClient(client.getIp());
    }

    private void destroyClient(String ip){
        CPConstant.CLIENTS.get(ip).close();
        CPConstant.CLIENTS.remove(ip);
    }

    //心跳检测
    class CheckHeartbeat implements Runnable{
        public void run() {
            while(!isCheckHeartbeat){
                Client client;
                ConcurrentHashMap<String,Client> clients = CPConstant.CLIENTS;
                Set<String> ips = clients.keySet();
                for(String ip:ips){
                    client = clients.get(ip);
                    try {
                        client.getOs().write("0".getBytes());
                    } catch (IOException e) {
                        logger.info("客户端断开连接，ip{}",client.getIp());
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
