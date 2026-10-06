package com.ruoyi.ui.view.monitor.druid;


import com.ruoyi.ui.view.MainLayoutView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.IFrame;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

/**
 * 数据源监控
 *
 * @author muyun
 */
@Route(value = "monitor/druid", layout = MainLayoutView.class)
@PermitAll
public class DruidView extends Div {

    public DruidView() {
        IFrame iFrame = new IFrame();
        iFrame.setSrc("/druid/login.html");
        iFrame.setSizeFull();

        //
        add(iFrame);
        this.setSizeFull();
    }
}
