package com.ruoyi.ui.view.monitor;


import com.ruoyi.ui.view.MainLayoutView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

/**
 * 定时任务
 *
 * @author muyun
 */
@Route(value = "monitor/job", layout = MainLayoutView.class)
@PermitAll
public class JobView extends Div {

    public JobView() {
        Div contentDiv = new Div("job - 待实现");
        contentDiv.getStyle().setMarginLeft("30px").setMarginTop("20px").setFontSize("16px");
        //
        add(contentDiv);
    }
}
