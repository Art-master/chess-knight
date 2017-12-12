package com.app.art_master.chessknight;

import android.os.Message;

/**
 * алгоритм движения коня по шахматной доске
 * Created by Art-_-master
 */

public class KnightDriveThread implements Runnable {


    /** Массив предыдущих шагов коня */
    private int [] mLastKnightSteps;

    /** индекс 1 для массива ходов коня */
    private int mInitArray1 =0;

    /** индекс 2 для массива ходов коня */
    private int mInitArray2 =0;

    /** матрица ходов коня по шахматной доске */
    private int[][][] matrixChessBoard;

    /** Счетчик шагов коня */
    private int mPathKnight =1;

    /** количество горизонтальных линий в шахматной доске */
    private int mColumn;

    /** количество колонок в шахматной доске */
    private int mCell;

    /** индекс 1 для массива ходов коня для промежуточных вычислений */
    private int mLocalInit1 =0;

    /** индекс 2 для массива ходов коня для промежуточных вычислений */
    private int mLocalInit2 =0;

    /**
     *Счетчики и инициаторы
     */
    private boolean mKnightMoveControl =false;
    private boolean mControl =true;

    /** объект шахматной доски в Canvas и анимации движения коня по доске */
   // private DarwRectView mChessboard;

    /** матрица рпзрешенных ходов коня по доске */
    private boolean[][][] mMatrixSteps;

    /** Handler для отслеживания действий паралельного потока*/
    private HandlerPermissionStart mHandler;

    //в конструктор передаем объект шахматной доски
    KnightDriveThread(int initArray1, int initArray2, int numCell, int numColumn){

        //mChessboard=chessBoardObject;
        mColumn =numCell;
        mCell =numColumn;

        matrixChessBoard = new int[mColumn][mCell][1];
        mLastKnightSteps = new int[mColumn * mCell +1];
        mMatrixSteps = new boolean [mColumn][mCell][8];

        //В начале работы алгоритма получаем индексы матрицы, с текущими координатами коня
        mKnightMoveControl =true;
        mInitArray1 =initArray1;
        mInitArray2 =initArray2;
    }

    @Override
    public void run() {
            present();
    }

    /**
     * <paint>
     * Инициализируем алгаритм ходов коня
     * </paint>
     */
    public void present() {

        if(mKnightMoveControl){
            //количество обработанных алгаритмом ходов коня
            int controlBackStep = 0;
            //
            int forward;
            matrixChessBoard[mInitArray1][mInitArray2][0]=1;
            while(mControl){

                int[] steps1 ={2, 2,  -2,  -2, 1, -1, 1, -1};
                int[] steps2 ={-1, 1,  -1, 1, -2, -2, 2,  2};

                //находим доступные ходы
                forward = moveKnightInCell(controlBackStep, steps1, steps2);

                // перед каждым ходом обнуляем щетчик ходов
                controlBackStep = 0;


                //всего 8 возможных ходов коня по доске из заданной точки
                // i= шаг

                interrupt: for (int i = forward; i <8; i++) {
                    //если текущий шаг не запрещен
                    if (mMatrixSteps[mInitArray1][mInitArray2][i]) {
                        //i=forward;
                        // записываем координаты следующей точки в зависимости от того, куда шагнул конь
                            mInitArray1 += steps1[i];
                            mInitArray2 += steps2[i];
                            actionToStepKnight(i);
                            break interrupt;
                    } else {

                        // Если по окончания цикла, ходов не обнаружено,
                        // подготавливаемся к ходу назад
                        controlBackStep++;
                        if (controlBackStep == 7) {
                            matrixChessBoard[0][0][0]=0;
                       /*     // счетчик всех ходов уменьшаем на единицу
                            mPathKnight--;

                            // Затираем следы (текущую ячейку обнуляем, для
                            // возможности альтернативного хода)
                            if (matrixChessBoard[mInitArray1][mInitArray2][0] !=1) {
                                matrixChessBoard[mInitArray1][mInitArray2][0] = 0;
                                for (int i1 = 0; i1 <= mColumn - 1; i1++) {
                                    for (int i2 = 0; i2 <= mCell - 1; i2++) {
                                        if (matrixChessBoard[i1][i2][0] == mPathKnight) {
                                            mInitArray1 = i1;
                                            mInitArray2 = i2;
                                        }
                                    }
                                }
                            } */
                            // Если ход не удался, ищем в массиве значение с
                            // предыдущим path и переходим на нее
                            // перезаписываем массив ходов коня
                            break interrupt;
                        }
                    }

                }
            }
            Message msg=new Message();
            msg.arg2=3;
            mHandler.getHandler().sendMessage(msg);
            mKnightMoveControl =false;
        }

    }

