package com.vithushan.sixdegrees.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.vithushan.sixdegrees.R;

/**
 * Created by vnama on 10/14/2015.
 */
public class CirclesLogo extends View {

    private int color1 = getResources().getColor(R.color.dark_blue);
    private int color2 = getResources().getColor(R.color.dark_blue);
    private int color3 = getResources().getColor(R.color.dark_blue);
    private int color4 = getResources().getColor(R.color.dark_blue);
    private int color5 = getResources().getColor(R.color.dark_blue);
    private int color6 = getResources().getColor(R.color.dark_blue);

    Paint paint = new Paint();
    private Paint paintBorder = new Paint();

    public CirclesLogo(Context context) {
        super(context);
        paintBorder.setStyle(Paint.Style.STROKE);
        paintBorder.setStrokeWidth(2);
    }

    public CirclesLogo(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void setColor1(int color) {
        color1 = color;
        invalidate();
    }

    public void setColor2(int color) {
        color2 = color;
        invalidate();
    }

    public void setColor3(int color) {
        color3 = color;
        invalidate();
    }


    public void setColor4(int color) {
        color4 = color;
        invalidate();
    }


    public void setColor5(int color) {
        color5 = color;
        invalidate();
    }


    public void setColor6(int color) {
        color6 = color;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        float width = getWidth();
        float height = getHeight();

        float halfWidth = width/2;
        float halfHeight = height/2;

        float quarterWidth = width/4;

        float radius;

        radius = getWidth()/8;

        // The height of an equilateral triangle with a side length equal to the radius
        float heightShift = (float) (Math.sqrt((radius * radius) - ((radius/2) * (radius/2))));

        /*
            Logo is drawn using hexagonal circle packing. Then changing the radius of certain circles
         */

        // This is the center circle that all other circle draw around
        //canvas.drawCircle(halfWidth, halfHeight, radius, paint);

        //bottom right
        paint.setColor(color3);
        drawBorderedCircle(canvas, halfWidth + (halfWidth - quarterWidth) / 2, halfHeight + 2 * heightShift, radius);
        //bottom left
        paint.setColor(color4);
        drawBorderedCircle(canvas, halfWidth - (halfWidth - quarterWidth) / 2, halfHeight + 2 * heightShift, radius);

        //middle right
        paint.setColor(color2);
        drawBorderedCircle(canvas, halfWidth + quarterWidth, halfHeight, (float) (radius / 1.5));
        //middle left
        paint.setColor(color5);
        drawBorderedCircle(canvas, halfWidth - quarterWidth, halfHeight, (float) (radius / 1.5));

        //top right
        paint.setColor(color1);
        drawBorderedCircle(canvas, halfWidth + (halfWidth - quarterWidth) / 2, halfHeight - 2 * heightShift, radius / 2);
        //top left
        paint.setColor(color6);
        drawBorderedCircle(canvas, halfWidth - (halfWidth - quarterWidth) / 2, halfHeight - 2 * heightShift, radius / 2);

    }

    private void drawBorderedCircle(Canvas canvas, float x, float y, float radius) {
        canvas.drawCircle(x, y, radius+2, paintBorder);
        canvas.drawCircle(x, y, radius, paint);
    }
}
