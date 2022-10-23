package com.example.guessaceleb;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.MotionEvent;
import android.view.View;

public class DrawView extends  View{


        Paint paintSrc;
        Paint paintDst;
        Paint paintBorder;

        Path pathSrc;
        Path pathDst;
        Path pathDraw;

        Bitmap bitmapSrc;
        Bitmap bitmapDst;

        // PorterDuff режим
        PorterDuff.Mode mode = PorterDuff.Mode.XOR;

        int colorDst = Color.BLUE;
        int colorSrc = Color.YELLOW;

        public DrawView(Context context) {
            super(context);

            // необходимо для корректной работы
            if (android.os.Build.VERSION.SDK_INT >= 11) {
                setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }

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
            pathSrc.moveTo(0, 0);
            pathSrc.lineTo(500, 0);
            pathSrc.lineTo(0, 500);
            pathSrc.close();

            // создание SRC bitmap
            bitmapSrc = createBitmap(pathSrc, colorSrc);

            // кисть для вывода SRC bitmap
            paintSrc = new Paint();
            paintSrc.setXfermode(new PorterDuffXfermode(mode));

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
            canvas.translate(390, 80);

            // DST bitmap
            canvas.drawPath(pathDst, paintDst);

            canvas.drawPath(pathDraw,paintSrc);

            // SRC bitmap
            //canvas.drawBitmap(bitmapSrc, 0, 0, paintSrc);

            // рамка
            canvas.drawRect(0, 0, 500, 500, paintBorder);

        }
        public boolean onTouchEvent(MotionEvent event){
            float xPos = event.getX();
            float yPos = event.getY();

            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    //canvas.drawRect(0,0,1000,2000, rectPaint);
                    pathDraw.moveTo(xPos, yPos);
                    return true;
                case MotionEvent.ACTION_MOVE:
                    pathDraw.lineTo(xPos, yPos);
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                default:
                    return false;
            }/**/
            invalidate();
            return true;
        }

}
