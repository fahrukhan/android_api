package com.fahru.restapi.menu;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fahru.restapi.R;
import com.fahru.restapi.callback.JsonPlaceHolderApi;
import com.fahru.restapi.model.BaseModel;
import com.fahru.restapi.model.BookModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Other extends BaseModel implements OnSpinnerItemSelectedListener {
    NiceSpinner methodSpin;
    TextView address, test;
    String url;
    MaterialButton sendBtn;
    TextInputEditText path;
    JsonPlaceHolderApi jsonPlaceHolderApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        test = findViewById(R.id.bookTextTest);
        sendBtn = findViewById(R.id.bookSendBtn);
        address = findViewById(R.id.bookAddress);
        path = findViewById(R.id.bookPath);
        url = settings.getString("url_connect", null);
        if (url != null){
            String url = "You connected to: "+this.url;
            address.setText(url);
        }

        methodSpin = findViewById(R.id.bookSpinner);
        ArrayAdapter<String> methodAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, method);
        methodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        methodSpin.setAdapter(methodAdapter);
        methodSpin.setOnSpinnerItemSelectedListener(this);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String p = path.getText().toString();
                doApi(p);
            }
        });

    }

    private void doApi(String p) {
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(okHttpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        Call<String> call = jsonPlaceHolderApi.getDataOther(p);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String value = "";
                if (!response.isSuccessful()) {
                    toastWarning(Other.this, response.code()+": "+response.message());
                    return;
                }
                value = response.body();
                test.setText(value.replaceAll(",", "\n").replaceAll("\\{","{\n").replaceAll("\\}","\n}"));
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                toastError(Other.this, t.getMessage());
            }
        });
    }

    @Override
    public void onItemSelected(NiceSpinner parent, View view, int position, long id) {

    }
}
