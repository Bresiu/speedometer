package bresiu.speedometer.logs;

public class Point {
    long timestamp;
    float xAcc;
    float yAcc;
    float zAcc;
    double ySpeed;
    double yDistance;

    public Point(long timestamp, float xAcc, float yAcc, float zAcc, double ySpeed,
                    double yDistance) {
        this.timestamp = timestamp;
        this.xAcc = xAcc;
        this.yAcc = yAcc;
        this.zAcc = zAcc;
        this.ySpeed = ySpeed;
        this.yDistance = yDistance;
    }

    @Override
    public String toString() {
        return timestamp + " " + xAcc + " " + yAcc + " " + zAcc + " " + ySpeed + " " + yDistance;
    }
}
