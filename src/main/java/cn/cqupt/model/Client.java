package cn.cqupt.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by LiuMian on 2015/11/24.
 */
public class Client {

    private Socket socket;
    private InputStream is;
    private OutputStream os;
    private String ip;

    public Client(Socket socket){
        this.socket = socket;
        try {
            is = socket.getInputStream();
            os = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ip = socket.getInetAddress().getHostAddress();
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public InputStream getIs() {
        return is;
    }

    public void setIs(InputStream is) {
        this.is = is;
    }

    public OutputStream getOs() {
        return os;
    }

    public void setOs(OutputStream os) {
        this.os = os;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void close(){
        if(is != null){
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(os != null){
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(socket != null){
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
