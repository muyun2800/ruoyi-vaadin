package com.ruoyi.framework.config;

import com.ruoyi.framework.security.handle.LogoutSuccessHandlerImpl;
import com.ruoyi.framework.security.vaadin.CustomNavigationAccessControl;
import com.vaadin.flow.server.auth.NavigationAccessControl;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * spring security配置
 *
 * @author ruoyi
 */
//@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
@EnableWebSecurity
@Configuration
public class SecurityConfig extends VaadinWebSecurity {

    /**
     * 退出处理类
     */
    @Autowired
    private LogoutSuccessHandlerImpl logoutSuccessHandler;


    /**
     * 允许匿名访问的地址
     */
    // @Autowired
    // private PermitAllUrlProperties permitAllUrl;

    /**
     * 身份验证实现
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    /**
     * anyRequest          |   匹配所有请求路径
     * access              |   SpringEl表达式结果为true时可以访问
     * anonymous           |   匿名可以访问
     * denyAll             |   用户不能访问
     * fullyAuthenticated  |   用户完全认证可以访问（非remember-me下自动登录）
     * hasAnyAuthority     |   如果有参数，参数表示权限，则其中任何一个权限可以访问
     * hasAnyRole          |   如果有参数，参数表示角色，则其中任何一个角色可以访问
     * hasAuthority        |   如果有参数，参数表示权限，则其权限可以访问
     * hasIpAddress        |   如果有参数，参数表示IP地址，如果用户IP和参数匹配，则可以访问
     * hasRole             |   如果有参数，参数表示角色，则其角色可以访问
     * permitAll           |   用户可以任意访问
     * rememberMe          |   允许通过remember-me登录的用户访问
     * authenticated       |   用户登录后可访问
     */
    /**
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> {
            // vaadin 资源
            auth.requestMatchers("/login", "/register", "/captchaImage",
                        "/ui/**", "/icon/**", "/image/**",   "/favicon.ico")
                .permitAll()
                // 静态资源，可匿名访问
                .requestMatchers(HttpMethod.GET, "/", "/*.html", "/**.html", "/style/**", "/**.css", "/**.js", "/profile/**")
                .permitAll()
                // swagger
                .requestMatchers("/swagger-ui.html", "/v3/api-docs/**", "/swagger-ui/**")
                .permitAll();

        });
        //
        //  frameOptions检验, 允许同源请求（即：页面只能被本站页面嵌入到iframe）
        http.headers().frameOptions().sameOrigin();
        http.securityContext(context -> context.requireExplicitSave(false));
        http.logout(logout -> logout.logoutSuccessHandler(logoutSuccessHandler));
        // em 父类的configure方法会对http进行了一些配置，这些配置可能会覆盖上面对http的配置, 导致上面的配置失效。
        super.configure(http);
    }


    /**
     * 配置资源不被拦截
     *
     * @return
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/druid/**", "/system/user/profile/avatar2");
    }


    /**
     * 强散列哈希加密实现
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


    /**
     * 自定义【导航权限权限控制】实现
     *
     * @return
     */
    @Bean
    @Primary
    public NavigationAccessControl navigationAccessControl2() {
        return new CustomNavigationAccessControl();
    }


}
