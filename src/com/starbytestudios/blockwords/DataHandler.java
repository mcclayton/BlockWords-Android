package com.starbytestudios.blockwords;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;

public class DataHandler 
{
	ArrayList<String> dataList;
	final String FILENAME = "data.txt";

	public DataHandler()
	{
		dataList = new ArrayList<String>();
		dataList.add("--:--:--");	//0 : Best Easy Puzzle Time
		dataList.add("--:--:--");	//1 : Best Hard Puzzle Time
		dataList.add("--:--:--");	//2 : Best Expert Puzzle Time
		dataList.add("0");          //3 : Number Of Easy Puzzles Solved
		dataList.add("0");          //4 : Number Of Hard Puzzles Solved
		dataList.add("0");          //5 : Number Of Expert Puzzles Solved
		dataList.add("0");          //6 : Number Of Total Puzzles Solved
		
		dataList.add("00:00:00");   //7 : Total Time Spent Playing Apps
	}


	public void writeToFile(ArrayList<String> dataArrayList, Context context)
	{
		try
		{
			File file = context.getFileStreamPath(FILENAME);

			if (!file.exists()) {
				file.createNewFile();
			}

			FileOutputStream writer = context.openFileOutput(file.getName(), Context.MODE_PRIVATE);

			for (String string : dataArrayList)
			{
				writer.write(string.getBytes());
				writer.write("\n".getBytes());
				writer.flush();
			}

			writer.close();
		} catch (FileNotFoundException d){
		}catch (IOException d){
		}catch (Exception d){
		}   
	}
	
	
	
	public ArrayList<String> readFromFile(Context context)
	{
		ArrayList<String> dataArrayList = new ArrayList<String>();
		
		try
		{
			File file = context.getFileStreamPath(FILENAME);

			if (file.exists()) 
			{
				String line;
				
			    BufferedReader reader = new BufferedReader(new FileReader(file));
			    while((line = reader.readLine())!=null)
			    {
			    	dataArrayList.add(line);
			    }
			    reader.close();
			} else return dataList;

		} catch (FileNotFoundException d){
		}catch (IOException d){
		}catch (Exception d){
		}  
		
		return dataArrayList;
	}

}

