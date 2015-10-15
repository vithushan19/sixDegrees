package com.vithushan.sixdegrees.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;

import com.vithushan.sixdegrees.R;

/**
 * Created by vnama on 10/14/2015.
 */
public class Circle extends View {

    private String _colorString = "#FFFFFF";
    private int _backgroundColor;
    private Paint paint = new Paint();

    public Circle(Context context) {
        super(context);
    }

    public Circle(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        ColorDrawable colorDrawable = (ColorDrawable) getBackground();
        _backgroundColor = colorDrawable.getColor();
        TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.Circle);

        _colorString = a.getString(R.styleable.Circle_circleColor);

    }

    private void animate2() {
        final float[] from = new float[3],
                to =   new float[3];

        Color.colorToHSV(Color.parseColor("#FFFFFFFF"), from);   // from white
        Color.colorToHSV(Color.parseColor(_colorString), to);     // to red

        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);   // animate from 0 to 1
        anim.setDuration(300);                              // for 300 ms

        final float[] hsv  = new float[3];                  // transition color
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
            @Override public void onAnimationUpdate(ValueAnimator animation) {
                // Transition along each axis of HSV (hue, saturation, value)
                hsv[0] = from[0] + (to[0] - from[0])*animation.getAnimatedFraction();
                hsv[1] = from[1] + (to[1] - from[1])*animation.getAnimatedFraction();
                hsv[2] = from[2] + (to[2] - from[2])*animation.getAnimatedFraction();

                paint.setColor(Color.HSVToColor(hsv));
                setBackgroundColor(Color.HSVToColor(hsv));
                invalidate();
            }
        });

        anim.setRepeatCount(Animation.INFINITE);
        anim.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        float x = getWidth();
        float y = getHeight();
        float radius;

        radius = getWidth()/8;
        float heightShift = (float) (Math.sqrt((radius * radius) - ((radius/2) * (radius/2))));

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawPaint(paint);
        paint.setColor(_backgroundColor);
        // Use Color.parseColor to define HTML colors
        //paint.setColor(Color.parseColor(_colorString));
        //canvas.drawCircle(x / 2, y / 2, radius, paint);

        canvas.drawCircle(x / 2 - (x / 4), y / 2, (float) (radius/1.5), paint);
        canvas.drawCircle(x / 2 + (x/4), y / 2, (float) (radius/1.5), paint);

        canvas.drawCircle(x/2 + ((x/2) - (x/4))/2, y / 2 + 2*heightShift, radius, paint);
        canvas.drawCircle(x / 2 - ((x / 2) - (x / 4)) / 2, y / 2 + 2 * heightShift, radius, paint);

        canvas.drawCircle(x / 2 + ((x / 2) - (x / 4)) / 2, y / 2 - 2 * heightShift, radius / 2, paint);
        canvas.drawCircle(x / 2 - ((x / 2) - (x / 4)) / 2, y / 2 - 2 * heightShift, radius / 2, paint);

        //animate2();
    }
}
