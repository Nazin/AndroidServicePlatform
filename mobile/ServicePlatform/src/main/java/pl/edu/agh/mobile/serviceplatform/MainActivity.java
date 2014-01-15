package pl.edu.agh.mobile.serviceplatform;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

    private Messenger mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        startService();
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };


    private void startService() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo srv : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("pl.edu.agh.mobile.serviceplatform.MainService".equals(srv.service.getClassName())) return;
        }
        Intent intent = new Intent(MainActivity.this, MainService.class);
        intent.putExtra("receiver", new MyResultReceiver(null));
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    public void validateSecurityCode(View view) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        EditText securityCodeText = (EditText)findViewById(R.id.securityCode);
        if (securityCodeText.getText().toString().equals(sharedPrefs.getString("security_code", String.valueOf(R.string.default_security_code)))) {
            findViewById(R.id.securityPanel).setVisibility(View.INVISIBLE);
            securityCodeText.setText("");
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            Toast.makeText(getApplicationContext(), "Security code valid!", Toast.LENGTH_SHORT).show();
            try {
                mService.send(Message.obtain(null, 1, 0, 0));
            } catch (RemoteException e) {
                Toast.makeText(getApplicationContext(), "Could not communicate with service!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Security code NOT valid!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

    class ToggleSecurityPanel implements Runnable {

        private boolean visible;

        public ToggleSecurityPanel(boolean visible) {
            this.visible = visible;
        }

        public void run() {
            findViewById(R.id.securityPanel).setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
            ((EditText)findViewById(R.id.securityCode)).setText("");
        }
    }

    class MyResultReceiver extends ResultReceiver {

        public MyResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            runOnUiThread(new ToggleSecurityPanel(resultCode==1));
        }
    }
}
