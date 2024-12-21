package org.mentorbridge;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DynamicDatabaseApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void testEncryptionKey() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword("key-mentorbridge"); // encryptor's private key
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);

        String plaintext = "mongodb+srv://dhilprojects:Q19R8CduAFVD9JmO@cluster0.051e3.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0";
        System.out.println("Encrypted key : " + encryptor.encrypt(plaintext));
    }
}
