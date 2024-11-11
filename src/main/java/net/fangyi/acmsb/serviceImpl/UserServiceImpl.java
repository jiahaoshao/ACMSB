package net.fangyi.acmsb.serviceImpl;

import net.fangyi.acmsb.entity.User;
import net.fangyi.acmsb.mapper.UserMapper;
import net.fangyi.acmsb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User findByUser(String uname) {
        User u = userMapper.findByUser(uname);
        return u;
    }

    @Override
    public void register(String uname, String psw) {
        userMapper.add(uname,psw);
    }
}
