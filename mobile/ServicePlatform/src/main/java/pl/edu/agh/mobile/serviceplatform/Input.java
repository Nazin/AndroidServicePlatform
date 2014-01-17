package pl.edu.agh.mobile.serviceplatform;

import java.util.ArrayList;

public class Input {

    private String serviceName;
    private ArrayList<String> files;

    public ArrayList<String> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<String> files) {
        this.files = files;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
