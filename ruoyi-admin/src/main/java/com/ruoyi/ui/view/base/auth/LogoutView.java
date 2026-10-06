package com.ruoyi.ui.view.base.auth;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("logout")
@AnonymousAllowed
// 标识未 AnonymousAllowed 的页面，可以匿名访问
public class LogoutView extends Div {

    public LogoutView() {
        add(new Div("logout page"));


    }

}