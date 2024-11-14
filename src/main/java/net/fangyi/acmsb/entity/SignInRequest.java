package net.fangyi.acmsb.entity;

import lombok.Data;

@Data
public class SignInRequest {
    /**
     * 登录账号
     */
    private String userAccount ;

    /**
     * 密码
     */
    private String password;
    /**
     * 公钥
     */
    private String publicKey;

    /**
     * 新密码
     */
    private String newPassword;
}

