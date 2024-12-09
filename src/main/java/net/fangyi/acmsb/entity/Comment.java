package net.fangyi.acmsb.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


/**
 * @author Y·C
 * @version 1.0.0
 * @ClassName Comment.java
 * @Description TODO
 * @createTime 2023年04月21日 21:52:00
 */
@Data
@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String content;
    private String username;
    private Integer userId;
    private BigDecimal rate;
    private Integer foreignId;
    private Integer pid;
    private String target;
    @JsonFormat(pattern = "yyy-MM-dd HH:mm:ss")
    private String createTime;

    @Transient
    List<Comment> children;
}
