package com.example.bestbrofinderlogin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    Context context;
    List<userDataNotificationPage> userDataNotificationPages;
    StorageReference notiReference;
    private FirebaseStorage notificationImageStorage = FirebaseStorage.getInstance();

    public MyAdapter(Context context, List<userDataNotificationPage> userDataNotificationPages) {
        this.context = context;
        this.userDataNotificationPages = userDataNotificationPages;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_view,parent,false));
    }

    // Updates each individuals first name and phone number in notification screen
    @Override
    public void onBindViewHolder(@NonNull  MyViewHolder holder, int position) {
        holder.firstName.setText(userDataNotificationPages.get(position).getFirstName());
        holder.phoneNumber.setText(userDataNotificationPages.get(position).getPhoneNumber());

        notiReference = notificationImageStorage.getReference("Images/"+userDataNotificationPages.get(position).getImageUrl());
        try {
            File localfile = File.createTempFile("tempFile","");
            notiReference.getFile(localfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Picasso.get().load(localfile).into(holder.userImage);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



    }

    @Override
    public int getItemCount() {
        return userDataNotificationPages.size();
    }
}
