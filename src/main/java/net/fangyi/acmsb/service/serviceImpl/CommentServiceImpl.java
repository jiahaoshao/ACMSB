package net.fangyi.acmsb.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import net.fangyi.acmsb.entity.Comment;
import net.fangyi.acmsb.mapper.CommentMapper;
import net.fangyi.acmsb.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Y·C
 * @version 1.0.0
 * @ClassName CommentServiceImpl.java
 * @Description TODO
 * @createTime 2023年04月22日 10:21:00
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Resource
    private CommentMapper commentMapper;

    @Override
    public List<Comment> findAllByForeignId(Integer foreignId) {

        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.eq("foreign_id",foreignId);
        List<Comment> comments = commentMapper.selectList(foreignId);

        return comments;
    }
}
