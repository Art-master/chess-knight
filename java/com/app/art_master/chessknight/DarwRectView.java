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
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

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
    public int mNumColumn;

    /** Количество линий в доске */
    public int mNumCell;

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

     /** Сторона квадрата шахматной доски */
    private int mRectSide;

    /** Изображение */
    private ImageView imageView;

     /** Ресурс из изображения. Для рисования */
    private Drawable drawable;

     /** Задатчик для общения двух объектов*/
    private HandlerPermissionStart mHandler;

     /** Позиция коня в матрице координат */
    private int mArrayIndex1;
    
    /**  Позиция коня в матрице координат */
    private int mArrayIndex2;

    
    /** Запущена ли анимация в настоящее время*/
    private boolean mAnimateRun=false;

    private int mTextSize;


    //==================================================================================\\
    private int mStartKnihtArrayIndex1;
    private int mStartKnihtArrayIndex2;
    private int mPathKnight;
    //==================================================================================\\


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

        //вычисляем размер текста
        mTextSize=getResources().getDimensionPixelSize(R.dimen.fontCanvasText);

        int textSize=0;

       //Устанавливаем размер текста и размер ячейки шахм. доски
        if(mNumColumn<=mNumCell){
            mRectSide = height/ mNumCell;
            textSize=mTextSize/ mNumCell;
        }else{
            mRectSide =height/ mNumColumn;
            textSize=mTextSize/ mNumColumn;
        }
        mPaint.setTextSize(textSize);
        mTextSize=textSize;

        //объявляем переменные
        mBoardLayer = new Picture();

        mRectLayer= new Picture[mNumCell][mNumColumn];

        mRect = new Rect();

        mRectsCoordinates = new int[cell][column][2];

        mRectsCoordinates[0][0][0]=0;

        //рисуем шахматную доску
        drawBoard(width, height);

        //устанавливаем слушателя, который при касании какой-то ячейки шахматной доски
        //ставит туда коня и сохраняет координаты этой ячейки
        this.setOnTouchListener(new DarwRectView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //если не запущена анимация
                if (!mAnimateRun) {
                    //если пользователь коснуля экрана и убрал палец
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        //сохраняем координаты
                        int x = (int) event.getX();
                        int y = (int) event.getY();
                        //выполняем вычисления, если касания произошли не за областью доски
                        if (x <= mWidth & y <= mHeight) {
                            //перебираем матрицу координат ячеек
                            for (int i = 0; i < mNumCell; i++) {
                                for (int i1 = 0; i1 < mNumColumn; i1++) {
                                    //если координаты касания пользователя в пределах ячейки, устанавливаем туда коня
                                    if ((x > mRectsCoordinates[i][i1][0] &
                                            x <= mRectsCoordinates[i][i1][0] + mRectSide)
                                            & (y > mRectsCoordinates[i][i1][1] &
                                            y <= mRectsCoordinates[i][i1][1] + mRectSide)) {

                                        //получаем битовую карту из изображения
                                        Bitmap knight = getBitmapFromAsset("knight.png");
                                        //создаем изображение из битовой карты изображения
                                        imageView = new ImageView(getContext());
                                        imageView.setImageBitmap(knight);
                                        //устанавливаем позицию изображения
                                        imageView.setLeft(mRectsCoordinates[i][i1][0] + mRectSide);
                                        imageView.setTop(mRectsCoordinates[i][i1][1]);
                                        imageView.setRight(mRectsCoordinates[i][i1][0]);
                                        imageView.setBottom(mRectsCoordinates[i][i1][1] + mRectSide);
                                        //получаем ресурс из изображения
                                        //устанавливаем границы
                                        drawable = imageView.getDrawable();
                                        drawable.setBounds(mRectsCoordinates[i][i1][0] + mRectSide,
                                                mRectsCoordinates[i][i1][1],
                                                mRectsCoordinates[i][i1][0],
                                                mRectsCoordinates[i][i1][1] + mRectSide);

                                        //сохраняем текущую позицию коня
                                        mArrayIndex1 = i;
                                        mArrayIndex2 = i1;
                                        //активируем Handler (активируем кнопку старт)
                                        Message msg = new Message();
                                        msg.arg2 = 0;
                                        mHandler.getHandler().sendMessage(msg);
                                        //перерисовывам все вместе
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
                return false;
            }
        });

    }

    @Override
    protected void onDraw(Canvas canvas) {
        //рисут основной слой с шахматной доской
        if(mBoardLayer!=null){
        canvas.drawPicture(mBoardLayer);
        }

        //рисут дополнительные слои с метками номера хода шахматного коня
            for(int i = 0; i< mNumCell; i++) {
                for (int i1 = 0; i1 < mNumColumn; i1++) {
                    if(mRectLayer[i][i1]!=null){
                        canvas.drawPicture(mRectLayer[i][i1]);
                    }
                }
            }
        
        //Рисует шахматного коня
        if(drawable!=null){
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
           //Создает новый слой, записывая в нем все последующие действия
            Canvas canvas = mBoardLayer.beginRecording(width, height);

            int alternation;
        
            //В цикле происходит построение шаматного поля с записью координат в матрицу
            for(int i = 0; i< mNumCell; i++) {
                for (int i1 = 0; i1 < mNumColumn; i1++) {
                    mRectsCoordinates[i][i1][0]= mRectsCoordinates[i][i1][0]+ mRectSide *i1;
                    mRectsCoordinates[i][i1][1]= mRectsCoordinates[i][i1][1]+ mRectSide *i;
                    mRect.set(mRectsCoordinates[i][i1][0],
                            mRectsCoordinates[i][i1][1] + mRectSide,
                            mRectsCoordinates[i][i1][0] + mRectSide,
                            mRectsCoordinates[i][i1][1]);

                   //Определяет цвет шахматных полей
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

                    // рисует прямоугольник
                    canvas.drawRect(mRect, mPaint);
                }
            }
        //завершает запись слоя
        mBoardLayer.endRecording();
        }

      /**
     * Рисует метку с номером хода коня по доске
     *
     * @param arrayIndex1  - координата 1 метки в матрице
     * @param arrayIndex2 - координата 2 метки в матрице
     * @param color - цвет метки 
     * @param text - текст внутри метки
     */  
    public void drawCircle(int arrayIndex1, int arrayIndex2, int color, String text){

        //инициализирует матрицу слоев
        mRectLayer[arrayIndex1][arrayIndex2] = new Picture();
        //начало записи слоя
        Canvas canvas = mRectLayer[arrayIndex1][arrayIndex2].beginRecording(mWidth, mHeight);
        //устанавливаем границы примоугольника
        mRect.set(mRectsCoordinates[arrayIndex1][arrayIndex2][0],
                mRectsCoordinates[arrayIndex1][arrayIndex2][1] + mRectSide,
                mRectsCoordinates[arrayIndex1][arrayIndex2][0] + mRectSide,
                mRectsCoordinates[arrayIndex1][arrayIndex2][1]);
        //цвет примоугольника
        mPaint.setColor(color);
        RectF rectF= new RectF(mRect);
        //рисуем овал
        canvas.drawOval(rectF, mPaint);
        //черный текст для текста
        mPaint.setColor(Color.BLACK);
        //рисуем текст
        float textX=mRectSide /3;
        float textY=mRectSide /1.4f;
        if(text.length()==2){
            textX/=2;
        }else if(text.length()==3){
            textX/=4;
            textY/=1.1f;
            mPaint.setTextSize(mTextSize/1.3f);
        }else if(text.length()==4){
            textX/=8;
            textY/=1.15f;
            mPaint.setTextSize(mTextSize/1.5f);
        }
        canvas.drawText(text, mRectsCoordinates[arrayIndex1][arrayIndex2][0]+textX,
                mRectsCoordinates[arrayIndex1][arrayIndex2][1]+textY,  mPaint);

        //конец записи слоя
        mRectLayer[arrayIndex1][arrayIndex2].endRecording();
        //разрешаем перерисоквку
        invalidate();
    }
    
    /**
     * Удаляет метку с номером хода коня по доске
     *
     * @param arrayIndex1  - координата 1 метки в матрице
     * @param arrayIndex2 - координата 2 метки в матрице
     * @param color - цвет метки 
     */   
    public void clearCircle(int arrayIndex1, int arrayIndex2, int color){
        if(mRectLayer[arrayIndex1][arrayIndex2]!=null){
            mRectLayer[arrayIndex1][arrayIndex2]=null;
        }
    }
    
    /**
     * Получает битовую карту из ресурса
     *
     * @param strName  - имя ресурса в каталоге Assets
     * @return  битовая карта
     */   
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
    
    /**
     *
     *
     * @param arrayIndex1  - начальная координата 1 метки в матрице
     * @param arrayIndex2 - начальная координата 2 метки в матрице
     * @param arrayIndexStop1 - конечная координата 1 метки в матрице
     * @param arrayIndexStop2 - конечная координата 2 метки в матрице
     * @return запущена ли анимация в данный момент или нет
     */    
    public void animateStepKnightStart(int arrayIndex1, int arrayIndex2, int arrayIndexStop1, int arrayIndexStop2){
        if(drawable!=null & !mAnimateRun) {
            mAnimateRun=true;
            //создаем таймер, который будет двигать фигуру на n пикселей 
            final Timer timer= new Timer();
            //Задача для таймера
            //TimerChess timerTask= new TimerChess(mArrayIndex1, mArrayIndex2, 1, 2, 1);
            TimerChess timerTask= new TimerChess(arrayIndex1, arrayIndex2, 1, arrayIndexStop1, arrayIndexStop2);
            //Устанавлиаеим таймеру задачу, которая будет выполняться каждые n милисекунд
            timer.schedule(timerTask, 1, 1);
            }
    }
    public boolean isAnimationStop(){
        return mAnimateRun;
    }
    
    /**
     * Устанавливает обработчик
     *
     * @param handler  - обработчик
     */    
    public void setHandler(HandlerPermissionStart handler){
         mHandler=handler;
    }

    /**
     * Класс-задатчик для таймера
     * определяет в какую сторону и на какое расстояние двинуть коня по доске
     */  
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
            
            if((((mArrayIndexStop1+1)-(mArrayInd1+1)%2)&0x01)==1){
                if(mXposition!=mRectsCoordinates[mArrayIndexStop1][mArrayIndexStop2][0]){
                    if((mArrayIndexStop2<mArrayInd2 & mDistance>0) || (mArrayIndexStop2>=mArrayInd2 & mDistance<0))mDistance*=-1;
                    mXposition+=mDistance;
                }else {
                    if((mArrayIndexStop1<mArrayInd1 & mDistance>0) || (mArrayIndexStop1>=mArrayInd1 & mDistance<0))mDistance*=-1;
                    mYposition+=mDistance;
                    if(mRectsCoordinates[mArrayIndexStop1][mArrayIndexStop2][1]==(mYposition+mDistance)){
                        mAnimateRun=false;
                        this.cancel();
                        Message msg=new Message();
                        msg.arg2=3;
                        mHandler.getHandler().sendMessage(msg);
                        }
                }

            } else
            if((((mArrayIndexStop2+1)-(mArrayInd2+1)%2)&0x01)==1){
                if(mYposition!=mRectsCoordinates[mArrayIndexStop1][mArrayIndexStop2][1]){
                    if((mArrayIndexStop1<mArrayInd1 & mDistance>0) || (mArrayIndexStop1>=mArrayInd1 & mDistance<0))mDistance*=-1;
                    mYposition+=mDistance;
                }else {
                    if((mArrayIndexStop2<mArrayInd2 & mDistance>0) || (mArrayIndexStop2>=mArrayInd2 & mDistance<0))mDistance*=-1;
                    mXposition+=mDistance;
                    if(mRectsCoordinates[mArrayIndexStop1][mArrayIndexStop2][0]==(mXposition+mDistance)){
                        mAnimateRun=false;
                        this.cancel();
                        Message msg=new Message();
                        msg.arg2=3;
                        mHandler.getHandler().sendMessage(msg);
                    }
                }
            }

            postInvalidate();
        }

    }

    public int getArrayInitIndex(int indexNum){
        if(indexNum==1){
            return mArrayIndex1;
        }else if(indexNum==2){
            return mArrayIndex2;
        }
        return 0;
    }

    public void startAnim(int[][][] matrixPosition){
        if(matrixPosition[0][0][0]==0){
            Toast toast = Toast.makeText(getContext(), "Ходов не найдено", Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        if(mPathKnight==0){
            mPathKnight=1;
            mStartKnihtArrayIndex1=mArrayIndex1;
            mStartKnihtArrayIndex2=mArrayIndex2;
            drawCircle(mStartKnihtArrayIndex1, mStartKnihtArrayIndex2, Color.rgb(230, 64, 64), mPathKnight+"");
            mPathKnight=2;
        }else {
            drawCircle(mStartKnihtArrayIndex1, mStartKnihtArrayIndex2, Color.rgb(43, 153, 74), mPathKnight+"");
        }

        if(mPathKnight<=(mNumCell*mNumColumn)){
            i:for (int i1 = 0; i1 <= mNumCell-1; i1++) {
                for (int i2 = 0; i2 <= mNumColumn-1; i2++) {
                    if(matrixPosition[i1][i2][0]==mPathKnight){
                        animateStepKnightStart(mStartKnihtArrayIndex1, mStartKnihtArrayIndex2, i1, i2);
                         mStartKnihtArrayIndex1 =i1;
                         mStartKnihtArrayIndex2 =i2;
                         mPathKnight++;
                        break i;
                    }

                }

            }
        }
    }

}



