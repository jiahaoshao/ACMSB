package net.fangyi.acmsb.controller;

import com.alibaba.fastjson.JSONObject;
import io.micrometer.common.util.StringUtils;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletRequest;
import net.fangyi.acmsb.AcmsbApplication;
import net.fangyi.acmsb.Util.DateUtil;
import net.fangyi.acmsb.Util.RSAUtils;
import net.fangyi.acmsb.Util.TokenUtil;
import net.fangyi.acmsb.entity.*;
import net.fangyi.acmsb.repository.SignRepository;
import net.fangyi.acmsb.repository.UserRepository;
import net.fangyi.acmsb.result.Result;
import net.fangyi.acmsb.service.SignService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/sign")
@CrossOrigin //解决跨域问题

/**
 *
 * SignController 用于处理用户注册、登录、重设密码等功能
 */
public class SignController {
    private static final Logger logger = LoggerFactory.getLogger(SignController.class);
    //注入SignService
    @Autowired
    private SignService signService;

    @Autowired
    private SignRepository signRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    JavaMailSender javaMailSender; //可直接注入邮件发送的对象

    @Value("${spring.mail.username}") //动态获取配置文件中 发送邮件的账户
    private String sendemail;

    /**
     * 获取公钥
     *
     * @param request   HttpServletRequest对象
     * @return
     */
    @ApiOperation(value = "登陆，获取公钥", httpMethod = "GET")
    @GetMapping("/getkey")
    public ResponseEntity<?> getKey(HttpServletRequest request) {
        RSAKey key = generateKey(request);
        addAdminUser(); //添加管理员账号
        return ResponseEntity.ok(Result.success("Key", key));
    }
    /**
     * 生成随机码
     * @param request HttpServletRequest对象
     */
    private RSAKey generateKey(HttpServletRequest request) {
        Map<String, Object> keyMap = RSAUtils.genKeyPair();
        String publicKey = RSAUtils.getPublicKey(keyMap);
        String privateKey = RSAUtils.getPrivateKey(keyMap);
        request.getSession().setAttribute("privateKey", privateKey);
        logger.info("generatePublicKey: " + publicKey);
        logger.info("generatePrivateKey: " + privateKey);
        RSAKey rsaKey = new RSAKey(StringUtils.isBlank(publicKey) ? "" : publicKey, StringUtils.isBlank(privateKey) ? "" : privateKey);
        return rsaKey;
    }

    /**
     * 登录
     * 此处简化，根据自己业务自定
     * @param request     HttpServletRequest对象
     * @param signInRequest      登录请求体
     */
    @ApiOperation(value = "登录", httpMethod = "POST")
    @PostMapping(value = "/signin")
    public ResponseEntity<?> signin(HttpServletRequest request, @RequestBody SignInRequest signInRequest) {
        String privateKey = (String) request.getSession().getAttribute("privateKey");
        logger.info("signin privateKey: {}", privateKey);
        String account = signInRequest.getUserAccount();
        // 获取登录人信息
        Sign sysUser = signRepository.findByUsername(account);

        if(sysUser == null) {
            return ResponseEntity.ok(Result.error("用户不存在"));
        }

        //从前端获取的密码
        String inputPassword = signInRequest.getPassword();
        if(inputPassword.isEmpty()) {
            return ResponseEntity.ok(Result.error("密码不能为空！"));
        }
        // 密码解密
        String decryptPassword = RSAUtils.decryptDataOnJava(inputPassword, privateKey);
        logger.info("signin username:{}", account);
        logger.info("signin inputpassword:{}", inputPassword);
        logger.info("signin decryptPassword: {}", decryptPassword);

        // 输入和db存入的密码对比， 盐
        boolean isMatch = RSAUtils.hashPasswordIsMatch(sysUser.getPassword(), decryptPassword, sysUser.getSalt());
        //配置token
        JSONObject jsonObject = new JSONObject();
        if(isMatch) {
            User user = userRepository.findByUid(sysUser.getId() + 1000000);

            String token = TokenUtil.sign(user);
            jsonObject.put("token", token);
            jsonObject.put("user", user);
            jsonObject.put("message", "登录成功");
            jsonObject.put("code", 200);
            return ResponseEntity.ok(jsonObject);
        }else {
            jsonObject.put("message", "账号或密码错误");
            jsonObject.put("code", 500);
            return ResponseEntity.ok(jsonObject);
        }

    }

