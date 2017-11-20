package com.app.art_master.chessknight;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 *
 * Created by Art-_-master on 18.11.2017.
 */

public class DarwRectView extends View{
    Paint p;
    Rect rect;

    RectF mRectF;

    int numCollumn;
    int numCell;

    boolean init=true;

    public static int [][][] rectsCoordinate;

    public DarwRectView(Context context, AttributeSet atr, int column, int cell) {
        super(context);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        numCell=cell;
        numCollumn=column;
        p = new Paint();
        rect = new Rect();

        mRectF = new RectF(rect);

        rectsCoordinate = new int[cell][column][2];
        rectsCoordinate[0][0][0]=0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // заливка канвы цветом

        if(init){
            int rectWidth=getHeight()/numCollumn;
            int rectHeight= getHeight()/numCell;

            int alternation;

            for(int i=0; i<numCell; i++) {
                for (int i1 = 0; i1 < numCollumn; i1++) {
                    rectsCoordinate[i][i1][0]= rectsCoordinate[i][i1][0]+rectWidth*i1;
                    rectsCoordinate[i][i1][1]= rectsCoordinate[i][i1][1]+rectHeight*i;
                    rect.set(rectsCoordinate[i][i1][0],
                            rectsCoordinate[i][i1][1] + rectHeight,
                            rectsCoordinate[i][i1][0] + rectWidth,
                            rectsCoordinate[i][i1][1]);

                    if (i%2==0) {
                        alternation=1;
                    }else{
                        alternation=0;
                    }
                    switch (alternation)
                    {
                        case 0:
                            if (i1%2!=0) {
                                p.setColor(Color.WHITE);
                            }else{
                                p.setColor(Color.BLACK);
                            }
                            break;
                        case 1:
                            if (i1%2!=0) {
                                p.setColor(Color.BLACK);
                            }
                            else{
                                p.setColor(Color.WHITE);
                            }
                            break;
                    }

                    // настройка кисти
                    // рисуем прямоугольник из объекта rect
                    canvas.drawRect(rect, p);

                }
            }
            //canvas.saveLayer(mRectF, p, Canvas.ALL_SAVE_FLAG);
        }

    }

}
