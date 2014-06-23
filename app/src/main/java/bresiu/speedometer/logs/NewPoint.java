package bresiu.speedometer.logs;

public class NewPoint {
    long timestamp;
    double vector;

    public NewPoint(long timestamp, double vector) {
        this.timestamp = timestamp;
        this.vector = vector;
    }

    @Override
    public String toString() {
        return timestamp + " " + vector;
    }
}
