package net.fangyi.acmsb;

import net.fangyi.acmsb.Util.RSAUtils;
import net.fangyi.acmsb.entity.Sign;
import net.fangyi.acmsb.entity.User;
import net.fangyi.acmsb.repository.SignRepository;
import net.fangyi.acmsb.repository.UserRepository;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
//@EnableOpenApi
@CrossOrigin
@MapperScan("net.fangyi.acmsb.mapper")
public class AcmsbApplication {
     Logger logger = LoggerFactory.getLogger(getClass());

    public static void main(String[] args) {
        SpringApplication.run(AcmsbApplication.class, args);
    }
}
