package pl.mkotra.movies.controller.ratelimitter;

public class UserRequest {

    private int requestCount = 0;
    private long lastRequestTime = 0;

    int getRequestCount() {
        return requestCount;
    }

    long getLastRequestTime() {
        return lastRequestTime;
    }

    public void setRequestCount(int requestCount) {
        this.requestCount = requestCount;
    }

    public void setLastRequestTime(long lastRequestTime) {
        this.lastRequestTime = lastRequestTime;
    }

    void incrementRequestCount() {
        this.requestCount++;
    }

    void reset(long currentTime) {
        this.requestCount = 0;
        this.lastRequestTime = currentTime;
    }
}
