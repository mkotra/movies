package pl.mkotra.movies.controller.ratelimitter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "rate-limiter")
public class RateLimiterProperties {

    private int maxRequests;
    private long timeWindow;

    public int getMaxRequests() {
        return maxRequests;
    }

    public void setMaxRequests(int maxRequests) {
        this.maxRequests = maxRequests;
    }

    public long getTimeWindow() {
        return timeWindow;
    }

    public void setTimeWindow(long timeWindow) {
        this.timeWindow = timeWindow;
    }
}
