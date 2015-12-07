package cn.cqupt.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;

/**
 * Created by Cbillow on 15/12/3.
 */
public class SpringUtil implements ApplicationContextAware, ServletContextAware {

    private static ApplicationContext applicationContext = null;
    public static ServletContext context;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtil.applicationContext = applicationContext;
    }

    public static ServletContext getContext() {
        return context;
    }

    public void setServletContext(ServletContext context) {
        System.out.println("===============================");

        SpringUtil.context = context;
    }

    public static Object getBean(String name){ return applicationContext.getBean(name); }

    public static <T> T getBean(String name, Class<T> type) {
        return (T) applicationContext.getBean(name);
    }

}
