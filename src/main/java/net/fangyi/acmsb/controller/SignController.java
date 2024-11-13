package net.fangyi.acmsb.controller;

import net.fangyi.acmsb.entity.Sign;
import net.fangyi.acmsb.repository.UserRepository;
import net.fangyi.acmsb.result.Result;
import net.fangyi.acmsb.service.SignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.MessageDigest;
import java.util.List;

@RestController
@RequestMapping("/sign")
@CrossOrigin //解决跨域问题
public class SignController {
    //注入userService
    @Autowired
    private SignService signService;

    @Autowired
    private UserRepository userRepository;

    // MD5加密，32位
    public static String MD5(String str) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }
        char[] charArray = str.toCharArray();
        byte[] byteArray = new byte[charArray.length];
        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

    /**
     * 用户注册接口
     *
     * @param username,password 包含用户名和密码的用户对象，通过RequestParam接收前端传来的数据
     * @return 返回注册结果，如果用户名不存在，则返回注册成功结果；否则返回错误信息
     * 对参数没有进行验证，仅用于演示
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestParam String username, @RequestParam String password) {
        username = MD5(username);
        password = MD5(password);
        Sign byUser = signService.findByUsername(username);
        if (byUser == null) {
            signService.register(username, password);
            return ResponseEntity.ok(Result.success("注册成功！"));
        }
        return ResponseEntity.ok(Result.error("用户名已被占用！"));
    }
    /**
     * 用户登录接口
     *
     * @param username,password 包含用户名和密码的用户对象，通过RequestParam接收前端传来的数据
     * @return 返回登录结果，如果用户名正确且密码匹配，则返回登录成功结果；否则返回错误信息
     * 此登陆接口没有涉及对密码的加密，因此仅用于演示，在实际应用中应使用加密技术保护密码
     * 也没有设计token，因此登录成功后，用户无法退出，需要手动关闭浏览器或重新打开浏览器
     */
    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestParam String username, @RequestParam String password) {
        username = MD5(username);
        password = MD5(password);
        Sign byUser = signService.findByUsername(username);
        if (byUser == null) {
            return ResponseEntity.ok(Result.error("用户名不存在"));
        }
        if (password.equals(byUser.getPassword())) {
            return ResponseEntity.ok(Result.success("登录成功！"));
        }
        return ResponseEntity.ok(Result.error("密码错误！"));
    }

    @GetMapping("/findAll")
    public List<Sign> findAll() {
        return userRepository.findAll();
    }
}
