package com.example.myapplicationtask;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //imposta il layout dell'attività con il file xml
        setContentView(R.layout.activity_main);
    }

}