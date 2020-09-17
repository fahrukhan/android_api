package com.fahru.restapi.menu;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fahru.restapi.R;
import com.fahru.restapi.adapter.DataAdapter;
import com.fahru.restapi.callback.JsonPlaceHolderApi;
import com.fahru.restapi.model.BaseModel;
import com.fahru.restapi.model.BookModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Book extends BaseModel implements OnSpinnerItemSelectedListener {
    NiceSpinner methodSpin;
    TextView address, test;
    String url;
    MaterialButton sendBtn;
    TextInputEditText path;
    JsonPlaceHolderApi jsonPlaceHolderApi;
    ArrayList<BookModel> model = new ArrayList<>();
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        test = findViewById(R.id.bookTextTest);
        sendBtn = findViewById(R.id.bookSendBtn);
        address = findViewById(R.id.bookAddress);
        path = findViewById(R.id.bookPath);
        recyclerView = findViewById(R.id.bookRV);
        model = new ArrayList<>();
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
                hideSoftKeyboard();
            }
        });

        setRecView();

    }

    private void setRecView() {
        adapter = new DataAdapter(model);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(Book.this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void doApi(String p) {
        if (model.size() > 0){
            model.clear();
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        Call<List<BookModel>> call = jsonPlaceHolderApi.getData(p);
        call.enqueue(new Callback<List<BookModel>>() {
            @Override
            public void onResponse(Call<List<BookModel>> call, Response<List<BookModel>> response) {
                if (!response.isSuccessful()) {
                    toastWarning(Book.this, response.code()+": "+response.message());
                    return;
                }

                List<BookModel> posts = response.body();
                for (BookModel post : posts) {
                    model.add(new BookModel(post.get_Id(), post.getTitle(), post.getAuthor(), post.getPublished_date(), post.getPages(), post.getLanguage()));
                    setRecView();
                }
            }

            @Override
            public void onFailure(Call<List<BookModel>> call, Throwable t) {
                toastError(Book.this, t.getMessage());
            }
        });

    }

    @Override
    public void onItemSelected(NiceSpinner parent, View view, int position, long id) {

    }
}
