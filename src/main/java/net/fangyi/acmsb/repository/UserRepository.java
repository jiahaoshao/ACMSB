package net.fangyi.acmsb.repository;

import net.fangyi.acmsb.entity.Sign;
import net.fangyi.acmsb.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUid(int uid);
}
