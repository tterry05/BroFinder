package com.example.bestbrofinderlogin;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bestbrofinderlogin.databinding.ActivityMainBinding;
import com.example.bestbrofinderlogin.databinding.ActivityProfileBinding;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MyViewHolder extends RecyclerView.ViewHolder {

    ImageView userImage;
    TextView firstName, phoneNumber;


    public MyViewHolder(@NonNull View itemView) {
        super(itemView);


        // Replace userImage with however we can enable user uploaded images
        userImage = itemView.findViewById(R.id.imageview);
        firstName = itemView.findViewById(R.id.firstName);
        phoneNumber = itemView.findViewById(R.id.phoneNumber);
    }



}
