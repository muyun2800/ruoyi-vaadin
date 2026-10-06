package com.ruoyi.ui.vo;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.util.List;

@Data
@FieldNameConstants(innerTypeName = "Fields")
public class AssignRoleVo {



    private Long userId;


    private List<Long> roleIds;



}
