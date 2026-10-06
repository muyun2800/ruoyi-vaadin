package com.ruoyi.ui.view.tool.swagger;


import com.ruoyi.ui.view.MainLayoutView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

/**
 * 系统接口
 *
 * @author muyun
 */
@Route(value = "tool/swagger", layout = MainLayoutView.class)
@PermitAll
public class SwaggerView extends Div {

    public SwaggerView() {
        Div contentDiv = new Div("swagger - 待实现");
        contentDiv.getStyle().setMarginLeft("30px").setMarginTop("20px").setFontSize("16px");
        //
        add(contentDiv);
    }
}
