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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;

import static android.content.ContentValues.TAG;

/**
 * Прорисовывает шахматную доску, а так же выполняет отрисовку других элементов
 * Created by Art-_-master.
 */

public class DarwRectView extends View implements Animator.AnimatorListener{

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

    private HandlerPermissionStart mHandler;

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

                                    drawable=imageView.getDrawable();
                                    drawable.setBounds(mRectsCoordinates[i][i1][0]+mRectSide,
                                            mRectsCoordinates[i][i1][1],
                                            mRectsCoordinates[i][i1][0],
                                            mRectsCoordinates[i][i1][1]+mRectSide);

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

    public void animateStepKnightStart(int moveSide){
        if(imageView!=null){
            //imageView.setAnimation(new TranslateAnimation(getContext(), null));
            TranslateAnimation animation= new TranslateAnimation(imageView.getX(), imageView.getX()+200, imageView.getY(), imageView.getY());
            animation.setDuration(1000);
            //animation.setFillAfter(true);
            imageView.startAnimation(animation);

            int[] steps1 ={2, 2,  -2,  -2, 1, -1, 1, -1};
            int[] steps2 ={-1, 1,  -1, 1, -2, -2, 2,  2};
        }
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        //invalidate();
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

     public void setHandler(HandlerPermissionStart handler){
         mHandler=handler;
    }
}
