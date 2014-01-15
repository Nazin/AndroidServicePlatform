package pl.edu.agh.mobile.serviceplatform;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class SecurityCodePreference extends DialogPreference {

    View changePassword = null;

    public SecurityCodePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SecurityCodePreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected View onCreateDialogView() {
        LayoutInflater i = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        changePassword = i.inflate(R.layout.set_password, null);
        return changePassword;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        super.onClick(dialog, which);
        if (changePassword != null && which == DialogInterface.BUTTON_POSITIVE) {

            final String old_password = ((EditText) changePassword.findViewById(R.id.old_password)).getText().toString();
            final String password = ((EditText) changePassword.findViewById(R.id.password)).getText().toString();
            final String repassword = ((EditText) changePassword.findViewById(R.id.password_confirm)).getText().toString();

            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());

            if (!old_password.equals(sharedPrefs.getString("security_code", String.valueOf(R.string.default_security_code)))) {
                Toast.makeText(getContext(), "Security code is not valid!", Toast.LENGTH_LONG).show();
                showDialog(new Bundle());
            } else {
                if (password.equals("")) {
                    Toast.makeText(getContext(), "Security code is to short", Toast.LENGTH_LONG).show();
                    showDialog(new Bundle());
                } else if (!password.equals(repassword)) {
                    Toast.makeText(getContext(), "Security codes do not match", Toast.LENGTH_LONG).show();
                    showDialog(new Bundle());
                } else {
                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    editor.putString("security_code", password);
                    editor.commit();
                    Toast.makeText(getContext(), "Security code updated", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}