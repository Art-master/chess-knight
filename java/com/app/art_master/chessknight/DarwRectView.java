package com.app.art_master.chessknight;

import android.animation.Animator;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Прорисовывает шахматную доску, а так же выполняет отрисовку других элементов
 * Created by Art-_-master.
 */

public class DarwRectView extends View{

    /** Объект Paint. Устанавливает параметры отрисовки */
    private Paint mPaint;

    /** Отрисовывает поля для хода на шахматной доске */
    private Rect mRect;

    /** Отрисовывает метки на полях для хода */
    //private Ova mCircle;

    /** Количество столбцов в доске */
    private int mNumColumn;

    /** Количество линий в доске */
    private int mNumCell;

    /** Координаты полей для хода */
    public static int [][][] mRectsCoordinates;

    /** Объект Picture. Для сохранения шахматной доски в доп. слое */
    private Picture mBoardLayer;

    /** Объект Picture. Для сохранения шахматной доски в доп. слое */
    private Picture [][] mRectLayer;

    /** Высота текущего View */
    private  int mHeight;

    /** Ширина текущего View */
    private  int mWidth;

    /** Разрешает перерисокву Canvas */
    private boolean mReDraw=false;

    private int mRectSide;

    private Bitmap knight;

    ImageView imageView;

    Drawable drawable;

    public static final byte ANIMATION_UP=0;

    public static final byte ANIMATION_LEFT=1;

    public static final byte ANIMATION_DOWN=2;

    public static final byte ANIMATION_RIGHT=3;

    private HandlerPermissionStart mHandler;

    private int mArrayIndex1;

    private int mArrayIndex2;

    private int mDistance;

    private boolean mTimerRun=false;

    public DarwRectView(final Context context, AttributeSet atr, int column, int cell, int height, int width) {
        super(context);

        this.setFocusableInTouchMode(true);
        this.setFocusable(true);
        this.setClickable(true);
        this.setEnabled(true);

        mNumCell =cell;
        mNumColumn =column;
        mHeight=height;
        mWidth=width;

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //setOnTouchListener(this);

        int textSize=getResources().getDimensionPixelSize(R.dimen.fontCanvasText);

        if(mNumColumn<=mNumCell){
            mRectSide = height/ mNumCell;
            mPaint.setTextSize(textSize/ mNumCell);
        }else{
            mRectSide =height/ mNumColumn;
            mPaint.setTextSize(textSize/ mNumColumn);
        }

        mBoardLayer = new Picture();

        mRectLayer= new Picture[mNumCell][mNumColumn];

        mRect = new Rect();

        mRectsCoordinates = new int[cell][column][2];

        mRectsCoordinates[0][0][0]=0;

        drawBoard(width, height);

        this.setOnTouchListener(new DarwRectView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int x=(int)event.getX();
                    int y=(int)event.getY();
                    if(x<=mWidth & y<=mHeight){
                        for(int i = 0; i< mNumCell; i++) {
                            for (int i1 = 0; i1 < mNumColumn; i1++) {
                                if((x > mRectsCoordinates[i][i1][0] &
                                        x <= mRectsCoordinates[i][i1][0]+mRectSide)
                                & (y > mRectsCoordinates[i][i1][1] &
                                        y<=mRectsCoordinates[i][i1][1]+mRectSide))  {

                                    knight=getBitmapFromAsset("knight.png");

                                    imageView = new ImageView(getContext());
                                    imageView.setImageBitmap(knight);
                                    imageView.setLeft(mRectsCoordinates[i][i1][0]+mRectSide);
                                    imageView.setTop(mRectsCoordinates[i][i1][1]);
                                    imageView.setRight(mRectsCoordinates[i][i1][0]);
                                    imageView.setBottom(mRectsCoordinates[i][i1][1]+mRectSide);

                                    drawable=imageView.getDrawable();
                                    drawable.setBounds(mRectsCoordinates[i][i1][0]+mRectSide,
                                            mRectsCoordinates[i][i1][1],
                                            mRectsCoordinates[i][i1][0],
                                            mRectsCoordinates[i][i1][1]+mRectSide);

                                    mArrayIndex1 =i;
                                    mArrayIndex2 =i1;
                                    mHandler.getHandler().sendEmptyMessage(0);
                                    invalidate();
                                }
                            }
                        }
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });

    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawPicture(mBoardLayer);

        if(mReDraw){
            for(int i = 0; i< mNumCell; i++) {
                for (int i1 = 0; i1 < mNumColumn; i1++) {
                    if(mRectLayer[i][i1]!=null){
                        canvas.drawPicture(mRectLayer[i][i1]);
                    }
                }
            }
        }
        //if(knight!=null) canvas.drawBitmap(knight,3,200,mPaint);
        if(imageView!=null){
            drawable.draw(canvas);
        }
        if(imageView!=null){
            //imageView.draw(canvas);
        }

    }

