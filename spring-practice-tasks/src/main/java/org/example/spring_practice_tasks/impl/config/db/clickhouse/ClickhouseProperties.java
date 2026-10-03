package org.example.spring_practice_tasks.impl.config.db.clickhouse;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "clickhouse")
public class ClickhouseProperties {
    private String url;
    private String user;
    private String password;
    private String driverClassName;
}
