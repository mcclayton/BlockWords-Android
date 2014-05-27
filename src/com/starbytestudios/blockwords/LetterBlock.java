package com.starbytestudios.blockwords;

import java.util.Locale;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;

public class LetterBlock 
{
	private static int SIZE = -1000;
	private char letter = '0';
	private int xCoord = 0;
	private int yCoord = 0;
	private Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
	private int selectionState = 0;
	private Boolean shouldHighlightRow = false;
	private Boolean shouldHighlightColumn = false;
	private Boolean shouldLock = false;
	private static Bitmap tileBitmap;
	private static Bitmap tilePressedBitmap;
	
	private static Context context;

	public LetterBlock(char lttr, int x, int y)
	{
		letter = lttr;
		xCoord = x;
		yCoord = y;
	}

	public static void initializeBitmaps(Context c)
	{
		context = c;
		tileBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.tile);
		tileBitmap = Bitmap.createScaledBitmap(tileBitmap, SIZE, SIZE, true);
		tilePressedBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.tile_pressed);
		tilePressedBitmap = Bitmap.createScaledBitmap(tilePressedBitmap, SIZE, SIZE, true);
	}
	
	public void setHighlightRow(Boolean bool)
	{
		shouldHighlightRow = bool;
	}

	public void setHighlightColumn(Boolean bool)
	{
		shouldHighlightColumn = bool;
	}
	
	public Boolean getHighlightRow()
	{
		return shouldHighlightRow;
	}

	public Boolean getHighlightColumn()
	{
		return shouldHighlightColumn;
	}

	public void setShouldLock(Boolean bool)
	{
		shouldLock = bool;
	}
	
	public Boolean getLockStatus()
	{
		return shouldLock;
	}

	public void setLetter(char c)
	{
		letter = c;
	}

	public char getLetter()
	{
		return letter;
	}

	public static int getSize()
	{
		return SIZE;
	}
	
	public static void setSize(int size)
	{
		SIZE = size;
	}

	public void setSelectionState(int state)
	{
		selectionState = state;
	}

	public int getSelectionState()
	{
		return selectionState;
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

	public void draw(Canvas canvas)
	{

		if(shouldLock)
		{			
			paint.setColor(Color.rgb(8, 119, 155));
			paint.setStyle(Paint.Style.FILL);
			canvas.drawRect(xCoord, yCoord, xCoord+SIZE, yCoord+SIZE, paint);

			//Draw border
			paint.setColor(Color.rgb(10, 136, 177));
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(3);
			canvas.drawRect(xCoord, yCoord, xCoord+SIZE, yCoord+SIZE, paint);

			//canvas.drawBitmap(tileLockedBitmap, xCoord, yCoord, paint);
			
			paint.setColor(Color.WHITE);
			paint.setTextSize(SIZE/2.5f);
			paint.setStyle(Paint.Style.FILL);
			paint.setDither(true);                 
			paint.setAntiAlias(true);
			paint.setTextAlign(Align.CENTER);
			paint.setFakeBoldText(true);
			canvas.drawText(Character.toString(letter).toUpperCase(), xCoord+(SIZE/2), yCoord+(SIZE/2)+paint.getTextSize()/4, paint);	

		} else
		{

			switch(selectionState)
			{
			case 0:    //Not selected or highlighted
				//paint.setColor(Color.WHITE);
				//paint.setStyle(Paint.Style.FILL);
				//canvas.drawRect(xCoord, yCoord, xCoord+SIZE, yCoord+SIZE, paint);
				canvas.drawBitmap(tileBitmap, xCoord, yCoord, paint);
				
				//paint.setColor(Color.BLACK);
				//paint.setStyle(Paint.Style.STROKE);
				//paint.setStrokeWidth(3);
				//canvas.drawRect(xCoord, yCoord, xCoord+SIZE, yCoord+SIZE, paint);
				break;
			case 1:    //Highlighted
				//paint.setColor(Color.LTGRAY);
				//paint.setStyle(Paint.Style.FILL);
				//canvas.drawRect(xCoord, yCoord, xCoord+SIZE, yCoord+SIZE, paint);

				canvas.drawBitmap(tilePressedBitmap, xCoord, yCoord, paint);
				
				//paint.setColor(Color.BLACK);
				//paint.setStyle(Paint.Style.STROKE);
				//paint.setStrokeWidth(3);
				//canvas.drawRect(xCoord, yCoord, xCoord+SIZE, yCoord+SIZE, paint);
				break;			
			default:
				break;

			}
			paint.setColor(Color.BLACK);
			paint.setAlpha(25);
			paint.setTextSize(SIZE/2.5f);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(5);
			paint.setDither(true);                 
			paint.setAntiAlias(true);
			paint.setTextAlign(Align.CENTER);
			paint.setFakeBoldText(true);
			canvas.drawText(Character.toString(letter).toUpperCase(Locale.US), xCoord+(SIZE/2), yCoord+(SIZE/2)+paint.getTextSize()/4, paint);
			paint.setColor(Color.WHITE);
			paint.setStyle(Paint.Style.FILL);
			canvas.drawText(Character.toString(letter).toUpperCase(Locale.US), xCoord+(SIZE/2), yCoord+(SIZE/2)+paint.getTextSize()/4, paint);

		}


	}

}
