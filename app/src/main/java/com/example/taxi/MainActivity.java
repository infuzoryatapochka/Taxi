package com.example.taxi;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView tv_name, tv_surname, tv_phone;
    private EditText et_name, et_surname, et_phone;
    private Button btn_registration;
    SharedPreferences nPref;
    SharedPreferences sPref;
    SharedPreferences pPref;
    final String SAVED_NAME = "saved_name";
    final String SAVED_SURNAME = "saved_surname";
    final String SAVED_PHONE = "saved_phone";
    private static final String TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "MainActivity: onCreate");
        setContentView(R.layout.activity_main);
        initViews();
        nPref = getPreferences(MODE_PRIVATE);
        sPref = getPreferences(MODE_PRIVATE);
        pPref = getPreferences(MODE_PRIVATE);
        loadText();
    }

    private void initViews() {
        tv_name =(TextView) findViewById(R.id.tv_name);
        tv_surname = (TextView) findViewById(R.id.tv_surname);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        et_name = (EditText) findViewById(R.id.et_name);
        et_surname = (EditText) findViewById(R.id.et_surname);
        et_phone = (EditText) findViewById(R.id.et_phone);
        btn_registration = (Button) findViewById(R.id.btn_registration);

        btn_registration.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_registration) {
            if (et_name.getText().toString().isEmpty() || et_surname.getText().toString().isEmpty() || et_phone.getText().toString().isEmpty())
                Toast.makeText(this, "Enter all fields!", Toast.LENGTH_SHORT).show();
            else if (et_phone.getText().toString().matches(".*\\d.*") && et_phone.getText().toString().startsWith("+375"))
                try {
                    Double.parseDouble(et_phone.getText().toString());
                } catch(NumberFormatException e){
                    Toast.makeText(this, "Use numbers in phone!" , Toast.LENGTH_SHORT).show();
                }
            else if (et_phone.getText().toString().length() != 12 || !(et_phone.getText().toString().startsWith("375")))
                Toast.makeText(this, "Enter the whole phone number as +375 with operator code!", Toast.LENGTH_SHORT).show();
            else {
                Log.d(TAG, "MainActivity: onPause");
                saveText();
                Intent intent = new Intent(MainActivity.this, ActivityOutput.class);
                intent.putExtra("name", et_name.getText().toString());
                intent.putExtra("surname", et_surname.getText().toString());
                intent.putExtra("phone", et_phone.getText().toString());
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "MainActivity: onDestroy");
        super.onDestroy();
    }

    private void saveText() {
        SharedPreferences.Editor nEditor = nPref.edit();
        nEditor.putString(SAVED_NAME, et_name.getText().toString());
        nEditor.commit();
        SharedPreferences.Editor sEditor = sPref.edit();
        sEditor.putString(SAVED_SURNAME, et_surname.getText().toString());
        sEditor.commit();
        SharedPreferences.Editor pEditor = pPref.edit();
        pEditor.putString(SAVED_PHONE, et_phone.getText().toString());
        pEditor.commit();
        Toast.makeText(this, "Text saved", Toast.LENGTH_SHORT).show();
    }

    private void loadText() {
        String savedName = nPref.getString(SAVED_NAME, "");
        et_name.setText(savedName);
        String savedSurname = sPref.getString(SAVED_SURNAME, "12312");
        et_surname.setText(savedSurname);
        String savedPhone = pPref.getString(SAVED_PHONE, "123123");
        et_phone.setText(savedPhone);

        Toast.makeText(this, "Text loaded", Toast.LENGTH_SHORT).show();

        if (savedName != null && savedSurname != null && savedPhone != null){
            btn_registration.setText("Log In");
        }
    }
}
