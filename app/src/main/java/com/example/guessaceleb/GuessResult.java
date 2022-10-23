package com.example.guessaceleb;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class GuessResult extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.guess_result,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setView(v);
        Bundle bundle = getArguments();
        if(null!=bundle){
            Integer resultOption = bundle.getInt("resultOption");
            TextView congratTxtView = v.findViewById(R.id.guessResultTxt1);
            congratTxtView.setText(getResources().getString(R.string.result_text).split(",")[resultOption]);
        }
        return builder.create();
    }
}
