package net.fangyi.acmsb.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int aid; // 文章ID
    private String title;  // 文章标题
    private String content;  // 文章内容
    @Column(name = "authorid")
    private int authorId;  // 作者id
    private String createTime; //创建时间
    private String classify; //文章分类
    private String tags; //文章标签
    private String status; //文章状态

}
