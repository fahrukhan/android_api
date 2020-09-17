package com.fahru.restapi.model;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fahru.restapi.MainActivity;
import com.fahru.restapi.R;
import com.fahru.restapi.adapter.DataAdapter;
import com.fahru.restapi.menu.Menu;

public class SettingLoader extends BaseModel {
    Handler handler;
    int THREAD_SLEEP = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_loader);
        handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SettingLoader.this, Menu.class);
                if (checkSinglePath()){
                    toastSuccess(SettingLoader.this, "SETTING PATH SUCCESS");
                    startActivity(intent);
                }else {
                    toastError(SettingLoader.this, "SETTING PATH ERROR");
                }
            }
        }, THREAD_SLEEP);
    }

    private boolean checkSinglePath() {
        String key = getIntent().getStringExtra("key");
        String singlePath = settings.getString(key, null);
        return singlePath != null;
    }
}
