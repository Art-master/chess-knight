package com.app.art_master.chessknight;

import android.os.Message;

/**
 * алгоритм движения коня по шахматной доске
 * Created by Art-_-master
 */

class KnightDriveThread implements Runnable {


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

    /**
     *Счетчики и инициаторы
     */
    private boolean mKnightMoveControl =false;

    private boolean mControl =true;

    /** Handler для отслеживания действий паралельного потока*/
    private HandlerPermissionStart mHandler;

    /** константа, свидетельствующая о том, что коню ходить некуда*/
    final int NOT_MOVE_KNIGHT=0xFF;

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
    private void present() {

        if(mKnightMoveControl){
            int forward;
            matrixChessBoard[mInitArray1][mInitArray2][0]=1;
            while(mControl){

                int[] steps1 ={2, 2,  -2,  -2, 1, -1, 1, -1};
                int[] steps2 ={-1, 1,  -1, 1, -2, -2, 2,  2};

                //находим доступные ходы
                forward = moveKnightInCell(steps1, steps2);
                if(forward==NOT_MOVE_KNIGHT){
                    matrixChessBoard[0][0][0]=0;
                    break;
                }
                // записываем координаты следующей точки в зависимости от того, куда шагнул конь
                mInitArray1 += steps1[forward];
                mInitArray2 += steps2[forward];
                actionToStepKnight(forward);
            }

            //отправляем сообщение в родительский поток
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
    private void actionToStepKnight(int i){

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

    /**
     *
     * @param steps1 массив 1 с возможными ходами коня
     * @param steps2 массив 2 с возможными ходами коня
     * @return номер хода
     */
    private int moveKnightInCell(int[] steps1, int[] steps2) {
        int a = NOT_MOVE_KNIGHT;
        int t = 0;
        int valuePath = 8;

        for (int i = 0; i <= 7; i++) {

            int stepHorizontal = mInitArray1 + steps1[i];
            int stepVertical = mInitArray2 + steps2[i];
            //если шаг коня выходит за границы или конь уже бывал в этой клетке
            if (checkStep(stepHorizontal, stepVertical)) {
                //проверяем вес каждого хода коня
                for (int i2 = 0; i2 <= 7; i2++) {
                    // индекс 1 для массива ходов коня для промежуточных вычислений
                     int mLocalInit1= stepHorizontal + steps1[i2];
                    // индекс 2 для массива ходов коня для промежуточных вычислений
                     int mLocalInit2 = stepVertical + steps2[i2];
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

    /**
     *
     * @param stepHorizontal шаг по горизонтали
     * @param stepVertical шаг по вертикали
     * @return true если ход разрешен и не выходит за границы и не было хода в эту клетку раньше
     */
    private boolean checkStep(int stepHorizontal, int stepVertical) {
        boolean q;
        q = !(stepHorizontal > mColumn - 1 || stepHorizontal < 0) &&
                !(stepVertical < 0 || stepVertical > mCell - 1)
                && matrixChessBoard[stepHorizontal][stepVertical][0] <= 0;
        return q;
    }

    /**
     * Устанавливает обработчик
     *
     * @param handler  - обработчик
     */
    void setHandler(HandlerPermissionStart handler){
        mHandler=handler;
    }

/**
 * Устанавливает обработчик
 * @return матрицу с ходами коня
 */
    int[][][] getMatrixStepsKnight(){
        return matrixChessBoard;
    }
}
