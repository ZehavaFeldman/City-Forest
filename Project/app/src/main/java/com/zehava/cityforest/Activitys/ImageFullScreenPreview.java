package com.zehava.cityforest.Activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.zehava.cityforest.R;

import static com.zehava.cityforest.Constants.IMAGE_NAME;

/**
 * Created by avigail on 07/03/18.
 */

public class ImageFullScreenPreview extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activiye_image_preview);

        Intent intent = getIntent();
        String imageName = intent.getStringExtra(IMAGE_NAME);
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(imageName);

        ImageView imageView = findViewById(R.id.imageView);

        Glide.with(ImageFullScreenPreview.this /* context */)
                .using(new FirebaseImageLoader())
                .load(ref)
                .into(imageView);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
