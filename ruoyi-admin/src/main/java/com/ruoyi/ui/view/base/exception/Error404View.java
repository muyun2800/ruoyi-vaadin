package com.ruoyi.ui.view.base.exception;

import com.muyun.vaadin.antd.button.Button;
import com.muyun.vaadin.antd.button.enums.ButtonType;
import com.muyun.vaadin.antd.result.Result;
import com.muyun.vaadin.antd.result.enums.ResultStatus;
import com.ruoyi.ui.view.base.auth.LoginView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.servlet.http.HttpServletResponse;


/**
 * @author 何德海
 * @version 2026/02/04
 **/
@Route("error/404")
@AnonymousAllowed
public class Error404View extends Div implements HasErrorParameter<NotFoundException> {

    public Error404View() {
        Result result = new Result(ResultStatus.ERROR_404);
        result.setTitle("404");
        result.setSubTitle("抱歉, 您访问的页面不存在.");
        Div extraDiv = new Div();
        {
            Button button1 = new Button("返回首页", ButtonType.PRIMARY);
            button1.addClickListener(clickEvent -> {
                UI.getCurrent().navigate(LoginView.class);
            });
            extraDiv.add(button1);
        }
        result.setExtra(extraDiv);
        //
        add(result);
    }

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {
        return HttpServletResponse.SC_NOT_FOUND;
    }


}