    /**
     * 重设密码
     * @param request     HttpServletRequest对象
     * @param signUpRequest       登录请求体
     */
    @ApiOperation(value = "重设密码", httpMethod = "POST")
    @PostMapping(value = "/resetpassword")
    public ResponseEntity<?> updatePasswordCostDashboard(HttpServletRequest request, @RequestBody SignUpRequest signUpRequest) {
        String privateKey = (String) request.getSession().getAttribute("privateKey");
        String account = signUpRequest.getUserAccount();
        String password = signUpRequest.getPassword();
        String email = signUpRequest.getEmail();
        Sign sysUser = signRepository.findByUsername(account);
        if(sysUser == null) {
            return ResponseEntity.ok(Result.error("用户不存在!"));
        }
        if(!email.equals(sysUser.getEmail())){
            return ResponseEntity.ok(Result.error("请检查邮箱是否正确！"));
        }
        // 解密后的密码
        String DecryptPwd = RSAUtils.decryptDataOnJava(password, privateKey);
        // 生成盐
        byte[] saltBytes = RSAUtils.generateSalt();
        String saltStr = RSAUtils.bytesToHex(saltBytes);
        // 加盐后的密码
        String hashPassword =  RSAUtils.hashPassword(DecryptPwd, saltBytes);
        // 输入和db存入的密码对比， 盐
        boolean isMatch = RSAUtils.hashPasswordIsMatch(sysUser.getPassword(), DecryptPwd, sysUser.getSalt());
        if(isMatch) {
            return ResponseEntity.ok(Result.error("新密码不能与原密码相同！"));
        }
        signService.resetPassword(account, hashPassword, saltStr);
        return ResponseEntity.ok(Result.success("密码重置成功！"));
    }


    /**
     * 用户注册接口
     * 此处简化，根据自己业务自定
     * @param request    HttpServletRequest对象
     * @param signUpRequest        注册请求体
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(HttpServletRequest request, @RequestBody SignUpRequest signUpRequest){
        String privateKey = (String) request.getSession().getAttribute("privateKey");
        logger.info("signup privateKey: {}", privateKey);
        String account = signUpRequest.getUserAccount();
        String password = signUpRequest.getPassword();
        String email = signUpRequest.getEmail();
        String phone = signUpRequest.getPhone();
        Sign sysUser = signRepository.findByUsername(account);
        if(sysUser != null) {
            return ResponseEntity.ok(Result.error("用户已存在"));
        }
        // 解密后的密码
        String DecryptPwd = RSAUtils.decryptDataOnJava(password, privateKey);
        logger.info("signup decryptPassword: {}", DecryptPwd);
        logger.info("signup username:{}", account);
        // 生成盐
        byte[] saltBytes = RSAUtils.generateSalt();
        String saltStr = RSAUtils.bytesToHex(saltBytes);
        // 加盐后的密码
        String hashPassword =  RSAUtils.hashPassword(DecryptPwd, saltBytes);
        int uid = 1000000 + signService.SignUp(account, hashPassword, email, phone, saltStr);
        User user = new User();
        user.setUid(uid);
        user.setUserAccount(account);
        user.setUsername("acm_" + account);
        user.setEmail(email);
        user.setPhone(phone);
        user.setCreateTime(DateUtil.getNowTime());
        user.setRole("普通用户");
        user.setStatus("正常");
        user.setAvatar("https://jsd.cdn.zzko.cn/gh/fangyi002/picture_bed/images/avatar/default.png");
        userRepository.save(user);
        return ResponseEntity.ok(Result.success("注册成功！"));
    }

    /**
     * @param email 邮箱
     *              发送验证码并返回验证码
     * @param request 请求对象
     * @return
     */
    @GetMapping("/getemailverifycode")
    public ResponseEntity<?> getEmailVerifyCode(HttpServletRequest request, @RequestParam String email) {
        //随机生成一个四位数的验证码
        int yzm = new Random().nextInt(9999) % (9999 - 1000 + 1) + 1000;
        //创建简单邮件消息
        SimpleMailMessage message = new SimpleMailMessage();
        //发送邮件账户
        message.setFrom(sendemail);
        //谁要接收
        message.setTo(email);
        //邮件标题
        message.setSubject("创意交流平台验证码");
        //邮件内容
        String content = String.format("您的验证码为 %s", yzm);
        message.setText(content);
        //发送邮件
        javaMailSender.send(message);
        logger.info("EmailVerifyCode: {}, send to: {}", yzm, email);
        return ResponseEntity.ok(Result.success("验证码已发送至邮箱，请查收！", yzm));
    }

    /**
     * 获取Sign数据库列表
     *
     */
    @GetMapping("/findAll")
    public List<Sign> findAll() {
        return signRepository.findAll();
    }

    private void addAdminUser() {
        if(signRepository.findByUsername("admin") != null) {
            return;
        }
        // 生成盐
        byte[] saltBytes = RSAUtils.generateSalt();
        String saltStr = RSAUtils.bytesToHex(saltBytes);
        // 加盐后的密码
        String hashPassword =  RSAUtils.hashPassword("123456", saltBytes);

        Sign sign = new Sign();
        sign.setUsername("admin");
        sign.setPassword(hashPassword);
        sign.setSalt(saltStr);
        sign.setEmail("2370145097@qq.com");
        sign.setPhone("15727931358");
        signRepository.save(sign);
        sign = signRepository.findByUsername("admin");

        User user = new User();
        user.setUid(sign.getId() + 1000000);
        user.setUserAccount(sign.getUsername());
        user.setUsername("acm_" + sign.getUsername() );
        user.setEmail(sign.getEmail());
        user.setPhone(sign.getPhone());
        user.setCreateTime(DateUtil.getNowTime());
        user.setRole("管理员");
        user.setStatus("正常");
        user.setAvatar("https://jsd.cdn.zzko.cn/gh/fangyi002/picture_bed/images/avatar/default.png");
        userRepository.save(user);
    }
}
