package pl.edu.agh.mobile.serviceplatform;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import pl.edu.agh.mobile.serviceplatform.exceptions.NoFiles;
import pl.edu.agh.mobile.serviceplatform.exceptions.VersionDoesNotMatch;

public class MainService extends Service {

    private FileObserver observer;
    private Handler handler;

    static final String INPUT_PARAMS = "input-params";
    static final String DESKTOP_FINISHED = "desktop-finished";

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {

        Toast.makeText(getApplicationContext(), "Service started", Toast.LENGTH_LONG).show();

        handler = new Handler();
        String path = Environment.getExternalStorageDirectory() + File.separator + "ServicePlatform";
        String inputPath = path + File.separator + "Input";
        String outputPath = path + File.separator + "Output";

        boolean mExternalStorageWriteable = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()), ready = false;

        if (mExternalStorageWriteable) {
            File mainDirectory = new File(path);
            ready = mainDirectory.exists();
            if (!mainDirectory.exists() && mainDirectory.mkdirs()) {
                ready = true;
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(mainDirectory)));
            }
            if (ready) {
                File inputFile = new File(inputPath);
                if (!inputFile.exists() && inputFile.mkdirs()) {
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(inputFile)));
                }
                File outputFile = new File(outputPath);
                if (!outputFile.exists() && outputFile.mkdirs()) {
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(outputFile)));
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), "Cannot create directory", Toast.LENGTH_LONG).show();
        }

        if (!ready) {
            Toast.makeText(getApplicationContext(), "Cannot access ServicePlatform directory", Toast.LENGTH_LONG).show();
        } else {
            observer = initSingleDirectoryObserver(new CommunicationManager(path, inputPath, outputPath, getResources().getString(R.string.app_versionName)));
            observer.startWatching();
            Toast.makeText(getApplicationContext(), "Observer started", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroy() {
        Toast.makeText(getApplicationContext(), "Service stopped", Toast.LENGTH_LONG).show();
        observer.stopWatching();
        super.onDestroy();
    }

    private FileObserver initSingleDirectoryObserver(final CommunicationManager manager) {

        final MainService service = this;

        return new FileObserver(manager.getDirectory()) {

            private boolean inputFileOpened = false;
            private boolean waitingForFinishedFile = false;
            private Input input;

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
                        for (File outputFile : outputFiles) {
                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(outputFile)));
                        }
                        service.postMessage("Processing completed!");
                        mobileFinished();
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

            private void mobileFinished() throws IOException {
                File f = new File(manager.getDirectory() + File.separator + "mobile-finished");
                f.createNewFile();
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(f)));
            }
        };
    }

    private void postMessage(final String message) {
        handler.post(new Runnable() {
            public void run() {
                Toast toast = Toast.makeText(MainService.this, message, Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }
}
