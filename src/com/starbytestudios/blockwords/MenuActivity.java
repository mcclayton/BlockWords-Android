package com.starbytestudios.blockwords;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.text.style.StyleSpan;
import android.view.Display;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;


public class MenuActivity extends Activity
{
	Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
	Intent difficultyMenuActivity, scoresIntent, helpIntent;
	Point size;
	ArrayList<CustomButton> buttonList = new ArrayList<CustomButton>();
	CustomButton startButton = null;
	CustomButton endButton = null;
	int lineEndX = 0, lineEndY = 0;
	Boolean buttonPressedAlready = false;
	int BUTTON_SIZE = 0;
	int SMALL_BUTTON_SIZE = 0;
	Bitmap titleBitmap;
	AlertDialog dialog;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		buttonPressedAlready = false;
				
		//The intent to be started upon a mode selection press
		difficultyMenuActivity = new Intent(getBaseContext(), DifficultyMenuActivity.class);
		scoresIntent = new Intent(getBaseContext(), ScoresActivity.class);
		helpIntent = new Intent(getBaseContext(), HelpActivity.class);

		
		//Sets the intents flag so that the intent will not be kept in the intent history
		difficultyMenuActivity.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);		

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
		
		BUTTON_SIZE = size.x/3;
		SMALL_BUTTON_SIZE = size.x/4;
		
		//Create the buttons
		buttonList.add(new CustomButton("PLAY", size.x/2, size.y/2, BUTTON_SIZE));
		buttonList.add(new CustomButton("STATS", size.x/2, (size.y/2)+BUTTON_SIZE, SMALL_BUTTON_SIZE));
		buttonList.add(new CustomButton("INFO", size.x/5, (size.y/3)+((3*SMALL_BUTTON_SIZE)/4), SMALL_BUTTON_SIZE));
		buttonList.add(new CustomButton("HELP", size.x-size.x/5, (size.y/3)+((3*SMALL_BUTTON_SIZE)/4), SMALL_BUTTON_SIZE));
		

		titleBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.title);
		titleBitmap = Bitmap.createScaledBitmap(titleBitmap, size.x, size.x/2, true);


		super.onCreate(savedInstanceState);

		// Create a new RelativeLayout
		RelativeLayout relativeLayout = new RelativeLayout(this);

		// Create the RelativeLayout parameters, here it matches the size of the parent
		RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT); 

		
		//Create an alert dialog
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Information");

        String messageString = new String("BlockWords was developed by\nMichael Clayton.\nBlockWords\' word list is based on the Enhanced North American Benchmark Lexicon.\nVersion 1.0");
		Spannable WordtoSpan = new SpannableString(messageString);        
		WordtoSpan.setSpan(new StyleSpan(Typeface.BOLD), 27, 43, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		WordtoSpan.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 27, 43, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		WordtoSpan.setSpan(new StyleSpan(Typeface.BOLD), messageString.length()-11, messageString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		WordtoSpan.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), messageString.length()-11, messageString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		        
        builder.setMessage(WordtoSpan);
        builder.setPositiveButton("OK", null);
        builder.setIcon(R.drawable.portrait);
        dialog = builder.create();
        //messageText.setGravity(Gravity.CENTER);
		
		
		

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
			paint.setDither(true);                 
			paint.setAntiAlias(true);
			
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
			paint.setAlpha(255);
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
						startActivity(difficultyMenuActivity);
					} 
					if (button == buttonList.get(1))
					{
						startActivity(scoresIntent);
					} 
				    if (button == buttonList.get(2))
					{
						// Show Alert Message
						dialog.show();
					}
				    if (button == buttonList.get(3))
					{
						startActivity(helpIntent);
					}
					button.setPressStatus(false);
					buttonPressedAlready = false;

				}
			}
			
			paint.setAlpha(165);
			canvas.drawBitmap(titleBitmap, 0, titleBitmap.getHeight()/10, paint);


			invalidate();
		}
	}



}

