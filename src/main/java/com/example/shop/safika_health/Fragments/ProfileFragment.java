package com.example.shop.safika_health.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shop.safika_health.Activities.HomeActivity;
import com.example.shop.safika_health.Activities.LoginActivity;
import com.example.shop.safika_health.Activities.MainActivity;
import com.example.shop.safika_health.Activities.TermsActivity;
import com.example.shop.safika_health.Adapter.HealthIssueAdapter;
import com.example.shop.safika_health.BuildConfig;
import com.example.shop.safika_health.Model.Products;
import com.example.shop.safika_health.NewEncrypter;
import com.example.shop.safika_health.Prevalent.Prevalent;
import com.example.shop.safika_health.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import io.paperdb.Paper;


public class ProfileFragment extends Fragment {


    public static ProfileFragment getInstance(){
        return new ProfileFragment();
    }

    String [] healthIssues = new String[Prevalent.problems.size()];
    boolean [] healthIssues_check = new boolean[Prevalent.problems.size()];
    List<String> my_issues = Prevalent.currentOnlineUser.getHealthProblems();

    private RecyclerView mRecycleView;
    private ImageView photo;
    RecyclerView.LayoutManager layoutMAnager;
    HealthIssueAdapter adapter;
    FirebaseStorage storage;
    StorageReference storageReference;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);


        for (int i = 0;i<Prevalent.problems.size();i++) {
            healthIssues[i] = Prevalent.problems.get(i).getName();
            healthIssues_check[i] = false;
        }
        mRecycleView = view.findViewById(R.id.id_profile_list);
        photo = view.findViewById(R.id.id_profile_photo);
        Picasso.get()
                .load(Prevalent.currentOnlineUser.getPhoto())
                .placeholder(R.drawable.profile)
                .into(photo);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        mRecycleView.setLayoutManager(layoutManager);
        adapter = new HealthIssueAdapter(new ArrayList<String>());
        mRecycleView.setAdapter(adapter);
        if (my_issues.size() >= 1) {
            for (int i =0;i<healthIssues.length;i++) {
                if (my_issues.contains(healthIssues[i])) {
                    healthIssues_check[i] = true;
                }
            }
            adapter.setModels(my_issues);
        }


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        TextView name = view.findViewById(R.id.id_profile_name);
        name.setText(Prevalent.currentOnlineUser.getName());


        view.findViewById(R.id.id_profile_signout).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MainActivity.class);
                Prevalent.currentOnlineUser = null;
                Prevalent.all_products = new ArrayList<Products>();
                Prevalent.recommended_products = new ArrayList<Products>();
                Prevalent.favorited_products = new ArrayList<Products>();
                Prevalent.product_page_index = 0;
                Paper.book().delete(Prevalent.UserEmailKey);
                Paper.book().delete(Prevalent.UserPasswordkey);
                startActivity(intent);
            }
        });

        view.findViewById(R.id.id_profile_issues).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Add Health Issues");

                builder.setMultiChoiceItems(healthIssues, healthIssues_check, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        // user checked or unchecked a box
                        if (isChecked) {
                            my_issues.add(healthIssues[which]);
                        }
                        else {
                            my_issues.remove(healthIssues[which]);
                        }

                    }
                });

                builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {                        // user clicked OK

                        final List<String> new_issues = new ArrayList<>();
                        for(String issue: my_issues) {
                            try {
                                String encrypted = NewEncrypter.encrypt(issue);
                                new_issues.add(encrypted);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        final DatabaseReference RootRef;
                        RootRef = FirebaseDatabase.getInstance().getReference().child("Users");

                        RootRef.orderByChild("id").equalTo(Prevalent.currentOnlineUser.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                if((dataSnapshot.exists())) {
                                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                                        snapshot.getRef().child("healthProblems").setValue(new_issues);
                                        Prevalent.currentOnlineUser.setHealthProblems(my_issues);
                                    }

                                } else {
                                    Toast.makeText(getContext(),"This product is not exist", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        adapter.setModels(my_issues);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        view.findViewById(R.id.id_profile_favo).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.id_profile_terms).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), TermsActivity.class);
                startActivity(intent);
            }
        });

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Do you want to change profile photo?");
                final EditText input = new EditText(getContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pickFromGallery();

                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });

                builder.show();
            }
        });

        return view;
    }

    private void pickFromGallery(){
        //Create an Intent with action as ACTION_PICK
        Intent intent=new Intent(Intent.ACTION_PICK);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        // Launching the Intent
        startActivityForResult(intent,100);

    }

    private void captureFromCamera() {

        Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, 200);
    }

    private String cameraFilePath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //This is the directory in which the file will be created. This is the default location of Camera photos
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for using again
        cameraFilePath = "file://" + image.getAbsolutePath();
        return image;
    }

    public void onActivityResult(int requestCode,int resultCode,Intent data){

        // Result code is RESULT_OK only if the user selects an Image
        if (resultCode == Activity.RESULT_OK) {

            Uri filePath = data.getData();
            final ProgressDialog progressDialog
                    = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            final StorageReference ref
                    = storageReference
                    .child(
                            "images/"
                                    + UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            final Uri downloadUrl = uri;
                                            final DatabaseReference RootRef;
                                            RootRef = FirebaseDatabase.getInstance().getReference().child("Users");

                                            RootRef.orderByChild("id").equalTo(Prevalent.currentOnlineUser.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                                    if((dataSnapshot.exists())) {
                                                        for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                                                            snapshot.getRef().child("photo").setValue(downloadUrl.toString());
                                                        }

                                                    } else {
                                                        Toast.makeText(getContext(),"This product is not exist", Toast.LENGTH_SHORT).show();
                                                    }

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });

                                            Picasso.get()
                                                    .load(uri.toString())
                                                    .placeholder(R.drawable.profile)
                                                    .into(photo);


                                        }
                                    });
                                    progressDialog.dismiss();

                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast
                                    .makeText(getContext(),
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int)progress + "%");
                                }
                            });
        }

    }

    public static SecretKey generateKey()
            throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        SecretKey secret = new SecretKeySpec("aesEncryptionKey".getBytes(), "AES");
        return secret;
    }

    public static byte[] encryptMsg(String message, SecretKey secret)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidParameterSpecException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException
    {
        /* Encrypt the message. */
        Cipher cipher = null;
        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secret);
        byte[] cipherText = cipher.doFinal(message.getBytes("UTF-8"));
        return cipherText;
    }

    public static String decryptMsg(byte[] cipherText, SecretKey secret)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidParameterSpecException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException
    {
        /* Decrypt the message, given derived encContentValues and initialization vector. */
        Cipher cipher = null;
        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secret);
        String decryptString = new String(cipher.doFinal(cipherText), "UTF-8");
        return decryptString;
    }

}

