package com.ruoyi.framework.security.vaadin;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.WrappedSession;
import com.vaadin.flow.server.auth.AccessCheckDecision;
import com.vaadin.flow.server.auth.AccessCheckResult;
import com.vaadin.flow.server.auth.NavigationAccessControl;
import com.vaadin.flow.server.auth.NavigationContext;

/**
 * CustomNavigationAccessControl
 *
 * @author 何德海
 * @version 2026/02/04
 **/
public class CustomNavigationAccessControl extends NavigationAccessControl {

    private String loginUrl = "login";

    private String error403Url = "error/403";


    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        VaadinRequest request = VaadinRequest.getCurrent();
        NavigationContext context = new NavigationContext(event,
                getPrincipal(request), getRolesChecker(request));
        AccessCheckResult result = checkAccess(context,
                isProductionMode(event));
        if (result.decision() != AccessCheckDecision.ALLOW) {
            // 无主体，即未登录
            if (context.getPrincipal() == null) {
                // todo 理解这段代码
                storeRedirectURL(event, request);
                // 跳转到登录页
                event.forwardToUrl(loginUrl);
            }
            // 跳转到无权限页面
            else {
                event.forwardToUrl(error403Url);
            }
        }
    }


    private boolean isProductionMode(BeforeEnterEvent beforeEnterEvent) {
        return beforeEnterEvent.getUI().getSession().getConfiguration()
                               .isProductionMode();

    }


    private void storeRedirectURL(BeforeEnterEvent event, VaadinRequest request) {
        WrappedSession session = request != null ? request.getWrappedSession() : null;
        if (session != null) {
            String servletHostAndPath = getRequestURL(request);
            String viewPathAndParameters = event.getLocation().getPathWithQueryParameters();
            session.setAttribute(SESSION_STORED_REDIRECT, viewPathAndParameters);
            session.setAttribute(SESSION_STORED_REDIRECT_ABSOLUTE, servletHostAndPath + viewPathAndParameters);
        } else {
            if (request == null) {
                System.out.println("Unable to store redirect in session because no request is available");
            }
        }
    }


}