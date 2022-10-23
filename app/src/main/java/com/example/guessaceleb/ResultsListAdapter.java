package com.example.guessaceleb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;


public class ResultsListAdapter extends RecyclerView.Adapter<ResultsListAdapter.ViewHolder> {

    public Context context;
    private ArrayList<JSONObject> results = new ArrayList<JSONObject>();
    private Bitmap bmp, adjustedBmp;
    public ResultsListAdapter(Context context) {
        this.context = context;

    }

    @NonNull
    @Override
    public ResultsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.result_item, parent, false);
        return new ResultsListAdapter.ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ResultsListAdapter.ViewHolder holder, int position) {

        /*try {
            Log.d("adapter check", "onBindViewHolder: "+results.get(position).getInt("photoId")+"  "+R.drawable.angelina_jolie);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        Log.d("qwerty", "onBindViewHolder: "+position);
        try {
            String path = "thumbnail/";
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeStream((InputStream)context.getAssets().open(path+results.get(position).getString("photoName")), null, options);

            holder.celebIm.setImageBitmap(bitmap);
            holder.flName.setText(results.get(position).getString("flName"));
            Log.d("adapter check flname", "onBindViewHolder: "+results.get(position).getString("flName"));
            holder.scores.setText(String.valueOf(results.get(position).getInt("scores")));
        } catch (JSONException | FileNotFoundException e) {
            e.printStackTrace();
            Log.d("try", "onBindViewHolder: gone wrong");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // holder.abilitiesRecView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        Log.d("adapter pos", "onBindViewHolder: "+position);
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public void setResults(ArrayList<JSONObject> results) throws JSONException {
        this.results = results;
        notifyDataSetChanged();
        Log.d("adapter check", "setResults: "+Arrays.asList(results));
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView celebIm;
        public TextView flName;
        public TextView scores;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            celebIm = (ImageView) itemView.findViewById(R.id.celebIm);
            flName = (TextView) itemView.findViewById(R.id.celebFLNameTxt);
            scores = (TextView) itemView.findViewById(R.id.scoresTxt);

        }
    }

}
