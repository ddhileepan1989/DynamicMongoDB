package org.mentorbridge;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableEncryptableProperties
public class DynamicDatabaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(DynamicDatabaseApplication.class, args);
    }

}
