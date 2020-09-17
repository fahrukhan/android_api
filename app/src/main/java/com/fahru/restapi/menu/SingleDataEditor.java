package com.fahru.restapi.menu;

import androidx.appcompat.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.fahru.restapi.R;
import com.fahru.restapi.callback.JsonPlaceHolderApi;
import com.fahru.restapi.model.BaseModel;
import com.fahru.restapi.model.BookModel;
import com.fahru.restapi.model.SettingLoader;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class SingleDataEditor extends BaseModel {
    TextInputEditText title, author, page, lang, publish;
    MaterialButton saveBtn;
    JsonPlaceHolderApi jsonPlaceHolderApi;
    String id, url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_data_editor);

        title = findViewById(R.id.sde_title);
        author = findViewById(R.id.sde_author);
        page = findViewById(R.id.sde_pages);
        lang = findViewById(R.id.sde_lang);
        publish = findViewById(R.id.sde_published);
        saveBtn = findViewById(R.id.sde_save_btn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkPostPath()){
                    createPostPathDialog();
                }else {
                    createPost();
                }
            }
        });

        url = settings.getString("url_connect", null);
        if (url == null){
            toastWarning(this, "You are disconnected from server");
            saveBtn.setEnabled(false);
        }

        id = getIntent().getStringExtra("key");
        if (id != null){
            setData(id);
        }else {
            toastWarning(this, "Something wrong! contact developer");
        }
    }

    private void createPost() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        String p = settings.getString("postPath", null);
        BookModel book = new BookModel(title.getText().toString(),
                author.getText().toString(),
                publish.getText().toString(),
                Integer.parseInt(page.getText().toString()),
                lang.getText().toString());

        Call<BookModel> call = jsonPlaceHolderApi.createPost(p.replace("{id}", id), book);
        call.enqueue(new Callback<BookModel>() {
            @Override
            public void onResponse(Call<BookModel> call, Response<BookModel> response) {
                if (!response.isSuccessful()) {
                    toastWarning(SingleDataEditor.this, response.code()+": "+response.message());
                }

                toastSuccess(SingleDataEditor.this, "Book Successfully Updated");
                startActivity( new Intent(SingleDataEditor.this, Book.class));
            }

            @Override
            public void onFailure(Call<BookModel> call, Throwable t) {
                toastError(SingleDataEditor.this, t.getMessage());
            }
        });
    }

    private void createPostPathDialog() {
        final AlertDialog.Builder inputDialog = new AlertDialog.Builder(SingleDataEditor.this);
        View inputView = LayoutInflater.from(SingleDataEditor.this).inflate(R.layout.custom_dialog_set_path, null);
        TextInputEditText inputPath = inputView.findViewById(R.id.custom_dialog_set_path_input);
        inputDialog.setTitle("SET POST PATH TO UPDATE DATA");
        inputPath.setHint("example: book/update/{id}");

        inputDialog.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String path = inputPath.getText().toString();
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("postPath", path).apply();
                Intent intent = new Intent(SingleDataEditor.this, SettingLoader.class);
                intent.putExtra("key", "singleGetPath");
                startActivity(intent);
            }
        });
        inputDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        inputDialog.setView(inputView);
        inputDialog.show();
    }

    private boolean checkPostPath() {
        String postPath = settings.getString("postPath", null);
        return postPath != null;
    }

    private void setData(String id) {
        String p = settings.getString("singleGetPath", null);
        if (p != null){
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
            Call<BookModel> call = jsonPlaceHolderApi.getSingleData(p.replace("{id}", id));
            call.enqueue(new Callback<BookModel>() {
                @Override
                public void onResponse(Call<BookModel> call, Response<BookModel> response) {
                    if (!response.isSuccessful()) {
                        toastWarning(SingleDataEditor.this, response.code()+": "+response.message());
                    }else {
                        BookModel book = response.body();
                        String title1 = book.getTitle();
                        String author1 = book.getAuthor();
                        String page1 = String.valueOf(book.getPages());
                        String lang1 = book.getLanguage();
                        String publish1 = book.getPublished_date();
                        title.setText(title1);
                        author.setText(author1);
                        page.setText(page1);
                        lang.setText(lang1);
                        publish.setText(publish1);
                    }
                }

                @Override
                public void onFailure(Call<BookModel> call, Throwable t) {
                    toastError(SingleDataEditor.this, t.getMessage());
                }
            });
        }else {
            toastWarning(SingleDataEditor.this, "Oops! Something wrong with your path, please check your PATH SETTING.");
        }

    }
}
