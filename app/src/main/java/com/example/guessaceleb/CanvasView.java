package com.example.guessaceleb;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;




public class CanvasView extends View {



    Paint blackRectPaint;
    Paint transparentRectPaint;
    Path blackRectPath;
    Path transparentRectPath;
    Bitmap blackRectBitmap;
    Bitmap transparentRectBitmap;
    int i=0;
    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        blackRectPaint = new Paint();
        transparentRectPaint = new Paint();
        transparentRectPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
        transparentRectPaint.setColor(getResources().getColor(R.color.purple_200));
        blackRectPath = new Path();
        blackRectPath.moveTo(300,300);
        blackRectPath.lineTo(1000,300);
        blackRectPath.lineTo(1000,1000);
        blackRectPath.lineTo(300,1000);
        blackRectPath.close();
        transparentRectPath = new Path();
        transparentRectPath.moveTo(400,400);
        transparentRectPath.lineTo(550,400);
        transparentRectPath.lineTo(550,550);
        transparentRectPath.lineTo(400,550);
        transparentRectPath.close();
        blackRectBitmap = createBitmap(blackRectPath, Color.BLACK, 0,0,1000,1000);
        transparentRectBitmap = createBitmap(transparentRectPath, Color.BLUE, 0,0,1000,1000);
        /*rect2Paint = new Paint();
        rectPaint.setARGB(255,0,0,0);
        rectPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
        rect2Paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
        rect2Paint.setARGB(255,200,150,30);

        paint = new Paint();
        path = new Path();
        paint.setAntiAlias(true);
        paint.setARGB(255,255,255,255);
        //paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(25f);*/
    }

    private Bitmap createBitmap(Path path, int color, int left, int top, int right, int bottom) {
        // создание bitmap и канвы для него
        Bitmap bitmap = Bitmap.createBitmap(right-left, bottom-top,
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

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(blackRectBitmap, 0,0,blackRectPaint);
        canvas.drawBitmap(transparentRectBitmap, 300,700,transparentRectPaint);
        //canvas.drawRect(100,100,500,500, rectPaint);
        //canvas.drawRect(200+i,200,350+i,350, rect2Paint);
        //i+=30;
        //canvas.drawPath(path, paint);
        // DRAW STUFF HERE
    }

    public boolean onTouchEvent(MotionEvent event){
        float xPos = event.getX();
        float yPos = event.getY();

        /*switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                canvas.drawRect(0,0,1000,2000, rectPaint);
                return true;
            case MotionEvent.ACTION_MOVE:
                //path.lineTo(xPos, yPos);
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                return false;
        }*/
        invalidate();
        return true;
    }
}
