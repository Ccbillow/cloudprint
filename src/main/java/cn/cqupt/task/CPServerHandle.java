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
    private CPServerTask cpServerTask;
    private static ObjectOutputStream oos;

    public CPServerHandle(CPClient client, CPServerTask cpServerTask) {
        this.client = client;
        this.cpServerTask = cpServerTask;
        logger.info("用户：" + client.getIp() + "接入");
    }

    public void run() {
        Object obj;
        ObjectInputStream ois;
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
                        cpServerTask.destroyClient(client);
                        return;
                    }

                    /**
                     * 当data为md5Code时，将md5Code放入全局hashmap中
                     */
                    CPConstant.CLIENTS.put(req.getMd5Code(), client);
                    client.setMd5Code(req.getMd5Code());
                    logger.info("读取到客户端发送来的数据 Md5Code:{}, client:{}", req.getMd5Code(), client.getIp());
                }

                /**
                 * 休眠3秒
                 */
                Thread.sleep(3000);

                /*ClientReq request = new ClientReq();
                User user = new User();
                user.setNickname("谢谢谢谢谢谢谢、");
                user.setWeixin("oFVKgjn3AuOnMhjaq9ud1QtQUYCI");
                PrintFile pf1 = new PrintFile();
                pf1.setFilename("云打印协议.docx");
                pf1.setPath("http://cquptcloudprint.oss-cn-hangzhou.aliyuncs.com/oFVKgjn3AuOnMhjaq9ud1QtQUYCI/个人简历-李鑫其.doc");
                pf1.setIsColorful(0);
                pf1.setNumber(2);
                PrintFile pf2 = new PrintFile();
                pf2.setFilename("Baby.docx");
                pf2.setPath("http://cquptcloudprint.oss-cn-hangzhou.aliyuncs.com/oFVKgjn3AuOnMhjaq9ud1QtQUYCI/Baby.docx");
                pf2.setIsColorful(0);
                pf2.setNumber(2);
                ArrayList<PrintFile> files = new ArrayList<PrintFile>();
                files.add(pf1);
                files.add(pf2);
                request.setFiles(files);
                request.setUser(user);
                request.setSuccess(true);
                request.setMd5Code(req.getMd5Code());
                System.out.println("-------发送数据---------");
                CommonRes<String> commonRes=null;
                for (int i = 0; i < 2; i++) {
                    commonRes = writeObjectToClient(request, request.getMd5Code());
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println(commonRes);*/
            }
        } catch (EOFException ie) {
            System.out.println("客户端关闭连接 e:" + ie.getMessage());
            logger.error("客户端关闭连接 e:{}" + ie);
            cpServerTask.destroyClient(client);
        } catch (SocketException ee) {
            logger.error("客户端关闭连接 e:{}" + ee);
            System.out.println("客户端关闭连接 e:" + ee.getMessage());
            cpServerTask.destroyClient(client);
        } catch (IOException e) {
            e.printStackTrace();
            cpServerTask.destroyClient(client);
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
        try {
            if (oos == null) {
                oos = new ObjectOutputStream(client.getOs());
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
