package com.fahru.restapi.callback;

import com.fahru.restapi.Post;
import com.fahru.restapi.model.BaseModel;
import com.fahru.restapi.model.BookModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface JsonPlaceHolderApi {

    @GET("books/{id}")
    Call<Post> getSingleContentById(@Path("id") String idBook);

    @GET("books")
    Call<List<Post>> getAllContent();

    @GET("android")
    Call<Void> getConnect();

    @GET
    Call<List<BookModel>> getData(@Url String url);

    @GET
    Call<String> getDataOther(@Url String url);

    @GET
    Call<BookModel> getSingleData(@Url String url);

    @POST
    Call<BookModel> createPost(@Url String url,
                               @Body BookModel book);

    @DELETE
    Call<BookModel> delete(@Url String url);
}
