package net.fangyi.acmsb.controller;

import io.micrometer.common.util.StringUtils;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletRequest;
import net.fangyi.acmsb.AcmsbApplication;
import net.fangyi.acmsb.Util.RSAUtils;
import net.fangyi.acmsb.entity.SignInRequest;
import net.fangyi.acmsb.entity.Sign;
import net.fangyi.acmsb.entity.SignUpRequest;
import net.fangyi.acmsb.repository.SignRepository;
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

import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/sign")
@CrossOrigin //解决跨域问题
public class SignController {
    private static final Logger logger = LoggerFactory.getLogger(SignController.class);
    //注入SignService
    @Autowired
    private SignService signService;

    @Autowired
    private SignRepository signRepository;

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
    @GetMapping("/getpublickey")
    public ResponseEntity<?> getPublicKey(HttpServletRequest request) {
        String publicKey = generateKey(request);
        return ResponseEntity.ok(Result.success("publicKey", publicKey));
    }
    /**
     * 生成随机码
     * @param request HttpServletRequest对象
     */
    private String generateKey(HttpServletRequest request) {
        Map<String, Object> keyMap = RSAUtils.genKeyPair();
        String publicKey = RSAUtils.getPublicKey(keyMap);
        String privateKey = RSAUtils.getPrivateKey(keyMap);
        request.getSession().setAttribute("privateKey", privateKey);
        logger.info("generatePublicKey: " + publicKey);
        logger.info("generatePrivateKey: " + privateKey);
        return StringUtils.isBlank(publicKey) ? "" : publicKey;
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
        logger.info("signin privateKey: " + privateKey);
        String account = signInRequest.getUserAccount();
        // 获取登录人信息
        Sign sysUser = signService.findByUsername(account);

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
        logger.info("signin username:" + account);
        logger.info("signin inputpassword:" + inputPassword);
        logger.info("signin decryptPassword: " + decryptPassword);

        // 输入和db存入的密码对比， 盐
        boolean isMatch = RSAUtils.hashPasswordIsMatch(sysUser.getPassword(), decryptPassword, sysUser.getSalt());
        if(isMatch) {
            return ResponseEntity.ok(Result.success("登录成功！"));
        }
        return ResponseEntity.ok(Result.error("用户名或密码错误！"));
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
        Sign sysUser = signService.findByUsername(account);
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
        System.out.println("signup privateKey: " + privateKey);
        String account = signUpRequest.getUserAccount();
        String password = signUpRequest.getPassword();
        String email = signUpRequest.getEmail();
        String phone = signUpRequest.getPhone();
        Sign sysUser = signService.findByUsername(account);
        if(sysUser != null) {
            return ResponseEntity.ok(Result.error("用户已存在"));
        }
        // 解密后的密码
        String DecryptPwd = RSAUtils.decryptDataOnJava(password, privateKey);
        logger.info("signup decryptPassword: " + DecryptPwd);
        logger.info("signup username:" + account);
        // 生成盐
        byte[] saltBytes = RSAUtils.generateSalt();
        String saltStr = RSAUtils.bytesToHex(saltBytes);
        // 加盐后的密码
        String hashPassword =  RSAUtils.hashPassword(DecryptPwd, saltBytes);
        signService.SignUp(account, hashPassword, email, phone, saltStr);
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
}
