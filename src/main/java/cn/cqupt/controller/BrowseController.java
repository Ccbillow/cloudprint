package cn.cqupt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Cbillow on 15/11/13.
 */
@Controller
public class BrowseController {
    @RequestMapping(value="/index")
    public String toIndex() {
        System.out.println("1111111111111");
        return "uploadFile";
    }
}
