package vn.hust.social.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@CrossOrigin(origins = "*")
public class HustSocialNetworkApplication {

    public static void main(String[] args) {
        SpringApplication.run(HustSocialNetworkApplication.class, args);
    }

}
