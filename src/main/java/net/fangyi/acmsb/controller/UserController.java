package net.fangyi.acmsb.controller;


import net.fangyi.acmsb.entity.User;
import net.fangyi.acmsb.repository.UserRepository;
import net.fangyi.acmsb.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@RestController
@RequestMapping("/user")
@CrossOrigin //解决跨域问题
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserRepository userRepository;

    @Value("${Github.branch}")
    private String branch;  // Github 仓库的分支

    @Value("${Github.name}")
    private String committerName;  // 提交者的姓名

    @Value("${Github.email}")
    private String committerEmail;  // 提交者的邮箱

    @Value("${Github.message}")
    private String commitMessage;  // 提交的消息

    @Value("${Github.OWNER}")
    private String owner;  // Github 仓库的所有者

    @Value("${Github.REPO}")
    private String repo;  // Github 仓库的名称

    //@Value("${Github.Authorization}")
    private String token = "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";  // Github 个人访问令牌

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

    @RequestMapping("/finduserbyuid")
    public ResponseEntity<?> findUserByUid(@RequestParam int uid) {
        User user = userRepository.findByUid(uid);
        if (user == null) {
            return ResponseEntity.ok(Result.error("用户不存在"));
        }
        return ResponseEntity.ok(Result.success("查询成功", user));
    }

    @PostMapping("/updateavatar")
    public ResponseEntity<?> updateAvatar(@RequestParam("file") MultipartFile file, @RequestParam("uid") int uid) throws IOException, InterruptedException {
        // logger.info("file: {}, uid: {}", file, uid);
        User user = userRepository.findByUid(uid);
        if (user == null) {
            return ResponseEntity.ok(Result.error("用户不存在"));
        }

        // 上传头像
        GithubController githubController = new GithubController();
        String avatar = githubController.uploadAvatar(file, branch, committerName, committerEmail, commitMessage, owner, repo, token);
        if(Objects.equals(avatar, "上传失败"))
        {
            return ResponseEntity.ok(Result.error("上传失败", user));
        }

        user.setAvatar(avatar);
        userRepository.save(user);
        return ResponseEntity.ok(Result.success("更新成功", user));
    }
}
