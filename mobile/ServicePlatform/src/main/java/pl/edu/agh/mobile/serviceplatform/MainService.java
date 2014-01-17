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
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.StringTokenizer;

import dalvik.system.DexFile;

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
            observer = initSingleDirectoryObserver(new CommunicationManager(this, path, inputPath, outputPath));
            observer.startWatching();
            Toast.makeText(getApplicationContext(), "Observer started", Toast.LENGTH_LONG).show();
            updateServices(path);
        }
    }

    private void updateServices(String path) {

        try {

            ArrayList<String> servicesNames = new ArrayList<String>();
            DexFile df = new DexFile(getPackageCodePath());

            for (Enumeration<String> itrs = df.entries(); itrs.hasMoreElements();) {

                String s = itrs.nextElement();
                StringTokenizer tokens = new StringTokenizer(s, ".");
                String packageName = tokens.nextToken();

                for (int i=0, l = tokens.countTokens(); i<l-1; i++) {
                    packageName = packageName.concat(".").concat(tokens.nextToken());
                }

                String className = tokens.nextToken();

                if (packageName.equals("pl.edu.agh.mobile.serviceplatform.services") && !className.equals("AbstractFactory")) {
                    servicesNames.add(className.substring(0, className.length() - 7));
                }
            }

            String servicesFile = path + File.separator + "services";

            File f = new File(servicesFile);
            if (!f.exists()) {
                f.createNewFile();
            }

            FileWriter fw = new FileWriter(servicesFile, false);

            for (String serviceName : servicesNames) {
                fw.write(serviceName + "\n");
            }

            fw.close();
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(f)));
        } catch (IOException e) {
            e.printStackTrace();
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
