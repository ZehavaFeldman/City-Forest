package com.zehava.cityforest.Activities;

/**
 * Created by avigail on 24/05/18.
 */



        import android.app.Fragment;
        import android.content.Intent;
        import android.os.Bundle;
        import android.support.annotation.NonNull;
        import android.support.design.widget.NavigationView;
        import android.support.v4.view.GravityCompat;
        import android.support.v4.widget.DrawerLayout;
        import android.support.v7.app.ActionBarDrawerToggle;
        import android.support.v7.app.AppCompatActivity;
        import android.support.v7.widget.Toolbar;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.ImageView;
        import android.widget.TextView;

        import com.bumptech.glide.Glide;

        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.ValueEventListener;
        import com.zehava.cityforest.Constants;
        import com.zehava.cityforest.FirebaseUtils;
        import com.zehava.cityforest.HomeFragment;
        import com.zehava.cityforest.Models.Post;
        import com.zehava.cityforest.Models.User;
        import com.zehava.cityforest.R;


public class PostListActivity  extends AppCompatActivity {

    protected String mModelKey;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list);

        Intent intent = getIntent();
        mModelKey = (String) intent.getSerializableExtra(Constants.EXTRA_MODEL_KEY);

        Bundle bundle = new Bundle();
        bundle.putString(Constants.EXTRA_MODEL_KEY, mModelKey);
        HomeFragment homeFragment = new HomeFragment();
        homeFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.container,homeFragment )
                .commit();




    }






}