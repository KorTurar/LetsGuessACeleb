package com.example.guessaceleb;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DrawView2 extends View {

        public interface GetErasedArea{
            void onGetErasedAreaResult(float erasedArea);
        }

        public interface SetAnswerOptionsBtnEnabled{
            void onSetAnswerOptionsBtnEnabledResult(boolean gameStarted);
        }

        private GetErasedArea getErasedArea;
        private SetAnswerOptionsBtnEnabled setAnswerOptionsBtnEnabled;
        int prevPointX;
        int prevPointY;
        int nextPointX;
        int nextPointY;
        Paint paintSrc;
        Paint paintDst;
        Paint paintBorder;
        Paint photoPaint;

        Path pathSrc;
        Path pathDst;
        Path pathDraw;

        Bitmap bitmapSrc;
        Bitmap bitmapDst;
        Bitmap photoBitmap;

        float xPos;
        float yPos;

        Context context;
        boolean guessingStarted;
        public Chronometer myChronometer;
        ArrayList<ArrayList<Boolean>> erasedAreaGrid;
        int viewAreaSquare;
        int erasedArea;
        float erasurePercent;
        TextView percentView;
        int displayWidth;
        int displayHeight;

        // PorterDuff режим
        PorterDuff.Mode mode = PorterDuff.Mode.XOR;

        int colorDst = Color.BLUE;
        int colorSrc = Color.YELLOW;

        public DrawView2(Context context, Chronometer chronometer, ArrayList<ArrayList<Boolean>> erasedAreaGrid, int viewAreaSquare, TextView percentView, int displayWidth, int displayHeight) {
            super(context);

            // необходимо для корректной работы
            if (android.os.Build.VERSION.SDK_INT >= 11) {
                setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }
            this.context = context;
            this.guessingStarted = false;
            this.myChronometer = chronometer;
            this.erasedAreaGrid = erasedAreaGrid;
            this.viewAreaSquare = viewAreaSquare;
            this.erasedArea = 0;
            this.erasurePercent = 0;
            this.percentView = percentView;
            this.displayWidth = displayWidth;
            this.displayHeight = displayHeight;
            pathDraw = new Path();
            // DST фигура
            pathDst = new Path();
            pathDst.moveTo(0, 0);
            pathDst.lineTo(500, 0);
            pathDst.lineTo(500, 500);
            pathDst.lineTo(0, 500);
            pathDst.close();

            // создание DST bitmap
            bitmapDst = createBitmap(pathDst, colorDst);

            // кисть для вывода DST bitmap
            paintDst = new Paint();

            // SRC фигура
            pathSrc = new Path();
            pathSrc.moveTo(200, 200);
            pathSrc.lineTo(350, 200);
            pathSrc.lineTo(350, 350);
            pathSrc.lineTo(200, 350);
            pathSrc.close();

            // создание SRC bitmap
            bitmapSrc = createBitmap(pathSrc, colorSrc);

            // кисть для вывода SRC bitmap
            paintSrc = new Paint();
            paintSrc.setStrokeJoin(Paint.Join.ROUND);
            paintSrc.setStyle(Paint.Style.STROKE);
            paintSrc.setStrokeWidth(40f);
            paintSrc.setXfermode(new PorterDuffXfermode(mode));

            photoPaint = new Paint();
            // кисть для рамки
            paintBorder = new Paint();
            paintBorder.setStyle(Paint.Style.STROKE);
            paintBorder.setStrokeWidth(3);
            paintBorder.setColor(Color.BLACK);
        }

        private Bitmap createBitmap(Path path, int color) {
            // создание bitmap и канвы для него
            Bitmap bitmap = Bitmap.createBitmap(500, 500,
                    Bitmap.Config.ARGB_8888);
            Canvas bitmapCanvas = new Canvas(bitmap);

            // создание кисти нужного цвета
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setColor(color);

            // рисование фигуры на канве bitmap
            bitmapCanvas.drawPath(path, paint);

            return bitmap;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            //canvas.translate(390, 80);
            canvas.drawColor(Color.BLACK);

            //canvas.drawCircle(xPos, yPos, 20, paintSrc);
            //canvas.drawBitmap(photoBitmap,-500,-500, photoPaint);
            // DST bitmap
            //canvas.drawBitmap(bitmapDst,0,0, paintDst);

            canvas.drawPath(pathDraw, paintSrc);

            // SRC bitmap
            //canvas.drawBitmap(bitmapSrc, 0, 0, paintSrc);

            // рамка
            //canvas.drawRect(0, 0, 500, 500, paintBorder);

        }
        public boolean onTouchEvent(MotionEvent event){

            xPos = event.getX()<0?0:event.getX();
            yPos = event.getY()<0?0:event.getY();

            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    pathDraw.moveTo(xPos, yPos);
                    //prevPointX= (int) xPos;
                    //prevPointY= (int) yPos;
                    break;
                case MotionEvent.ACTION_MOVE:
                    pathDraw.lineTo(xPos, yPos);
                    //nextPointX = (int) xPos;
                    //nextPointY = (int) yPos;

                    int yIndex = (int)Math.floor(yPos/40) >= erasedAreaGrid.get(0).size()?erasedAreaGrid.get(0).size()-1:(int)Math.floor(yPos)/40;
                    int xIndex = (int)Math.floor(xPos/40) >= erasedAreaGrid.size()?erasedAreaGrid.size()-1:(int)Math.floor(xPos/40);
                    boolean erased = erasedAreaGrid.get(xIndex).get(yIndex);
                    if (!guessingStarted)
                    {
                        guessingStarted = true;
                        myChronometer.setBase(SystemClock.elapsedRealtime());

                        myChronometer.start();
                        setAnswerOptionsBtnEnabled = (SetAnswerOptionsBtnEnabled) context;
                        setAnswerOptionsBtnEnabled.onSetAnswerOptionsBtnEnabledResult(true);

                    }
                    if (!erased)
                    {
                        erasedAreaGrid.get(xIndex).set(yIndex, true);
                        erasedArea+=1600;

                    }
                    /*int dist = (int) Math.sqrt(Math.pow((nextPointX-prevPointX),2)+Math.pow((nextPointY-prevPointY),2));
                    Log.d("dist", "onTouchEvent: "+dist);
                    if (dist>100)
                    {
                        float tang = (nextPointY-prevPointY)/(nextPointX-nextPointY);
                        for (int i=1; i<dist/40;i++)
                        {
                            int intermX = (int) (prevPointX+((40*i)/tang));
                            int intermY = (int) (prevPointY+(40*i));
                            yIndex = (int)Math.floor(intermY/40) >= erasedAreaGrid.get(0).size()?erasedAreaGrid.get(0).size()-1:(int)Math.floor(intermY)/40;
                            xIndex = (int)Math.floor(intermX/40) >= erasedAreaGrid.size()?erasedAreaGrid.size()-1:(int)Math.floor(intermX/40);
                            if(!erasedAreaGrid.get(xIndex).get(yIndex))
                            {
                                erasedAreaGrid.get(xIndex).set(yIndex, true);
                                erasedArea+=1600;
                                Log.d("erasure correction", "onTouchEvent: done");
                            }
                        }
                    }*/

                    erasurePercent =  ((float)erasedArea/(float)viewAreaSquare)*100;
                    getErasedArea = (GetErasedArea) context;
                    //Log.d("rand pos", "onItemClick: "+randomIndex + "  "+ position);
                    getErasedArea.onGetErasedAreaResult((float)erasurePercent);
                    percentView.setText(String.format("%.1f", erasurePercent) +"%");
                    Log.d("area inc", "onTouchEvent: erased area increased; erasedArea - "+erasedArea+", viewAreSquare - "+viewAreaSquare+", erasurePercent - "+erasurePercent);
                    //prevPointX=nextPointX;
                    //prevPointY=nextPointY;
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                default:
                    return false;
            }/**/
            Log.d("positions", "onTouchEvent: "+xPos+"  "+yPos);
            invalidate();
            return true;
        }

        //нужны методы для смены изображения и обнуления игры и получения результатов
        public JSONObject getResults() throws JSONException {
            //myChronometer.stop();
            int timeInSecSpentToGuess = (int)((SystemClock.elapsedRealtime() - myChronometer.getBase())/1000);
            int mins = timeInSecSpentToGuess/60;
            int secs = timeInSecSpentToGuess%60;
            String myTime = String.valueOf(mins)+":"+String.valueOf(secs);
            String percent = percentView.getText().toString();
            JSONObject results = new JSONObject();
            results.put("time", myTime);
            results.put("percent", percent);
            return results;
        }



}

