package com.ruoyi.ui.view.tool.build;


import com.ruoyi.ui.view.MainLayoutView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

/**
 * 表单构建
 *
 * @author muyun
 */
@Route(value = "tool/build", layout = MainLayoutView.class)
@PermitAll
public class BuildView extends Div {

    public BuildView() {
        Div contentDiv = new Div("build - 待实现");
        contentDiv.getStyle().setMarginLeft("30px").setMarginTop("20px").setFontSize("16px");
        //
        add(contentDiv);
    }
}
