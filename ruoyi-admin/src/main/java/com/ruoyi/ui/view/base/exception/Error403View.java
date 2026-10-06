package com.ruoyi.ui.view.base.exception;

import com.muyun.vaadin.antd.button.Button;
import com.muyun.vaadin.antd.button.enums.ButtonType;
import com.muyun.vaadin.antd.result.Result;
import com.muyun.vaadin.antd.result.enums.ResultStatus;
import com.ruoyi.ui.view.base.auth.LoginView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;


/**
 *
 *
 * @author 何德海
 * @version 2026/02/04
 **/
@Route("error/403")
@AnonymousAllowed
public class Error403View extends Div {



    public Error403View() {
        Result result = new Result(ResultStatus.ERROR_403);
        result.setTitle("403");
        result.setSubTitle("抱歉, 您没有权限访问此页面.");
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



}