package com.example.bestbrofinderlogin;
import static android.content.ContentValues.TAG;
import static android.webkit.ConsoleMessage.MessageLevel.LOG;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bestbrofinderlogin.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Document;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ImageView profilePic;
    FirebaseAuth auth;
    ImageView buttonProfile;
    Button buttonLogout;
    ImageView buttonNotification;
    Button buttonYes;
    Button buttonNo;
    TextView userName;
    TextView userBio;
    FirebaseUser user;
    String userPhoneNumber;
    ArrayList<UserInformation> userInfoList;
    ArrayList<String> userInfoId;
    ArrayList<DocumentReference> userInfoDocuments;
    ArrayList<String> currentMatches;
    DocumentReference currentUser;
    FirebaseUser[] otherUsers;
    FirebaseStorage mainStorage = FirebaseStorage.getInstance();
    StorageReference storages;
    ActivityMainBinding bindings;
    String imageNames;
    int currentUserIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bindings =ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(bindings.getRoot());

        auth = FirebaseAuth.getInstance();
        buttonProfile = findViewById(R.id.profileButton);
        buttonYes = findViewById(R.id.yesButton);
        buttonNo = findViewById(R.id.noButton);
        userName = findViewById(R.id.user_name);
        userBio = findViewById(R.id.user_details);
        buttonNotification = findViewById(R.id.notificationButton);
        userInfoList = new ArrayList<UserInformation>();
        user = auth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        currentUser = db.collection("userProfileInformation").document(user.getUid());
        ArrayList<String> userInfoId = new ArrayList<>();
        ArrayList<DocumentReference> userInfoDocuments = new ArrayList<>();
        ArrayList<String> currentMatches = new ArrayList<>();


        // Start listing users from the beginning, 1000 at a time.]



        if(user==null){
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }
        DocumentReference docRef = db.collection("userProfileInformation").document(user.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    UserInformation userInfo = documentSnapshot.toObject(UserInformation.class);
                    userPhoneNumber = userInfo.getPhoneNumber();
                    for(int i = 0; i < userInfo.getCurrentMatches().size(); i++){
                        if(userInfo.getCurrentMatches().get(i) != null) {
                            currentMatches.add(userInfo.getCurrentMatches().get(i));
                        }
                    }
                }
                else{
                    Log.d("User Info", "User information doesn't exist in database");
                }

            }
        });


        db.collection("userProfileInformation")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Log.d("NULL POINTER TEST", document.getId() + " => " + document.getData());
                                userInfoList.add(document.toObject(UserInformation.class));
                                String id = document.getId();
                                if(document.getId() != null) {
                                    userInfoId.add(id);
                                }
                                if(document.getReference() != null){
                                    userInfoDocuments.add(document.getReference());
                                }

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        if(userInfoList.get(currentUserIndex)!=null){
                            if(userInfoList.get(currentUserIndex).getPhoneNumber().equals(userPhoneNumber)) {
                                currentUserIndex++;
                                if (userInfoList.size() - 1 < currentUserIndex) {
                                    currentUserIndex = 0;
                                }
                            }
                            else {
                                UserInformation firstUser = userInfoList.get(currentUserIndex);
                                userName.setText(firstUser.getFirstLast());
                                userBio.setText(firstUser.getBioStuff());

                                imageNames = firstUser.getImageRef();
                                //Retrieves image from database
                                storages = mainStorage.getReference("Images/"+imageNames);
                                try {
                                    File localfile = File.createTempFile("tempFile","");
                                    storages.getFile(localfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                            Bitmap bitmap= BitmapFactory.decodeFile(localfile.getAbsolutePath());
                                            bindings.profilePic.setImageBitmap(bitmap);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(MainActivity.this, "Failed",Toast.LENGTH_SHORT);
                                        }
                                    });
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                        else{
                            userName.setText("NO USERS EXIST?!");
                        }

                    }
                });


        buttonYes.setOnClickListener(new View.OnClickListener() {
            /*
                This is the entire section of code dealing with users being able to match with each other
                Only god understands how it works at this point
             */
            @Override
            public void onClick(View v) {

                db.collection("userMatches")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    boolean alreadyMatched = false;
                                    // goes through every single document in usermatches
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        boolean breakHere = false;
                                        Log.d("TEST FIREBASE", document.getId() + " => " + document.getData().get("currentUser"));
                                        // testing for if already matched
                                        for(int i = 0; i < currentMatches.size(); i++){
                                            if (currentMatches.get(i).equals(userInfoId.get(currentUserIndex))){
                                                breakHere = true;
                                                break;
                                            }
                                        }
                                        // if they already matched break out of for loop
                                        if(breakHere){
                                            break;
                                        }

                                        // testing for if other user matched with them
                                        if(document.getData().get("currentUser").equals(userInfoId.get(currentUserIndex)) && document.getData().get("targetUser").equals(user.getUid())){
                                            DocumentReference otherUser = userInfoDocuments.get(currentUserIndex);
                                            otherUser.update("currentMatches", FieldValue.arrayUnion(user.getUid()));
                                            currentUser.update("currentMatches", FieldValue.arrayUnion(userInfoId.get(currentUserIndex)));
                                        }
                                        // testing if current user already matched
                                        if(document.getData().get("currentUser").equals(user.getUid()) && document.getData().get("targetUser").equals(userInfoId.get(currentUserIndex))){
                                            alreadyMatched = true;
                                            break;
                                        }
                                    }
                                    // updating currentMatches
                                    if(!alreadyMatched) {
                                        Map<String, String> docData = new HashMap<>();
                                        docData.put("currentUser", user.getUid());
                                        docData.put("targetUser", userInfoId.get(currentUserIndex));
                                        db.collection("userMatches").document(String.valueOf(Math.random())).set(docData);
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
                nextUser();
            }

        });

        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextUser();
            }
        });
        // Method for switching to profile screen from login screen.
        buttonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Profile.class);
                startActivity(intent);
                finish();
            }
        });

        // Switching to notification screen
        buttonNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), notificationPage.class);
                startActivity(intent);
                finish();
            }
        });

    }

    /*
        This is the code responsible for updating
        the swipe page when you click yes or no for the user
     */
    void nextUser(){
        currentUserIndex++;
        if (userInfoList.size() - 1 < currentUserIndex) {
            currentUserIndex = 0;
        }
        while (userPhoneNumber.equals(userInfoList.get((currentUserIndex)).getPhoneNumber())) {
                currentUserIndex++;
                if (userInfoList.size() - 1 < currentUserIndex) {
                    currentUserIndex = 0;
                }
            }


        if (userInfoList.size() - 1 < currentUserIndex) {
            currentUserIndex = 0;
        }
        if(userInfoList.get(currentUserIndex)!=null){
            UserInformation firstUser = userInfoList.get(currentUserIndex);
            userName.setText(firstUser.getFirstLast());
            userBio.setText(firstUser.getBioStuff());

            imageNames = firstUser.getImageRef();
            //Retrieves image from database
            storages = mainStorage.getReference("Images/"+imageNames);
            try {
                File localfile = File.createTempFile("tempFile","");
                storages.getFile(localfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap bitmap= BitmapFactory.decodeFile(localfile.getAbsolutePath());
                        bindings.profilePic.setImageBitmap(bitmap);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Failed",Toast.LENGTH_SHORT);
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else{
            userName.setText("NO USERS EXIST?!");
        }
    }
}