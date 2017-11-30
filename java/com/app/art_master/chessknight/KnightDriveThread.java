package com.app.art_master.chessknight;

import android.graphics.Color;

/**
 * Created by Art-_-master on 21.11.2017.
 */

public class KnightDriveThread implements Runnable {


    /**
     *Массив предыдущих шагов коня
     */
    private int [] lastKnightSteps;
    /**
     *Установка для массива при ходе коня
     */
    private int initArray1=0;
    /**
     *Установка для массива при ходе коня
     */
    private int initArray2=0;
    /**
     *Координата x выбранной пользователем начальной позиции коня
     */
    private float SetColorOneRectX=0;
    /**
     *Координата y выбранной пользователем начальной позиции коня
     */
    private float SetColorOneRectY=0;
    /**
     *Матрица шахматной доски
     */
    private float[][][] matrixСhessboard;

    /**
     *Счетчик шагов коня
     */
    private int pathKnight=1;
    /**
     *Массив-инициатор шагов коня
     */

    int column;
    int cell;

    private int localInit1=0;
    private int localInit2=0;

    /**
     *Счетчики и инициаторы
     */
    private int count=0;
    private String countTruePath="";
    private boolean countSetColorOneRect=false;
    private boolean knightMoveControl=false;
    private boolean chessboardDrawControl=true;
    private boolean controlTouchButtonLetsGo=false;
    private boolean control=true;
    private DarwRectView mChessboard;

    private boolean[][][] matrixSteps;

    KnightDriveThread(DarwRectView chessBoardObject){
        //chessBoardObject.animateStepKnightStart(0, 0, 3, 5000L);
        mChessboard=chessBoardObject;
        column=chessBoardObject.mNumCell;
        cell=chessBoardObject.mNumColumn;

        matrixСhessboard= new float[column][cell][3];
        lastKnightSteps= new int[column*cell+1];
        matrixSteps = new boolean [column][cell][8];
    }

    @Override
    public void run() {
        while (true){
            present();
        }
    }

    /**
     * <paint>
     * Инициализируем ход коня
     * </paint>
     */
    public void present() {

        if(knightMoveControl){
            int controlBackStep = 0;
            int forward =0;
            matrixСhessboard[initArray1][initArray2][2]=1;
            while(control){

                //находим доступные ходы
                forward = moveKnightInCell(controlBackStep);
                //count=0;

                // countFalse Разрешает ход назад, если ходить некуда
                controlBackStep = 0;
                interrupt: for (int i = 0; i <=8; i++) {
                    if (matrixSteps[initArray1][initArray2][i]) {
                        i=forward;

                        // записываем координаты следующей точки
                        switch (i) {
                            case 0:
                                initArray1 += 2;
                                initArray2 -= 1;
                                actionToStepKnight(i);
                                break interrupt;
                            case 1:
                                initArray1 += 2;
                                initArray2 += 1;
                                actionToStepKnight(i);
                                break interrupt;
                            case 2:
                                initArray1 -= 2;
                                initArray2 -= 1;
                                actionToStepKnight(i);
                                break interrupt;

                            case 3:
                                initArray1 -= 2;
                                initArray2 += 1;
                                actionToStepKnight(i);
                                break interrupt;

                            case 4:
                                initArray1 += 1;
                                initArray2 -= 2;
                                actionToStepKnight(i);
                                break interrupt;

                            case 5:
                                initArray1 -= 1;
                                initArray2 -= 2;
                                actionToStepKnight(i);
                                break interrupt;
                            case 6:
                                initArray1 += 1;
                                initArray2 += 2;
                                actionToStepKnight(i);
                                break interrupt;
                            case 7:
                                initArray1 -= 1;
                                initArray2 += 2;
                                actionToStepKnight(i);
                                break interrupt;

                        }
                    } else {

                        // Если по окончания цикла, ходов не обнаружено,
                        // подготавливаемся к ходу назад
                        controlBackStep++;
                        if (controlBackStep == 8) {

                            // oбщую длительность ходов уменьшаем на единицу
                            pathKnight--;

                            // Затираем следы (текущую ячейку обнуляем, для
                            // возможности альтернативного хода)
                            if ((int) matrixСhessboard[initArray1][initArray2][2] !=1) {
                                matrixСhessboard[initArray1][initArray2][2] = 0;
                                for (int i1 = 0; i1 <= column - 1; i1++) {
                                    for (int i2 = 0; i2 <= cell - 1; i2++) {
                                        if ((int) matrixСhessboard[i1][i2][2] == pathKnight) {
                                            initArray1 = i1;
                                            initArray2 = i2;
                                        }
                                    }
                                }
                            }
                            // Если ход не удался, ищем в массиве значение с
                            // предыдущим path и переходим на нее
                            // перезаписываем массив ходов коня
                            break interrupt;
                        }
                    }

                }
            }
            drawMoveKnight();
            knightMoveControl=false;
        }
            count=1;
            knightMoveControl=true;
            initArray1=mChessboard.getArrayInitIndex(1);
            initArray2=mChessboard.getArrayInitIndex(2);
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
        lastKnightSteps[pathKnight]=i;
        // Метка пути(указывает текущее местоположение коня) Увеличиваем при каждом шаге
        pathKnight++;
        //Записываем путь коня на шахматной матрице
        matrixСhessboard[initArray1][initArray2][2]=pathKnight;
        //Если путь больше произведения ячеек, закрываем счетчик
        if (pathKnight==(cell*column-1)) {
            control = false;
        }
    }

