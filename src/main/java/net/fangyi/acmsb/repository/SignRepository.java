package net.fangyi.acmsb.repository;

import net.fangyi.acmsb.entity.Sign;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SignRepository extends JpaRepository<Sign, Integer> {
    Sign findByUsername(String username);

}
