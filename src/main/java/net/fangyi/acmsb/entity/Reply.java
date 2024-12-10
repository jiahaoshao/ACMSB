package net.fangyi.acmsb.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Reply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int parentid; //  回复的评论的cid
    private String content;  // 评论的内容
    private int authorid;  // 作者id
    private String createTime; //创建时间
    private int likes;
}