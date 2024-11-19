package net.fangyi.acmsb.service;

public interface SignService {

    //注册账户
    int SignUp(String username, String password, String email, String phone, String salt);

    //重置密码
    void resetPassword(String username, String password, String salt);
}
