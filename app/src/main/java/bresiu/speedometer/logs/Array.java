package bresiu.speedometer.logs;

import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Array {
    public ArrayList<Point> arrayList = new ArrayList<Point>();

    public void insertPoint(Point point) {
        arrayList.add(point);
    }

    public ArrayList<Point> returnArray() {
        return this.arrayList;
    }

    public void saveLogs(ArrayList<Point> arrayList) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root);
        // myDir.mkdirs();
        String fname = "acc_log.dat";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream f = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(f);
            for (Point point : arrayList) {
                pw.println(point.toString());
                Lo.g("point added");
            }
            pw.flush();
            pw.close();
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
