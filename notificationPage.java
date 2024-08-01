package com.example.bestbrofinderlogin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class notificationPage extends AppCompatActivity {
    private UserInformation thisUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificationpage);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        RecyclerView recyclerView = findViewById(R.id.recyclerview);

        // Return to swipe page button
        ImageView returnToSwipe = findViewById(R.id.returnMain);
        returnToSwipe.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });




        DocumentReference docRef = db.collection("userProfileInformation").document(user.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                thisUser = documentSnapshot.toObject(UserInformation.class);
                ArrayList<String> matchUUID = thisUser.getCurrentMatches();
                List<userDataNotificationPage> userDisplay = new ArrayList<userDataNotificationPage>();
                for(int i = 0; i < matchUUID.size(); i++){
                    DocumentReference matchRef = db.collection("userProfileInformation").document(matchUUID.get(i));
                    matchRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            UserInformation matchUser = documentSnapshot.toObject(UserInformation.class);
                            // matchDisplay is where each matched users information is stored
                            // MyViewHolder is what is actually displayed on the page and it is displayed for each user in this array
                            userDataNotificationPage matchDisplay = new userDataNotificationPage(matchUser.getPhoneNumber(), matchUser.getFirstName(), matchUser.getImageRef());


                            userDisplay.add(matchDisplay);
                            recyclerView.setLayoutManager(new LinearLayoutManager(notificationPage.this)); // Pass context
                            recyclerView.setAdapter(new MyAdapter(getApplicationContext(), userDisplay));
                        }
                    });
                }
            }
        });
    }
}