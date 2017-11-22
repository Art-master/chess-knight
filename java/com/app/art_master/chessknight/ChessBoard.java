package com.app.art_master.chessknight;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;


public class ChessBoard extends AppCompatActivity {

    FrameLayout chessBoard;

    EditText textNumColumn;

    EditText textNumCell;

    Button startButton;

    Button stopButton;

    Button viewButton;

    Button clearButton;

    int [][] matrixPosition;


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
                boardSheess.setLayoutParams(chessBoard.getLayoutParams());

                boardSheess.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return false;
                    }
                });

                chessBoard.addView(boardSheess);

                viewButton.setEnabled(false);
                clearButton.setEnabled(true);
                stopButton.setEnabled(false);
                startButton.setEnabled(true);
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chessBoard.removeView(boardSheess);
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

                knightDriveThread= new KnightDriveThread();
                boardSheess.drawCircle(3, 1, Color.GREEN);
                boardSheess.drawCircle(4, 2, Color.RED);
                boardSheess.drawCircle(2, 3, Color.CYAN);
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


}
