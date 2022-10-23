package com.example.guessaceleb;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class answerOptionsDialog extends DialogFragment {
    public interface GetGuessingRes{
        void onGetGuessingResResult(boolean result);
    }

    private GetGuessingRes getGuessingRes;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.answer_options,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setView(v);
        Bundle bundle = getArguments();
        if(null!=bundle){
            String rightAnswer = bundle.getString("rightAnswer");
            String[] answerOptions = bundle.getStringArray("answerOptions");
            if(null!=answerOptions){
                GridView answerOptionsGridView = (GridView) v.findViewById(R.id.answerOptionsGridView);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, answerOptions);

                answerOptionsGridView.setAdapter(adapter);
                answerOptionsGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        getGuessingRes = (GetGuessingRes) getActivity();
                        getGuessingRes.onGetGuessingResResult(parent.getItemAtPosition(position).equals(rightAnswer));
                        dismiss();


                    }
                });
            }
        }
        return builder.create();
    }
}
