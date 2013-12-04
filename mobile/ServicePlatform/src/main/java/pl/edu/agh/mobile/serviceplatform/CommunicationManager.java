package pl.edu.agh.mobile.serviceplatform;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import pl.edu.agh.mobile.serviceplatform.exceptions.NoFiles;
import pl.edu.agh.mobile.serviceplatform.exceptions.VersionDoesNotMatch;
import pl.edu.agh.mobile.serviceplatform.services.AbstractFactory;

public class CommunicationManager {

    private String directory;
    private String inputDirectory;
    private String outputDirectory;
    private String appVersion;

    public CommunicationManager(String directory, String inputDirectory, String outputDirectory, String appVersion) {
        this.directory = directory;
        this.inputDirectory = inputDirectory;
        this.outputDirectory = outputDirectory;
        this.appVersion = appVersion;
    }

    public Input readInputFile(String filename) throws IOException, VersionDoesNotMatch, NoFiles {

        Input input = new Input();
        BufferedReader br = new BufferedReader(new FileReader(directory + File.separator + filename));

        input.setVersion(br.readLine());
        input.setServiceName(br.readLine());

        String line;
        ArrayList<String> files = new ArrayList<String>();
        while ((line = br.readLine()) != null) {
            files.add(inputDirectory + File.separator + line);
        }
        br.close();

        input.setFiles(files);

        if (!input.getVersion().equals(appVersion)) {
            throw new VersionDoesNotMatch("Desktop version: " + input.getVersion() + ", mobile version: " + appVersion);
        }

        if (input.getFiles().isEmpty()) {
            throw new NoFiles();
        }

        return input;
    }

    public ArrayList<File> processInput(Input input) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {

        AbstractFactory service = AbstractFactory.create("pl.edu.agh.mobile.serviceplatform.services." + input.getServiceName() + "Service");

        ArrayList<File> outputFiles = new ArrayList<File>();

        for (String inputFile : input.getFiles()) {
            String outputFile = inputFile.replace(inputDirectory, outputDirectory);
            service.process(inputFile, outputFile);
            outputFiles.add(new File(outputFile));
        }

        return outputFiles;
    }

    public String getDirectory() {
        return directory;
    }
}