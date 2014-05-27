package com.starbytestudios.blockwords;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.os.Vibrator;


public class CustomButton 
{
	private int SIZE = -1000;
	private int xCoord = 0;
	private int yCoord = 0;
	private Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
	private Paint outerFadePaint = new Paint();
	private Paint innerFadePaint = new Paint();
	private int selectionState = 0;
	private String buttonText = null;
	private int innerFadeValue = 75;
	private int outerFadeValue = 75;
	private Boolean shouldOuterRipple = false;
	private Boolean outerRippleDone = true;
	private Boolean shouldInnerRipple = false;	
	private Boolean innerRippleDone = true;
	private int outerRippleSize = 0;
	private int innerRippleSize = 20;
	private Vibrator vibrator;
	private Boolean isPressed = false;


	public CustomButton(Vibrator vibrate, String text, int x, int y, int size)
	{
		buttonText = text;
		xCoord = x;
		yCoord = y;
		outerFadePaint.setColor(Color.rgb(8, 119, 155));
		outerFadePaint.setAlpha(outerFadeValue);
		outerFadePaint.setStyle(Paint.Style.STROKE);
		outerFadePaint.setStrokeWidth(3);
		innerFadePaint.setColor(Color.rgb(8, 119, 155));
		innerFadePaint.setAlpha(innerFadeValue);
		vibrator = vibrate;
		SIZE = size;
	}

	public int getSize()
	{
		return SIZE;
	}

	public void setSize(int size)
	{
		SIZE = size;
	}

	public int getX()
	{
		return xCoord;
	}

	public int getY()
	{
		return yCoord;
	}

	public void setX(int x)
	{
		xCoord = x;
	}

	public void setY(int y)
	{
		yCoord = y;
	}


	public void setShouldInnerRipple(Boolean bool)
	{
		if (innerRippleDone)
		{
		shouldInnerRipple = bool;
			innerRippleSize = SIZE/3;
			innerRippleDone = false;
		}
	}

	public void setShouldOuterRipple(Boolean bool)
	{
		if (outerRippleDone)
		{
			try
			{
				vibrator.vibrate(25);
			} catch (SecurityException e)
			{
			} catch (Exception e)
			{
			}
			shouldOuterRipple = bool;
			outerRippleSize = SIZE;
			outerRippleDone = false;
		}
	}

	
	public Boolean getPressStatus()
	{
		return isPressed;
	}
	
	public void setPressStatus(Boolean bool)
	{
		isPressed = bool;
	}
	
	
	public void draw(Canvas canvas)
	{

		if (shouldInnerRipple && !innerRippleDone)
		{
			canvas.drawCircle(xCoord, yCoord, ((innerRippleSize+=6)/2), innerFadePaint);
			innerFadePaint.setAlpha(innerFadeValue-=3);
			if (innerFadeValue<=0)
			{
				shouldInnerRipple = false;
				innerRippleDone = true;
				innerFadeValue = 75;
				innerRippleSize = SIZE/3;
			} 
		}

		if (shouldOuterRipple && !outerRippleDone)
		{
			canvas.drawCircle(xCoord, yCoord, ((outerRippleSize+=4)/2), outerFadePaint);
			outerFadePaint.setAlpha(outerFadeValue-=3);
			if (outerFadeValue<=0)
			{
				shouldOuterRipple = false;
				outerRippleDone = true;
				outerFadeValue = 50;
				outerRippleSize = SIZE;
				isPressed = true;
			} 
		}


		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.rgb(68, 178, 209));
		paint.setAlpha(110);

		if (shouldInnerRipple && !innerRippleDone)
		{
			canvas.drawCircle(xCoord, yCoord+5, SIZE/2, paint);
			canvas.drawCircle(xCoord, yCoord+5, SIZE/2.5f, paint);

			paint.setColor(Color.WHITE);
			paint.setTextSize(SIZE/5);
			paint.setStyle(Paint.Style.FILL);
			paint.setDither(true);                 
			paint.setAntiAlias(true);
			paint.setTextAlign(Align.CENTER);
			paint.setFakeBoldText(true);
			canvas.drawText(buttonText, xCoord, 5+yCoord+paint.getTextSize()/4, paint);
		} else
		{
			canvas.drawCircle(xCoord, yCoord, SIZE/2, paint);
			canvas.drawCircle(xCoord, yCoord, SIZE/2.5f, paint);

			paint.setColor(Color.WHITE);
			paint.setTextSize(SIZE/5);
			paint.setStyle(Paint.Style.FILL);
			paint.setDither(true);                 
			paint.setAntiAlias(true);
			paint.setTextAlign(Align.CENTER);
			paint.setFakeBoldText(true);
			canvas.drawText(buttonText, xCoord, yCoord+paint.getTextSize()/4, paint);
		}

	}

}
