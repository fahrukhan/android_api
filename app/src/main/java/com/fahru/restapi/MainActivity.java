package com.fahru.restapi;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.fahru.restapi.callback.JsonPlaceHolderApi;
import com.fahru.restapi.menu.Menu;
import com.fahru.restapi.model.BaseModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends BaseModel {
    private TextView text;
    TextInputEditText ipAdd, port;
    JsonPlaceHolderApi jsonPlaceHolderApi;
    MaterialButton connBtn;
    LottieAnimationView animationView;
    TextView wait;
    CheckBox cbIP;
    Boolean rememberIP = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ipAdd = findViewById(R.id.mainIPAddress);
        port = findViewById(R.id.mainPort);
        connBtn = findViewById(R.id.mainConnectBtn);
        animationView = findViewById(R.id.mainLoader);
        wait = findViewById(R.id.mainTextWait);
        cbIP = findViewById(R.id.mainCB);

        connBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard();
                tryConnect();
            }
        });

        cbIP.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                rememberIP = b;
            }
        });

        String ip = settings.getString("myIP", null);
        if (ip != null) {
            ipAdd.setText(ip);
            cbIP.setChecked(true);
        }

        firstRunCheck();
        addFilterToIPAddressInput();
    }

    private void tryConnect() {
        editor = settings.edit();
        if (rememberIP) {
            editor.putString("myIP", ipAdd.getText().toString()).apply();
        } else {
            editor.putString("myIP", null).apply();
        }

        animationView.setVisibility(View.VISIBLE);
        wait.setVisibility(View.VISIBLE);
        String url = "http://" + ipAdd.getText().toString() + ":" + port.getText().toString() + "/";

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        Call<Void> call = jsonPlaceHolderApi.getConnect();
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                animationView.setVisibility(View.GONE);
                wait.setVisibility(View.GONE);
                if (!response.isSuccessful()) {
                    toastWarning(getApplicationContext(), "Code: " + response.code() + "\nStatus:" + response.message());
                } else {
                    editor.putString("url_connect", url).apply();
                    toastSuccess(getApplicationContext(), "Connection " + response.message());
                    startActivity(new Intent(MainActivity.this, Menu.class));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                animationView.setVisibility(View.GONE);
                wait.setVisibility(View.GONE);
                dialogMessage(MainActivity.this, t.getMessage());
            }
        });

    }

    private void firstRunCheck() {
        firstRun = settings.getBoolean("firstRun", true);
        if (firstRun) {
            startActivity(new Intent(this, SplashScreen.class));
        }
    }

    private void getSingleContent() {
        Call<Post> call = jsonPlaceHolderApi.getSingleContentById("5ea331bf9fea83588052f35f");
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if (!response.isSuccessful()) {
                    text.setText("Code: " + response.code());
                    return;
                }

                Post posts = response.body();
                String content = "";
                content += "ID: " + posts.getId() + "\n";
                content += "Title: " + posts.getTitle() + "\n";
                content += "Author: " + posts.getAuthor() + "\n";
                content += "Published: " + posts.getPublished_date() + "\n";
                content += "Pages: " + posts.getPages() + "\n";
                content += "Language: " + posts.getLanguage() + "\n\n";

                text.append(content);
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {

            }
        });
    }

    private void getAllContent() {
        Call<List<Post>> call = jsonPlaceHolderApi.getAllContent();
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (!response.isSuccessful()) {
                    text.setText("Code: " + response.code());
                    return;
                }

                List<Post> posts = response.body();


                text.setText(posts.toString());
                for (Post post : posts) {
                    String content = "";
                    content += "ID: " + post.getId() + "\n";
                    content += "Title: " + post.getTitle() + "\n";
                    content += "Author: " + post.getAuthor() + "\n";
                    content += "Published: " + post.getPublished_date() + "\n";
                    content += "Pages: " + post.getPages() + "\n";
                    content += "Language: " + post.getLanguage() + "\n\n";

                    text.append(content);
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                text.setText(t.getMessage());
            }
        });
    }

    private void addFilterToIPAddressInput() {
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       android.text.Spanned dest, int dstart, int dend) {
                if (end > start) {
                    String destTxt = dest.toString();
                    String resultingTxt = destTxt.substring(0, dstart)
                            + source.subSequence(start, end)
                            + destTxt.substring(dend);
                    if (!resultingTxt
                            .matches("^\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")) {
                        return "";
                    } else {
                        String[] splits = resultingTxt.split("\\.");
                        for (int i = 0; i < splits.length; i++) {
                            if (Integer.valueOf(splits[i]) > 255) {
                                return "";
                            }
                        }
                    }
                }
                return null;
            }
        };
        ipAdd.setFilters(filters);
    }

    @Override
    protected void onResume() {
        super.onResume();
        firstRunCheck();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
    }
}
