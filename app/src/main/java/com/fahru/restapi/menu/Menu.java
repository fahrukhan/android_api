package com.fahru.restapi.menu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.fahru.restapi.R;
import com.google.android.material.button.MaterialButton;

public class Menu extends AppCompatActivity implements View.OnClickListener {
    MaterialButton book, other;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        book = findViewById(R.id.menuBookBtn);
        other = findViewById(R.id.menuOtherBtn);

        book.setOnClickListener(this);
        other.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.menuBookBtn:
                startActivity(new Intent(view.getContext(), Book.class));
                break;
            case R.id.menuOtherBtn:
                startActivity(new Intent(view.getContext(), Other.class));
                break;
        }
    }
}
