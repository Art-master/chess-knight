package com.app.art_master.chessknight;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;


/**
 * Начальный клас для отрисовки основного интерфейса
 * Created by Art-_-master
 */
public class ChessBoard extends AppCompatActivity implements HandlerPermissionStart {
    
     /** разметка для расположения шахматной доски */
    FrameLayout mChessBoard;

    /** поле ввода количества горизонтальных линий в шахматной доске*/
    EditText mTextNumColumn;
    
    /** поле ввода количества колонок в шахматной доске*/
    EditText mTextNumCell;

    /** Кнопка запуска алгоритма ходов коня*/
    Button mStartButton;

    /** Кнопка отрисовки шахм. доски с заданными параметрами*/
    Button mViewButton;

    /** Кнопка очистки шахм. доски*/
    Button mCleanButton;

    /** Handler для отслеживания действий паралельного потока*/
    private Handler mHandler;
    
    /** Объект с графикой шахматной доски в Canvas'e*/
    public DrawRectView mBoardChess;

     /** объект с алгоритмом вычисления пути коня по шахматной доске в другом потоке*/
    KnightDriveThread knightDriveThread;

    /** Количество линий в доске */
    int mCell;

    /** Количество столбцов в доске */
    int mColumn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shess_board);

        mTextNumColumn = (EditText)findViewById(R.id.editTextNumColumn);
        mTextNumCell = (EditText)findViewById(R.id.editTextNumCell);
        mChessBoard =(FrameLayout) findViewById(R.id.chessBoard);
        mStartButton =(Button) findViewById(R.id.buttonStart);
        mViewButton =(Button) findViewById(R.id.buttonView);
        mCleanButton =(Button) findViewById(R.id.buttonClear);

        //Устанавливаем реакцию Handler'a
        mHandler = new Handler(){

            @Override
            public void handleMessage(Message msg) {
                if(msg.arg2==0){
                    mStartButton.setEnabled(true);
                }
                if(msg.arg2==3){
                    if(knightDriveThread!=null & mBoardChess !=null){
                        mBoardChess.startAnim(knightDriveThread.getMatrixStepsKnight());
                    }
                }
            }
        };
        //слушатель для кнопки прорисовки доски
        mViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //если текст есть, забираем его из форм
                if(!mTextNumColumn.getText().toString().isEmpty()){
                    mColumn =Integer.parseInt(mTextNumColumn.getText().toString());
                    if(mColumn <5){
                        mTextNumColumn.setText(getResources().getText(R.string.minCellAndColValue));
                        mColumn =5;
                        showMessage();
                    }else if(mColumn > 50){
                        mTextNumColumn.setText(getResources().getText(R.string.maxCellAndColValue));
                        mColumn =50;
                        showMessage();
                    }
                }
                if(!mTextNumCell.getText().toString().isEmpty()){
                    mCell =Integer.parseInt(mTextNumCell.getText().toString());

                    if(mCell <5){
                        mTextNumCell.setText(getResources().getText(R.string.minCellAndColValue));
                        mCell =5;
                        showMessage();
                    }else if(mCell > 50){
                        mTextNumCell.setText(getResources().getText(R.string.maxCellAndColValue));
                        mCell =50;
                        showMessage();
                    }

                }

                //запускаем прорисовку шахматной доски, устанавливаем параметры и цепряем обработчик
                mBoardChess =new DrawRectView(getApplicationContext(),null, mColumn, mCell,
                        mChessBoard.getHeight(), mChessBoard.getWidth());
                mBoardChess.setHandler(ChessBoard.this);

                mChessBoard.addView(mBoardChess, mChessBoard.getWidth(), mChessBoard.getHeight());


                //активируем и декактивируем кнопки
                mViewButton.setEnabled(false);
                mCleanButton.setEnabled(true);
                mStartButton.setEnabled(false);
            }
        });
        
        //слушатель для кнопки очистки шахматной доски доски
        mCleanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChessBoard.removeView(mBoardChess);
                mBoardChess =null;
                
                 //активируем и декактивируем кнопки
                mViewButton.setEnabled(true);
                mCleanButton.setEnabled(false);
                mStartButton.setEnabled(false);
            }
        });
        
        //слушатель для кнопки запуска алгоритма
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                 //активируем и декактивируем кнопки
                mViewButton.setEnabled(false);
                mCleanButton.setEnabled(true);
                mStartButton.setEnabled(false);

                // создаем и запускаем новый поток для вычисления алгоритма 
                knightDriveThread= new KnightDriveThread(mBoardChess.getArrayInitIndex(1),
                        mBoardChess.getArrayInitIndex(2),
                        mBoardChess.mNumCell,
                        mBoardChess.mNumColumn);
                knightDriveThread.setHandler(ChessBoard.this);
                knightDriveThread.run();
            }
        });

    }


    @Override
    public Handler getHandler() {
        return mHandler;
    }

    public void showMessage(){
        Toast toast = Toast.makeText(getApplicationContext(),
                "Значение должно быть \n не меньше 5 и не больше 50", Toast.LENGTH_LONG);
        toast.show();
    }
}
