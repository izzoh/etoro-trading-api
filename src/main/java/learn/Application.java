package learn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Application {


    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Application.class);
        ConfigurableApplicationContext ctx = application.run(args);
    }
}
