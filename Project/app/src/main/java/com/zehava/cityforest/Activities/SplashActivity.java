package com.zehava.cityforest.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.zehava.cityforest.R;

/**
 * Created by avigail on 06/01/18.
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //show applicaion splash screen while appliaction data being uploaded

        setContentView(R.layout.activity_spalsh);

        TextView title_txt = (TextView)findViewById(R.id.titleTxt);
        TextView snippet_txt = (TextView)findViewById(R.id.snippetTxt);
        Typeface typeFaceBold=Typeface.createFromAsset(getAssets(),"fonts/Alef-Bold.ttf");
        Typeface typeFaceReg=Typeface.createFromAsset(getAssets(),"fonts/Alef-Regular.ttf");
        title_txt.setTypeface(typeFaceBold);
        snippet_txt.setTypeface(typeFaceReg);

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {

                Intent intent = new Intent(SplashActivity.this,HomeActivity.class);
//                Intent intent = new Intent(SplashActivity.this,MyNavigationActivity.class);
                startActivity(intent);
                finish();


            }
        }, 1000);
    }

}
