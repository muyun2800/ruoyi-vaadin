package com.ruoyi.ui.vo;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginVo {

    @NotNull(message = "名字必填")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;


    @NotNull(message = "密码必填")
    @Size(min = 6, max = 50, message = "密码长度必须介于6和50")
    private String password;


   // @Size(min = 8, max = 50, message = "密码长度必须介于8和50")
    private String captchaCode;





}
