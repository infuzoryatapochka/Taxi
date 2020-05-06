package com.example.taxi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class ActivityOutput extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_outputName, tv_outputPhone, tv_path;
    private Button btn_path, btn_call;
    private static final String TAG = "myLogs";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "ActivityOutput: onCreate");
        setContentView(R.layout.activity_output);

        initViews();
        setData();
    }

    private void setData() {
        tv_outputName.setText(getIntent().getStringExtra("name").concat(" ").concat(getIntent().getStringExtra("surname")));
        tv_outputPhone.setText(getIntent().getStringExtra("phone"));
    }

    private void initViews() {
        tv_outputName = (TextView) findViewById(R.id.tv_outputName);
        tv_outputPhone = (TextView) findViewById(R.id.tv_outputPhone);
        tv_path = (TextView) findViewById(R.id.tv_path);
        btn_path = (Button) findViewById(R.id.btn_path);
        btn_call = (Button) findViewById(R.id.btn_call);

        btn_path.setOnClickListener(this);
        btn_call.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_call:
                if (tv_path.getText().toString().isEmpty())
                    Toast.makeText(this, "Set your path!", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Thank you! Taxi was just sent to you. ", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_path:
                Log.d(TAG, "ActivityOutput: onStop");
                Log.d(TAG, "ActivityPath: onStart");
                Intent intent = new Intent("android.intent.action.ActivityPath");
                startActivityForResult(intent, 1);
                break;


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        String text = data.getStringExtra("text");
        tv_path.setText(text);
    }

}