    /**
     * Отрисовывает шахматную доску в зависимости от введенных данных пользователем
     * и сохраняет ее в отдельном слое
     *
     * @param width  - ширина текущего View
     * @param height - высота текущего View
     */
    private void drawBoard(int width, int height){
            Canvas canvas = mBoardLayer.beginRecording(width, height);

            int alternation;

            for(int i = 0; i< mNumCell; i++) {
                for (int i1 = 0; i1 < mNumColumn; i1++) {
                    mRectsCoordinates[i][i1][0]= mRectsCoordinates[i][i1][0]+ mRectSide *i1;
                    mRectsCoordinates[i][i1][1]= mRectsCoordinates[i][i1][1]+ mRectSide *i;
                    mRect.set(mRectsCoordinates[i][i1][0],
                            mRectsCoordinates[i][i1][1] + mRectSide,
                            mRectsCoordinates[i][i1][0] + mRectSide,
                            mRectsCoordinates[i][i1][1]);

                    if (i%2==0) {
                        alternation=1;
                    }else{
                        alternation=0;
                    }
                    switch (alternation)
                    {
                        case 0:
                            if (i1%2!=0) {
                                mPaint.setColor(Color.WHITE);
                            }else{
                                mPaint.setColor(Color.BLACK);
                            }
                            break;
                        case 1:
                            if (i1%2!=0) {
                                mPaint.setColor(Color.BLACK);
                            }
                            else{
                                mPaint.setColor(Color.WHITE);
                            }
                            break;
                    }

                    // рисуем прямоугольник из объекта mRect
                    canvas.drawRect(mRect, mPaint);
                }
            }
        mBoardLayer.endRecording();
        }

    public void drawCircle(int arrayIndex1, int arrayIndex2, int color, String text){
        mReDraw=true;
        mRectLayer[arrayIndex1][arrayIndex2] = new Picture();
        Canvas canvas = mRectLayer[arrayIndex1][arrayIndex2].beginRecording(mWidth, mHeight);
        mRect.set(mRectsCoordinates[arrayIndex1][arrayIndex2][0],
                mRectsCoordinates[arrayIndex1][arrayIndex2][1] + mRectSide,
                mRectsCoordinates[arrayIndex1][arrayIndex2][0] + mRectSide,
                mRectsCoordinates[arrayIndex1][arrayIndex2][1]);
        mPaint.setColor(color);
        RectF rectF= new RectF(mRect);
        canvas.drawOval(rectF, mPaint);
        mPaint.setColor(Color.BLACK);
        canvas.drawText(text, mRectsCoordinates[arrayIndex1][arrayIndex2][0]+(mRectSide /3),
                mRectsCoordinates[arrayIndex1][arrayIndex2][1]+(mRectSide /1.4f),  mPaint);
        mRectLayer[arrayIndex1][arrayIndex2].endRecording();

        invalidate();
    }
    public void clearCircle(int arrayIndex1, int arrayIndex2, int color){
        if(mRectLayer[arrayIndex1][arrayIndex2]!=null){
            mRectLayer[arrayIndex1][arrayIndex2]=null;
        }
    }

