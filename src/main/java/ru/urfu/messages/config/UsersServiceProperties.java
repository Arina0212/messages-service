package ru.urfu.messages.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.users-service")
public class UsersServiceProperties {

    private String url = "http://localhost:9000";
    private Duration timeout = Duration.ofSeconds(3);

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Duration getTimeout() {
        return timeout == null ? Duration.ofSeconds(3) : timeout;
    }

    public void setTimeout(Duration timeout) {
        this.timeout = timeout;
    }
}
