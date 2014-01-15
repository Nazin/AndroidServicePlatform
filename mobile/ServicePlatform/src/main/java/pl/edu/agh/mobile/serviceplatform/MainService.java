package pl.edu.agh.mobile.serviceplatform;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.ResultReceiver;
import android.widget.Toast;

import java.io.File;

public class MainService extends Service {

    private ServiceFileObserver observer;
    private Handler handler;
    private ResultReceiver resultReceiver;

    final Messenger mMessenger = new Messenger(new IncomingHandler());

    @Override
    public IBinder onBind(Intent intent) {
        resultReceiver = intent.getParcelableExtra("receiver");
        return mMessenger.getBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        resultReceiver = intent.getParcelableExtra("receiver");
        return START_STICKY;
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
            observer = initSingleDirectoryObserver(new CommunicationManager(this, path, inputPath, outputPath, getResources().getString(R.string.app_versionName)));
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

    public ResultReceiver getResultReceiver() {
        return resultReceiver;
    }

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            observer.finishProcessing();
        }
    }

    private ServiceFileObserver initSingleDirectoryObserver(final CommunicationManager manager) {
        return new ServiceFileObserver(manager, this);
    }

    public void postMessage(final String message) {
        handler.post(new Runnable() {
            public void run() {
                Toast toast = Toast.makeText(MainService.this, message, Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }
}
