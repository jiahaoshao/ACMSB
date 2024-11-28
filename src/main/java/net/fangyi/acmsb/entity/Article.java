package net.fangyi.acmsb.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 文章 ID

    private String title;  // 文章标题
    private String content;  // 文章内容
    private String author;  // 作者

    // getter 和 setter 方法省略
}
