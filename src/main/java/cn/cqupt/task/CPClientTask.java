package cn.cqupt.task;

import cn.cqupt.model.Client;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by LiuMian on 2015/11/25.
 * 客户端线程
 */
public class CPClientTask implements Runnable {

    private Client client;
    private CPServerTask cpServerTask;

    public CPClientTask(Client client,CPServerTask cpServerTask){
        this.client = client;
        this.cpServerTask = cpServerTask;
    }

    public void run() {
        InputStream is = client.getIs();
        int size = -1;
        try {
            while(true){
                byte[] buff = new byte[is.available()];
                is.read(buff);
            }
        } catch (IOException e) {
            e.printStackTrace();
            cpServerTask.destroyClient(client);
        }
    }


}
