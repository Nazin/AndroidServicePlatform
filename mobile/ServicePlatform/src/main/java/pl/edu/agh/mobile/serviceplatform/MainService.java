package pl.edu.agh.mobile.serviceplatform;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MainService extends Service {

    private FileObserver observer;
    private File mainDirectory;
    private Handler handler;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {

        Toast.makeText(getApplicationContext(), "Service started", Toast.LENGTH_LONG).show();

        handler = new Handler();

        boolean mExternalStorageWriteable = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()), ready = false;

        if (mExternalStorageWriteable) {
            mainDirectory = new File(Environment.getExternalStorageDirectory() + File.separator + "ServicePlatform");
            ready = mainDirectory.exists() || mainDirectory.mkdirs();
        } else {
            Toast.makeText(getApplicationContext(), "Cannot create directory", Toast.LENGTH_LONG).show();
        }

        if (!ready) {
            Toast.makeText(getApplicationContext(), "Cannot access ServicePlatform directory", Toast.LENGTH_LONG).show();
        } else {

            try {
                File file = new File(mainDirectory, "example.txt");
                BufferedWriter output = new BufferedWriter(new FileWriter(file));
                output.write("ala ma kota");
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            observer = initSingleDirectoryObserver(mainDirectory.getAbsolutePath());
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

    private FileObserver initSingleDirectoryObserver(String directoryPath) {

        final MainService service = this;

        return new FileObserver(directoryPath) {
            @Override
            public void onEvent(int event, String file) {
                service.postMessage("something happened on observer, event: " + event);
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
