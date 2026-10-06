package com.ruoyi.ui.view.base.exception;

import com.muyun.vaadin.antd.button.Button;
import com.muyun.vaadin.antd.button.enums.ButtonType;
import com.muyun.vaadin.antd.result.Result;
import com.muyun.vaadin.antd.result.enums.ResultStatus;
import com.ruoyi.ui.view.base.auth.LoginView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.servlet.http.HttpServletResponse;


/**
 * @author 何德海
 * @version 2026/02/04
 **/
@Route("error/500")
@AnonymousAllowed
public class Error500View extends Div implements HasErrorParameter<Exception> {


    Result result;

    public Error500View() {
        result = new Result(ResultStatus.ERROR_500);
        result.setTitle("500");
        result.setSubTitle("抱歉, 服务端内部错误,请联系管理员.");
        Div extraDiv = new Div();
        {
            Button button1 = new Button("返回首页", ButtonType.PRIMARY);
            button1.addClickListener(clickEvent -> {
                UI.getCurrent().navigate(LoginView.class);
            });
            extraDiv.add(button1);
        }
        result.setExtra(extraDiv);
        super.add(result);

    }


    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<Exception> parameter) {
        // 在 500页面设置错误信息
        parameter.getException().printStackTrace();
        return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
    }
}