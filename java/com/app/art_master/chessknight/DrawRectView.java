package com.app.art_master.chessknight;

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

public class DrawRectView extends View{

    /** Объект Paint. Устанавливает параметры отрисовки */
    private Paint mPaint;

    /** Отрисовывает поля для хода на шахматной доске */
    private Rect mRect;

    /** Количество столбцов в доске */
    public int mNumColumn;

    /** Количество линий в доске */
    public int mNumCell;

    /** Координаты полей для хода */
    public static int [][][] mRectCoordinates;

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

    /** Размер текста канваса*/
    private int mTextSize;


    //==================================================================================\\
    /** Позиция коня при выполнении анимации */
    private int mStartKnightArrayIndex1;

    /** Позиция коня при выполнении анимации */
    private int mStartKnightArrayIndex2;

    /** Номер хода*/
    private int mPathKnight;
    //==================================================================================\\


    public DrawRectView(final Context context, AttributeSet atr, int column, int cell, int height, int width) {
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

        int textSize;

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

        mRectCoordinates = new int[cell][column][2];

        mRectCoordinates[0][0][0]=0;

        //рисуем шахматную доску
        drawBoard(width, height);

        //устанавливаем слушателя, который при касании какой-то ячейки шахматной доски
        //ставит туда коня и сохраняет координаты этой ячейки
        this.setOnTouchListener(new DrawRectView.OnTouchListener() {
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
                                    if ((x > mRectCoordinates[i][i1][0] &
                                            x <= mRectCoordinates[i][i1][0] + mRectSide)
                                            & (y > mRectCoordinates[i][i1][1] &
                                            y <= mRectCoordinates[i][i1][1] + mRectSide)) {

                                        //получаем битовую карту из изображения
                                        Bitmap knight = getBitmapFromAsset("knight.png");
                                        //создаем изображение из битовой карты изображения
                                        imageView = new ImageView(getContext());
                                        imageView.setImageBitmap(knight);
                                        //устанавливаем позицию изображения
                                        imageView.setLeft(mRectCoordinates[i][i1][0] + mRectSide);
                                        imageView.setTop(mRectCoordinates[i][i1][1]);
                                        imageView.setRight(mRectCoordinates[i][i1][0]);
                                        imageView.setBottom(mRectCoordinates[i][i1][1] + mRectSide);
                                        //получаем ресурс из изображения
                                        //устанавливаем границы
                                        drawable = imageView.getDrawable();
                                        drawable.setBounds(mRectCoordinates[i][i1][0] + mRectSide,
                                                mRectCoordinates[i][i1][1],
                                                mRectCoordinates[i][i1][0],
                                                mRectCoordinates[i][i1][1] + mRectSide);

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
                    mRectCoordinates[i][i1][0]= mRectCoordinates[i][i1][0]+ mRectSide *i1;
                    mRectCoordinates[i][i1][1]= mRectCoordinates[i][i1][1]+ mRectSide *i;
                    mRect.set(mRectCoordinates[i][i1][0],
                            mRectCoordinates[i][i1][1] + mRectSide,
                            mRectCoordinates[i][i1][0] + mRectSide,
                            mRectCoordinates[i][i1][1]);

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
        mRect.set(mRectCoordinates[arrayIndex1][arrayIndex2][0],
                mRectCoordinates[arrayIndex1][arrayIndex2][1] + mRectSide,
                mRectCoordinates[arrayIndex1][arrayIndex2][0] + mRectSide,
                mRectCoordinates[arrayIndex1][arrayIndex2][1]);
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
        canvas.drawText(text, mRectCoordinates[arrayIndex1][arrayIndex2][0]+textX,
                mRectCoordinates[arrayIndex1][arrayIndex2][1]+textY,  mPaint);

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
        /** 1 позиция массива для начала хода коня */
        private int mArrayInd1;

        /** 2 позиция массива для начала хода коня */
        private int mArrayInd2;

        /** Количество пикселей, на которое двигается фигура */
        private int mDistance;

        /** 1 позиция массива для окончания хода коня */
        private int mArrayIndexStop1;

        /** 2 позиция массива для окончания хода коня */
        private int mArrayIndexStop2;

        /** Х координата коня */
        private int mXPosition;

        /** У координата коня */
        private int mYPosition;


        TimerChess(int arrayIndex1, int arrayIndex2, int distance, int arrayIndexStop1, int arrayIndexStop2){
            mArrayInd1 =arrayIndex1;
            mArrayInd2 =arrayIndex2;
            mDistance=distance;
            mArrayIndexStop1=arrayIndexStop1;
            mArrayIndexStop2=arrayIndexStop2;
            mXPosition = mRectCoordinates[mArrayInd1][mArrayInd2][0];
            mYPosition = mRectCoordinates[mArrayInd1][mArrayInd2][1];
        }

        @Override
        public void run() {

            //Устанавливаем положение и размер фигуры
            drawable.setBounds(mXPosition +mRectSide,
                    mYPosition,
                    mXPosition,
                    mYPosition +mRectSide);

            //определяем, в какую сторону должен двигаться конь
            // 2 клетки влево или вправо и 1 клетка вверх или вниз
            if((((mArrayIndexStop1+1)-(mArrayInd1+1)%2)&0x01)==1){
                if(mXPosition != mRectCoordinates[mArrayIndexStop1][mArrayIndexStop2][0]){
                    //Определяем, если движение отрицательное, то заставляем фигуру двигаться в обр. напр
                    if((mArrayIndexStop2<mArrayInd2 & mDistance>0) ||
                            (mArrayIndexStop2>=mArrayInd2 & mDistance<0))mDistance*=-1;
                    //смещаем х координату
                    mXPosition +=mDistance;
                }else {
                    //Определяем, если движение отрицательное, то заставляем фигуру двигаться в обр. напр
                    if((mArrayIndexStop1<mArrayInd1 & mDistance>0) ||
                            (mArrayIndexStop1>=mArrayInd1 & mDistance<0))mDistance*=-1;
                    //смещаем у координату
                    mYPosition +=mDistance;
                    //если конь дошел до нужной точки, отправляем сообщение в вызывавший класс
                    if(mRectCoordinates[mArrayIndexStop1][mArrayIndexStop2][1]==(mYPosition +mDistance)){
                        mAnimateRun=false;
                        this.cancel();
                        Message msg=new Message();
                        msg.arg2=3;
                        mHandler.getHandler().sendMessage(msg);
                        }
                }
            } else
            // 2 клетки вверх или вниз и 1 клетка влево или вправо
            if((((mArrayIndexStop2+1)-(mArrayInd2+1)%2)&0x01)==1){
                if(mYPosition != mRectCoordinates[mArrayIndexStop1][mArrayIndexStop2][1]){
                    //Определяем, если движение отрицательное, то заставляем фигуру двигаться в обр. напр
                    if((mArrayIndexStop1<mArrayInd1 & mDistance>0) ||
                            (mArrayIndexStop1>=mArrayInd1 & mDistance<0))mDistance*=-1;
                    //смещаем у координату
                    mYPosition +=mDistance;
                }else {
                    //Определяем, если движение отрицательное, то заставляем фигуру двигаться в обр. напр
                    if((mArrayIndexStop2<mArrayInd2 & mDistance>0) ||
                            (mArrayIndexStop2>=mArrayInd2 & mDistance<0))mDistance*=-1;
                    //смещаем х координату
                    mXPosition +=mDistance;
                    //если конь дошел до нужной точки, отправляем сообщение в вызывавший класс
                    if(mRectCoordinates[mArrayIndexStop1][mArrayIndexStop2][0]==(mXPosition +mDistance)){
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
        //если первый элемент массива равен 0, то алгаритм не нашел путь
        if(matrixPosition[0][0][0]==0){
            Toast toast = Toast.makeText(getContext(),
                    "Невозможно построить путь\n из данной точки", Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        //инициализируем индексы и рисуем первый круг для начала пути коня
        if(mPathKnight==0){
            mPathKnight=1;
            mStartKnightArrayIndex1 =mArrayIndex1;
            mStartKnightArrayIndex2 =mArrayIndex2;
            drawCircle(mStartKnightArrayIndex1, mStartKnightArrayIndex2,
                    Color.rgb(230, 64, 64), mPathKnight+"");
            mPathKnight=2;
        //во всех остальных случаях рисуем зеленые круги
        }else {
            drawCircle(mStartKnightArrayIndex1, mStartKnightArrayIndex2,
                    Color.rgb(43, 153, 74), mPathKnight-1+"");
        }
        //затираем слушателя, чтоб не двигать коня
        setOnTouchListener(null);

        //ищем в массиве, поочередно, точки, по которым ходил конь рисуем их и выполняем анимацию коня
        if(mPathKnight<=(mNumCell*mNumColumn)){
            i:for (int i1 = 0; i1 <= mNumCell-1; i1++) {
                for (int i2 = 0; i2 <= mNumColumn-1; i2++) {
                    if(matrixPosition[i1][i2][0]==mPathKnight){
                        animateStepKnightStart(mStartKnightArrayIndex1, mStartKnightArrayIndex2, i1, i2);
                         mStartKnightArrayIndex1 =i1;
                         mStartKnightArrayIndex2 =i2;
                         mPathKnight++;
                        break i;
                    }

                }

            }
        }
    }

}



