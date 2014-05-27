package com.starbytestudios.blockwords;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Display;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;


public class HelpActivity extends Activity
{
	Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
	Point size;
	ArrayList<CustomButton> buttonList = new ArrayList<CustomButton>();
	CustomButton startButton = null;
	int lineEndX = 0, lineEndY = 0;
	Boolean buttonPressedAlready = false;
	int BUTTON_SIZE = 0;
	Intent mainMenuIntent;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		
		buttonPressedAlready = false;
		
		//Get Vibrator
		Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		
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


		//Create the button
		buttonList.add(new CustomButton(vibrator, "MENU", size.x/2, (size.y)-(BUTTON_SIZE+(BUTTON_SIZE/3)), BUTTON_SIZE));		
		
		//The intent to be started upon a mode selection press
		mainMenuIntent = new Intent(getBaseContext(), MenuActivity.class);

		//Sets the intents flag so that the intent will not be kept in the intent history
		mainMenuIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		

		super.onCreate(savedInstanceState); 
	

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

		// Create a new RelativeLayout
		RelativeLayout relativeLayout = new RelativeLayout(this);

		// Create the RelativeLayout parameters, here it matches the size of the parent
		RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		relativeLayout.addView(new MyView(this)); //adds the MyView to the layout
		
		
		

		ScrollView scrollView = new ScrollView(this.getBaseContext());
		scrollView.setId(1);
		// defines the paramaters for the ScrollView
		LinearLayout.LayoutParams scrollViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 0, 1.0f);
		scrollView.setFillViewport(true);
		scrollView.setBackgroundColor(Color.rgb(68, 178, 209));

		// sets created parameters to the scrollView
		scrollView.setLayoutParams(scrollViewParams);
		
		
		LinearLayout linearLayout = new LinearLayout(getBaseContext());
		// Create the linearLayout parameters
		LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT); 		
		linearLayout.setOrientation(LinearLayout.VERTICAL);
	
		
		ImageView imageViewHeader = new ImageView(this.getBaseContext());
		imageViewHeader.setId(7);
		// defines the paramaters for the Image View
		RelativeLayout.LayoutParams imageViewParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		imageViewHeader.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		imageViewHeader.setImageResource(R.drawable.instructions_header);
		// sets created parameters to the Image View
		imageViewHeader.setLayoutParams(imageViewParams);
		linearLayout.addView(imageViewHeader);
		
		ImageView imageView1 = new ImageView(this.getBaseContext());
		imageView1.setId(2);
		// defines the paramaters for the Image View
		imageView1.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		imageView1.setImageResource(R.drawable.locked_tile);
		// sets created parameters to the Image View
		imageView1.setLayoutParams(imageViewParams);
		linearLayout.addView(imageView1);
		
		ImageView imageView2 = new ImageView(this.getBaseContext());
		imageView2.setId(3);
		// defines the paramaters for the Image View
		imageView2.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		imageView2.setImageResource(R.drawable.free_tile);
		// sets created parameters to the Image View
		imageView2.setLayoutParams(imageViewParams);		
		linearLayout.addView(imageView2);
		
		ImageView imageView3 = new ImageView(this.getBaseContext());
		imageView3.setId(4);
		// defines the paramaters for the Image View
		imageView3.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		imageView3.setImageResource(R.drawable.swap_tile);
		// sets created parameters to the Image View
		imageView3.setLayoutParams(imageViewParams);		
		linearLayout.addView(imageView3);
		
		ImageView imageView4 = new ImageView(this.getBaseContext());
		imageView4.setId(5);
		// defines the paramaters for the Image View
		imageView4.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		imageView4.setImageResource(R.drawable.goal);
		// sets created parameters to the Image View
		imageView4.setLayoutParams(imageViewParams);		
		linearLayout.addView(imageView4);
		
		ImageView imageView5 = new ImageView(this.getBaseContext());
		imageView5.setId(6);
		// defines the paramaters for the Image View
		imageView5.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		imageView5.setImageResource(R.drawable.careful);
		// sets created parameters to the Image View
		imageView5.setLayoutParams(imageViewParams);		
		linearLayout.addView(imageView5);
		
		
		scrollView.addView(linearLayout);
		
		
		//TODO Make a new linear layout that will contain the scroll view as well as a blank view. Then add this linear layout (treat is as a half-sized scrollview) into the relative layout
		LinearLayout halvedLinearLayout = new LinearLayout(getBaseContext());
		// Create the linearLayout parameters
		LinearLayout.LayoutParams halvedLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 0, 2.5f); 		
		halvedLinearLayout.setOrientation(LinearLayout.VERTICAL);
		
		View blankView = new View(this);
		blankView.setLayoutParams(halvedLayoutParams);
		
		
		halvedLinearLayout.addView(scrollView);
		halvedLinearLayout.addView(blankView);
		relativeLayout.addView(halvedLinearLayout);
		//relativeLayout.addView(scrollView);

		
		
		//Disable hardware acceleration so lines can be rounded
		try
		{
			relativeLayout.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		} catch (NoSuchMethodError e)
		{

		}

		// Setting the RelativeLayout as our content view
		setContentView(relativeLayout, rlp); 
        //setContentView(R.layout.difficulty_menu);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_game, menu);
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

			
			//Draw white circle around button
			paint.setColor(Color.WHITE);
			paint.setStyle(Paint.Style.FILL);       
			paint.setAlpha(255);
			canvas.drawCircle(size.x/2, (size.y)-(BUTTON_SIZE+(BUTTON_SIZE/3)), BUTTON_SIZE, paint);
			
				
			//Draw the buttons
			for (CustomButton button : buttonList)
			{
				button.draw(canvas);
				
				//Continually check for button press status of each button
				if (button.getPressStatus())
				{
					if (button == buttonList.get(0))
					{
						//startActivity(mainMenuIntent);
						finish();
					} 

					button.setPressStatus(false);
					buttonPressedAlready = false;

				}
			}
			
			

			invalidate();
		}
	}



}


