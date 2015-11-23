package cn.cqupt.test;

import cn.cqupt.dao.PrintFileDao;
import cn.cqupt.dao.UserDao;
import cn.cqupt.model.PrintFile;
import cn.cqupt.model.User;
import cn.cqupt.service.PrintFileService;
import com.google.common.collect.Maps;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Cbillow on 15/10/29.
 */
public class Test {


    public static void main(String[] args) throws UnsupportedEncodingException {
        ApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"classpath:spring.xml"
                , "classpath:spring-mybatis.xml"});
        UserDao userDao = context.getBean(UserDao.class);
        PrintFileDao printFileDao = context.getBean(PrintFileDao.class);
        PrintFileService printService = context.getBean(PrintFileService.class);

//
//        User user = new User();
//        user.setMobile("18580741650");
//        PrintFile printFile = new PrintFile();
//        printFile.setFilename(new String("dasdasdas历.word".getBytes(), "UTF-8"));
//        printFile.setType(PrintType.WORD.getCode());
//        printFile.setNumber(10);
//        printFile.setOverdueTime(DateUtils.getNowTime());
//        printFile.setSha1("dfsfw12312");
//        printFile.setPath("www.9821398127.com");
//        printFile.setIsDelete(1);
//        printFile.setStatus(0);
//        printFile.setIsColorful(0);
//        printService.addPrintFile(printFile, user);

//        PrintFile file = printFileDao.loadPrintFileBySHA1("h1g2g3g4gg3g21g321h3h1");
//        Date date = new Date();
//        long time1 = date.getTime();
//        long time = file.getOverdueTime().getTime();
//        if (time1 - time >= 24 * 60 * 60 * 1000) {
//            System.out.println("11111");
//        }
//        System.out.println(file.toString());
//        System.out.println(time);

//        HashMap<String, Object> result = printService.deletePrintFile(1, 1);
//        System.out.println(result);

//        HashMap<String, Object> map = printService.updatePrintFile(16, 2);
//        System.out.println(map);

//        List<PrintFile> files = printFileDao.findAllPrintFile(1);
//        System.out.println(files);

//        User user = new User() ;
//        user.setPassword("123333");
//        user.setNickname("cbillow");
////        userDao.updateUser(user);
//        userDao.addUser(user);

//        System.out.println(DateUtils.getNowTime());

//        printService.timingDelete();
//        HashMap<String, Object> param = Maps.newHashMap();
//        param.put("overdueTime", "2015-11-01 00:00:00");
//        param.put("status1", "1");  //已上传
//        param.put("status2", "0");  //待打印
//        List<Integer> pidsBy3Days = printFileDao.findPidsBy3Days(param);
//        System.out.println(pidsBy3Days);

//        User user = new User();
//        user.setMobile("123123");
//        User user1 = userDao.loadUserByMobile(user);
//        System.out.println(user1.toString());

    }
}
