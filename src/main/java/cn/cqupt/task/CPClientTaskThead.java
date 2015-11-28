package cn.cqupt.task;

import cn.cqupt.model.request.CPClient;
import cn.cqupt.util.CPHelps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by LiuMian on 2015/11/25.
 * 每一个客户端连接上来，则起一个线程去监听
 * 用户客户端向服务端写入数据
 */
public class CPClientTaskThead implements Runnable {


    private static final Logger logger = LoggerFactory.getLogger(CPClientTaskThead.class);

    private CPClient client;
    private CPServerTask cpServerTask;

    public CPClientTaskThead(CPClient client, CPServerTask cpServerTask){
        this.client = client;
        this.cpServerTask = cpServerTask;
    }

    public void run() {
        InputStream is = client.getIs();
        int size = -1;
        try {
            while(true){
                byte[] buff = new byte[512];
                is.read(buff);

                //TODO 接受并处理来自客户端的信息
            }
        } catch (IOException e) {
            logger.error("client connection is fail, Disconnect the connection from IP:{}, e:{}", client.getIp(), e.getMessage());
            cpServerTask.destroyClient(client);
        }
    }


}
