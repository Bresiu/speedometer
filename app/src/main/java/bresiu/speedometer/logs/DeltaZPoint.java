package bresiu.speedometer.logs;

public class DeltaZPoint {
    long timestamp;
    double xAcc;
    double yAcc;
    double zAcc;

    public DeltaZPoint(long timestamp, double xAcc, double yAcc, double zAcc) {
        this.timestamp = timestamp;
        this.xAcc = xAcc;
        this.yAcc = yAcc;
        this.zAcc = zAcc;
    }

    @Override
    public String toString() {
        return timestamp + " " + xAcc + " " + yAcc + " " + zAcc;
    }
}
