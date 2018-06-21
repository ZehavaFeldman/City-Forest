package com.zehava.cityforest.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.login.LoginManager;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.Query;
import com.zehava.cityforest.FirebaseUtils;
import com.zehava.cityforest.Models.Track;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zehava.cityforest.FirebaseTrackListAdapter;
import com.zehava.cityforest.R;

import static com.zehava.cityforest.Constants.Q_ENDING_POINT;
import static com.zehava.cityforest.Constants.Q_STARTING_POINT;
import static com.zehava.cityforest.Constants.SELECTED_TRACK;

public class SearchTracksResultsActivity extends AppCompatActivity {


    private ListView tracks_list;
    FirebaseListAdapter adapter;

    private String q_starting_point;
    private String q_ending_point;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_tracks_results);

        tracks_list = (ListView)findViewById(R.id.tracks_list);

        Intent i = getIntent();
        q_starting_point = i.getStringExtra(Q_STARTING_POINT);
        q_ending_point = i.getStringExtra(Q_ENDING_POINT);


        //Query query = tracks.orderByChild("starting_point").equalTo("הר הרצל");
        //Query q2 = tracks.orderByChild("suitable_for_dogs").equalTo(true);

        Query query = FirebaseUtils.getTracksRef();
        FirebaseListOptions<Track> options =
                new FirebaseListOptions.Builder<Track>()
                        .setQuery(query, Track.class)
                        .setLayout(R.layout.track_list_view)
                        .build();
        adapter = new FirebaseTrackListAdapter(options);
        tracks_list.setAdapter(adapter);

        tracks_list.setOnItemClickListener(new ItemClickListener());

    }


    private class ItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Track track = (Track) parent.getItemAtPosition(position);

            Intent i = new Intent(SearchTracksResultsActivity.this, SelectedTrackMapActivity.class);
            i.putExtra(SELECTED_TRACK, track.getDb_key());
            startActivity(i);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.startListening();
    }

    @Override
    protected void onStart() {

        super.onStart();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch(item.getItemId()){
            case R.id.aboutActivity:
                i = new Intent(this, AboutUsActivity.class);
                startActivity(i);
                return true;

            case R.id.contactUsActivity:
                i = new Intent(this, ContactUsActivity.class);
                startActivity(i);
                return true;
//
            case R.id.home:
                i = new Intent(this, HomeActivity.class);
                startActivity(i);
                return true;

            case R.id.userGuideActivity:
                i = new Intent(this, UserGuideActivity.class);
                startActivity(i);
                return true;

            case R.id.searchTracksActivity:
                i = new Intent(this, AlgoliaSearchActivity.class);
                startActivity(i);
                return true;

            case R.id.tracksActivity:
                i = new Intent(this, TracksActivity.class);
                startActivity(i);
                return true;

            case R.id.signOut:
                signOut();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /*Method signs out user's google account*/
    private void signOut() {
        FirebaseAuth.getInstance().signOut();

        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {

                if (currentAccessToken == null){
                    //User logged out

                    LoginManager.getInstance().logOut();
                    Intent i = new Intent(SearchTracksResultsActivity.this, HomeActivity.class);
                    startActivity(i);
                }
            }
        };

    }

}
