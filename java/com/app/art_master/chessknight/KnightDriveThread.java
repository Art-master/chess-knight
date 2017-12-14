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

    /** Handler для отслеживания действий паралельного потока*/
    private HandlerPermissionStart mHandler;

    //в конструктор передаем объект шахматной доски
    KnightDriveThread(int initArray1, int initArray2, int numCell, int numColumn){

        //mChessboard=chessBoardObject;
        mColumn =numCell;
        mCell =numColumn;

        matrixChessBoard = new int[mColumn][mCell][1];
        mLastKnightSteps = new int[mColumn * mCell +1];

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
            int forward;
            matrixChessBoard[mInitArray1][mInitArray2][0]=1;
            while(mControl){

                int[] steps1 ={2, 2,  -2,  -2, 1, -1, 1, -1};
                int[] steps2 ={-1, 1,  -1, 1, -2, -2, 2,  2};

                //находим доступные ходы
                forward = moveKnightInCell(steps1, steps2);
                if(forward==0xFF){
                    matrixChessBoard[0][0][0]=0;
                    break;
                }
                // записываем координаты следующей точки в зависимости от того, куда шагнул конь
                mInitArray1 += steps1[forward];
                mInitArray2 += steps2[forward];
                actionToStepKnight(forward);
                // перед каждым ходом обнуляем щетчик ходов
                //controlBackStep = 0;

             /*           // Если по окончания цикла, ходов не обнаружено,
                        // подготавливаемся к ходу назад
                        controlBackStep++;
                        if (controlBackStep == 7) {
                            //matrixChessBoard[0][0][0]=0;
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
                            }
                            // Если ход не удался, ищем в массиве значение с
                            // предыдущим path и переходим на нее
                            // перезаписываем массив ходов коня
                           // break interrupt;
                        }
                    }

                }*/
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

    private int moveKnightInCell(int[] steps1, int[] steps2) {
        int a = 0xff;
        int t = 0;
        int valuePath = 8;

        for (int i = 0; i <= 7; i++) {

            int stepHorizontal = mInitArray1 + steps1[i];
            int stepVertical = mInitArray2 + steps2[i];
            //если шаг коня выходит за границы или конь уже бывал в этой клетке
            if (checkStep(stepHorizontal, stepVertical)) {
                //проверяем вес каждого хода коня
                for (int i2 = 0; i2 <= 7; i2++) {
                    mLocalInit1 = stepHorizontal + steps1[i2];
                    mLocalInit2 = stepVertical + steps2[i2];
                    //если шаг коня выходит за границы
                    if (checkStep(mLocalInit1, mLocalInit2)) {
                        t++;
                    }
                }
            }
                //выбираем клетку с наименьшим весом, но вес должен быть больше ноля
                if (t < valuePath & t>0) {
                    valuePath = t;
                    a = i;
                }
                t = 0;
            }
            //если ходов никаких не обнаружено, то функция вернет 0xFF,
            //в ином случае вернет номер хода
        return a;
    }

    private boolean checkStep(int stepHorizontal, int stepVertical){
        boolean q;
        if (stepHorizontal > mColumn - 1 || stepHorizontal < 0) {
            q= false;
        }else if(stepVertical < 0 || stepVertical> mCell - 1){
            q= false;
        }else if(matrixChessBoard[stepHorizontal][stepVertical][0]>0){
            q= false;
        }else{
            q= true;
        }
        return q;
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
