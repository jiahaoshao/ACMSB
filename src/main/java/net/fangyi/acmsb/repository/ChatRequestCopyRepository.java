package net.fangyi.acmsb.repository;

import net.fangyi.acmsb.entity.ChatRequestCopy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRequestCopyRepository extends JpaRepository<ChatRequestCopy, Integer> {
    ChatRequestCopy findByChatid(int chatid);

    List<ChatRequestCopy> findAllByUid(int uid);
}