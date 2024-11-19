package net.fangyi.acmsb.service.serviceImpl;

import net.fangyi.acmsb.entity.Sign;
import net.fangyi.acmsb.mapper.SignMapper;
import net.fangyi.acmsb.repository.SignRepository;
import net.fangyi.acmsb.service.SignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SignServiceImpl implements SignService {

    @Autowired
    private SignMapper signMapper;

    @Autowired
    private SignRepository signRepository;

    @Override
    public int SignUp(String username, String password, String email, String phone, String salt) {
        Sign sign = new Sign();
        sign.setUsername(username);
        sign.setPassword(password);
        sign.setEmail(email);
        sign.setPhone(phone);
        sign.setSalt(salt);
        signRepository.save(sign);
        return signRepository.findByUsername(username).getId();
    }

    @Override
    public void resetPassword(String username, String password, String salt) {
        signMapper.updatePassword(username, password, salt);
    }

}
