package pl.edu.agh.mobile.serviceplatform.services;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DoNothingService extends AbstractFactory {

    @Override
    public void process(String inputFile, String outputFile) throws IOException {

        InputStream in = new FileInputStream(inputFile);
        OutputStream out = new FileOutputStream(outputFile);

        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
}
