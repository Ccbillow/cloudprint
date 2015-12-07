package cn.cqupt.task;

import cn.cqupt.model.PrintFile;
import cn.cqupt.model.User;
import cn.cqupt.model.request.CPClient;
import cn.cqupt.model.request.ClientReq;
import cn.cqupt.util.CPConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by LiuMian on 2015/11/25.
 * 每一个客户端连接上来，则起一个线程去监听
 * 用户客户端向服务端写入数据
 * 服务端的处理线程
 */
public class CPServerHandle implements Runnable {


    private static final Logger logger = LoggerFactory.getLogger(CPServerHandle.class);

    private CPClient client;
    private CPServerTask cpServerTask;

    public CPServerHandle(CPClient client, CPServerTask cpServerTask) {
        this.client = client;
        this.cpServerTask = cpServerTask;
        logger.info("用户" + client.getIp() + "接入");
    }

    public void run() {
        ObjectInputStream ois = null;
        ObjectOutputStream oos = null;
        try {
            ois = new ObjectInputStream(new BufferedInputStream(client.getIs()));

            Object obj = ois.readObject();
            if (obj != null) {
                ClientReq req = (ClientReq) obj;

                /**
                 * 如果传过来md5Code为over，就断开客户端连接
                 */
                if (req.getMd5Code().trim().equalsIgnoreCase("!over")) {
                    logger.error("客户端与服务器断开连接，接收到 !over, Disconnect the connection from IP:{}", client.getIp());
                    cpServerTask.destroyClient(client);
                    return;
                }

                /**
                 * 当data为md5Code时，将md5Code放入全局hashmap中
                 */
                CPConstant.CLIENTS.put(req.getMd5Code(), client);
                client.setMd5Code(req.getMd5Code());
                Iterator<Map.Entry<String, CPClient>> iterator = CPConstant.CLIENTS.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, CPClient> entry = iterator.next();
                    String key = entry.getKey();
                    System.out.println(key);
                }

                logger.info("读取到客户端发送来的数据 data:{}", req.getMd5Code());
                System.out.println("读取到客户端发送的来数据：" + req.getMd5Code());


                User user = new User();
                user.setWeixin("3123123");
                PrintFile file1 = new PrintFile();
                file1.setFilename("dasda");
                file1.setPath("www.badiu.com");
                PrintFile file2 = new PrintFile();
                file2.setFilename("21312");
                file2.setPath("www.dasda.com");
                ArrayList<PrintFile> files = new ArrayList<PrintFile>();

                ClientReq response = new ClientReq();
                response.setUser(user);
                response.setFiles(files);
                oos = new ObjectOutputStream(client.getOs());
                oos.writeObject(response);
                oos.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                ois.close();
                oos.close();
                cpServerTask.destroyClient(client);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
