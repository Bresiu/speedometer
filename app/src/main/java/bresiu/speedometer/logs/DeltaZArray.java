package bresiu.speedometer.logs;

import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class DeltaZArray {
    public ArrayList<DeltaZPoint> arrayList = new ArrayList<DeltaZPoint>();

    public void insertPoint(DeltaZPoint point) {
        arrayList.add(point);
    }

    public ArrayList<DeltaZPoint> returnArray() {
        return this.arrayList;
    }

    public void saveLogs(ArrayList<DeltaZPoint> arrayList) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root);
        // myDir.mkdirs();
        String fname = "DeltaZPoint.dat";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream f = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(f);
            for (DeltaZPoint point : arrayList) {
                pw.println(point.toString());
                Lo.g("DeltaZPoint added");
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
