package am.platform.movie.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableMongoRepositories(
        basePackages = "am.platform.movie.common.repository",
        createIndexesForQueryMethods = true
)
@ComponentScan(basePackages = {
        "am.platform.movie.api", "am.platform.movie.common"
})
@EnableWebSecurity
public class MoviePlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoviePlatformApplication.class, args);
    }

}
