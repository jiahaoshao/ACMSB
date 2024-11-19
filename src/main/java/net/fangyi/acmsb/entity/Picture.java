package net.fangyi.acmsb.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "picture")
public class Picture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "pid")
    private int pid;

    @Column(name = "name")
    private String name;

    @Column(name = "pic_data")
    private byte[] pic_data;


}

