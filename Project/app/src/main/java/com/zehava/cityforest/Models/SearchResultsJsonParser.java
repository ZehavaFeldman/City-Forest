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

public class SearchResultsJsonParser
{
    private MovieJsonParser movieParser = new MovieJsonParser();
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
            PointOfInterest movie = movieParser.parse(hit);
            if (movie == null)
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
            HighlightedResult<PointOfInterest> result = new HighlightedResult<>(movie);
            result.addHighlight("title", new Highlight("title", value));
            results.add(result);
        }
        return results;
    }
}