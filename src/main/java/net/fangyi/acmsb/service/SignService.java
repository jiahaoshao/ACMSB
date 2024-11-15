package net.fangyi.acmsb.service;

import net.fangyi.acmsb.entity.Sign;

public interface SignService {
    //根据用户名查询数据库
    Sign findByUsername(String uname);

    //注册账户
    long SignUp(String username, String password, String email, String phone, String salt);

    //重置密码
    void resetPassword(String username, String password, String salt);
}
