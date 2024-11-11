package net.fangyi.acmsb.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class User {
    @Id
    private Integer id;
    private String username;
    private String password;
}