    /**
     * <paint>
     * Прорисовываем графические элементы экрана
     * </paint>
     *
     * @param i
     *            текущий номер хода коня
     */
    public void actionToStepKnight(int i){

        mLastKnightSteps[mPathKnight]=i;
        // Метка пути(указывает текущее местоположение коня) Увеличиваем при каждом шаге
        mPathKnight++;
        //Записываем путь коня на шахматной матрице
        matrixChessBoard[mInitArray1][mInitArray2][0]= mPathKnight;
        //Если путь больше произведения ячеек, закрываем счетчик
        if (mPathKnight ==(mCell * mColumn -1)) {
            for (int y = 0; y <= mColumn -1; y++) {
                for (int y2 = 0; y2 <= mCell - 1; y2++) {
                    if(matrixChessBoard[y][y2][0]==0){
                        matrixChessBoard[y][y2][0]= mPathKnight +1;
                    }
                }
            }
            mControl = false;
        }
    }

    private int moveKnightInCell(int controlBackStep, int[] steps1, int[] steps2) {
        int a = 0;
        int t = 0;
        int valuePath = 8;
        for (int y = 0; y <= mColumn -1; y++) {
            for (int y2 = 0; y2 <= mCell -1; y2++) {
                for (int y3 = 0; y3 <= 7; y3++) {
                    mMatrixSteps[y][y2][y3] = true;
                }
            }
        }
        for (int i = 0; i <= 7; i++) {
            if (mInitArray1 + steps1[i] > mColumn - 1 || mInitArray1 + steps1[i]<0 ) {
                mMatrixSteps[mInitArray1][mInitArray2][i] = false;
            }else if (mInitArray2 + steps2[i]  < 0  ||  mInitArray2 + steps2[i]> mCell - 1) {
                mMatrixSteps[mInitArray1][mInitArray2][i] = false;
            }else if (matrixChessBoard[mInitArray1 + steps1[i]][mInitArray2 +steps2[i]][0] > 0) {
                mMatrixSteps[mInitArray1][mInitArray2][i] = false;
            }else if (controlBackStep==8) {
                int q = mLastKnightSteps[mPathKnight];
                mMatrixSteps[mInitArray1][mInitArray2][q] = false;
            }

            //Отправляеем коня в будущее
            else if (mMatrixSteps[mInitArray1][mInitArray2][i]) {
                mLocalInit1 = mInitArray1 + steps1[i];
                mLocalInit2 = mInitArray2 + steps2[i];
                for (int i2 = 0; i2 <= 7; i2++) {
                    if (mLocalInit1 + steps1[i2] > mColumn - 1 || mLocalInit1 + steps1[i2]<0  ) {
                        mMatrixSteps[mLocalInit1][mLocalInit2][i2] = false;
                    } else if (mLocalInit2 + steps2[i2]> mCell - 1  || mLocalInit2 + steps2[i2]  < 0  ) {
                        mMatrixSteps[mLocalInit1][mLocalInit2][i2] = false;
                    } else if (matrixChessBoard[mLocalInit1 + steps1[i2]][mLocalInit2 +steps2[i2]][0] > 0) {
                        mMatrixSteps[mLocalInit1][mLocalInit2][i2] = false;
                    }else if (mMatrixSteps[mLocalInit1][mLocalInit2][i2]) {
                        t++;
                    }
                }
            }
            if (t < valuePath & t>0) {
                valuePath = t;
                a=i;
            } else {
                mMatrixSteps[mLocalInit1][mLocalInit2][i] = false;
            }
            t=0;
        }
        return a;

    }

    /**
     * Устанавливает обработчик
     *
     * @param handler  - обработчик
     */
    public void setHandler(HandlerPermissionStart handler){
        mHandler=handler;
    }

    public int[][][] getMatrixStepsKnight(){
        return matrixChessBoard;
    }
}
