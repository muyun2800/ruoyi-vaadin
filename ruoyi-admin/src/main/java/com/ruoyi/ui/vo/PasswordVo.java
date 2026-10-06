package com.ruoyi.ui.vo;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordVo {


    @NotNull(message = "密码必填")
    @Size(min = 8, max = 50, message = "密码长度必须介于8和50")
    private String oldPassword;

    @NotNull(message = "密码必填")
    @Size(min = 8, max = 50, message = "密码长度必须介于8和50")
    private String newPassword;

    @NotNull(message = "密码必填")
    @Size(min = 8, max = 50, message = "密码长度必须介于8和50")
    private String newPassword2;




}
