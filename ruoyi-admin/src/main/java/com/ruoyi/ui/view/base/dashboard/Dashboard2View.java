package com.ruoyi.ui.view.base.dashboard;


import com.ruoyi.ui.view.MainLayoutView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

/**
 * @author muyun
 */
@PageTitle("数据看板2")
@Route(value = "dashboard/dashboard2", layout = MainLayoutView.class)
@PermitAll  // 允许登录的用户访问
public class Dashboard2View extends Div {


    public Dashboard2View() {
        // nop
    }
}
