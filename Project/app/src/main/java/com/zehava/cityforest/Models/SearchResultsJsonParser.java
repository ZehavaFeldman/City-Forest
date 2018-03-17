package com.zehava.cityforest.Models;

import com.zehava.cityforest.Highlight;
import com.zehava.cityforest.HighlightedResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by avigail on 10/01/18.
 */

/*parsing data recivd from algolia serach via PointOfInterestParser
then send parsed result back to activity to show to user
 */

public class SearchResultsJsonParser
{
    private PointOfInterestJsonParser pointOfInterestJsonParser = new PointOfInterestJsonParser();
    private TrackJsonParser trackJsonParser = new TrackJsonParser();

    public List<HighlightedResult<PointOfInterest>> parseResults(JSONObject jsonObject)
    {
        if (jsonObject == null)
            return null;
        List<HighlightedResult<PointOfInterest>> results = new ArrayList<>();
        JSONArray hits = jsonObject.optJSONArray("hits");
        if (hits == null)
            return null;
        for (int i = 0; i < hits.length(); ++i) {
            JSONObject hit = hits.optJSONObject(i);
            if (hit == null)
                continue;
            PointOfInterest point = pointOfInterestJsonParser.parse(hit);
            if (point == null)
                continue;
            String indexName = hit.optString("index");
            if(indexName == null || !indexName.equalsIgnoreCase("points_of_interest"))
                continue;
            JSONObject highlightResult = hit.optJSONObject("_highlightResult");
            if (highlightResult == null)
                continue;
            JSONObject highlightTitle = highlightResult.optJSONObject("title");
            if (highlightTitle == null)
                continue;
            String value = highlightTitle.optString("value");
            if (value == null)
                continue;
            HighlightedResult<PointOfInterest> result = new HighlightedResult<>(point);
            result.addHighlight("title", new Highlight("title", value));
            results.add(result);
        }
        return results;
    }

    public List<HighlightedResult<Track>> parseTrackResults(JSONObject jsonObject)
    {
        if (jsonObject == null)
            return null;
        List<HighlightedResult<Track>> results = new ArrayList<>();
        JSONArray hits = jsonObject.optJSONArray("hits");
        if (hits == null)
            return null;
        for (int i = 0; i < hits.length(); ++i) {
            JSONObject hit = hits.optJSONObject(i);
            if (hit == null)
                continue;
            Track track = trackJsonParser.parse(hit);
            if (track == null)
                continue;
            String indexName = hit.optString("index");
            if(indexName == null || !indexName.equalsIgnoreCase("track"))
                continue;
            JSONObject highlightResult = hit.optJSONObject("_highlightResult");
            if (highlightResult == null)
                continue;
            JSONObject highlightTitle = highlightResult.optJSONObject("track_name");
            if (highlightTitle == null)
                continue;
            String value = highlightTitle.optString("value");
            if (value == null)
                continue;
            HighlightedResult<Track> result = new HighlightedResult<>(track);
            result.addHighlight("track_name", new Highlight("track_name", value));
            results.add(result);
        }
        return results;
    }
}