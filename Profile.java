package com.example.bestbrofinderlogin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bestbrofinderlogin.databinding.ActivityMainBinding;
import com.example.bestbrofinderlogin.databinding.ActivityProfileBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.OnProgressListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;


public class Profile extends AppCompatActivity {

    TextInputEditText editFirstName, editLastName, editLocation, editAge, editSchedule, editGender, editBio, editPhoneNumber;
    ProgressBar progressBar;
    ImageView userImage;
    Button button;
    Button buttonLogout;
    FirebaseUser user;
    FirebaseAuth auth;
    Button btnUpload;

    StorageReference storageReference;

    ActivityResultLauncher<Intent> resultLauncher;
    FirebaseStorage storage =FirebaseStorage.getInstance();
    StorageReference fileRef;
    String imageName;

    ActivityProfileBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Image layout that has to be here for text to be displayed in boxes
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        // Initializing all objects
        userImage = findViewById(R.id.userImage);
        editFirstName = findViewById(R.id.userFirstName);
        editLastName = findViewById(R.id.userLastName);
        editLocation = findViewById(R.id.userLocation);
        editAge = findViewById(R.id.userAge);
        editSchedule = findViewById(R.id.userSchedule);
        editGender = findViewById(R.id.userGender);
        editBio = findViewById(R.id.userBio);
        editPhoneNumber = findViewById(R.id.userPhoneNumber);
        progressBar = findViewById(R.id.progressBarProfile);
        button = findViewById(R.id.btn_submitProfile);
        buttonLogout = findViewById(R.id.logoutButton);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();



        // Return to swipe page button
        ImageView returnToSwipe = findViewById(R.id.returnMain);
        returnToSwipe.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        /*
        Block of code responsible for user picking image to upload
         */
        btnUpload = findViewById(R.id.btnUpload);
        registerResult();
        btnUpload.setOnClickListener(view -> pickImage());

        /*
        This block of code searches the database for the users information
        The collection path "userProfileInformation" is where the data for all users is stored
        The document user.getUid() is the path of the users specific information
        If it finds information in the database for the user already it updates the
        text boxes in the activity_profile.xml to be the data in the database already
         */
        /*
            Another note, this is how we call a specific users information
            DocumentReference docRef = db.collection("userProfileInformation").document(user.getUid());
            If you would want to grab all users or other users you will have to look in the Firestore
            documentation to figure that out
         */
        DocumentReference docRef = db.collection("userProfileInformation").document(user.getUid());

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    UserInformation userInfo = documentSnapshot.toObject(UserInformation.class);
                    editFirstName.setText(userInfo.getFirstName());
                    editLastName.setText(userInfo.getLastName());
                    editAge.setText(String.valueOf(userInfo.getAge()));
                    editLocation.setText(userInfo.getLocation());
                    editBio.setText(userInfo.getBio());
                    editSchedule.setText(userInfo.getSchedule());
                    editGender.setText(userInfo.getGender());
                    editPhoneNumber.setText(userInfo.getPhoneNumber());
                    if(userInfo.getImageRef()!=null)
                        imageName = userInfo.getImageRef();


                    //Retrieves image from database
                    storageReference = FirebaseStorage.getInstance().getReference("Images/"+imageName);
                    try {
                        File localfile = File.createTempFile("tempFile","");
                        storageReference.getFile(localfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Bitmap bitmap= BitmapFactory.decodeFile(localfile.getAbsolutePath());
                                binding.userImage.setImageBitmap(bitmap);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Profile.this, "Failed",Toast.LENGTH_SHORT);
                            }
                        });
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }



                }
                else{
                    Log.d("User Info", "User information doesn't exist in database");
                }

            }
        });


        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
                FirebaseAuth.getInstance().signOut();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String firstName, lastName, location, schedule, gender, bio, phoneNumber,imageRef;
                int age;
                firstName = String.valueOf(editFirstName.getText());
                lastName = String.valueOf(editLastName.getText());
                location = String.valueOf(editLocation.getText());
                schedule = String.valueOf(editSchedule.getText());
                gender = String.valueOf(editGender.getText());
                phoneNumber = String.valueOf(editPhoneNumber.getText());
                bio = String.valueOf(editBio.getText());
                imageRef = imageName;
                try {
                    age = Integer.parseInt(String.valueOf(editAge.getText()));
                } catch (NumberFormatException e) {
                    Toast.makeText(Profile.this, "Enter a valid age", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(firstName)){
                    Toast.makeText(Profile.this, "Enter First Name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(lastName)){
                    Toast.makeText(Profile.this, "Enter Last Name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(location)){
                    Toast.makeText(Profile.this, "Enter Location", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(schedule)){
                    Toast.makeText(Profile.this, "Enter Schedule", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(gender)){
                    Toast.makeText(Profile.this, "Enter Gender", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(bio)){
                    Toast.makeText(Profile.this, "Enter Bio", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(phoneNumber)){
                    Toast.makeText(Profile.this, "Enter Phone Number", Toast.LENGTH_SHORT).show();
                    return;
                }


                /*
                This block of code is responsible for uploading information to the database.
                The information entered is turned into a UserInformation class
                This class is uploaded to the database and parsed by the server to properly upload it
                 */
                UserInformation userInfo = new UserInformation(firstName,lastName, location, schedule, gender, bio, age, phoneNumber, new ArrayList<String>(), imageRef);

                // This is the actual server call to upload
                // "userProfileInformation" is where all user information is stored
                // "user.getUid()" is where the specific users information is stored
                db.collection("userProfileInformation").document(user.getUid()).set(userInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Profile.this, "Data success", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Profile.this, "Data failure", Toast.LENGTH_SHORT).show();
                            }
                        });
                progressBar.setVisibility(View.GONE);

            }
        });
    }

    // User picks an image
    private void pickImage(){
        Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
        resultLauncher.launch(intent);
    }

    private void registerResult(){
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        try{
                            Uri imageUri =result.getData().getData();
                            userImage.setImageURI(imageUri);
                            imageName = UUID.randomUUID().toString();

                            // Set the imageRef to the generated image name


                            // Upload the image to Firebase Storage
                            storageReference = storage.getReference().child("Images");
                            fileRef = storageReference.child(imageName);
                            UploadTask uploadTask = fileRef.putFile(imageUri);



                        }
                        catch(Exception e){
                            Toast.makeText(Profile.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

}