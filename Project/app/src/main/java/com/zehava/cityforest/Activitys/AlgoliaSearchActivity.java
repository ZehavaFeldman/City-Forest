package com.zehava.cityforest.Activitys;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v7.widget.SearchView;
import android.widget.Toast;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.IndexQuery;
import com.algolia.search.saas.Query;
import com.zehava.cityforest.HighlightRenderer;
import com.zehava.cityforest.HighlightedResult;
import com.zehava.cityforest.Models.PointOfInterest;
import com.zehava.cityforest.Models.SearchResultsJsonParser;
import com.zehava.cityforest.Models.Track;
import com.zehava.cityforest.R;
import com.zehava.cityforest.SeparatedListAdapter;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

import static com.zehava.cityforest.Constants.SELECTED_TRACK;

public class AlgoliaSearchActivity extends AppCompatActivity implements View.OnClickListener, SearchView.OnQueryTextListener, AbsListView.OnScrollListener {

    private Client client;
    private Index index;
    List<IndexQuery> queries;
    private Query query, trackquery;
    private Client.MultipleQueriesStrategy strategy;
    private SearchResultsJsonParser resultsParser = new SearchResultsJsonParser();
    private int lastSearchedSeqNo;
    private int lastDisplayedSeqNo;
    private int lastRequestedPage;
    private int lastDisplayedPage;
    private boolean endReached , endTrackReached;
    private String filter =null;

    // UI:
    private SearchView searchView;
    private ListView resultListView;
    private PointOfInterestAdapter pointsListAdapter;
    private SeparatedListAdapter sla;
    private TracksListAdapter tracksListAdapter;

    private HighlightRenderer highlightRenderer;

    private ImageView dogFilter, cutloryFilter, bikeFilter,waterFilter, historyFilter;

    // Constants

    private static final int HITS_PER_PAGE = 20;

    /** Number of items before the end of the list past which we start loading more content. */
    private static final int LOAD_MORE_THRESHOLD = 5;

    // Lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setContentView(R.layout.activity_algolia_serach);

        pointsListAdapter = new PointOfInterestAdapter(this, R.layout.hits_item);
        tracksListAdapter = new TracksListAdapter(this, R.layout.hits_item);
        sla = new SeparatedListAdapter(this);
        sla.addSection("Tracks",tracksListAdapter);
        sla.addSection("PointsOfInterest",pointsListAdapter);
        
        // Bind UI components.
        resultListView = (ListView) findViewById(R.id.listview_movies);
        resultListView.setAdapter(sla);
        resultListView.setOnScrollListener(this);

        // Init Algolia.
        client = new Client(getString(R.string.ALGOLIA_APP_ID),getString(R.string.ALGOLIA_SEARCH_API_KEY));
        index = client.getIndex(getString(R.string.ALGOLIA_INDEX_NAME));

        // Pre-build query.
        query = new Query();
        query.setAttributesToRetrieve("title", "snippet", "position", "type");
        query.setAttributesToHighlight("title","snippet");
        query.setHitsPerPage(HITS_PER_PAGE);

        trackquery = new Query();
        trackquery.setAttributesToRetrieve("route", "objectID","track_name","starting_point",
                "ending_point","duration","length","additional_info","starting_point_json_latlng",
                "ending_point_json_latlng");
        trackquery.setAttributesToHighlight("track_name","additional_info");
        trackquery.setHitsPerPage(HITS_PER_PAGE);


        //init filters and attach to click listener
        dogFilter = (ImageView)findViewById(R.id.dog);
        cutloryFilter = (ImageView)findViewById(R.id.cutlery);
        bikeFilter = (ImageView)findViewById(R.id.bicycle);
        waterFilter = (ImageView)findViewById(R.id.water);
        historyFilter = (ImageView)findViewById(R.id.history);

        dogFilter.setOnClickListener(this);
        cutloryFilter.setOnClickListener(this);
        bikeFilter.setOnClickListener(this);
        waterFilter.setOnClickListener(this);
        historyFilter.setOnClickListener(this);

        highlightRenderer = new HighlightRenderer(this);

        queries = new ArrayList<>();

