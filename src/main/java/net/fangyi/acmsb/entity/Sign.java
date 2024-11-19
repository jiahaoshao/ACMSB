package net.fangyi.acmsb.entity;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "sign")
public class Sign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "username")
    private String username; //账户
    @Column(name = "password")
    private String password; //密码（加密后）
    @Column(name = "email")
    private String email;
    @Column(name = "phone")
    private String phone;
    @Column(name = "salt")
    private String salt; //密码盐
}
