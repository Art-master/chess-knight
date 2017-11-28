package com.app.art_master.chessknight;

import android.graphics.Color;

/**
 * Created by Art-_-master on 21.11.2017.
 */

public class KnightDriveThread implements Runnable {


    /**
     *Массив предыдущих шагов коня
     */
    private int [] lastKnightSteps = new int[this.column*this.cell+1];
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
    private float[][][] matrixСhessboard = new float[this.column][this.cell][3];

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

    private boolean[][][] matrixSteps = new boolean [this.column][this.cell][8];

    KnightDriveThread(DarwRectView chessBoardObject){
        //chessBoardObject.drawCircle(0, 0, Color.RED, "1");
        chessBoardObject.animateStepKnightStart(0, 0, 3, 5000L);
        //chessBoardObject.invalidate();
        //chessBoardObject.invalidate();
    }

    @Override
    public void run() {
        while (true){

        }
    }

    /**
     * <paint>
     * Инициализируем ход коня
     * </paint>
     */
    public void present() {

        if(this.knightMoveControl){
            int controlBackStep = 0;
            int forward =0;
            this.matrixСhessboard[this.initArray1][this.initArray2][2]=1;
            while(this.control){

                //находим доступные ходы
                forward = this.moveKnightInCell(controlBackStep);
                //this.count=0;

                // countFalse Разрешает ход назад, если ходить некуда
                controlBackStep = 0;
                interrupt: for (int i = 0; i <=8; i++) {
                    if (this.matrixSteps[this.initArray1][this.initArray2][i]) {
                        i=forward;

                        // записываем координаты следующей точки
                        switch (i) {
                            case 0:
                                this.initArray1 += 2;
                                this.initArray2 -= 1;
                                actionToStepKnight(i);
                                break interrupt;
                            case 1:
                                this.initArray1 += 2;
                                this.initArray2 += 1;
                                actionToStepKnight(i);
                                break interrupt;
                            case 2:
                                this.initArray1 -= 2;
                                this.initArray2 -= 1;
                                actionToStepKnight(i);
                                break interrupt;

                            case 3:
                                this.initArray1 -= 2;
                                this.initArray2 += 1;
                                actionToStepKnight(i);
                                break interrupt;

                            case 4:
                                this.initArray1 += 1;
                                this.initArray2 -= 2;
                                actionToStepKnight(i);
                                break interrupt;

                            case 5:
                                this.initArray1 -= 1;
                                this.initArray2 -= 2;
                                actionToStepKnight(i);
                                break interrupt;
                            case 6:
                                this.initArray1 += 1;
                                this.initArray2 += 2;
                                actionToStepKnight(i);
                                break interrupt;
                            case 7:
                                this.initArray1 -= 1;
                                this.initArray2 += 2;
                                actionToStepKnight(i);
                                break interrupt;

                        }
                    } else {

                        // Если по окончания цикла, ходов не обнаружено,
                        // подготавливаемся к ходу назад
                        controlBackStep++;
                        if (controlBackStep == 8) {

                            // oбщую длительность ходов уменьшаем на единицу
                            this.pathKnight--;

                            // Затираем следы (текущую ячейку обнуляем, для
                            // возможности альтернативного хода)
                            if ((int) this.matrixСhessboard[this.initArray1][this.initArray2][2] !=1) {
                                this.matrixСhessboard[this.initArray1][this.initArray2][2] = 0;
                                for (int i1 = 0; i1 <= this.column - 1; i1++) {
                                    for (int i2 = 0; i2 <= this.cell - 1; i2++) {
                                        if ((int) this.matrixСhessboard[i1][i2][2] == this.pathKnight) {
                                            this.initArray1 = i1;
                                            this.initArray2 = i2;
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
            this.knightMoveControl=false;
        }
        /*this.chessboard();
        if(this.chessboardDrawControl==true){
            this.chessboardDraw();
            this.chessboardDrawControl=false;
        }

        this.count=1;
        if(this.countSetColorOneRect==true){
            this.setColorOneRect();
        }
        else{
            this.setPlacingKnight();
        } */
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
        this.lastKnightSteps[this.pathKnight]=i;
        // Метка пути(указывает текущее местоположение коня) Увеличиваем при каждом шаге
        this.pathKnight++;
        //Записываем путь коня на шахматной матрице
        this.matrixСhessboard[this.initArray1][this.initArray2][2]=this.pathKnight;
        //Если путь больше произведения ячеек, закрываем счетчик
        if (this.pathKnight==(this.cell*this.column-1)) {
            this.control = false;
        }
    }

    /**
     * <paint>
     * Ловим касания пользователя
     * </paint>
     */
    /*
    public void setPlacingKnight() {
        List<TouchEvent> touchEvents = game.getInput().getTouchEvents();

        int len = touchEvents.size();
        for(int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);


            if(event.type == TouchEvent.TOUCH_DOWN) {
                if(this.controlTouchButtonLetsGo==false){
                    for (int i2 = 0; i2 <= this.matrixСhessboard.length-1; i2++) {
                        for (int i3 = 0; i3 <= this.matrixСhessboard[i2].length-1; i3++) {
                            float valueX=(float)this.matrixСhessboard[i2][i3][0];
                            float valueY=(float)this.matrixСhessboard[i2][i3][1];
                            if(inBounds(event, valueX,  valueY, this.length, this.length)){
                                this.SetColorOneRectX=valueX;
                                this.SetColorOneRectY=valueY;
                                this.countSetColorOneRect=true;
                                this.g.drawRect(AndroidGame.y+20, 20, this.infoPanelWidth-40 ,AndroidGame.y/6, Color.YELLOW);
                                float textSize=60;
                                this.g.drawText("Let's go", AndroidGame.y+(this.infoPanelWidth/2), 80, textSize, Color.BLACK, Paint.Align.CENTER);
                                this.controlTouchButtonLetsGo=true;
                                this.initArray1=i2;
                                this.initArray2=i3;

                            }

                        }

                    }
                }else{
                    if(inBounds(event, AndroidGame.y+20, 20, this.infoPanelWidth-40, AndroidGame.y/6)){
                        this.knightMoveControl=true;
                        this.g.drawRect(AndroidGame.y+10, 10,this.infoPanelWidth-20 ,AndroidGame.y-20 , Color.DKGRAY);

                    }
                }
            }
        }
    }
*/

    private int moveKnightInCell(int controlBackStep) {
        int[] steps1 ={2, 2,  -2,  -2, 1, -1, 1, -1};
        int[] steps2 ={-1, 1,  -1, 1, -2, -2, 2,  2};
        int a = 0;
        int t = 0;
        int valuePathes = 8;
        for (int y= 0; y <= this.column-1; y++) {
            for (int y2 = 0; y2 <= this.cell-1; y2++) {
                for (int y3 = 0; y3 <= 7; y3++) {
                    this.matrixSteps[y][y2][y3] = true;
                }
            }
        }
        for (int i = 0; i <= 7; i++) {
            if (this.initArray1 + steps1[i] > this.column - 1 || this.initArray1 + steps1[i]<0 ) {
                this.matrixSteps[this.initArray1][this.initArray2][i] = false;
            }else if (this.initArray2 + steps2[i]  < 0  ||  this.initArray2 + steps2[i]> this.cell - 1) {
                this.matrixSteps[this.initArray1][this.initArray2][i] = false;
            }else if ((int) this.matrixСhessboard[this.initArray1 + steps1[i]][this.initArray2 +steps2[i]][2] > 0) {
                this.matrixSteps[this.initArray1][this.initArray2][i] = false;
            }else if (controlBackStep==8) {
                int q = this.lastKnightSteps[this.pathKnight];
                this.matrixSteps[this.initArray1][this.initArray2][q] = false;
            }

            //Отправляеем коня в будущее
            else if (this.matrixSteps[this.initArray1][this.initArray2][i]) {
                this.localInit1 = this.initArray1 + steps1[i];
                this.localInit2 = this.initArray2 + steps2[i];
                for (int i2 = 0; i2 <= 7; i2++) {
                    if (this.localInit1 + steps1[i2] > this.column - 1 || this.localInit1 + steps1[i2]<0  ) {
                        this.matrixSteps[this.localInit1][this.localInit2][i2] = false;
                    } else if (this.localInit2 + steps2[i2]> this.cell - 1  || this.localInit2 + steps2[i2]  < 0  ) {
                        this.matrixSteps[this.localInit1][this.localInit2][i2] = false;
                    } else if ((int) this.matrixСhessboard[this.localInit1 + steps1[i2]][this.localInit2 +steps2[i2]][2] > 0) {
                        this.matrixSteps[localInit1][localInit2][i2] = false;
                    }else if (this.matrixSteps[localInit1][localInit2][i2]) {
                        t++;
                    }
                }
            }
            if (t < valuePathes & t>0) {
                valuePathes = t;
                a=i;
            } else {
                //this.matrixSteps[this.localInit1][this.localInit2][i] = false;
            }
            t=0;
        }
        return a;

    }
}
