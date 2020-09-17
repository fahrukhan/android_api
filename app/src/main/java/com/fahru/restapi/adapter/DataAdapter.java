package com.fahru.restapi.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.fahru.restapi.R;
import com.fahru.restapi.SplashScreen;
import com.fahru.restapi.menu.SingleDataEditor;
import com.fahru.restapi.model.BaseModel;
import com.fahru.restapi.model.BookModel;
import com.fahru.restapi.model.SettingLoader;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.viewHolder> {
    private ArrayList<BookModel> list;
    private DataAdapter adapter;

    public DataAdapter(ArrayList<BookModel> list) {
        this.list = list;
        this.adapter = this;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_data, parent, false);
        viewHolder vh = new viewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        BookModel book = list.get(position);
        holder.id.append(book.get_Id());
        holder.body.append(
                "title: "+book.getTitle()+
                "\nauthor: "+book.getAuthor()+
                "\npages: "+book.getPages()+
                "\nlanguage: "+book.getLanguage()+
                "\npublished_date\t: "+book.getPublished_date());
        holder.linlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(holder.context, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView id, body;
        Context context;
        LinearLayout linlay;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.itemDataId);
            body = itemView.findViewById(R.id.itemDataBody);
            linlay = itemView.findViewById(R.id.itemDataLinLay);
            context = itemView.getContext();
        }
    }

    private void showDialog(Context context, int i){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final CharSequence[] dialogItem = {"Get Single Data","Delete"};
        builder.setTitle("Pilihan Menu");
        builder.setItems(dialogItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i3) {
                switch (i3) {
                    case 0:
                        if (!checkSinglePath()){
                            createPathDialog(context);
                        }else {
                            Intent intent = new Intent(context, SingleDataEditor.class);
                            intent.putExtra("key", list.get(i).get_Id());
                            context.startActivity(intent);
                        }
//                        BaseGate.toastError(context, "Get Single Data "+list.get(i).get_Id()+" "+Boolean.toString(checkSinglePath()));
                        break;
                    case 1:
                        BaseGate.toastSuccess(context, "Edit");
                        break;
                }
            }
        });
        builder.create().show();
    }

    private void createPathDialog(Context context) {
        final AlertDialog.Builder inputDialog = new AlertDialog.Builder(context);
        View inputView = LayoutInflater.from(context).inflate(R.layout.custom_dialog_set_path, null);
        TextInputEditText inputPath = inputView.findViewById(R.id.custom_dialog_set_path_input);
//        TextView tit = inputView.findViewById(R.id.custom_dialog_set_path_input);
        inputDialog.setTitle("SET PATH FOR SINGLE GET");
        inputPath.setHint("ex: data/{id}");

        inputDialog.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String name = inputPath.getText().toString();
                SharedPreferences.Editor editor = BaseGate.settings.edit();
                editor.putString("singleGetPath", name).apply();
                Intent intent = new Intent(context, SettingLoader.class);
                intent.putExtra("key", "singleGetPath");
                context.startActivity(intent);
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

    private boolean checkSinglePath() {
        String singlePath = BaseGate.settings.getString("singleGetPath", null);
        return singlePath != null;
    }

    @SuppressLint("Registered")
    public static class BaseGate extends BaseModel{}
}
