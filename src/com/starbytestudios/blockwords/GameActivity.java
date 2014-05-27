package com.starbytestudios.blockwords;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Display;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

@SuppressLint("NewApi")
public class GameActivity extends Activity 
{

	Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
	ArrayList<LetterBlock> blockList = new ArrayList<LetterBlock>();
	Point size = new Point();
	int lineStartX = -999, lineStartY = -999, lineEndX = -999, lineEndY = -999;	
	LetterBlock startBlock = null;
	LetterBlock endBlock = null;
	SolutionBuilder builder = null;
	long timerStart;
	char[][] solutionGrid = null;
	char[][] shuffledGrid = null;
	int difficultyLevel = 3;
	Boolean startTimer = false;
	Boolean gotNewHighScore = false;
	Boolean didWin = false; //Used to determine if user has won the current puzzle
	Bitmap winMenuBitmap, starBitmap;
	AssetManager assetManager;
	String puzzleSolvedTime;
	ArrayList<String> dataArrayList;
	DataHandler dataHandler = new DataHandler();
	String timeHighScore = "--:--:--";
	Matrix matrix = new Matrix();
	float rotationAngle = 0;
	CustomButton startButton = null;
	Intent mainMenuIntent;
	
	ArrayList<Ripple> rippleList = new ArrayList<Ripple>();
	List<Ripple> finishedRipples = new ArrayList<Ripple>();
	ArrayList<CustomButton> buttonList = new ArrayList<CustomButton>();


	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{

		//Get the difficultyLevel variable passed in from the MainActivity and use it to set the color theme.
		difficultyLevel = getIntent().getExtras().getInt("difficultyLevel");


		assetManager = this.getAssets();    //Get the asset manager

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

		LetterBlock.setSize(size.x/4);    //Set the size of the letter blocks
		LetterBlock.initializeBitmaps(this);    //Set the size of the letter blocks

		//Get Vibrator
		Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		
		//The intent to be started upon the main menu button press
		mainMenuIntent = new Intent(getBaseContext(), MenuActivity.class);
		
		//Sets the intents flag so that the intent will not be kept in the intent history
		mainMenuIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		
		int SMALL_BUTTON_SIZE = (3*size.x)/13;
		
		//Create the buttons
//		buttonList.add(new CustomButton(vibrator, "RESET", size.x/8, (size.y)-(SMALL_BUTTON_SIZE+(SMALL_BUTTON_SIZE/3)), SMALL_BUTTON_SIZE));
//		buttonList.add(new CustomButton(vibrator, "NEW", (size.x/2)-size.x/8, (size.y)-(SMALL_BUTTON_SIZE+(SMALL_BUTTON_SIZE/3)), SMALL_BUTTON_SIZE));
//		buttonList.add(new CustomButton(vibrator, "MENU", size.x-(size.x/8), (size.y)-(SMALL_BUTTON_SIZE+(SMALL_BUTTON_SIZE/3)), SMALL_BUTTON_SIZE));
//		buttonList.add(new CustomButton(vibrator, "❚❚", (size.x/2)+(size.x/8), (size.y)-(SMALL_BUTTON_SIZE+(SMALL_BUTTON_SIZE/3)), SMALL_BUTTON_SIZE));

		
		buttonList.add(new CustomButton(vibrator, "RESET", size.x/5, (size.y)-(SMALL_BUTTON_SIZE+(SMALL_BUTTON_SIZE/3)), SMALL_BUTTON_SIZE));
		buttonList.add(new CustomButton(vibrator, "NEW", (size.x/2), (size.y)-(SMALL_BUTTON_SIZE+(SMALL_BUTTON_SIZE/3)), SMALL_BUTTON_SIZE));
		buttonList.add(new CustomButton(vibrator, "MENU", size.x-(size.x/5), (size.y)-(SMALL_BUTTON_SIZE+(SMALL_BUTTON_SIZE/3)), SMALL_BUTTON_SIZE));


		winMenuBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.win_menu);
		winMenuBitmap = Bitmap.createScaledBitmap(winMenuBitmap, size.x, size.x, true);
		starBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.star);
		starBitmap = Bitmap.createScaledBitmap(starBitmap, LetterBlock.getSize()/2, LetterBlock.getSize()/2, true);


		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		//Set the content view
		//setContentView(new MyView(this));

		// Create a new RelativeLayout
		RelativeLayout relativeLayout = new RelativeLayout(this);

		// Create the RelativeLayout parameters, here it matches the size of the parent
		RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT); 

		//Assign a new puzzle to the playing area and set didWin to false
		getNewPuzzle();


		//Handle presses on the view
		this.findViewById(android.R.id.content).setOnTouchListener(new View.OnTouchListener() 
		{
			@Override
			public boolean onTouch(View v, MotionEvent event) 
			{	
				if (event.getAction() == MotionEvent.ACTION_DOWN) 
				{
					if (!didWin)
					{
						//Handle Letter Block Down Press
						for (LetterBlock block : blockList)
						{
							if (!block.getLockStatus())
							{
								if ((event.getX()>block.getX()&&event.getX()<(block.getX()+LetterBlock.getSize())) && (event.getY()>block.getY()&&event.getY()<(block.getY()+LetterBlock.getSize())))
								{
									block.setSelectionState(1);
									startBlock = block;
									lineStartX = block.getX()+(LetterBlock.getSize()/2);
									lineStartY = block.getY()+(LetterBlock.getSize()/2);
								} else
								{
									if (block!=startBlock)
										block.setSelectionState(0);
								}
							}
						}
					}

					//Handle Custom Button Down Press
					for (CustomButton button : buttonList)
					{						
						float distanceToButton = (float) Math.sqrt((event.getX()-button.getX())*(event.getX()-button.getX())+(event.getY()-button.getY())*(event.getY()-button.getY()));

						if(distanceToButton <= button.getSize()/2)
						{
							button.setShouldInnerRipple(true);
							startButton = button;
						}
					}

				} else if (event.getAction() == MotionEvent.ACTION_UP) 
				{
					//Handle Letter Block Action Up Event
					if(!didWin)
					{
						for (LetterBlock block : blockList)
						{
							if (!block.getLockStatus())
							{
								if ((event.getX()>block.getX()&&event.getX()<(block.getX()+LetterBlock.getSize())) && (event.getY()>block.getY()&&event.getY()<(block.getY()+LetterBlock.getSize())) && block==endBlock)
								{
									if (startBlock!=null)
									{
										endBlock = block;
									}
								} 
								block.setSelectionState(0);
							}
						}   

						if (startBlock != null && endBlock!= null && startBlock!=endBlock)
						{
							char character1 = startBlock.getLetter();
							char character2 = endBlock.getLetter();

							startBlock.setLetter(character2);
							endBlock.setLetter(character1);

							builder.highlightWordMatches(blockList);
							
							//Make ripples at blocks that are swapping
							rippleList.add(new Ripple(LetterBlock.getSize(), startBlock.getX()+LetterBlock.getSize()/2, startBlock.getY()+LetterBlock.getSize()/2));
							rippleList.add(new Ripple(LetterBlock.getSize(), endBlock.getX()+LetterBlock.getSize()/2, endBlock.getY()+LetterBlock.getSize()/2));

							startBlock = null;
							endBlock = null;
						}

						startBlock = null;
						endBlock = null;
						lineStartX = -999;
						lineStartY = -999;
						lineEndX = -999;
						lineEndY = -999;
					}


					//Handle Custom Button Action Up Event
					for (CustomButton button : buttonList)
					{
						float distanceToButton = (float) Math.sqrt((event.getX()-button.getX())*(event.getX()-button.getX())+(event.getY()-button.getY())*(event.getY()-button.getY()));

						if(distanceToButton <= button.getSize()/2)
						{
							if (startButton!=null)
							{
								button.setShouldOuterRipple(true);
								startButton = null;
								
								handleButtonPress(button); //Handles the button press according to which button was pressed
							}
						} 
					}   

					startButton = null;


				} else if (event.getAction() == MotionEvent.ACTION_MOVE && !didWin) 
				{

					for (LetterBlock block : blockList)
					{
						if (!block.getLockStatus())
						{
							if ((event.getX()>block.getX()&&event.getX()<(block.getX()+LetterBlock.getSize())) && (event.getY()>block.getY()&&event.getY()<(block.getY()+LetterBlock.getSize())) && startBlock!=null)
							{
								if (block!=startBlock)
								{
									block.setSelectionState(1);
								}
								lineEndX = block.getX()+(LetterBlock.getSize()/2);
								lineEndY = block.getY()+(LetterBlock.getSize()/2);
								endBlock = block;
							} else
							{
								if (block!=startBlock && block!=endBlock)
									block.setSelectionState(0);
							}
						}
					}
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
			
			//Draw background color
			paint.setColor(Color.WHITE);
			paint.setStyle(Paint.Style.FILL);       
			canvas.drawRect(0, 0, size.x, size.y, paint);
			
			//Draw timer
			paint.setColor(Color.rgb(8, 100, 129));
			paint.setAlpha(200);
			paint.setTextSize(size.x/5);
			paint.setStyle(Paint.Style.FILL);
			paint.setDither(true);                 
			paint.setAntiAlias(true);
			paint.setTextAlign(Align.CENTER);
			paint.setFakeBoldText(true);
			if (!didWin)
				canvas.drawText(getTimerString(timerStart), size.x/2, ((size.y-(LetterBlock.getSize()*4))/3)+(LetterBlock.getSize()*4), paint);
			else
				canvas.drawText(puzzleSolvedTime, size.x/2, ((size.y-(LetterBlock.getSize()*4))/3)+(LetterBlock.getSize()*4), paint);


			//Draw box around tiles
			paint.setStrokeWidth(3);
			paint.setColor(Color.rgb(10, 136, 177));
			paint.setStyle(Paint.Style.STROKE);
			canvas.drawRect(0, 0, size.x, size.x, paint);

			//Draw the blocks
			for (LetterBlock block : blockList)
			{
				block.draw(canvas);
			}


			//Draw lines signifying word matches
			drawHighlightMatches(blockList, canvas);

			//Draws ripples
			for(Ripple ripple : rippleList)
			{
				ripple.draw(canvas);

				if(!ripple.isRippling())
				{
					finishedRipples.add(ripple);
				}
			}
			//Remove ripples that are finished rippling
			rippleList.removeAll(finishedRipples);
			finishedRipples.clear();
			

			//Draw a smooth rounded line
			if (!(lineEndX==-999 || lineEndX==-999 || lineStartX==-999 || lineStartX==-99))
			{
				paint.setColor(Color.rgb(44, 69, 99));
				paint.setAlpha(110);
				paint.setStrokeWidth(LetterBlock.getSize()/3);
				paint.setDither(true);                 
				paint.setStyle(Paint.Style.STROKE);       
				paint.setStrokeJoin(Paint.Join.ROUND);   
				paint.setStrokeCap(Paint.Cap.ROUND);      
				paint.setAntiAlias(true);

				canvas.drawLine(lineStartX, lineStartY, lineEndX, lineEndY, paint);
			}


			//Draw the buttons
			for (CustomButton button : buttonList)
			{
				button.draw(canvas);
			}

			//Check if the user completed the puzzle and handles one time stuff if user won
			if (blockList.size()>15 && !didWin)
			{
				if (builder.didIWin(blockList))
				{
					didWin = true;
					puzzleSolvedTime = getTimerString(timerStart);

					dataArrayList = dataHandler.readFromFile(getContext());

					if (difficultyLevel==1)
					{
						timeHighScore = dataArrayList.get(0);
						dataArrayList.set(3, Integer.toString((Integer.parseInt(dataArrayList.get(3))+1))); //Set number of easy puzzles solved to +1
					}
					else if (difficultyLevel==2)
					{
						timeHighScore = dataArrayList.get(1);
						dataArrayList.set(4, Integer.toString((Integer.parseInt(dataArrayList.get(4))+1))); //Set number of hard puzzles solved to +1
					}
					else if (difficultyLevel==3)
					{
						timeHighScore = dataArrayList.get(2);
						dataArrayList.set(5, Integer.toString((Integer.parseInt(dataArrayList.get(5))+1))); //Set number of expert puzzles solved to +1
					}

					//If new highscore was gotten, update the highscore
					if (findMinTime(timeHighScore, puzzleSolvedTime).compareTo(timeHighScore)!=0)
					{
						gotNewHighScore = true;
						timeHighScore = puzzleSolvedTime;

						if (difficultyLevel==1)
							dataArrayList.set(0, timeHighScore);
						else if (difficultyLevel==2)
							dataArrayList.set(1, timeHighScore);
						else if (difficultyLevel==3)
							dataArrayList.set(2, timeHighScore);

					}

					dataHandler.writeToFile(dataArrayList, getContext());

				}
			}


			//Handles infinitely redrawn stuff if the user has won
			if (didWin)
			{
			
				//Draw the rounded rectangle
				paint.setDither(true);                 
				paint.setAntiAlias(true);
				paint.setAlpha(50);
				canvas.drawBitmap(winMenuBitmap, 0, 0, paint);
				paint.setStyle(Paint.Style.FILL);

				//Draw the Circle
				paint.setColor(Color.rgb(44, 69, 99));
				paint.setAlpha(75);
				canvas.drawCircle(size.x/2, size.x/2, size.x/2, paint);

				//Draw the text
				paint.setTextSize(size.x/5);
				paint.setStyle(Paint.Style.FILL);
				paint.setTextAlign(Align.CENTER);
				paint.setColor(Color.WHITE);
				paint.setFakeBoldText(true);
				paint.setAlpha(180);
				canvas.drawText("YOU WIN!", size.x/2, size.x/2+paint.getTextSize()/4, paint);

				paint.setTextSize(size.x/10);				
				if (!gotNewHighScore)
					canvas.drawText("High Score: "+timeHighScore, size.x/2, size.x-size.x/3, paint);
				else
				{
					canvas.drawText("*High Score: "+timeHighScore, size.x/2, size.x-size.x/3, paint);

					//Draw three rotating stars
					matrix.reset();
					rotationAngle = (rotationAngle+.5f)%360;
					matrix.postRotate(rotationAngle+=.5f, starBitmap.getWidth()/2, starBitmap.getHeight()/2);
					matrix.postTranslate((size.x/2)-(starBitmap.getWidth()/2), (size.x-size.x/4));
					canvas.drawBitmap(starBitmap, matrix, paint);

					matrix.reset();
					rotationAngle = (rotationAngle+.5f)%360;
					matrix.postRotate(rotationAngle+=.5f, starBitmap.getWidth()/2, starBitmap.getHeight()/2);
					matrix.postTranslate((size.x/2)-(starBitmap.getWidth()/2)+(starBitmap.getWidth()+starBitmap.getWidth()/2), size.x-size.x/4);
					canvas.drawBitmap(starBitmap, matrix, paint);

					matrix.reset();
					rotationAngle = (rotationAngle+.5f)%360;
					matrix.postRotate(rotationAngle+=.5f, starBitmap.getWidth()/2, starBitmap.getHeight()/2);
					matrix.postTranslate((size.x/2)-(starBitmap.getWidth()/2)-(starBitmap.getWidth()+starBitmap.getWidth()/2), size.x-size.x/4);
					canvas.drawBitmap(starBitmap, matrix, paint);
				}


			}

			invalidate();
		}
	}

	public String getTimerString(long startTime)
	{
		if(startTimer)
		{
			long currentTime = System.nanoTime();
			int elapsedSeconds = (int)((currentTime-startTime)/1000000000.0);

			int hours = (int)(elapsedSeconds/3600);
			int minutes = (int)((elapsedSeconds-(hours*3600))/60);
			int seconds	= (int)((elapsedSeconds-(hours*3600))-(minutes*60));


			String timer = Integer.toString(hours).length()>=2?Integer.toString(hours):("0"+Integer.toString(hours))+":"+(Integer.toString(minutes).length()>=2?Integer.toString(minutes):("0"+Integer.toString(minutes)))+":"+(Integer.toString(seconds).length()>=2?Integer.toString(seconds):("0"+Integer.toString(seconds)));
			return timer;
		}
		else
			return "00:00:00";
	}


	public void drawHighlightMatches(ArrayList<LetterBlock> blockList, Canvas canvas)
	{		
		if (blockList.size()>14)
		{
			paint.setColor(Color.WHITE);
			
			paint.setAlpha(80);
			paint.setStrokeWidth(LetterBlock.getSize()/3);
			paint.setDither(true);                 
			paint.setStrokeJoin(Paint.Join.ROUND);   
			paint.setStrokeCap(Paint.Cap.ROUND);      
			paint.setAntiAlias(true);

			if (blockList.get(0).getHighlightColumn())
			{
				paint.setStyle(Paint.Style.FILL);       
				canvas.drawCircle(blockList.get(0).getX()+(LetterBlock.getSize()/2), blockList.get(0).getY()+(LetterBlock.getSize()/2), (LetterBlock.getSize()/3), paint);
				canvas.drawCircle(blockList.get(0).getX()+(LetterBlock.getSize()/2), blockList.get(0).getY()+7*(LetterBlock.getSize()/2), (LetterBlock.getSize()/3), paint);
				paint.setStyle(Paint.Style.STROKE);       
				canvas.drawLine(blockList.get(0).getX()+(LetterBlock.getSize()/2), blockList.get(0).getY()+(LetterBlock.getSize()/2), blockList.get(0).getX()+(LetterBlock.getSize()/2), blockList.get(0).getY()+7*(LetterBlock.getSize()/2), paint);
			}
			if (blockList.get(4).getHighlightColumn())
			{
				paint.setStyle(Paint.Style.FILL);       
				canvas.drawCircle(blockList.get(4).getX()+(LetterBlock.getSize()/2), blockList.get(4).getY()+(LetterBlock.getSize()/2), (LetterBlock.getSize()/3), paint);
				canvas.drawCircle(blockList.get(4).getX()+(LetterBlock.getSize()/2), blockList.get(4).getY()+7*(LetterBlock.getSize()/2), (LetterBlock.getSize()/3), paint);
				paint.setStyle(Paint.Style.STROKE);       
				canvas.drawLine(blockList.get(4).getX()+(LetterBlock.getSize()/2), blockList.get(4).getY()+(LetterBlock.getSize()/2), blockList.get(4).getX()+(LetterBlock.getSize()/2), blockList.get(4).getY()+7*(LetterBlock.getSize()/2), paint);
			}
			if (blockList.get(8).getHighlightColumn())
			{
				paint.setStyle(Paint.Style.FILL);       
				canvas.drawCircle(blockList.get(8).getX()+(LetterBlock.getSize()/2), blockList.get(8).getY()+(LetterBlock.getSize()/2), (LetterBlock.getSize()/3), paint);
				canvas.drawCircle(blockList.get(8).getX()+(LetterBlock.getSize()/2), blockList.get(8).getY()+7*(LetterBlock.getSize()/2), (LetterBlock.getSize()/3), paint);
				paint.setStyle(Paint.Style.STROKE);       
				canvas.drawLine(blockList.get(8).getX()+(LetterBlock.getSize()/2), blockList.get(8).getY()+(LetterBlock.getSize()/2), blockList.get(8).getX()+(LetterBlock.getSize()/2), blockList.get(8).getY()+7*(LetterBlock.getSize()/2), paint);
			}
			if (blockList.get(12).getHighlightColumn())
			{
				paint.setStyle(Paint.Style.FILL);       
				canvas.drawCircle(blockList.get(12).getX()+(LetterBlock.getSize()/2), blockList.get(12).getY()+(LetterBlock.getSize()/2), (LetterBlock.getSize()/3), paint);
				canvas.drawCircle(blockList.get(12).getX()+(LetterBlock.getSize()/2), blockList.get(12).getY()+7*(LetterBlock.getSize()/2), (LetterBlock.getSize()/3), paint);
				paint.setStyle(Paint.Style.STROKE);       
				canvas.drawLine(blockList.get(12).getX()+(LetterBlock.getSize()/2), blockList.get(12).getY()+(LetterBlock.getSize()/2), blockList.get(12).getX()+(LetterBlock.getSize()/2), blockList.get(12).getY()+7*(LetterBlock.getSize()/2), paint);
			}
			if (blockList.get(0).getHighlightRow())
			{
				paint.setStyle(Paint.Style.FILL);       
				canvas.drawCircle(blockList.get(0).getX()+(LetterBlock.getSize()/2), blockList.get(0).getY()+(LetterBlock.getSize()/2), (LetterBlock.getSize()/3), paint);
				canvas.drawCircle(blockList.get(0).getX()+7*(LetterBlock.getSize()/2), blockList.get(0).getY()+(LetterBlock.getSize()/2), (LetterBlock.getSize()/3), paint);
				paint.setStyle(Paint.Style.STROKE);       
				canvas.drawLine(blockList.get(0).getX()+(LetterBlock.getSize()/2), blockList.get(0).getY()+(LetterBlock.getSize()/2), blockList.get(0).getX()+7*(LetterBlock.getSize()/2), blockList.get(0).getY()+(LetterBlock.getSize()/2), paint);
			}
			if (blockList.get(1).getHighlightRow())
			{
				paint.setStyle(Paint.Style.FILL);
				canvas.drawCircle(blockList.get(1).getX()+(LetterBlock.getSize()/2), blockList.get(1).getY()+(LetterBlock.getSize()/2), (LetterBlock.getSize()/3), paint);
				canvas.drawCircle(blockList.get(1).getX()+7*(LetterBlock.getSize()/2), blockList.get(1).getY()+(LetterBlock.getSize()/2), (LetterBlock.getSize()/3), paint);
				paint.setStyle(Paint.Style.STROKE);       
				canvas.drawLine(blockList.get(1).getX()+(LetterBlock.getSize()/2), blockList.get(1).getY()+(LetterBlock.getSize()/2), blockList.get(1).getX()+7*(LetterBlock.getSize()/2), blockList.get(1).getY()+(LetterBlock.getSize()/2), paint);
			}
			if (blockList.get(2).getHighlightRow())
			{
				paint.setStyle(Paint.Style.FILL);
				canvas.drawCircle(blockList.get(2).getX()+(LetterBlock.getSize()/2), blockList.get(2).getY()+(LetterBlock.getSize()/2), (LetterBlock.getSize()/3), paint);
				canvas.drawCircle(blockList.get(2).getX()+7*(LetterBlock.getSize()/2), blockList.get(2).getY()+(LetterBlock.getSize()/2), (LetterBlock.getSize()/3), paint);
				paint.setStyle(Paint.Style.STROKE);       
				canvas.drawLine(blockList.get(2).getX()+(LetterBlock.getSize()/2), blockList.get(2).getY()+(LetterBlock.getSize()/2), blockList.get(2).getX()+7*(LetterBlock.getSize()/2), blockList.get(2).getY()+(LetterBlock.getSize()/2), paint);
			}
			if (blockList.get(3).getHighlightRow())
			{
				paint.setStyle(Paint.Style.FILL);
				canvas.drawCircle(blockList.get(3).getX()+(LetterBlock.getSize()/2), blockList.get(3).getY()+(LetterBlock.getSize()/2), (LetterBlock.getSize()/3), paint);
				canvas.drawCircle(blockList.get(3).getX()+7*(LetterBlock.getSize()/2), blockList.get(3).getY()+(LetterBlock.getSize()/2), (LetterBlock.getSize()/3), paint);
				paint.setStyle(Paint.Style.STROKE);       
				canvas.drawLine(blockList.get(3).getX()+(LetterBlock.getSize()/2), blockList.get(3).getY()+(LetterBlock.getSize()/2), blockList.get(3).getX()+7*(LetterBlock.getSize()/2), blockList.get(3).getY()+(LetterBlock.getSize()/2), paint);
			}
		}
	}



	public void getNewPuzzle()
	{
		didWin = false;
		gotNewHighScore = false;
		rotationAngle = 0;

		InputStream inStream1 = null;
		InputStream inStream2 = null;
		BufferedReader chooseReader = null;
		BufferedReader checkReader = null;

		//Start the timer
		timerStart = System.nanoTime();
		startTimer = true;

		//TODO On click stuffs
		try 
		{        	
			inStream1 = assetManager.open("chooseList.txt");
			chooseReader = new BufferedReader(new InputStreamReader(inStream1));

			inStream2 = assetManager.open("checkList.txt");
			checkReader = new BufferedReader(new InputStreamReader(inStream2));

			builder = new SolutionBuilder(chooseReader, checkReader);
			solutionGrid = builder.getGridSolution();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally
		{
			try {
				checkReader.close();
				chooseReader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		blockList.clear();    //Remove all blocks in list



		for (int i=0; i<4; i++)
		{
			for (int j=0; j<4; j++)
			{
				blockList.add(new LetterBlock(solutionGrid[i][j], i*LetterBlock.getSize(), j*LetterBlock.getSize()));
			}
		}


		int shuffleAttempts = 0;
		//Shuffle up the grid
		do
		{
			shuffledGrid = builder.shuffleGrid(solutionGrid, difficultyLevel);

			blockList.clear();

			for (int i=0; i<4; i++)
			{
				for (int j=0; j<4; j++)
				{
					blockList.add(new LetterBlock(shuffledGrid[i][j], i*LetterBlock.getSize(), j*LetterBlock.getSize()));
				}
			}

			//Limit amount of shuffles so that an un-shuffelable solution grid will be replaced with a new solution grid instead of entering an infinite loop.
			if(++shuffleAttempts > 20)
			{
				getNewPuzzle();
			}

		} while(builder.highlightWordMatches(blockList));


		switch(difficultyLevel)
		{
		case 1:
			//Lock the blocks for the easy setting
			blockList.get(0).setShouldLock(true);
			blockList.get(3).setShouldLock(true);
			blockList.get(5).setShouldLock(true);
			blockList.get(6).setShouldLock(true);
			blockList.get(9).setShouldLock(true);
			blockList.get(10).setShouldLock(true);
			blockList.get(12).setShouldLock(true);
			blockList.get(15).setShouldLock(true);
			break;
		case 2:
			//Lock the blocks for the hard setting
			blockList.get(0).setShouldLock(true);
			blockList.get(3).setShouldLock(true);
			blockList.get(12).setShouldLock(true);
			blockList.get(15).setShouldLock(true);
			break;
		case 3:
			//Don't lock any for expert mode
			break;
		default:
			break;
		}	

	}

	//Returns the minimum time of two times in the format HH:MM:SS
	public static String findMinTime(String time1, String time2)
	{
		int hours1;
		int minutes1;
		int seconds1;
		int hours2;
		int minutes2;
		int seconds2;


		Scanner scanner1 = new Scanner(time1).useDelimiter(":");
		Scanner scanner2 = new Scanner(time2).useDelimiter(":");

		if (time1.equals("--:--:--"))
		{
			hours1 = 9999;
			minutes1 = 9999;
			seconds1 = 9999;
		} else
		{
			hours1 = scanner1.nextInt();
			minutes1 = scanner1.nextInt();
			seconds1 = scanner1.nextInt();
		}

		if (time2.equals("--:--:--"))
		{
			hours2 = 9999;
			minutes2 = 9999;
			seconds2 = 9999;
		} else
		{
			hours2 = scanner2.nextInt();
			minutes2 = scanner2.nextInt();
			seconds2 = scanner2.nextInt();
		}

		if (hours1>hours2)
		{
			return time2;
		}
		else if (hours1<hours2)
		{
			return time1;
		}
		else
		{
			if (minutes1>minutes2)
			{
				return time2;
			}
			else if (minutes1<minutes2)
			{
				return time1;
			}
			else
			{
				if (seconds1>seconds2)
				{
					return time2;
				}
				else if (seconds1<seconds2)
				{
					return time1;
				}
				else
				{
					return time1;
				}
			}
		}
	}
	
	
	public void handleButtonPress(CustomButton button)
	{
		if (button == buttonList.get(0))
		{
			if (!didWin)
			{
				if(shuffledGrid!=null)
				{
					blockList.clear();

					for (int i=0; i<4; i++)
					{
						for (int j=0; j<4; j++)
						{
							blockList.add(new LetterBlock(shuffledGrid[i][j], i*LetterBlock.getSize(), j*LetterBlock.getSize()));
						}
					}

					//Locks appropriate blocks according to what level of difficulty was chosen
					switch(difficultyLevel)
					{
					case 1:
						//Lock the blocks for the easy setting
						blockList.get(0).setShouldLock(true);
						blockList.get(3).setShouldLock(true);
						blockList.get(5).setShouldLock(true);
						blockList.get(6).setShouldLock(true);
						blockList.get(9).setShouldLock(true);
						blockList.get(10).setShouldLock(true);
						blockList.get(12).setShouldLock(true);
						blockList.get(15).setShouldLock(true);
						break;
					case 2:
						//Lock the blocks for the hard setting
						blockList.get(0).setShouldLock(true);
						blockList.get(3).setShouldLock(true);
						blockList.get(12).setShouldLock(true);
						blockList.get(15).setShouldLock(true);
						break;
					case 3:
						//Don't lock any for expert mode
						break;
					default:
						break;
					}
				}
			}
		} else if (button == buttonList.get(1))
		{
			//TODO ask if user is sure they want a new board

			//Assign a new puzzle to the playing area
			getNewPuzzle();
		}else if (button == buttonList.get(2))
		{
			startActivity(mainMenuIntent);
		} else if (button == buttonList.get(3))
		{
			//TODO Handle a pause event
		}

	}

}
