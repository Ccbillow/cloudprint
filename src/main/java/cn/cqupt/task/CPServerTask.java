package cn.cqupt.task;

import cn.cqupt.model.Client;
import cn.cqupt.util.CPConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Cbillow on 15/11/24.
 */

public class CPServerTask extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(CPServerTask.class);

    public void run() {
        ServerSocket serverSocket = null;
        int num = 0;
        logger.info("server start.");

        try {
            serverSocket = new ServerSocket(CPConstant.PORT);
            while (true) {
                //使用循环方式一直等待客户端的连接
                num++;
                Socket accept = serverSocket.accept();
                //启动一个新的线程，接管与当前客户端的交互会话
//                new Thread(new ServerThread(accept), "Client " + num).start();

                Client client = new Client(accept);
                CPConstant.clients.put(client.getIp(),client);

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
        Client client;
        HashMap<String,Client> clients = CPConstant.clients;
        Set<String> ips = clients.keySet();
        for(String ip:ips){
            client = clients.get(ip);
            client.close();
        }
    }





    /*********下面可能不用**********/
    /**
     * @author JCC
     *         服务器处理客户端会话的线程
     */
    class ServerThread implements Runnable {

        Socket socket = null;

        public ServerThread(Socket socket) {
            System.out.println("Create a new ServerThread...");
            this.socket = socket;
        }

        public void run() {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = socket.getInputStream();
                out = socket.getOutputStream();
                //使用循环的方式，不停的与客户端交互会话
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //处理客户端发来的数据
                    doRead(in);
                    System.out.println("send Message to client.");
                    //发送数据回客户端
                    doWrite(out);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                    out.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        /**
         * 读取数据
         *
         * @param in
         * @return
         */
        public boolean doRead(InputStream in) {
            //引用关系，不要在此处关闭流
            try {
                byte[] bytes = new byte[in.available()];
                in.read(bytes);
                System.out.println("line:" + new String(bytes).trim());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return true;
        }

        /**
         * 写入数据
         *
         * @param out
         * @return
         */
        public boolean doWrite(OutputStream out) {
            //引用关系，不要在此处关闭流
            try {
                out.write("welcome you client.".getBytes());
                out.flush();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return true;
        }

    }
}
