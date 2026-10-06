package com.ruoyi.web.controller.system;

import com.ruoyi.common.config.RuoYiConfig;
import com.vaadin.flow.server.DefaultDeploymentConfiguration;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * 首页
 *
 * @author ruoyi
 */
@RestController
public class SysIndexController {
    /**
     * 系统基础配置
     */
    @Autowired
    private RuoYiConfig ruoyiConfig;

    /**
     * 访问首页，提示语
     */
    @RequestMapping("/")
    public void index(HttpServletResponse response) throws IOException {
        // return StringUtils.format("欢迎使用{}后台管理框架，当前版本：v{}，请通过前端地址访问。", ruoyiConfig.getName(), ruoyiConfig.getVersion());
        // todo 做判定， 如果登录了，跳转到后台，否则跳转到登录页面
        response.sendRedirect("/ui/login");

        // todo 判断登录状态


    }
}
