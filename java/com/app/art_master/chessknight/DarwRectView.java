package com.app.art_master.chessknight;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

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

    /** Высота текущего View */
    private  int mHeight;

    /** Ширина текущего View */
    private  int mWidth;

    /** Разрешает перерисокву Canvas */
    private boolean mReDraw=false;

    public DarwRectView(Context context, AttributeSet atr, int column, int cell, int height, int width) {
        super(context);

        mNumCell =cell;
        mNumColumn =column;
        mHeight=height;
        mWidth=width;

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mRect = new Rect();

        mRectsCoordinates = new int[cell][column][2];

        mRectsCoordinates[0][0][0]=0;

        drawBoard(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawPicture(mBoardLayer);

        if(mReDraw){
            mRect. set(0, mHeight/ mNumColumn, mHeight/ mNumCell, 0);
            mPaint.setColor(Color.GREEN);
            //canvas.drawRect(mRect, mPaint);
            RectF rectF= new RectF(mRect);
            //rectF.set(mRect);
            canvas.drawOval(rectF, mPaint);
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
            int rectWidth=height/ mNumColumn;
            int rectHeight= height/ mNumCell;

            mBoardLayer = new Picture();
            Canvas canvas = mBoardLayer.beginRecording(width, height);

            int alternation;

            for(int i = 0; i< mNumCell; i++) {
                for (int i1 = 0; i1 < mNumColumn; i1++) {
                    mRectsCoordinates[i][i1][0]= mRectsCoordinates[i][i1][0]+rectWidth*i1;
                    mRectsCoordinates[i][i1][1]= mRectsCoordinates[i][i1][1]+rectHeight*i;
                    mRect.set(mRectsCoordinates[i][i1][0],
                            mRectsCoordinates[i][i1][1] + rectHeight,
                            mRectsCoordinates[i][i1][0] + rectWidth,
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

    public void click(){
        mReDraw=true;
        invalidate();
    }

}
