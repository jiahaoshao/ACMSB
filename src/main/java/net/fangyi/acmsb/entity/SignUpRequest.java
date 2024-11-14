package net.fangyi.acmsb.entity;

import lombok.Data;

@Data
public class SignUpRequest {
    private String userAccount;  // 注册账号
    private String password;  // 注册密码
    private String publicKey;  // 公钥
    private String email;  // 邮箱
    private String phone;  // 手机号
}
