package net.fangyi.acmsb.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.fangyi.acmsb.entity.Comment;

import java.util.List;

/**
 * @author Y·C
 * @version 1.0.0
 * @ClassName CommentService.java
 * @Description TODO
 * @createTime 2023年04月22日 10:20:00
 */
public interface CommentService {

    List<Comment> findAllByForeignId(Integer foreignId);
}
