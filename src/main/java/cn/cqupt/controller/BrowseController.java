package cn.cqupt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 默认页面
 */
@Controller
public class BrowseController {
    @RequestMapping(value="/index")
    public String toIndex() {
        return "cloudprint";
    }
}
