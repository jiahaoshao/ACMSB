package net.fangyi.acmsb.entity;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "uid")
    private Integer uid; //用户ID sign表的id+10000000
    @Column(name = "user_account")
    private String userAccount; //账户
    @Column(name = "username")
    private String username; //用户名
    @Column(name = "email")
    private String email;
    @Column(name = "phone")
    private String phone;
    @Column(name = "create_time")
    private String createTime; //创建时间
    @Column(name = "role")
    private String role;
    @Column(name = "status")
    private String status; //状态 zc:正常，dj:冻结，ysc:已删除
    @Column(name = "avatar")
    private String avatar;//头像pid

}
