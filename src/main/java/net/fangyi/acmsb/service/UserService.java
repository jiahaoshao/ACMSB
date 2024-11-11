package net.fangyi.acmsb.service;

import net.fangyi.acmsb.entity.User;

public interface UserService {
    //根据用户名查询数据库
    User findByUser(String uname);
    //注册  将用户名和密码添加到数据库中
    void register(String uname, String psw);
}
