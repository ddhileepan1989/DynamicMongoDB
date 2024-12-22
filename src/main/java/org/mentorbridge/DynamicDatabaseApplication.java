package org.mentorbridge;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableEncryptableProperties
@EnableCaching
public class DynamicDatabaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(DynamicDatabaseApplication.class, args);
    }

}
