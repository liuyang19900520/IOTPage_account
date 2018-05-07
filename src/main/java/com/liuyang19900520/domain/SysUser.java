package com.liuyang19900520.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiResponse;
import lombok.Data;

import java.util.Date;

/**
 * @author liuya
 */
@ApiModel
@Data
public class SysUser {
    private Long id;

    private String mobilePhone;

    private String userName;

    private String nickname;

    private String password;

    private String salt;

    private String signature;

    private Boolean gender;

    private Long qq;

    private String email;

    private String avatar;

    private String province;

    private String city;

    private String regIp;

    private Integer score;

    private Integer status;

    private Long createBy;

    private Date createAt;

    private Long updateBy;

    private Date updateAt;
    @ApiModelProperty(notes = "The token of the product", required = true)
    private String token;
    @ApiModelProperty(notes = "The refreshToken of the product", required = true)
    private String refreshToken;


}