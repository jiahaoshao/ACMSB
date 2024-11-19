package net.fangyi.acmsb.repository;

import net.fangyi.acmsb.entity.Picture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PictureRepository extends JpaRepository<Picture,Integer> {
    Picture findByPid(int pid);

}


