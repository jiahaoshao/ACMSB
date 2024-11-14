package net.fangyi.acmsb.serviceImpl;

import net.fangyi.acmsb.entity.Sign;
import net.fangyi.acmsb.mapper.SignMapper;
import net.fangyi.acmsb.service.SignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SignServiceImpl implements SignService {

    @Autowired
    private SignMapper signMapper;

    @Override
    public Sign findByUsername(String uname) {
        Sign u = signMapper.findByUsername(uname);
        return u;
    }

    @Override
    public void SignUp(String username, String password, String email, String phone, String salt) {
        signMapper.signup(username, password, email, phone, salt);
    }

    @Override
    public void resetPassword(String username, String password, String salt) {
        signMapper.updatePassword(username, password, salt);
    }

}
