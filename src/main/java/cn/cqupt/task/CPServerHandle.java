package cn.cqupt.task;

import cn.cqupt.model.CommonRes;
import cn.cqupt.model.request.CPClient;
import cn.cqupt.model.request.ClientReq;
import cn.cqupt.util.CPConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.SocketException;

/**
 * Created by LiuMian on 2015/11/25.
 * 每一个客户端连接上来，则起一个线程去监听
 * 用户客户端向服务端写入数据
 * 服务端的处理线程
 */
public class CPServerHandle implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(CPServerHandle.class);
    private CPClient client;
    private static ObjectOutputStream oos;
    private ObjectInputStream ois;

    public CPServerHandle(CPClient client) {
        this.client = client;
        logger.info("用户：" + client.getIp() + "接入");
    }

    public void run() {
        Object obj;
        ClientReq req = null;
        try {
            ois = new ObjectInputStream(new BufferedInputStream(client.getIs()));
            oos = new ObjectOutputStream(new BufferedOutputStream(client.getOs()));

            while (true) {
                /**
                 * 等待从客户端传来数据
                 */
                obj = ois.readObject();
                if (obj != null) {
                    req = (ClientReq) obj;

                    /**
                     * 当且仅当 errcode=4且errmsg为 "!over"时关闭这个客户端的连接
                     */
                    if (req.getErrCode() == 4 && req.getErrMsg().trim().equalsIgnoreCase("!over")) {
                        logger.error("客户端与服务器断开连接，接收到 !over, Disconnect the connection from IP:{}", client.getIp());
                        System.out.println("接收到关闭消息 !over ,关闭客户端");
                        ois.close();
                        /**
                         * 关闭客户端后，需要将连接重置
                         */
                        oos.reset();
                        return;
                    }

                    /**
                     * 当data为md5Code时，将md5Code放入全局hashmap中
                     */
                    CPConstant.CLIENTS.put(req.getMd5Code(), client);
                    client.setMd5Code(req.getMd5Code());
                    logger.info("读取到客户端发送来的数据 Md5Code:{}, client:{}", req.getMd5Code(), client.getIp());
//                    System.out.println("当data为md5Code时，将md5Code放入全局hashmap中");
                }

                /**
                 * 休眠3秒
                 */
                Thread.sleep(3000);
            }
        } catch (EOFException ie) {
            logger.error("客户端关闭连接 e:{}" + ie);
//            System.out.println("客户端关闭连接 e:" + ie.getMessage());
            client.close();
            CPConstant.CLIENTS.remove(client.getMd5Code());
        } catch (SocketException ee) {
            logger.error("客户端关闭连接 e:{}" + ee);
//            System.out.println("客户端关闭连接 e:" + ee.getMessage());
            client.close();
            CPConstant.CLIENTS.remove(client.getMd5Code());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 发送消息给客户端
     *
     * @param req
     * @return
     */
    public static CommonRes<String> writeObjectToClient(ClientReq req, CPClient client) {
        CommonRes<String> response = new CommonRes<String>();
        response.setSuccess(false);
        try {
            if (oos == null) {
                oos = new ObjectOutputStream(new BufferedOutputStream(client.getOs()));
            }
            logger.info("发送对象给客户端:{},  发送的信息:ClientReq:{}", client.getIp(),  req);
            oos.writeObject(req);
            oos.flush();
        } catch (IOException e) {
            logger.error("打印文件传送到客户端出错， 错误信息:{}", e);
            response.setErrorMsg("向客户端写入出错-----" + e.getMessage());
            return response;
        }
        logger.info("printing file writeObjectToClient success, client:{}", client.getIp());
        response.setSuccess(true);
        return response;
    }

}
