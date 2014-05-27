package com.starbytestudios.blockwords;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;


public class DifficultyMenuActivity extends Activity
{
	Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
	Intent gameIntent, mainMenuIntent;
	Point size;
	ArrayList<CustomButton> buttonList = new ArrayList<CustomButton>();
	CustomButton startButton = null;
	CustomButton endButton = null;
	int lineEndX = 0, lineEndY = 0;
	Boolean buttonPressedAlready = false;
	int BUTTON_SIZE;
	int SMALL_BUTTON_SIZE;
	Bitmap difficultyBitmap;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		buttonPressedAlready = false;
				
		//The intent to be started upon a mode selection press
		gameIntent = new Intent(getBaseContext(), GameActivity.class);
		mainMenuIntent = new Intent(getBaseContext(), MenuActivity.class);
		
		//Sets the intents flag so that the intent will not be kept in the intent history
		gameIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		mainMenuIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


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

		//Set the size of the Buttons
		BUTTON_SIZE = size.x/3;
		SMALL_BUTTON_SIZE = size.x/4;
		
		
		difficultyBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.difficulty);
		difficultyBitmap = Bitmap.createScaledBitmap(difficultyBitmap, size.x, size.x/2, true);
		
		
		//Create the buttons
		buttonList.add(new CustomButton("EASY", size.x/5, (size.y/3)+((3*BUTTON_SIZE)/4)+(difficultyBitmap.getHeight()/10), BUTTON_SIZE));
		buttonList.add(new CustomButton("HARD", size.x/2, size.y/3+(difficultyBitmap.getHeight()/10), BUTTON_SIZE));
		buttonList.add(new CustomButton("EXPERT", size.x-size.x/5, (size.y/3)+((3*BUTTON_SIZE)/4)+(difficultyBitmap.getHeight()/10), BUTTON_SIZE));
		buttonList.add(new CustomButton("MENU", size.x/2, (size.y)-(SMALL_BUTTON_SIZE+(SMALL_BUTTON_SIZE/3)), SMALL_BUTTON_SIZE));

		
		
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

						if(distanceToButton <= BUTTON_SIZE/2 && !buttonPressedAlready)
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

						if(distanceToButton <= BUTTON_SIZE/2)
						{
							if (startButton!=null)
							{
								button.setShouldOuterRipple(true);
								buttonPressedAlready = true;
								button.setPressStatus(true);
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
			//Draw background color
			paint.setColor(Color.WHITE);
			paint.setStyle(Paint.Style.FILL);       
			canvas.drawRect(0, 0, size.x, size.y, paint);	
			paint.setColor(Color.rgb(68, 178, 209));
			paint.setAlpha(110);
			canvas.drawRect(0, 0, size.x, size.y, paint);	
			
			
			//Draw white circle around button
			paint.setColor(Color.WHITE);
			paint.setStyle(Paint.Style.FILL);       
			canvas.drawCircle(size.x/2, size.y/2, 2*BUTTON_SIZE, paint);

			
			//Draw the buttons
			for (CustomButton button : buttonList)
			{
				button.draw(canvas);
				
				//Continually check for button press status of each button
				if (button.getPressStatus())
				{
					if (button == buttonList.get(0))
					{
						gameIntent.putExtra("difficultyLevel", 1); //Pass the variable levelDifficulty into the next activity 
						startActivity(gameIntent);
					} else if (button == buttonList.get(1))
					{
						gameIntent.putExtra("difficultyLevel", 2); //Pass the variable levelDifficulty into the next activity 
						startActivity(gameIntent);
					} else if (button == buttonList.get(2))
					{
						gameIntent.putExtra("difficultyLevel", 3); //Pass the variable levelDifficulty into the next activity 
						startActivity(gameIntent);
					} else if (button == buttonList.get(3))
					{
						finish();
						//startActivity(mainMenuIntent);
					}
					
					
					button.setPressStatus(false);
					buttonPressedAlready = false;

				}
			}
			
			paint.setAlpha(165);
			canvas.drawBitmap(difficultyBitmap, 0, difficultyBitmap.getHeight()/10, paint);

			invalidate();
		}
	}



}

