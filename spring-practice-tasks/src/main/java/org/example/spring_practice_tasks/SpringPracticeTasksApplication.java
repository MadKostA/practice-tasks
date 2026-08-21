package org.example.spring_practice_tasks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SpringPracticeTasksApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringPracticeTasksApplication.class, args);
    }

}
