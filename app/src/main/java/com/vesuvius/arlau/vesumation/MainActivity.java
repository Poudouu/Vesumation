package com.vesuvius.arlau.vesumation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startAutomationActivity(View v){
        Intent it = new Intent(this,AutomationActivity.class);
        startActivity(it);
    }

    public void startRobotActivity(View v){
        Intent it = new Intent(this,RobotActivity.class);
        startActivity(it);
    }
}
