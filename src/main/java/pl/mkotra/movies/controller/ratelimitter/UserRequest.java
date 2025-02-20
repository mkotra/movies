package pl.mkotra.movies.controller.ratelimitter;

public class UserRequest {

    private int requestCount = 0;
    private final long lastRequestTime;

    UserRequest(long lastRequestTime) {
        this.lastRequestTime = lastRequestTime;
    }

    int getRequestCount() {
        return requestCount;
    }

    long getLastRequestTime() {
        return lastRequestTime;
    }

    void incrementRequestCount() {
        this.requestCount++;
    }
}
