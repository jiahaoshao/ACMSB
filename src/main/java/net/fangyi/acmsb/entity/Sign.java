package net.fangyi.acmsb.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Sign {
    @Id
    private Integer id;
    private String username;
    private String password;
    private String email;
    private String phone;
}
