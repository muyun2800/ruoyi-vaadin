package com.ruoyi.ui.view.base.auth;

import cn.hutool.core.thread.ThreadUtil;
import com.muyun.vaadin.antd.button.Button;
import com.muyun.vaadin.antd.button.enums.ButtonSize;
import com.muyun.vaadin.antd.button.enums.ButtonType;
import com.muyun.vaadin.antd.button.enums.ButtonVariant;
import com.muyun.vaadin.antd.common.enums.SizeType;
import com.muyun.vaadin.antd.grid.Col;
import com.muyun.vaadin.antd.grid.Row;
import com.muyun.vaadin.antd.icon.Icon;
import com.muyun.vaadin.antd.image.Image;
import com.muyun.vaadin.antd.layout.pro.DefaultFooter;
import com.muyun.vaadin.antd.layout.pro.FooterLink;
import com.muyun.vaadin.antd.notification.Notification;
import com.muyun.vaadin.antd.procomponents.form.LoginForm;
import com.muyun.vaadin.antd.procomponents.form.text.ProFormPassword;
import com.muyun.vaadin.antd.procomponents.form.text.ProFormText;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.framework.web.service.SysLoginService;
import com.ruoyi.ui.view.base.dashboard.Dashboard1View;
import com.ruoyi.ui.vo.LoginVo;
import com.ruoyi.web.controller.common.CaptchaController;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.PropertyId;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.server.auth.NavigationAccessControl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import static com.muyun.vaadin.antd.icon.enums.IconName.*;

/**
 * LoginView2
 *
 * @author 何德海
 * @version 2026/01/27
 **/
@Route("login")
@RouteAlias("")
@AnonymousAllowed
@StyleSheet("style/login.css")
@Slf4j
public class LoginView extends Div {
    BeanValidationBinder<LoginVo> binder = new BeanValidationBinder<>(LoginVo.class);

    @PropertyId("name")
    ProFormText usernameField;

    @PropertyId("password")
    ProFormPassword passwordField;

    @PropertyId("captchaCode")
    ProFormText captchaCodeField;

    LoginForm loginForm;

    Image captchaImage;

    String uuid = "";


    @Autowired
    transient  CaptchaController captchaController;

    @Autowired
    transient SysLoginService loginService;


    private static final String DEFAULT_CAPTCHA_IMAGE = "data:image/png;base64,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";


    public LoginView() {
        // 初始化 UI
        initUI();

        // 初始化行为
        initBehavior();

        // 初始化样式
        initStyle();
    }


    // *****************************************************************************************************************
    //                                      结构
    // *****************************************************************************************************************

    void initUI() {
        loginForm = new LoginForm()
                .setTitle("Ant Design")
                .setSubTitle("Ant Design is the most influential web design specification in Xihu district")
                .setImage(new Image()
                        .setSrc("icon/logo.svg")
                        .setAlt("logo")
                );
        //
        addFormChildren();
        // 额外的登录选项
        addFormActions();

        // 添加Footer
        DefaultFooter footer = createFooter();

        //
        add(loginForm, footer);
    }


    void addFormActions() {
        Span span = new Span("Login With:");
        String iconClassName = "login-actions-icon";
        Icon alipayIcon = Icon.of(ALIPAY_CIRCLE_OUTLINED).setReactClassName(iconClassName);
        Icon taobaoIcon = Icon.of(TAOBAO_CIRCLE_OUTLINED).setReactClassName(iconClassName);
        Icon weiboIcon = Icon.of(WEIBO_CIRCLE_OUTLINED).setReactClassName(iconClassName);
        //
        loginForm.setActions(span, alipayIcon, taobaoIcon, weiboIcon);
    }


