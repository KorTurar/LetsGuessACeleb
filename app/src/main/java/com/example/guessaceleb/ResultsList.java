package com.example.guessaceleb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class ResultsList extends AppCompatActivity {
    private int celebIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_list);

        RecyclerView resultsRecView = findViewById(R.id.resultsRecView);
        TextView noResultsCaption = findViewById(R.id.noResultsCaption);
        ResultsListAdapter resultsListAdapter = new ResultsListAdapter(this);
        WorkWithDbClass db = new WorkWithDbClass(this);
        //db.deleteMyTables();
        db.makeTables();
        celebIndex = 0;
        try {
            ArrayList<JSONObject> results = db.getResults();
            if(results.size()==0||results.size()>=getAssets().list("picsforguessing").length){
                resultsRecView.setVisibility(View.GONE);
                noResultsCaption.setVisibility(View.VISIBLE);
                db.deleteMyTables();
                db.makeTables();
            }
            else
            {
                resultsRecView.setVisibility(View.VISIBLE);
                noResultsCaption.setVisibility(View.GONE);
                resultsListAdapter.setResults(results);
                resultsRecView.setAdapter(resultsListAdapter);
                resultsRecView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
                //Log.d("pics amount", "onCreate: "+getAssets().list("picsforguessing").length);
                celebIndex = db.getMaxIndex()+1;
                Log.d("index check", "onCreate: "+celebIndex);
            }
            /*for (JSONObject resultItem: results){
                Log.d("check results from db", "onCreate: "+resultItem.getString("celebIndex")+"   "+resultItem.getString("photoId")+resultItem.getString("flName")+resultItem.getString("scores"));
            }
            */
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        Button playBtn = findViewById(R.id.playBtn);
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultsList.this, GameActivity.class);
                intent.putExtra("celebIndex", celebIndex);
                startActivity(intent);
            }
        });
    }
}