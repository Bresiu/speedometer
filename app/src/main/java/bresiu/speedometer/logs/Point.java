package bresiu.speedometer.logs;

public class Point {
    int count;
    double xAcc;
    double yAcc;
    double zAcc;
    double vAcc;

    public Point(int count, double xAcc, double yAcc, double zAcc, double vAcc) {
        this.count = count;
        this.xAcc = xAcc;
        this.yAcc = yAcc;
        this.zAcc = zAcc;
        this.vAcc = vAcc;
    }

    @Override
    public String toString() {
        return count + " " + xAcc + " " + yAcc + " " + zAcc + " " + vAcc;
    }
}
