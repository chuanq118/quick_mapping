package cn.lqs.quick_mapping;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class QuickMappingApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(QuickMappingApplication.class);
        application.setBannerMode(Banner.Mode.OFF);
        application.run(args);
    }

}
