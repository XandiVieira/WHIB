package com.relyon.whib;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.relyon.whib.modelo.Product;
import com.relyon.whib.modelo.Util;

import java.util.Date;
import java.util.UUID;

public class AdmCreateStoreItem extends AppCompatActivity {

    public static final int PICK_IMAGE = 1;
    public static final int PERMISSION_CODE = 16;
    private EditText title;
    private EditText description;
    private EditText price;
    private ImageView image;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adm_create_store_item);

        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        price = findViewById(R.id.price);
        image = findViewById(R.id.image);

        image.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(AdmCreateStoreItem.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_CODE);
            } else {
                openGallery();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            if (!title.getText().toString().trim().equals("")) {
                if (!description.getText().toString().trim().equals("")) {
                    if (!price.getText().toString().trim().equals("")) {
                        if (data != null && data.getData() != null && !data.getData().toString().trim().equals("")) {
                            Glide.with(getApplicationContext()).load(data.getData()).into(image);
                            Product product = new Product(UUID.randomUUID().toString(), data.getData().toString(), title.getText().toString(), description.getText().toString(), Float.parseFloat(price.getText().toString()), new Date().getTime());
                            //Firebase
                            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                            uploadImage(data.getData(), storageReference, product);
                        } else {
                            Toast.makeText(getApplicationContext(), "Não esqueça da imagem.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Dê um preço ao item.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Dê uma descrição ao item.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Dê um nome ao item.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            }
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE);
    }

    private void uploadImage(Uri filePath, StorageReference storageReference, Product product) {

        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/" + product.getTitle());
            ref.putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> {
                        progressDialog.dismiss();
                        Toast.makeText(AdmCreateStoreItem.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        Util.mDatabaseRef.child("product").child(product.getItemSKU()).setValue(product);
                        title.setText("");
                        description.setText("");
                        price.setText("");
                        image.setImageDrawable(null);
                        Toast.makeText(getApplicationContext(), "Produto criado com sucesso", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(AdmCreateStoreItem.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + (int) progress + "%");
                    });
        }
    }
}