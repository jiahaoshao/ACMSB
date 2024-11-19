package net.fangyi.acmsb.service.serviceImpl;

import net.fangyi.acmsb.mapper.UserMapper;
import net.fangyi.acmsb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public void UpdateAvatar(int uid, String avatar) {
        userMapper.updateAvatar(uid, avatar);
    }
}
