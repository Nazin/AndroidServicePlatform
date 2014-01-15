package pl.edu.agh.mobile.serviceplatform;

import android.content.Intent;
import android.net.Uri;
import android.os.FileObserver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import pl.edu.agh.mobile.serviceplatform.exceptions.NoFiles;
import pl.edu.agh.mobile.serviceplatform.exceptions.VersionDoesNotMatch;

public class ServiceFileObserver extends FileObserver {

    static final String INPUT_PARAMS = "input-params";
    static final String DESKTOP_FINISHED = "desktop-finished";

    private boolean inputFileOpened = false;
    private boolean waitingForFinishedFile = false;
    private Input input;

    private MainService service;
    private CommunicationManager manager;

    public ServiceFileObserver(CommunicationManager manager, MainService service) {
        super(manager.getDirectory());
        this.manager = manager;
        this.service = service;
    }

    @Override
    public void onEvent(int event, String file) {

        if (event == OPEN && INPUT_PARAMS.equals(file)) {
            inputFileOpened = true;
        } else if (event == CLOSE_WRITE && INPUT_PARAMS.equals(file) && inputFileOpened) {

            inputFileOpened = false;

            try {
                input = manager.readInputFile(file);
                waitingForFinishedFile = true;
            } catch (IOException e) {
                service.postMessage("Unexpected error!");
            } catch (VersionDoesNotMatch versionDoesNotMatch) {
                service.postMessage("Version does not match!");
                service.postMessage(versionDoesNotMatch.getMessage());
            } catch (NoFiles noFiles) {
                service.postMessage("No files were provided!");
            }
        } else if (event == CLOSE_WRITE && DESKTOP_FINISHED.equals(file) && waitingForFinishedFile) {

            waitingForFinishedFile = false;

            try {
                ArrayList<File> outputFiles = manager.processInput(input);
                if (outputFiles != null) {
                    outputTheFiles(outputFiles);
                }
            } catch (InstantiationException e) {
                service.postMessage("Unexpected error!");
            } catch (IllegalAccessException e) {
                service.postMessage("Unexpected error!");
            } catch (ClassNotFoundException e) {
                service.postMessage("Service not found!");
            } catch (IOException e) {
                service.postMessage("Unexpected error!");
            }
        }
    }

    public void finishProcessing() {
        try {
            ArrayList<File> outputFiles = manager.finishProcessing();
            outputTheFiles(outputFiles);
        } catch (InstantiationException e) {
            service.postMessage("Unexpected error!");
        } catch (IllegalAccessException e) {
            service.postMessage("Unexpected error!");
        } catch (ClassNotFoundException e) {
            service.postMessage("Service not found!");
        } catch (IOException e) {
            service.postMessage("Unexpected error!");
        }
    }

    private void outputTheFiles(ArrayList<File> outputFiles) throws IOException {
        for (File outputFile : outputFiles) {
            service.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(outputFile)));
        }
        service.postMessage("Processing completed!");
        mobileFinished();
    }

    private void mobileFinished() throws IOException {
        File f = new File(manager.getDirectory() + File.separator + "mobile-finished");
        f.createNewFile();
        service.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(f)));
    }
}
