package com.app.art_master.chessknight;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;


/**
 * Начальный клас для отрисовки основного интерфейса
 * Created by Art-_-master
 */
public class ChessBoard extends AppCompatActivity implements HandlerPermissionStart {
    
     /** разметка для расположения шахматной доски */
    FrameLayout chessBoard;

    /** поле ввода количества горизонтальных линий в шахматной доске*/
    EditText textNumColumn;
    
    /** поле ввода количества колонок в шахматной доске*/
    EditText textNumCell;

    /** Кнопка запуска алгоритма ходов коня*/
    Button startButton;

    /** Кнопка останова алгоритма ходов коня*/
    Button stopButton;

    /** Кнопка отрисовки шахм. доски с заданными параметрами*/
    Button viewButton;

    /** Кнопка очистки шахм. доски*/
    Button clearButton;

    /** Handler для отслеживания действий паралельного потока*/
    private Handler mHandler;
    
    /** Объект с графикой шахматной доски в Canvas'e*/
    public DarwRectView boardSheess;

     /** объект с алгоритмом вычисления пути коня по шахматной доске в другом потоке*/
    KnightDriveThread knightDriveThread;

    int cell;

    int column;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shess_board);

        textNumColumn= (EditText)findViewById(R.id.editTextNumColumn);
        textNumCell= (EditText)findViewById(R.id.editTextNumCell);
        chessBoard=(FrameLayout) findViewById(R.id.chessBoard);
        startButton=(Button) findViewById(R.id.buttonStart);
        stopButton=(Button) findViewById(R.id.buttonStop);
        viewButton=(Button) findViewById(R.id.buttonView);
        clearButton=(Button) findViewById(R.id.buttonClear);

        //Устанавливаем реакцию Handler'a
        mHandler = new Handler(){

            @Override
            public void handleMessage(Message msg) {
                if(msg.arg2==0){
                    startButton.setEnabled(true);
                }
                if(msg.arg2==3){
                    if(knightDriveThread!=null & boardSheess!=null){
                        boardSheess.startAnim(knightDriveThread.getMatrixStepsKnight());
                    }
                }
            }
        };
        //слушатель для кнопки прорисовки доски
        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //если текст есть, забираем его из форм
                if(!textNumColumn.getText().toString().isEmpty()){
                    column =Integer.parseInt(textNumColumn.getText().toString());
                }
                if(!textNumCell.getText().toString().isEmpty()){
                    cell =Integer.parseInt(textNumCell.getText().toString());
                }

                //запускаем прорисовку шахматной доски, устанавливаем параметры и цепряем обработчик
                boardSheess =new DarwRectView(getApplicationContext(),null, column, cell,
                        chessBoard.getHeight(), chessBoard.getWidth());
                boardSheess.setHandler(ChessBoard.this);
                boardSheess.setLayoutParams(chessBoard.getLayoutParams());

                chessBoard.addView(boardSheess);

                //активируем и декактивируем кнопки
                viewButton.setEnabled(false);
                clearButton.setEnabled(true);
                stopButton.setEnabled(false);
                startButton.setEnabled(false);
            }
        });
        
        //слушатель для кнопки очистки шахматной доски доски
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chessBoard.removeView(boardSheess);
                boardSheess=null;
                
                 //активируем и декактивируем кнопки
                viewButton.setEnabled(true);
                clearButton.setEnabled(false);
                stopButton.setEnabled(false);
                startButton.setEnabled(false);
            }
        });
        
        //слушатель для кнопки запуска алгоритма
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                 //активируем и декактивируем кнопки
                viewButton.setEnabled(false);
                clearButton.setEnabled(false);
                stopButton.setEnabled(true);
                startButton.setEnabled(false);

                // создаем и запускаем новый поток для вычисления алгоритма 
                knightDriveThread= new KnightDriveThread(boardSheess.getArrayInitIndex(1),
                        boardSheess.getArrayInitIndex(2),
                        boardSheess.mNumCell,
                        boardSheess.mNumColumn);
                knightDriveThread.setHandler(ChessBoard.this);
                knightDriveThread.run();
            }
        });

        //слушатель для кнопки останова алгоритма
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                 //активируем и декактивируем кнопки
                viewButton.setEnabled(false);
                clearButton.setEnabled(true);
                stopButton.setEnabled(false);
                startButton.setEnabled(true);

            }
        });


    }


    @Override
    public Handler getHandler() {
        return mHandler;
    }
}
