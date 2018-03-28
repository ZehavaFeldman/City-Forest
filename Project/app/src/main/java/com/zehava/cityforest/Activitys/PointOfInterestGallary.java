package com.zehava.cityforest.Activitys;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.support.v7.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.zehava.cityforest.Models.Image;
import com.zehava.cityforest.Models.PointOfInterest;
import com.zehava.cityforest.R;

import java.util.Map;
import java.util.ArrayList;

import android.support.v7.widget.RecyclerView;
import android.widget.ProgressBar;

import static com.zehava.cityforest.Constants.IMAGE_EXTRA;
import static com.zehava.cityforest.Constants.IMAGE_NAME;

public class PointOfInterestGallary extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference images;

    private StorageReference reallImgref;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private ProgressBar loading_progress_bar;

    private ArrayList<Image> imagesPaths = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pointofinterest_gallary);

        Intent intent = getIntent();
        String directoryName = intent.getStringExtra(IMAGE_NAME);
        //linearLayout = findViewById(R.id.innerLayout);

        database = FirebaseDatabase.getInstance();
        images = database.getReference("storage_images");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        loading_progress_bar = findViewById(R.id.loadingProgress);

        images.child(directoryName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> imagesMap = (Map<String, Object>)dataSnapshot.getValue();
                if(imagesMap == null) {

                    return;
                }
                int size = imagesMap.size();
                /*Iterating all the coordinates in the list*/
                for (Map.Entry<String, Object> entry : imagesMap.entrySet()) {
                    /*For each coordinate in the database, we want to create a new marker
                    * for it and to show the marker on the map*/
                    Map<String, Object> image = ((Map<String, Object>) entry.getValue());

                    String imagePath = (String) image.get("imagePath");
                    imagesPaths.add(new Image(imagePath));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_images);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        PointOfInterestGallary.ImageGalleryAdapter adapter = new PointOfInterestGallary.ImageGalleryAdapter(this, imagesPaths);
        recyclerView.setAdapter(adapter);
    }

    private class ImageGalleryAdapter extends RecyclerView.Adapter<ImageGalleryAdapter.MyViewHolder>  {

        @Override
        public ImageGalleryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View photoView = inflater.inflate(R.layout.item_layout, parent, false);
            ImageGalleryAdapter.MyViewHolder viewHolder = new ImageGalleryAdapter.MyViewHolder(photoView);
            return viewHolder;
        }


        @Override
        public void onBindViewHolder(ImageGalleryAdapter.MyViewHolder holder, final int position) {

            Image image = mImages.get(position);
            ImageView imageView = holder.mPhotoImageView;

            reallImgref = storageReference.child(image.getImagePath());

            Glide.with(mContext /* context */)
                    .using(new FirebaseImageLoader())
                    .load(reallImgref)
                    .listener(new RequestListener<StorageReference, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, StorageReference model, Target<GlideDrawable> target, boolean isFirstResource) {
                            if(position == (mImages.size()-1))
                               loading_progress_bar.setVisibility(View.INVISIBLE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            if(position == (mImages.size()-1))
                                loading_progress_bar.setVisibility(View.INVISIBLE);
                            return false;
                        }
                    })
                    .into(imageView);

        }
        @Override
        public int getItemCount() {
            return (mImages.size());
        }

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public ImageView mPhotoImageView;

            public MyViewHolder(View itemView) {

                super(itemView);
                mPhotoImageView = (ImageView) itemView.findViewById(R.id.iv_photo);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {

                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION) {
                    Image image = mImages.get(position);
                    Intent intent = new Intent(mContext, ImageFullScreenPreview.class);
                    intent.putExtra(IMAGE_NAME, image.getImagePath());
                    startActivity(intent);
                }
            }
        }



        private ArrayList<Image> mImages;
        private Context mContext;

        public ImageGalleryAdapter(Context context, ArrayList<Image> Images) {
            mContext = context;
            mImages = Images;
        }
    }
}
