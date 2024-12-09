package net.fangyi.acmsb.repository;

import net.fangyi.acmsb.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    List<Comment> findAllByForeignId(Integer foreignId);
}
