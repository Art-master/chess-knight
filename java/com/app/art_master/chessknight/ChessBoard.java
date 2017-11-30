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


public class ChessBoard extends AppCompatActivity implements HandlerPermissionStart {

    FrameLayout chessBoard;

    EditText textNumColumn;

    EditText textNumCell;

    Button startButton;

    Button stopButton;

    Button viewButton;

    Button clearButton;

    int [][] matrixPosition;

    /**Handler для отслеживания действий паралельного потока*/
    private Handler mHandler;


    public DarwRectView boardSheess;

    int column;
    int cell;

    KnightDriveThread knightDriveThread;

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

        mHandler = new Handler(){

            @Override
            public void handleMessage(Message msg) {
                startButton.setEnabled(true);
            }
        };
        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!textNumColumn.getText().toString().isEmpty()){
                    column =Integer.parseInt(textNumColumn.getText().toString());
                }
                if(!textNumCell.getText().toString().isEmpty()){
                    cell =Integer.parseInt(textNumCell.getText().toString());
                }


                boardSheess =new DarwRectView(getApplicationContext(),null, column, cell,
                        chessBoard.getHeight(), chessBoard.getWidth());
                boardSheess.setHandler(ChessBoard.this);
                boardSheess.setLayoutParams(chessBoard.getLayoutParams());

                chessBoard.addView(boardSheess);

                viewButton.setEnabled(false);
                clearButton.setEnabled(true);
                stopButton.setEnabled(false);
                startButton.setEnabled(false);
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chessBoard.removeView(boardSheess);
                boardSheess=null;
                viewButton.setEnabled(true);
                clearButton.setEnabled(false);
                stopButton.setEnabled(false);
                startButton.setEnabled(false);
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewButton.setEnabled(false);
                clearButton.setEnabled(false);
                stopButton.setEnabled(true);
                startButton.setEnabled(false);

                knightDriveThread= new KnightDriveThread(boardSheess);
                knightDriveThread.run();
                //boardSheess.animateStepKnightStart(0, 0, 3, 5000L);
                //boardSheess.drawCircle(0, 0, Color.GREEN, "1");

                //boardSheess.drawCircle(4, 2, Color.RED, "2");
                //boardSheess.drawCircle(2, 3, Color.CYAN, "3");
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
