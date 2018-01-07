package com.example.ekene.imageboardcloudinary;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cloudinary.Transformation;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button uploadBtn;
    private ProgressBar progressBar;
    private int SELECT_VIDEO = 2;
    private ImageView img1, img2, img3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img1 = findViewById(R.id.img1);
        img2 = findViewById(R.id.img2);
        img3 = findViewById(R.id.img3);
        progressBar = findViewById(R.id.progress_bar);
        uploadBtn = findViewById(R.id.uploadBtn);

        MediaManager.init(this);
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickVideoFromGallery();

            }
            private void pickVideoFromGallery() {
                Intent GalleryIntent = new Intent();
                GalleryIntent.setType("video/*");
                GalleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(GalleryIntent, "select video"), SELECT_VIDEO);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {

        if (requestCode == SELECT_VIDEO && resultCode == RESULT_OK) {

            Uri selectedVideo = data.getData();
            MediaManager.get()
                    .upload(selectedVideo)
                    .unsigned("kennyy")
                    .option("resource_type", "video")
                    .callback(new UploadCallback() {
                        @Override
                        public void onStart(String requestId) {
                            progressBar.setVisibility(View.VISIBLE);
                            Toast.makeText(MainActivity.this, "Upload Started...", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onProgress(String requestId, long bytes, long totalBytes) {

                        }

                        @Override
                        public void onSuccess(String requestId, Map resultData) {

                            Toast.makeText(MainActivity.this, "Uploaded Succesfully", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            uploadBtn.setVisibility(View.INVISIBLE);

                            String publicId = resultData.get("public_id").toString();

                            String firstImgUrl = MediaManager.get().url().transformation(new Transformation().startOffset("12")
                                    .border("5px_solid_black").border("5px_solid_black")).resourceType("video")
                                    .generate(publicId+".jpg");
                            Picasso.with(getApplicationContext()).load(firstImgUrl).into(img1);

                            String secondImgUrl = MediaManager.get().url().transformation(new Transformation().startOffset("4")
                                    .width(200).height(150).radius(20).effect("saturation:50").border("5px_solid_black"))
                                    .resourceType("video").generate(publicId+".jpg");
                            Picasso.with(getApplicationContext()).load(secondImgUrl).into(img2);

                            String thirdImgUrl = MediaManager.get().url().transformation(new Transformation().startOffset("20")
                                    .width(200).height(150).radius(20).effect("grayscale").border("5px_solid_black").crop("crop"))
                                    .resourceType("video").generate(publicId+".jpg");
                            Picasso.with(getApplicationContext()).load(thirdImgUrl).into(img3);

                        }

                        @Override
                        public void onError(String requestId, ErrorInfo error) {

                            Toast.makeText(MainActivity.this, "Upload Error", Toast.LENGTH_SHORT).show();
                            Log.v("ERROR!!", error.getDescription());
                        }

                        @Override
                        public void onReschedule(String requestId, ErrorInfo error) {

                        }

                    }).dispatch();
        }else {

            Toast.makeText(MainActivity.this, "Can't Upload", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {

    }
}