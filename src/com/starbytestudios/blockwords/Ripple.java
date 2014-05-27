package com.starbytestudios.blockwords;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Ripple 
{
	int rippleSize = 0;
	int xCoord = -999;
	int yCoord = -999;
	int rippleFadeValue = 75;
	Paint paint = new Paint();
	Boolean isRippling = true;


	public Ripple(int size, int x, int y)
	{
		xCoord = x;
		yCoord = y;
		rippleSize = size/4;
	}

	public Boolean isRippling()
	{
		return isRippling;
	}

	public void draw(Canvas canvas)
	{
		//Draw the ripple
		if (isRippling)
		{
			paint.setColor(Color.WHITE);
			paint.setAlpha(rippleFadeValue-=5);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(rippleSize/10);
			canvas.drawCircle(xCoord, yCoord, rippleSize+=2, paint);

			if (rippleFadeValue <= 0)
			{
				int ripple1X = -999;
				rippleSize = 0;
				isRippling = false;
			}
		}
	}

}
