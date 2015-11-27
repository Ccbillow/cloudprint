package cn.cqupt.model.request;

import cn.cqupt.model.Account;
import cn.cqupt.model.PrintFile;
import cn.cqupt.model.User;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Cbillow on 15/11/27.
 */
public class ClientReq implements Serializable{

    private List<PrintFile> files;

    private Account account;

    private User user;

}
