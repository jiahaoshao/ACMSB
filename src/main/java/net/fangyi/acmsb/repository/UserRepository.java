package net.fangyi.acmsb.repository;

import net.fangyi.acmsb.entity.Sign;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Sign, Integer> {
}