    void addFormChildren() {
        // 用户名
        usernameField = new ProFormText()
                .setName("username")
                .setPlaceholder("请输入用户名")
                .setInitialValue("admin");
        usernameField.getField()
                     .setSize(SizeType.LARGE)
                     .setPrefix(Icon.of(USER_OUTLINED));
        // 密码
        passwordField = new ProFormPassword()
                .setName("password")
                .setPlaceholder("请输入密码")
                .setInitialValue("admin123");
        passwordField.getField()
                     .setSize(SizeType.LARGE)
                     .setPrefix(Icon.of(LOCK_OUTLINED));
        // 验证码
        Row captchaRow = new Row();
        {
            Col col1 = new Col().setFlex(2);
            {
                captchaCodeField = new ProFormText()
                        .setPlaceholder("请输入验证码");
                col1.add(captchaCodeField);
            }
            //
            Col col2 = new Col().setFlex(3);
            {
                captchaImage = new Image()
                        .setAlt("验证码")
                        .setPreview(false)
                        .setSrc(DEFAULT_CAPTCHA_IMAGE)
                        .addClickListener(event -> {
                            refreshCaptchaCodeAsync(0);
                        });
                setCaptchaImageStyle(captchaImage);
                col2.add(captchaImage);
            }
            captchaRow.add(col1, col2);
        }
        // 登录按钮
        Button btnSubmit = new Button("登录", ButtonType.PRIMARY)
                .setVariant(ButtonVariant.SOLID)
                .setSize(ButtonSize.LARGE);
        btnSubmit.addClickListener(event -> handleSubmit());
        //
        loginForm.add(usernameField, passwordField, captchaRow, btnSubmit);
    }


    DefaultFooter createFooter() {
        DefaultFooter defaultFooter = new DefaultFooter();
        defaultFooter.setLinks(
                new FooterLink().setTitle("Ant Design Pro").setHref("https://pro.ant.design").setBlankTarget(true),
                new FooterLink().setTitle("Ant Design").setHref("https://ant.design").setBlankTarget(true)
        );
        defaultFooter.setCopyright("2000-2026 xxx公司");
        return defaultFooter;

    }

    // *****************************************************************************************************************
    //                                     行为
    // *****************************************************************************************************************

    void initBehavior() {
        // todo 直接获取有问题，无法被执行, 需要添加延时
        // 获取验证码
        refreshCaptchaCodeAsync(200);
        // 绑定表单field
        binder.bindInstanceFields(this);
    }


    void handleSubmit() {
        try {
            LoginVo loginVo = new LoginVo();
            binder.writeBean(loginVo);
            // 生成令牌
            loginService.login(loginVo.getName(), loginVo.getPassword(), loginVo.getCaptchaCode(), uuid);
            // 登录成功后跳转
            String redirectUrl = getRedirectUrl();
            if (redirectUrl != null) {
                UI.getCurrent().navigate(redirectUrl);
            } else {
                // 跳转到默认界面
                UI.getCurrent().navigate(Dashboard1View.class);
            }
        } catch (Exception e) {
            log.error("登录失败", e);
            Notification.error("登录失败:" + e.getMessage());
            // 刷新验证码
            refreshCaptchaCodeAsync(0);
        }
    }


    /**
     * @return
     */
    private String getRedirectUrl() {
        // 获取重定向URL
        HttpServletRequest httpServletRequest = VaadinServletRequest.getCurrent().getHttpServletRequest();
        HttpSession session = httpServletRequest.getSession();
        return (String) session.getAttribute(NavigationAccessControl.SESSION_STORED_REDIRECT_ABSOLUTE);
    }


    void refreshCaptchaCodeAsync(long delayMillis) {
        UI ui = UI.getCurrent();
        ThreadUtil.execAsync(() -> {
            try {
                ThreadUtil.sleep(delayMillis);
                AjaxResult ajaxResult = captchaController.getCode(null);
                String imgData = "data:image/png;base64," + ajaxResult.get("img");
                ui.access(() -> {
                    captchaImage.setSrc(imgData);
                });
                uuid = (String) ajaxResult.get("uuid");

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    // *****************************************************************************************************************
    //                                    样式
    // *****************************************************************************************************************

    void initStyle() {
        // 样式 -- 页面背景图
        this.getStyle().set("display", "flex")
            .set("flex-direction", "column")
            .set("height", "100vh")
            .set("overflow", "auto")
            .set("background-image", "url('image/login-backgroud.png')")
            .set("background-size", "100% 100%");


        //
        loginForm.getStyle().setPaddingTop("100px");
    }


    void setCaptchaImageStyle(Component component) {
        component.getStyle().set("display", "inline-block")
                 .set("vertical-align", "top")
                 .set("cursor", "pointer")
                 .set("padding-left", "10px")
                 .set("width", "100px");
    }


}