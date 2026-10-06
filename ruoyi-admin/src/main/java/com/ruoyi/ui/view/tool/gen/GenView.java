package com.ruoyi.ui.view.tool.gen;


import com.ruoyi.ui.view.MainLayoutView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

/**
 * 代码生成
 *
 * @author muyun
 */
@Route(value = "tool/gen", layout = MainLayoutView.class)
@PermitAll
public class GenView extends Div {

    public GenView() {
        Div contentDiv = new Div("gen - 待实现");
        contentDiv.getStyle().setMarginLeft("30px").setMarginTop("20px").setFontSize("16px");
        //
        add(contentDiv);
    }
}
