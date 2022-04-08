package gaozhi.online.ubtp.im;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author LiFucheng
 * @version 1.0
 * @description: TODO
 * @date 2022/3/5 16:45
 */
@SpringBootApplication
@ComponentScan(basePackageClasses = {gaozhi.online.base.ScanClass.class,IMApplication.class})
@EnableScheduling
public class IMApplication {
    public static void main(String[] args) {
        SpringApplication.run(IMApplication.class);
    }
}
