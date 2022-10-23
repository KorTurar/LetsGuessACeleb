package com.example.guessaceleb;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Chronometer;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class GameActivity extends AppCompatActivity implements answerOptionsDialog.GetGuessingRes, DrawView2.GetErasedArea, DrawView2.SetAnswerOptionsBtnEnabled {

    private ImageView celebImView;
    private CanvasView canvasView;
    private Chronometer chronometer;
    private TextView percentView;
    private float erasurePercent;
    private ArrayList<JSONObject> imagesAndAnswers = new ArrayList<JSONObject>();
    int celebIndex;
    int displayHeight;
    int displayWidth;
    RelativeLayout imAreaRelLay;
    int viewAreaSquare;
    ArrayList<ArrayList<Boolean>> erasedAreaGrid;
    WorkWithDbClass db;
    int totalScore;
    ImageButton menu;
    Button answerOptionsBtn;
    final int SPLASH_TIME_OUT = 3000;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        /*
        View canView = new DrawView2(this);
        RelativeLayout RelativeLayout = (RelativeLayout) findViewById(R.id.imAreaRelLay);
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.p_nikol_kidman_30);
        RelativeLayout.addView(imageView);
        RelativeLayout.addView(canView);*/

        //
        getAllCelebStuff();
        db = new WorkWithDbClass(this);
        //db.deleteMyTables();
        db.makeTables();
        Intent intent = getIntent();
        celebIndex = intent.getIntExtra("celebIndex",0);
        if(celebIndex==0)
        {
            AlertDialog invitationDialog = new AlertDialog.Builder(this).create();
            View v = getLayoutInflater().inflate(R.layout.invitation_to_play,null);
            invitationDialog.setView(v);
            invitationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            invitationDialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    invitationDialog.dismiss();

                }
            }, SPLASH_TIME_OUT);

        }
        answerOptionsBtn = (Button) findViewById(R.id.answerOptionsBtn);
        chronometer = findViewById(R.id.my_chronometer);
        menu = findViewById(R.id.menuImB);
        imAreaRelLay = findViewById(R.id.imAreaRelLay);
        percentView = findViewById(R.id.percentView);

        answerOptionsBtn.setEnabled(false);
        answerOptionsBtn.setVisibility(View.VISIBLE);
        //RippleDrawable root_layout_bg = (RippleDrawable) answerOptionsBtn.getBackground();
        //root_layout_bg.setColor(ColorStateList.valueOf(Color.argb(0,255,255,255)));
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

        answerOptionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answerOptionsDialog answerOptionsDlg = new answerOptionsDialog();
                Bundle bundle = new Bundle();
                try {
                    bundle.putStringArray("answerOptions", (String[])imagesAndAnswers.get(celebIndex).get("answerOptions"));
                    bundle.putString("rightAnswer", (String) imagesAndAnswers.get(celebIndex).get("rightAnswer"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                answerOptionsDlg.setArguments(bundle);
                answerOptionsDlg.show(getSupportFragmentManager(),"answer options dialog");

            }
        });
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        displayHeight = displayMetrics.heightPixels;
        displayWidth = displayMetrics.widthPixels;


        celebImView = new ImageView(this);

        Bitmap b = null;
        try {

            //Uri PathUri = Uri.parse("android.assets://com.example.guessaceleb/picsforguessing/");
            String path = "picsforguessing/";
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            b = BitmapFactory.decodeStream((InputStream)getAssets().open(path+imagesAndAnswers.get(celebIndex).getString("celebPhotoGame")), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        int w = b.getWidth();
        int h = b.getHeight();
        Log.d("dwidth", "onCreate: "+displayWidth);
        float initCelebImageRatio = (float) h / w;
        int tarHeight = (int) (displayWidth * initCelebImageRatio);
        int neededHeightOfCelebImage = (int) (displayHeight - (displayHeight / 5));

        celebImView.setImageBitmap(Bitmap.createScaledBitmap(b, displayWidth, tarHeight, false));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT,
                android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        int marginForCelebIm = (int) (-1 * ((tarHeight - neededHeightOfCelebImage) / 2));
        params.setMargins(0, marginForCelebIm, 0, 0);
        celebImView.setLayoutParams(params);


        viewAreaSquare = displayWidth*(displayHeight-(displayHeight/5));


        makeNewErasedAreaGrid();
        View canView = new DrawView2(this, chronometer, erasedAreaGrid, viewAreaSquare, percentView, displayWidth, displayHeight);
        imAreaRelLay.addView(celebImView);
        imAreaRelLay.addView(canView);



        Log.d("log", "onCreate: bitmap width and height, ratio: " + w + "   " + h + ", " + initCelebImageRatio);
        Log.d("log", "onCreate: display width and height, ratio: " + displayWidth + "   " + displayHeight);
    }



    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.inflate(R.menu.menu);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.toResultList:
                                Intent intent = new Intent(GameActivity.this, ResultsList.class);
                                GameActivity.this.startActivity(intent);
                                return true;
                            default:
                                return false;
                        }
                    }
                });


        popupMenu.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onGetGuessingResResult(boolean result) {
        AlertDialog resultDialog = new AlertDialog.Builder(this).create();
        View v = getLayoutInflater().inflate(R.layout.guess_result,null);
        TextView resultTxt = v.findViewById(R.id.guessResultTxt1);
        TextView scoresTxt = v.findViewById(R.id.guessResultTxt3);
        TextView scoresCaptionTxt = v.findViewById(R.id.guessResultTxt2);
        ConstraintLayout constraintLayout = (ConstraintLayout) v.findViewById(R.id.guessResConsLay);

        GradientDrawable layout_bg = (GradientDrawable) constraintLayout.getBackground();
        resultDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        if (result){
            chronometer.stop();
            layout_bg.setColor(getResources().getColor(R.color.resultGuessed));
            int timeInSecSpentToGuess = (int)((SystemClock.elapsedRealtime() - chronometer.getBase())/1000);


            int timeScore = (int)(((float)5/(float)timeInSecSpentToGuess)*100);
            int areaScore = (int)((5/erasurePercent)*100);
            Log.d("scores", "onGetGuessingResResult: "+timeInSecSpentToGuess+"   "+timeScore+"   "+erasurePercent+"   "+areaScore);

            totalScore = timeScore + areaScore;
            scoresTxt.setText(String.valueOf(totalScore));


            //layout_bg.setStroke(1.0F);
            resultTxt.setText(getResources().getString(R.string.result_text).split(",")[0]);
            resultTxt.setTextColor(getResources().getColor(R.color.resultGuessedText));//bg - #8bf98b
            scoresTxt.setTextColor(getResources().getColor(R.color.resultGuessedText));
            scoresCaptionTxt.setTextColor(getResources().getColor(R.color.resultGuessedText));
            resultDialog.setView(v);
            resultDialog.show();

            JSONObject resultJSON = new JSONObject();
            try {
                resultJSON.put("celebIndex", celebIndex);
                resultJSON.put("photoName", imagesAndAnswers.get(celebIndex).get("celebPhotoThmb"));
                resultJSON.put("flName", imagesAndAnswers.get(celebIndex).get("rightAnswer"));
                resultJSON.put("scores", totalScore);
                db.recordResult(resultJSON);
                Log.d("record test", "run: "+db.getResults().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }/**/
            celebIndex++;


            Log.d("celebIndex", "onGetGuessingResResult: "+celebIndex);
        }
        else
        {
            layout_bg.setColor(getResources().getColor(R.color.resultNotGuessed));
            //layout_bg.getPaint().setStrokeWidth(1.0F);
            scoresTxt.setVisibility(View.GONE);
            scoresCaptionTxt.setVisibility(View.GONE);
            resultTxt.setText(getResources().getString(R.string.result_text).split(",")[1]);
            resultTxt.setTextColor(getResources().getColor(R.color.resultNotGuessedText));
            resultDialog.setView(v);
            resultDialog.show();

        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                resultDialog.dismiss();
                if(result)
                {



                    setNewGame();
                }

            }
        }, SPLASH_TIME_OUT);
    }

    public void setNewGame(){
        chronometer.setBase(SystemClock.elapsedRealtime());
        percentView.setText("0%");
        try {
            setImage();
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        makeNewErasedAreaGrid();
        int[] displaySizes = getDisplaySizes();
        imAreaRelLay.removeView(imAreaRelLay.getChildAt(1));
        imAreaRelLay.addView(new DrawView2(this, chronometer,erasedAreaGrid, viewAreaSquare, percentView, displaySizes[0], displaySizes[1]));
        answerOptionsBtn.setEnabled(false);

    }

    @Override
    public void onGetErasedAreaResult(float erasedArea) {
        erasurePercent = erasedArea;

    }

    public void getAllCelebStuff(){


        JSONObject celeb = new JSONObject();
        try {
            celeb.put("celebPhotoGame",  "nikol_kidman.jpg");
            celeb.put("celebPhotoThmb",  "thmb_nikol_kidman.jpg");
            celeb.put("rightAnswer", "Николь Кидман");
            celeb.put("answerOptions", new String[]{"Кэтрин Зета-Джонс","Кристен Стюарт","Николь Кидман","Дженнифер Лоуренс"});
        } catch (JSONException e) {
            e.printStackTrace();
        }
        imagesAndAnswers.add(celeb);

        celeb = new JSONObject();
        try {
            celeb.put("celebPhotoGame",  "angelina_jolie.jpg");
            celeb.put("celebPhotoThmb",  "thmb_angelina_jolie.jpg");
            celeb.put("rightAnswer", "Анджелина Джоли");
            celeb.put("answerOptions", new String[]{"Дженнифер Энистон", "Анджелина Джоли", "Кэтрин Хайгл", "Мэрил Стрип"});
        } catch (JSONException e) {
            e.printStackTrace();
        }
        imagesAndAnswers.add(celeb);
        celeb = new JSONObject();

        try {
            celeb.put("celebPhotoGame",  "ann_hathaway.jpg");
            celeb.put("celebPhotoThmb",  "thmb_ann_hathaway.jpg");
            celeb.put("rightAnswer", "Энн Хэттэуй");
            celeb.put("answerOptions", new String[]{"Элизабет Бэнкс", "Джессика Альба", "Кирстен Данст", "Энн Хэттэуй"});
        } catch (JSONException e) {
            e.printStackTrace();
        }
        imagesAndAnswers.add(celeb);
        celeb = new JSONObject();
        try {
            celeb.put("celebPhotoGame",  "brad_pitt.jpg");
            celeb.put("celebPhotoThmb",  "thmb_brad_pitt.jpg");
            celeb.put("rightAnswer", "Брэд Питт");
            celeb.put("answerOptions", new String[]{"Брэд Питт", "Том круз", "Вуди Харрельсон", "Том Хэнкс"});
        } catch (JSONException e) {
            e.printStackTrace();
        }
        imagesAndAnswers.add(celeb);
        celeb = new JSONObject();
        try {
            celeb.put("celebPhotoGame",  "bradley_cooper.jpg");
            celeb.put("celebPhotoThmb",  "thmb_bradley_cooper.jpg");
            celeb.put("rightAnswer", "Брэдли Купер");
            celeb.put("answerOptions", new String[]{"Джош Хартнет", "Оуэн Уилсон", "Брэдли Купер", "Майло Вентимилья"});
        } catch (JSONException e) {
            e.printStackTrace();
        }
        imagesAndAnswers.add(celeb);
        celeb = new JSONObject();
        try {
            celeb.put("celebPhotoGame",  "jennifer_lopez.jpg");
            celeb.put("celebPhotoThmb",  "thmb_jennifer_lopez.jpg");
            celeb.put("rightAnswer", "Дженнифер Лопез");
            celeb.put("answerOptions", new String[]{"Мадонна", "Дженнифер Лопез", "Фрэнсис Макдорманд", "Нелли Фуртадо"});
        } catch (JSONException e) {
            e.printStackTrace();
        }
        imagesAndAnswers.add(celeb);
        celeb = new JSONObject();
        try {
            celeb.put("celebPhotoGame",  "johnny_depp.jpg");
            celeb.put("celebPhotoThmb",  "thmb_johnny_depp.jpg");
            celeb.put("rightAnswer", "Джонни Депп");
            celeb.put("answerOptions", new String[]{"Орландо Блум", "Мэтт Дэймон", "Джонни Депп", "Вин Дизель"});
        } catch (JSONException e) {
            e.printStackTrace();
        }
        imagesAndAnswers.add(celeb);
        celeb = new JSONObject();
        try {
            celeb.put("celebPhotoGame",  "madonna.jpg");
            celeb.put("celebPhotoThmb",  "thmb_madonna.jpg");
            celeb.put("rightAnswer", "Мадонна");
            celeb.put("answerOptions", new String[]{"Мадонна", "Тэйлор Свифт", "Кэти Перри", "Данэлия Тулешова"});
        } catch (JSONException e) {
            e.printStackTrace();
        }
        imagesAndAnswers.add(celeb);
        celeb = new JSONObject();
        try {
            celeb.put("celebPhotoGame",  "robert_downey_jr.jpg");
            celeb.put("celebPhotoThmb",  "thmb_robert_downey_jr.jpg");
            celeb.put("rightAnswer", "Роберт Дауни мл.");
            celeb.put("answerOptions", new String[]{"Джеймс Кэмерон", "Джон Фавро", "Микки Рурк", "Роберт Дауни мл."});
        } catch (JSONException e) {
            e.printStackTrace();
        }
        imagesAndAnswers.add(celeb);
    }

    public void setImage() throws JSONException, IOException {
        String path = "picsforguessing/";
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap b = BitmapFactory.decodeStream((InputStream)getAssets().open(path+imagesAndAnswers.get(celebIndex).getString("celebPhotoGame")), null, options);

        Log.d("test", "setImage: test1");

        int w = b.getWidth();
        int h = b.getHeight();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;
        Log.d("test", "setImage: test2 "+w+"  "+h+"  "+displayWidth);
        float initCelebImageRatio = (float) h / w;
        Log.d("test", "setImage: test3");
        Log.d("dwidth", "onCreate: "+displayWidth);
        int tarHeight = (int) (displayWidth * initCelebImageRatio);
        int neededHeightOfCelebImage = (int) (displayHeight - (displayHeight / 5));
        Log.d("test", "setImage: test4");
        celebImView.setImageBitmap(Bitmap.createScaledBitmap(b, displayWidth, tarHeight, false));
    }

    public int[] getDisplaySizes(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        displayHeight = displayMetrics.heightPixels;
        displayWidth = displayMetrics.widthPixels;
        return new int[]{displayWidth, displayHeight};
    }

    public void makeNewErasedAreaGrid(){
        erasedAreaGrid = new ArrayList<ArrayList<Boolean>>();
        for(int i = 0; i<Math.ceil(displayWidth/40);i++){
            ArrayList<Boolean> gridRows = new ArrayList<Boolean>();
            for (int j = 0; j<Math.ceil((displayHeight-(displayHeight/5))/40);j++){

                gridRows.add(false);
            }
            erasedAreaGrid.add(gridRows);
        }
    }

    @Override
    public void onSetAnswerOptionsBtnEnabledResult(boolean gameStarted) {
        answerOptionsBtn.setEnabled(true);
    }
}