package pl.edu.agh.mobile.serviceplatform.services;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class GrayScaleService extends AbstractFactory {

    @Override
    public void process(String inputFile, String outputFile) throws IOException {

        Bitmap bitmap = BitmapFactory.decodeFile(inputFile);

        int width = bitmap.getWidth(), height = bitmap.getHeight();

        Bitmap bmpGrayScale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayScale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bitmap, 0, 0, paint);

        FileOutputStream out = new FileOutputStream(new File(outputFile));
        bmpGrayScale.compress(Bitmap.CompressFormat.JPEG, 90, out);
        out.flush();
        out.close();
    }
}