    private Bitmap getBitmapFromAsset(String strName)
    {
        AssetManager assetManager = getContext().getAssets();
        InputStream stream = null;
        try {
            stream = assetManager.open(strName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeStream(stream);
    }

    public void animateStepKnightStart(int arrayIndex1, int arrayIndex2, int moveSide, Long animDuration){
        if(drawable!=null) {
            //TranslateAnimation animation= new TranslateAnimation(imageView.getX(), imageView.getX()+200, imageView.getY(), imageView.getY());
            //animation.setDuration(1000);
            //imageView.startAnimation(animation);
            byte[] steps;

            switch (moveSide) {
                case ANIMATION_UP:
                    //steps = {};
                    break;
                case ANIMATION_LEFT:

                    break;
                case ANIMATION_DOWN:

                    break;
                case ANIMATION_RIGHT:

                    break;
            }
            //с какой скоростью должна двигаться фигура за 1 мс
            int distance=(mRectSide*4)/(animDuration.intValue()/1000);
            int knightPath=0;
            //создаем таймер, который будет ждать столько, сколько длиться анимация загрузки
            final Timer timer= new Timer();
            TimerChess timerTask= new TimerChess(mArrayIndex1, mArrayIndex2, 1, 2, 1);

            timer.schedule(timerTask, 1, 1);
            }

    }

    public void setHandler(HandlerPermissionStart handler){
         mHandler=handler;
    }

    private class TimerChess extends TimerTask {
        private int mArrayInd1;
        private int mArrayInd2;
        private int mDistance;
        private int mArrayIndexStop1;
        private int mArrayIndexStop2;
        private int mXposition;
        private int mYposition;


        TimerChess(int arrayIndex1, int arrayIndex2, int distance, int arrayIndexStop1, int arrayIndexStop2){
            mArrayInd1 =arrayIndex1;
            mArrayInd2 =arrayIndex2;
            mDistance=distance;
            mArrayIndexStop1=arrayIndexStop1;
            mArrayIndexStop2=arrayIndexStop2;
            mXposition=mRectsCoordinates[mArrayInd1][mArrayInd2][0];
            mYposition=mRectsCoordinates[mArrayInd1][mArrayInd2][1];
        }

        @Override
        public void run() {

            drawable.setBounds(mXposition+mRectSide,
                    mYposition,
                    mXposition,
                    mYposition+mRectSide);

            if((mArrayIndexStop1-mArrayInd1%2)==1){
                if(mXposition!=mRectsCoordinates[mArrayIndexStop1][mArrayIndexStop2][0]){
                    if(mArrayIndexStop1<mArrayInd1)mDistance=~mDistance;
                    mXposition+=mDistance;
                }else {
                    if(mArrayIndexStop2<mArrayInd2 | mDistance<0)mDistance=~mDistance;
                    mYposition+=mDistance;
                    if(mRectsCoordinates[mArrayIndexStop1][mArrayIndexStop2][1]==(mYposition+mDistance)){
                        this.cancel();
                    }
                }

            }
            if((mArrayIndexStop2-mArrayInd2%2)==1){
                if(mYposition!=mRectsCoordinates[mArrayIndexStop1][mArrayIndexStop2][1]){
                    if(mArrayIndexStop2<mArrayInd2)mDistance=~mDistance;
                    mYposition+=mDistance;
                }else {
                    if(mArrayIndexStop1<mArrayInd1 | mDistance<0)mDistance=~mDistance;
                    mXposition+=mDistance;
                    if(mRectsCoordinates[mArrayIndexStop1][mArrayIndexStop2][0]==(mXposition+mDistance)){
                        this.cancel();
                    }
                }
            }

            postInvalidate();
        }
    }

}



