package net.fangyi.acmsb.controller;


import net.fangyi.acmsb.entity.User;
import net.fangyi.acmsb.repository.UserRepository;
import net.fangyi.acmsb.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@CrossOrigin //解决跨域问题
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/update")
    public ResponseEntity<?> update(@RequestParam int uid) {
        User user = userRepository.findByUid(uid);
        if (user == null) {
            return ResponseEntity.ok(Result.error("用户不存在"));
        }
        return ResponseEntity.ok(Result.success("更新成功", user));
    }
}
