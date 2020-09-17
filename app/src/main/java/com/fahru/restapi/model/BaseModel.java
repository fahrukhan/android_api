package com.fahru.restapi.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.codingending.popuplayout.PopupLayout;
import com.fahru.restapi.R;

import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;
import okhttp3.OkHttpClient;

public abstract class BaseModel extends AppCompatActivity {
    public static SharedPreferences settings;
    protected boolean firstRun;
    public static SharedPreferences.Editor editor;
    protected String[] method = {"GET", "POST", "PUT", "DELETE"};

    protected final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(5, TimeUnit.SECONDS)
            .connectTimeout(5, TimeUnit.SECONDS)
            .build();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = getSharedPreferences("prefs", 0);
    }

    public static void toastSuccess(Context context, String msg) {
        Toasty.success(context, msg, Toasty.LENGTH_SHORT, true).show();
    }

    public static void toastWarning(Context context, String msg) {
        Toasty.warning(context, msg, 0, true).show();
    }

    public static void toastError(Context context, String msg) {
        Toasty.error(context, msg, Toasty.LENGTH_LONG, true).show();
    }


    public void dialogMessage(Context context, String msg) {
        View view = View.inflate(context, R.layout.custom_dialog, null);
        TextView textMsg = view.findViewById(R.id.dialogCustMsg);
        textMsg.setText(msg);
        PopupLayout popupLayout = PopupLayout.init(context, view);
        popupLayout.setUseRadius(true);
        popupLayout.show(PopupLayout.POSITION_CENTER);
        popupLayout.show();
    }


    public void hideSoftKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


}
