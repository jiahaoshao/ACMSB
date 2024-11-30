package net.fangyi.acmsb.controller;


import net.fangyi.acmsb.entity.User;
import net.fangyi.acmsb.repository.UserRepository;
import net.fangyi.acmsb.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    /**
     * 根据uid查询用户信息
     * @param uid
     * @return
     */

    @GetMapping("/finduserbyuid")
    public ResponseEntity<?> findUserByUid(@RequestParam int uid) {
        User user = userRepository.findByUid(uid);
        if (user == null) {
            return ResponseEntity.ok(Result.error("用户不存在"));
        }
        return ResponseEntity.ok(Result.success("查询成功", user));
    }
}