    private int moveKnightInCell(int controlBackStep) {
        int[] steps1 ={2, 2,  -2,  -2, 1, -1, 1, -1};
        int[] steps2 ={-1, 1,  -1, 1, -2, -2, 2,  2};
        int a = 0;
        int t = 0;
        int valuePathes = 8;
        for (int y= 0; y <= column-1; y++) {
            for (int y2 = 0; y2 <= cell-1; y2++) {
                for (int y3 = 0; y3 <= 7; y3++) {
                    matrixSteps[y][y2][y3] = true;
                }
            }
        }
        for (int i = 0; i <= 7; i++) {
            if (initArray1 + steps1[i] > column - 1 || initArray1 + steps1[i]<0 ) {
                matrixSteps[initArray1][initArray2][i] = false;
            }else if (initArray2 + steps2[i]  < 0  ||  initArray2 + steps2[i]> cell - 1) {
                matrixSteps[initArray1][initArray2][i] = false;
            }else if ((int) matrixСhessboard[initArray1 + steps1[i]][initArray2 +steps2[i]][2] > 0) {
                matrixSteps[initArray1][initArray2][i] = false;
            }else if (controlBackStep==8) {
                int q = lastKnightSteps[pathKnight];
                matrixSteps[initArray1][initArray2][q] = false;
            }

            //Отправляеем коня в будущее
            else if (matrixSteps[initArray1][initArray2][i]) {
                localInit1 = initArray1 + steps1[i];
                localInit2 = initArray2 + steps2[i];
                for (int i2 = 0; i2 <= 7; i2++) {
                    if (localInit1 + steps1[i2] > column - 1 || localInit1 + steps1[i2]<0  ) {
                        matrixSteps[localInit1][localInit2][i2] = false;
                    } else if (localInit2 + steps2[i2]> cell - 1  || localInit2 + steps2[i2]  < 0  ) {
                        matrixSteps[localInit1][localInit2][i2] = false;
                    } else if ((int) matrixСhessboard[localInit1 + steps1[i2]][localInit2 +steps2[i2]][2] > 0) {
                        matrixSteps[localInit1][localInit2][i2] = false;
                    }else if (matrixSteps[localInit1][localInit2][i2]) {
                        t++;
                    }
                }
            }
            if (t < valuePathes & t>0) {
                valuePathes = t;
                a=i;
            } else {
                //matrixSteps[localInit1][localInit2][i] = false;
            }
            t=0;
        }
        return a;

    }

    /**
     * <p>
     * Прорисовываем путь коня
     * </p>
     *
     */
    private void drawMoveKnight() {
        int path=1;
        int lastIndex1=mChessboard.getArrayInitIndex(1);
        int lastIndex2=mChessboard.getArrayInitIndex(2);

        //mChessboard.drawCircle(lastIndex1, lastIndex2, Color.RED, path+"");
        path=2;
     /*   for (int i1 = 0; i1 <= column-1; i1++) {
            reset:for (int i2 = 0; i2 <= cell-1; i2++) {
                if(matrixСhessboard[i1][i2][2]==path){
                    mChessboard.animateStepKnightStart(lastIndex1, lastIndex2, i1, i2);
                    while(true) {
                        if (!mChessboard.isAnimationStop()) {
                            i1=0;
                            lastIndex1=i1;
                            lastIndex2=i2;
                            path++;
                            break reset;
                        }
                    }
                }

            }
        } */
    }
}
