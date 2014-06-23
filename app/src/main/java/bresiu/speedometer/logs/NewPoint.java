package bresiu.speedometer.logs;

public class NewPoint {
    long timestamp;
    float vector;

    public NewPoint(long timestamp, float vector) {
        this.timestamp = timestamp;
        this.vector = vector;
    }

    @Override
    public String toString() {
        return timestamp + " " + vector;
    }
}
