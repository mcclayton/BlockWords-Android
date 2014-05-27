package com.starbytestudios.blockwords;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;


public class StatsActivity extends Activity
{
	Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
	Point size;
	ArrayList<CustomButton> buttonList = new ArrayList<CustomButton>();
	ArrayList<String> dataArrayList = new ArrayList<String>();
	CustomButton startButton = null;
	int lineEndX = 0, lineEndY = 0;
	Boolean buttonPressedAlready = false;
	int BUTTON_SIZE = 0;
	DataHandler handler;
	Matrix matrix = new Matrix();
	float rotationAngle = 0;
	float stringSize;
	Bitmap easyBarBitmap, hardBarBitmap, expertBarBitmap, starBitmap, puzzlesBitmap;
	Intent scoresIntent, mainMenuIntent;
	AlertDialog dialog;

	int BAR_NUMBER;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{		
		buttonPressedAlready = false;

		handler = new DataHandler();
		
		Display display = getWindowManager().getDefaultDisplay();
		size = new Point();

		try 
		{
			display.getSize(size);
			size.y-=10;
		} catch (java.lang.NoSuchMethodError ignore) { //Catches this error and handles it to allow support for older devices
			size.x = display.getWidth();
			size.y = display.getHeight();
		}

		BUTTON_SIZE = size.x/4;

		puzzlesBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.puzzles);
		puzzlesBitmap = Bitmap.createScaledBitmap(puzzlesBitmap, size.x, size.x/4, true);
		easyBarBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.long_bar_easy);
		easyBarBitmap = Bitmap.createScaledBitmap(easyBarBitmap, size.x, size.x/4, true);
		hardBarBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.long_bar_hard);
		hardBarBitmap = Bitmap.createScaledBitmap(hardBarBitmap, size.x, size.x/4, true);
		expertBarBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.long_bar_expert);
		expertBarBitmap = Bitmap.createScaledBitmap(expertBarBitmap, size.x, size.x/4, true);
		starBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.star);
		starBitmap = Bitmap.createScaledBitmap(starBitmap, easyBarBitmap.getHeight()/2, easyBarBitmap.getHeight()/2, true);

		//Create the button
		buttonList.add(new CustomButton("MENU", size.x/2, (size.y)-(BUTTON_SIZE+(BUTTON_SIZE/3)), BUTTON_SIZE));
		buttonList.add(new CustomButton("<--", size.x/5, (size.y)-(BUTTON_SIZE+(BUTTON_SIZE/3)), BUTTON_SIZE));
		buttonList.add(new CustomButton("RESET", size.x-(size.x/5), (size.y)-(BUTTON_SIZE+(BUTTON_SIZE/3)), BUTTON_SIZE));
		
		
		dataArrayList = handler.readFromFile(getBaseContext());
		
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        switch (which){
		        case DialogInterface.BUTTON_POSITIVE:
		            //Yes button clicked
					//Draw the Stats
		        	dataArrayList.set(3, "0");
		        	dataArrayList.set(4, "0");
		        	dataArrayList.set(5, "0");
		        	handler.writeToFile(dataArrayList, getBaseContext());
					
		            break;

		        case DialogInterface.BUTTON_NEGATIVE:
		            //No button clicked
		        	dialog.dismiss();
		            break;
		        }
		    }
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure you wish to reset the solved puzzles data?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener);
		dialog = builder.create();
		

		//The intent to be started upon a mode selection press
		scoresIntent = new Intent(getBaseContext(), ScoresActivity.class);
		mainMenuIntent = new Intent(getBaseContext(), MenuActivity.class);

		//Sets the intents flag so that the intent will not be kept in the intent history
		mainMenuIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		super.onCreate(savedInstanceState);

		// Create a new RelativeLayout
		RelativeLayout relativeLayout = new RelativeLayout(this);

		// Create the RelativeLayout parameters, here it matches the size of the parent
		RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT); 
		

		//Handle presses on the view
		this.findViewById(android.R.id.content).setOnTouchListener(new View.OnTouchListener() 
		{
			@Override
			public boolean onTouch(View v, MotionEvent event) 
			{	
				if (event.getAction() == MotionEvent.ACTION_DOWN) 
				{
					for (CustomButton button : buttonList)
					{						
						float distanceToButton = (float) Math.sqrt((event.getX()-button.getX())*(event.getX()-button.getX())+(event.getY()-button.getY())*(event.getY()-button.getY()));

						if(distanceToButton <= button.getSize()/2 && !buttonPressedAlready)
						{
							button.setShouldInnerRipple(true);
							startButton = button;
						}
					}

				} else if (event.getAction() == MotionEvent.ACTION_UP && !buttonPressedAlready) 
				{
					for (CustomButton button : buttonList)
					{
						float distanceToButton = (float) Math.sqrt((event.getX()-button.getX())*(event.getX()-button.getX())+(event.getY()-button.getY())*(event.getY()-button.getY()));

						if(distanceToButton <= button.getSize()/2)
						{
							if (startButton!=null)
							{
								button.setShouldOuterRipple(true);
								buttonPressedAlready = true;
								startButton = null;
								//TODO Go to next activity
							}
						} 
					}   

					startButton = null;

				}

				return true;
			}

		});


		relativeLayout.addView(new MyView(this)); //adds the MyView to the layout

		//Disable hardware acceleration so lines can be rounded
		try
		{
			relativeLayout.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		} catch (NoSuchMethodError e)
		{

		}

		// Setting the RelativeLayout as our content view
		setContentView(relativeLayout, rlp); 

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.activity_game, menu);
		return true;
	}


	private class MyView extends View 
	{

		public MyView(Context context) 
		{
			super(context);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void onDraw(Canvas canvas) 
		{
			paint.setTextSize(size.x/5);
			paint.setStrokeWidth(paint.getTextSize());
			paint.setDither(true);                 
			paint.setStyle(Paint.Style.STROKE);       
			paint.setStrokeJoin(Paint.Join.ROUND);   
			paint.setStrokeCap(Paint.Cap.ROUND);      
			paint.setAntiAlias(true);

			//Draw background color
			paint.setColor(Color.WHITE);
			paint.setStyle(Paint.Style.FILL);       
			canvas.drawRect(0, 0, size.x, size.y, paint);	
			paint.setColor(Color.rgb(68, 178, 209));
			paint.setAlpha(110);
			canvas.drawRect(0, 0, size.x, size.y, paint);	
			
			canvas.drawRect(0, 0, size.x, size.x/6+paint.getTextSize()/2, paint);
			
			
			//Draw white circle around button
			paint.setColor(Color.WHITE);
			paint.setStyle(Paint.Style.FILL);       
			paint.setAlpha(255);
			canvas.drawCircle(size.x/2, (size.y)-(BUTTON_SIZE+(BUTTON_SIZE/3)), BUTTON_SIZE+BUTTON_SIZE/6, paint);
			

			
			
			//Draw the buttons
			for (CustomButton button : buttonList)
			{
				button.draw(canvas);
				
				//Continually check for button press status of each button
				if (button.getPressStatus())
				{
					if (button == buttonList.get(0))
					{
						startActivity(mainMenuIntent);
					} 
					if (button == buttonList.get(1))
					{
						finish();
					}
					if (button == buttonList.get(2))
					{
						dialog.show();
					}
					
					button.setPressStatus(false);
					buttonPressedAlready = false;

				}
			}
						
			
			/*
			//Draw Title Text
			paint.setColor(Color.WHITE);
			paint.setAlpha(255);
			paint.setStyle(Paint.Style.FILL);
			paint.setDither(true);                 
			paint.setAntiAlias(true);
			paint.setFakeBoldText(true);
			paint.setTextSize((2*easyBarBitmap.getHeight())/3);
			paint.setTextAlign(Align.CENTER);
			canvas.drawText("Puzzles", size.x/2, (easyBarBitmap.getHeight() - (easyBarBitmap.getHeight()/2) - (paint.descent() + paint.ascent())/2), paint);
			paint.setTextAlign(Align.LEFT);
			*/

			canvas.drawBitmap(puzzlesBitmap, 0, 0, paint);
			
			
			
			//Draw long bar
			BAR_NUMBER = 2;

			canvas.drawBitmap(easyBarBitmap, 0, (BAR_NUMBER-1)*easyBarBitmap.getHeight(), paint);	
			
			//Draw rotating star
			matrix.reset();
			rotationAngle = (rotationAngle+.5f)%360;
			matrix.postRotate(rotationAngle+=.5f, starBitmap.getWidth()/2, starBitmap.getHeight()/2);
			matrix.postTranslate(starBitmap.getWidth()/3, ((BAR_NUMBER*2)*(easyBarBitmap.getHeight()/2)-(easyBarBitmap.getHeight()/2)-(starBitmap.getHeight()/2)));
			canvas.drawBitmap(starBitmap, matrix, paint);
			
			//Draw the text
			paint.setColor(Color.WHITE);
			paint.setAlpha(255);
			paint.setStyle(Paint.Style.FILL);
			paint.setDither(true);                 
			paint.setAntiAlias(true);
			paint.setFakeBoldText(true);
			paint.setTextSize((2*easyBarBitmap.getHeight())/3);
			
			//Draw the number of puzzles solved
			canvas.drawText(NumberFormat.getNumberInstance(Locale.US).format(Integer.parseInt(dataArrayList.get(3))), (3*starBitmap.getWidth())/2, (BAR_NUMBER*easyBarBitmap.getHeight() - (easyBarBitmap.getHeight()/2) - (paint.descent() + paint.ascent())/2), paint);
			stringSize = paint.measureText(NumberFormat.getNumberInstance(Locale.US).format(Integer.parseInt(dataArrayList.get(3))));
			paint.setTextSize((easyBarBitmap.getHeight())/4);
			canvas.drawText(" (Solved)", (3*starBitmap.getWidth())/2 + stringSize, (BAR_NUMBER*easyBarBitmap.getHeight() - (easyBarBitmap.getHeight()/2) - (paint.descent() + paint.ascent())/2), paint);

			
			
			//Draw long bar
			BAR_NUMBER = 3;

			canvas.drawBitmap(hardBarBitmap, 0, (BAR_NUMBER-1)*easyBarBitmap.getHeight(), paint);	
			
			//Draw rotating star
			matrix.reset();
			rotationAngle = (rotationAngle+.5f)%360;
			matrix.postRotate(rotationAngle+.5f, starBitmap.getWidth()/2, starBitmap.getHeight()/2);
			matrix.postTranslate(starBitmap.getWidth()/3, ((BAR_NUMBER*2)*(easyBarBitmap.getHeight()/2)-(easyBarBitmap.getHeight()/2)-(starBitmap.getHeight()/2)));
			canvas.drawBitmap(starBitmap, matrix, paint);
			
			//Draw the text
			paint.setColor(Color.WHITE);
			paint.setAlpha(255);
			paint.setStyle(Paint.Style.FILL);
			paint.setDither(true);                 
			paint.setAntiAlias(true);
			paint.setFakeBoldText(true);
			paint.setTextSize((2*easyBarBitmap.getHeight())/3);
			
			//Draw the number of puzzles solved
			canvas.drawText(NumberFormat.getNumberInstance(Locale.US).format(Integer.parseInt(dataArrayList.get(4))), (3*starBitmap.getWidth())/2, (BAR_NUMBER*easyBarBitmap.getHeight() - (easyBarBitmap.getHeight()/2) - (paint.descent() + paint.ascent())/2), paint);
			stringSize = paint.measureText(NumberFormat.getNumberInstance(Locale.US).format(Integer.parseInt(dataArrayList.get(4))));
			paint.setTextSize((easyBarBitmap.getHeight())/4);
			canvas.drawText(" (Solved)", (3*starBitmap.getWidth())/2 + stringSize, (BAR_NUMBER*easyBarBitmap.getHeight() - (easyBarBitmap.getHeight()/2) - (paint.descent() + paint.ascent())/2), paint);
			
			
			//Draw long bar
			BAR_NUMBER = 4;

			canvas.drawBitmap(expertBarBitmap, 0, (BAR_NUMBER-1)*easyBarBitmap.getHeight(), paint);	
			
			//Draw rotating star
			matrix.reset();
			rotationAngle = (rotationAngle+.5f)%360;
			matrix.postRotate(rotationAngle+.5f, starBitmap.getWidth()/2, starBitmap.getHeight()/2);
			matrix.postTranslate(starBitmap.getWidth()/3, ((BAR_NUMBER*2)*(easyBarBitmap.getHeight()/2)-(easyBarBitmap.getHeight()/2)-(starBitmap.getHeight()/2)));
			canvas.drawBitmap(starBitmap, matrix, paint);
			
			//Draw the text
			paint.setColor(Color.WHITE);
			paint.setAlpha(255);
			paint.setStyle(Paint.Style.FILL);
			paint.setDither(true);                 
			paint.setAntiAlias(true);
			paint.setFakeBoldText(true);
			paint.setTextSize((2*easyBarBitmap.getHeight())/3);

			//Draw the number of puzzles solved
			canvas.drawText(NumberFormat.getNumberInstance(Locale.US).format(Integer.parseInt(dataArrayList.get(5))), (3*starBitmap.getWidth())/2, (BAR_NUMBER*easyBarBitmap.getHeight() - (easyBarBitmap.getHeight()/2) - (paint.descent() + paint.ascent())/2), paint);
			stringSize = paint.measureText(NumberFormat.getNumberInstance(Locale.US).format(Integer.parseInt(dataArrayList.get(5))));
			paint.setTextSize((easyBarBitmap.getHeight())/4);
			canvas.drawText(" (Solved)", (3*starBitmap.getWidth())/2 + stringSize, (BAR_NUMBER*easyBarBitmap.getHeight() - (easyBarBitmap.getHeight()/2) - (paint.descent() + paint.ascent())/2), paint);
		
			
			
			
			//canvas.drawBitmap(scoreBarBitmap, 0, scoreBarBitmap.getHeight(), paint);
			//canvas.drawBitmap(scoreBarBitmap, 0, 2*scoreBarBitmap.getHeight(), paint);
			//canvas.drawBitmap(scoreBarBitmap, 0, 3*scoreBarBitmap.getHeight(), paint);
			//canvas.drawBitmap(scoreBarBitmap, 0, 4*scoreBarBitmap.getHeight(), paint);
			//canvas.drawBitmap(scoreBarBitmap, 0, 5*scoreBarBitmap.getHeight(), paint);



			invalidate();
		}
	}



}