        queries.add(new IndexQuery(getString(R.string.ALGOLIA_INDEX_NAME), trackquery));
        queries.add(new IndexQuery(getString(R.string.ALGOLIA_POINTS_OF_INTEREST), query));
        strategy = Client.MultipleQueriesStrategy.NONE; // Execute the sequence of queries until the end.


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_movie_search, menu);

        // Configure search view.
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        searchView.setQuery("",true);
        searchView.requestFocus();


        return true;
    }

    // Actions

    //search- search for data by users input
    private void search() {
        final int currentSearchSeqNo = ++lastSearchedSeqNo;
        query.setQuery(searchView.getQuery().toString());
        query.setFilters(null);
        trackquery.setQuery(searchView.getQuery().toString());
        trackquery.setFilters(null);
        lastRequestedPage = 0;
        lastDisplayedPage = -1;
        endReached = false;
        endTrackReached = false;
        startAlgoliaSearch(currentSearchSeqNo);
    }


    //filter- filter data by categorys
    private void filter() {
        final int currentSearchSeqNo = ++lastSearchedSeqNo;
        query.setFilters(filter);
        query.setQuery(null);
        trackquery.setFilters(filter);
        trackquery.setQuery(null);
        lastRequestedPage = 0;
        lastDisplayedPage = -1;
        endReached = false;
        startAlgoliaSearch(currentSearchSeqNo);
    }


    private void startAlgoliaSearch(final int currentSearchSeqNo){
//        tracksListAdapter.clear();
//        pointsListAdapter.clear();
        //        index.searchAsync(query, new CompletionHandler() {

        client.multipleQueriesAsync(queries, strategy, new CompletionHandler() {
            @Override
            public void requestCompleted(JSONObject content, AlgoliaException error) {

                if (content != null && error == null) {
                    // NOTE: Check that the received results are newer that the last displayed results.
                    //
                    // Rationale: Although TCP imposes a server to send responses in the same order as
                    // requests, nothing prevents the system from opening multiple connections to the
                    // same server, nor the Algolia client to transparently switch to another server
                    // between two requests. Therefore the order of responses is not guaranteed.
                    if (currentSearchSeqNo <= lastDisplayedSeqNo) {
                        return;
                    }
                    JSONArray result = content.optJSONArray("results");
                    if(result == null || result.length()==0)
                        return;
                    pointsListAdapter.clear();
                    tracksListAdapter.clear();
                    JSONObject pointsResult = result.optJSONObject(1);
                    if(pointsResult != null) {
                        List<HighlightedResult<PointOfInterest>> results = resultsParser.parseResults(pointsResult);
                        if (results.isEmpty()) {
//                            pointsListAdapter.clear();
                            endReached = true;
                        } else {
//                            pointsListAdapter.clear();
                            pointsListAdapter.addAll(results);
                            pointsListAdapter.notifyDataSetChanged();
                            sla.notifyDataSetChanged();
                            lastDisplayedSeqNo = currentSearchSeqNo;
                            lastDisplayedPage = 0;
                        }
                    }
                    JSONObject tracksResult = result.optJSONObject(0);
                    if(tracksResult != null) {
                        List<HighlightedResult<Track>> tracksResults = resultsParser.parseTrackResults(tracksResult);
                        if (tracksResults.isEmpty()) {
                           // tracksListAdapter.clear();
                            endTrackReached = true;
                        } else {
                          //  tracksListAdapter.clear();
                            tracksListAdapter.addAll(tracksResults);
                            tracksListAdapter.notifyDataSetChanged();
                            sla.notifyDataSetChanged();
                            lastDisplayedSeqNo = currentSearchSeqNo;
                            lastDisplayedPage = 0;
                        }
                    }

                    // Scroll the list back to the top.
                    resultListView.smoothScrollToPosition(0);
                }
            }
        });

    }

    //load more only on user scroll- inhancing proformance
    private void loadMore() {
        ArrayList<IndexQuery> loadMoreQuery = new ArrayList<>();
        Query loadMorePoint = new Query(query);
        loadMorePoint.setPage(++lastRequestedPage);
        Query loadMoreTrack = new Query(trackquery);
        loadMoreTrack.setPage(lastRequestedPage);
        loadMoreQuery.add(new IndexQuery(getString(R.string.ALGOLIA_INDEX_NAME), loadMoreTrack));
        loadMoreQuery.add(new IndexQuery(getString(R.string.ALGOLIA_POINTS_OF_INTEREST), loadMorePoint));


        final int currentSearchSeqNo = lastSearchedSeqNo;
//        index.searchAsync(loadMoreQuery, new CompletionHandler() {
        client.multipleQueriesAsync(loadMoreQuery, strategy, new CompletionHandler() {
            @Override
            public void requestCompleted(JSONObject content, AlgoliaException error) {
                if (content != null && error == null) {
                    // Ignore results if they are for an older query.
                    if (lastDisplayedSeqNo != currentSearchSeqNo) {
                        return;
                    }
                    JSONArray result = content.optJSONArray("results");
                    if(result == null || result.length()==0)
                        return;
                    JSONObject pointsResult = result.optJSONObject(1);
                    if(pointsResult != null) {
                        List<HighlightedResult<PointOfInterest>> results = resultsParser.parseResults(pointsResult);
                        if (results.isEmpty()) {
                            endReached = true;
                        } else {
                            pointsListAdapter.addAll(results);
                            pointsListAdapter.notifyDataSetChanged();
                            sla.notifyDataSetChanged();
                            lastDisplayedPage = lastRequestedPage;
                        }
                    }
                    JSONObject tracksResult = result.optJSONObject(0);
                    if(tracksResult != null) {
                        List<HighlightedResult<Track>> tracksResults = resultsParser.parseTrackResults(tracksResult);
                        if (tracksResults.isEmpty()) {
                            endTrackReached = true;
                        } else {
                            tracksListAdapter.addAll(tracksResults);
                            tracksListAdapter.notifyDataSetChanged();
                            sla.notifyDataSetChanged();
                            lastDisplayedPage = lastRequestedPage;
                        }
                    }


                }
            }
        });
    }





    // Data sources

    private class PointOfInterestAdapter extends ArrayAdapter<HighlightedResult<PointOfInterest>> {
        public PointOfInterestAdapter(Context context, int resource) {
            super(context, resource);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewGroup cell = (ViewGroup) convertView;
            if (cell == null) {
                cell = (ViewGroup) getLayoutInflater().inflate(R.layout.hits_item, null);
            }


            TextView titleTextView = (TextView) cell.findViewById(R.id.product_name);
            TextView yearTextView = (TextView) cell.findViewById(R.id.product_price);

            HighlightedResult<PointOfInterest> result = pointsListAdapter.getItem(position);

            titleTextView.setText(result.getResult().getTitle());
            yearTextView.setText( result.getResult().getSnippet());

            return cell;
        }

        @Override
        public void addAll(Collection<? extends HighlightedResult<PointOfInterest>> items) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                super.addAll(items);
            } else {
                for (HighlightedResult<PointOfInterest> item : items) {
                    add(item);
                }
            }
        }
    }

    private class TracksListAdapter extends ArrayAdapter<HighlightedResult<Track>> {
        public TracksListAdapter(Context context, int resource) {
            super(context, resource);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewGroup cell = (ViewGroup) convertView;
            if (cell == null) {
                cell = (ViewGroup) getLayoutInflater().inflate(R.layout.hits_item, null);
            }


            TextView titleTextView = (TextView) cell.findViewById(R.id.product_name);
            TextView yearTextView = (TextView) cell.findViewById(R.id.product_price);

            final HighlightedResult<Track> result = tracksListAdapter.getItem(position);

            titleTextView.setText(result.getResult().getTrack_name());
            yearTextView.setText(result.getResult().getAdditional_info());
            if(cell != null) {
                cell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(AlgoliaSearchActivity.this, SelectedTrackMapActivity.class);
                        i.putExtra(SELECTED_TRACK, result.getResult().getDb_key());
                        startActivity(i);
                    }
                });
            }

            return cell;
        }

        @Override
        public void addAll(Collection<? extends HighlightedResult<Track>> items) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                super.addAll(items);
            } else {
                for (HighlightedResult<Track> item : items) {
                    add(item);
                }
            }
        }
    }


    // View.onClickListener

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.dog:
                break;

            case R.id.water:

                break;
            case R.id.cutlery:
                filter="type:\"בית קפה\"";
                filter();
                break;
            case R.id.bicycle:
                break;
            case R.id.history:
                filter="type:\"אתר היסטורי\"";
                filter();
                break;
        }
    }

    // SearchView.OnQueryTextListener

    @Override
    public boolean onQueryTextSubmit(String query) {
        // Nothing to do: the search has already been performed by `onQueryTextChange()`.
        // We do try to close the keyboard, though.
        searchView.clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        search();
        return true;
    }

    // AbsListView.OnScrollListener

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // Nothing to do.
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // Abort if list is empty or the end has already been reached.
        if (totalItemCount == 0 || (endReached && endTrackReached)) {
            return;
        }

        // Ignore if a new page has already been requested.
        if (lastRequestedPage > lastDisplayedPage) {
            return;
        }

        // Load more if we are sufficiently close to the end of the list.
        int firstInvisibleItem = firstVisibleItem + visibleItemCount;
        if (firstInvisibleItem + LOAD_MORE_THRESHOLD >= totalItemCount) {
            loadMore();
        }
    }

}
