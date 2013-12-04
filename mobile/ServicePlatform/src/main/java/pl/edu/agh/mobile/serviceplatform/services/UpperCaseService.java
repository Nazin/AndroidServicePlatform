package pl.edu.agh.mobile.serviceplatform.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class UpperCaseService extends AbstractFactory {

    @Override
    public void process(String inputFile, String outputFile) throws IOException {

        String line;
        BufferedReader br = new BufferedReader(new FileReader(inputFile));
        BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));

        while ((line = br.readLine()) != null) {
            bw.write(line.toUpperCase() + "\n");
        }

        br.close();
        bw.close();
    }
}
