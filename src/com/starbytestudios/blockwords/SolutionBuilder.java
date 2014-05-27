package com.starbytestudios.blockwords;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;


public class SolutionBuilder
{
	ArrayList<String> chooseWordArrayList;
	ArrayList<String> checkWordArrayList;

	char[][] wordGrid = new char[4][4];
	List<List<String>> dividedWordList;
	BufferedReader chooseListReader;
	BufferedReader checkListReader;
	Random random = new Random();

	int index=-1, index1=-1, index2=-1, index3=-1, index4=-1, index5=-1, index6=-1, index7=-1; //Used in setWordGrid
	String word = null, word1 = null, word2 = null, word3 = null, word4 = null, word5 = null, word6 = null, word7 = null; //Used in setWordGrid
	int attempts = 0; //Used in setWordGrid

	public SolutionBuilder(BufferedReader chooseReader, BufferedReader checkReader)
	{
		chooseListReader = chooseReader;
		checkListReader = checkReader;
	}

	public char[][] getGridSolution()
	{
		chooseWordArrayList = getWordArrayList(chooseListReader);    //Gets the word array containing all four letter words
		checkWordArrayList = getWordArrayList(checkListReader);
		dividedWordList = getDividedWordArrayList();    //Each row corresponds to a List of four letter words with the same starting letter.

		int stage = 1;
		while((stage = setWordGrid(stage))!=0);

		return wordGrid;

	}



	public int setWordGrid(int stage)
	{
		final int ATTEMPT_LIMIT = 1300;
		
		switch(stage)
		{
		case 1:

			/**
			 * Stage One 
			 */
			
			word = chooseWordArrayList.get(random.nextInt(chooseWordArrayList.size()));

			int randomIndex = random.nextInt(26);
			index = random.nextInt(dividedWordList.get(randomIndex).size());
			word = dividedWordList.get(randomIndex).get(index); 

			for(int j=0; j<4; j++)
			{
				wordGrid[0][j] = word.charAt(j);
			}

		case 2:

			/**
			 * Stage Two 
			 */

			index1 = random.nextInt(dividedWordList.get(((int)wordGrid[0][0])-97).size());

			word1 = dividedWordList.get(((int)wordGrid[0][0])-97).get(index1);
			
			//Try to reduce chance of duplicate words
			if(word1.equals(word) && attempts<ATTEMPT_LIMIT)
			{
				attempts++;
				return 1;
			}


			for(int i=0; i<4; i++)
			{
				wordGrid[i][0] = word1.charAt(i);
			}

		case 3:

			/**
			 * Stage Three 
			 */

			index2 = random.nextInt(dividedWordList.get(((int)wordGrid[0][1])-97).size());
			if (index2==-1)
			{
				//Backtrack
				return 1;   
			}
			
			word2 = dividedWordList.get(((int)wordGrid[0][1])-97).get(index2);


			for(int i=0; i<4; i++)
			{
				wordGrid[i][1] = word2.charAt(i);
			}

		case 4:

			/**
			 * Stage Four 
			 */
			
			index3 = binarySearch(Character.toString(wordGrid[1][0])+Character.toString(wordGrid[1][1]), chooseWordArrayList, index3+1, chooseWordArrayList.size()-1);

			if (index3==-1)
			{
				//Backtrack
				return 1;   
			}

			word3 = chooseWordArrayList.get(index3);
			
			//Try to reduce chance of duplicate words
			if(word3.compareTo(word2)==0 && attempts<ATTEMPT_LIMIT)
			{
				attempts++;
				return 3;
			}
			
			for(int i=0; i<4; i++)
			{
				wordGrid[1][i] = word3.charAt(i);
			}

		case 5:

			/**
			 * Stage Five 
			 */

			index4 = binarySearch(Character.toString(wordGrid[0][2])+Character.toString(wordGrid[1][2]), chooseWordArrayList, index4+1, chooseWordArrayList.size()-1);

			if (index4==-1)
			{
				//Backtrack
				return 4;   
			}

			word4 = chooseWordArrayList.get(index4);
			for(int i=0; i<4; i++)
			{
				wordGrid[i][2] = word4.charAt(i);
			}

		case 6:

			/**
			 * Stage Six 
			 */

			index5 = binarySearch(Character.toString(wordGrid[2][0])+Character.toString(wordGrid[2][1])+Character.toString(wordGrid[2][2]), chooseWordArrayList, index5+1, chooseWordArrayList.size()-1);

			if (index5==-1)
			{
				//Backtrack
				return 5;   
			}

			word5 = chooseWordArrayList.get(index5);
			for(int i=0; i<4; i++)
			{
				wordGrid[2][i] = word5.charAt(i);
			}

		case 7:

			/**
			 * Stage Seven 
			 */

			index6 = binarySearch(Character.toString(wordGrid[0][3])+Character.toString(wordGrid[1][3])+Character.toString(wordGrid[2][3]), chooseWordArrayList, index6+1, chooseWordArrayList.size()-1);

			if (index6==-1)
			{
				//Backtrack
				return 6;   
			}

			word6 = chooseWordArrayList.get(index6);
			for(int i=0; i<4; i++)
			{
				wordGrid[i][3] = word6.charAt(i);
			}

		case 8:

			/**
			 * Stage Eight
			 * Check if bottom row is actual word
			 */
			
			index7 = binarySearch(Character.toString(wordGrid[3][0])+Character.toString(wordGrid[3][1])+Character.toString(wordGrid[3][2])+Character.toString(wordGrid[3][3]), chooseWordArrayList, 0, chooseWordArrayList.size()-1);

			if (index7==-1)
			{
				//Backtrack
				return 7;   
			}

			word7 = chooseWordArrayList.get(index7);
			for(int i=0; i<4; i++)
			{
				wordGrid[3][i] = word7.charAt(i);
			}

			/*
			//Test to see number of attempts used
			for(int i=0; i<Integer.toString(attempts).length(); i++)
			{
				wordGrid[3][i] = Integer.toString(attempts).charAt(i);
			}
			*/
			
			break;

		default:
			System.out.println("Switch statement error.");
			break;

		}

		//Successfully found solution grid, reset values and return 0.
		word = null;
		word1 = null;
		word2 = null;
		word3 = null;
		word4 = null;
		word5 = null;
		word6 = null;
		word7 = null;
		attempts = 0;
		return 0;
	}


	public ArrayList<String> getWordArrayList(BufferedReader reader)
	{
		ArrayList<String> wordArrayList = new ArrayList<String>();

		try{                        
			String line;

			while ((line = reader.readLine()) != null) 
			{
				wordArrayList.add(line);
			}

		}catch (FileNotFoundException d){
			System.out.println("File 'input.txt' could not be found!\nPlease make sure 'input.txt' is in the current directory before running this program.\n");
		}catch (IOException d){
			System.out.println("An I/O error occured!\n"); 
		}catch (Exception d){
			System.out.println("An Unexpected error occurred!\n"); 
			d.printStackTrace();
		}   

		return wordArrayList;
	}



	public List<List<String>> getDividedWordArrayList()
	{
		List<List<String>> dividedWordArray = new ArrayList<List<String>>();

		try{
			String line;

			char startLetter = 'a';

			List<String> tempList = new LinkedList<String>();

			while ((line = chooseListReader.readLine()) != null) 
			{
				if (line.charAt(0)==startLetter)
					tempList.add(line);
				else
				{
					++startLetter;
					dividedWordArray.add(tempList);
					tempList = new LinkedList<String>();
					tempList.add(line);
				}
			}



			for (int i=0; i<chooseWordArrayList.size(); i++)
			{
				if (chooseWordArrayList.get(i).charAt(0)==startLetter)
					tempList.add(chooseWordArrayList.get(i));
				else
				{
					++startLetter;
					dividedWordArray.add(tempList);
					tempList = new LinkedList<String>();
					tempList.add(chooseWordArrayList.get(i));
				}
			}



			dividedWordArray.add(tempList);

		}catch (FileNotFoundException d){
			System.out.println("File 'input.txt' could not be found!\nPlease make sure 'input.txt' is in the current directory before running this program.\n");
		}catch (IOException d){
			System.out.println("An I/O error occured!\n"); 
		}catch (Exception d){
			System.out.println("An Unexpected error occurred!\n"); 
			d.printStackTrace();
		}   

		return dividedWordArray;
	}

	
	/**
	 * Returns the index of the first key match in the array or -1 if none
	 */
	public int binarySearch(String key, ArrayList<String> arrayList, int minIndex, int maxIndex)
	{
		int midIndex;		
		while (maxIndex >= minIndex)
		{
			midIndex = (minIndex & maxIndex) + ((minIndex ^ maxIndex) >> 1);
			
			if (arrayList.get(midIndex).startsWith(key))
				return midIndex;
			else if (arrayList.get(midIndex).compareTo(key) < 0)
				minIndex = midIndex + 1;
			else if (arrayList.get(midIndex).compareTo(key) > 0)
				maxIndex = midIndex - 1;
			else
				return midIndex;
		}
		return -1;
	}


	public Boolean highlightWordMatches(ArrayList<LetterBlock> blockList)
	{
		String word;
		Boolean didHighlight = false;

		for (LetterBlock block : blockList)
		{
			block.setHighlightColumn(false);
			block.setHighlightRow(false);
		}

		word = Character.toString(blockList.get(0).getLetter())+Character.toString(blockList.get(1).getLetter())+Character.toString(blockList.get(2).getLetter())+Character.toString(blockList.get(3).getLetter());
		if (binarySearch(word, checkWordArrayList, 0, checkWordArrayList.size()-1) != -1)
		{
			blockList.get(0).setHighlightColumn(true);	
			didHighlight = true;
		}

		word = Character.toString(blockList.get(4).getLetter())+Character.toString(blockList.get(5).getLetter())+Character.toString(blockList.get(6).getLetter())+Character.toString(blockList.get(7).getLetter());
		if (binarySearch(word, checkWordArrayList, 0, checkWordArrayList.size()-1) != -1)
		{
			blockList.get(4).setHighlightColumn(true);
			didHighlight = true;
		}

		word = Character.toString(blockList.get(8).getLetter())+Character.toString(blockList.get(9).getLetter())+Character.toString(blockList.get(10).getLetter())+Character.toString(blockList.get(11).getLetter());
		if (binarySearch(word, checkWordArrayList, 0, checkWordArrayList.size()-1) != -1)
		{
			blockList.get(8).setHighlightColumn(true);
			didHighlight = true;
		}

		word = Character.toString(blockList.get(12).getLetter())+Character.toString(blockList.get(13).getLetter())+Character.toString(blockList.get(14).getLetter())+Character.toString(blockList.get(15).getLetter());
		if (binarySearch(word, checkWordArrayList, 0, checkWordArrayList.size()-1) != -1)
		{
			blockList.get(12).setHighlightColumn(true);
			didHighlight = true;
		}

		word = Character.toString(blockList.get(0).getLetter())+Character.toString(blockList.get(4).getLetter())+Character.toString(blockList.get(8).getLetter())+Character.toString(blockList.get(12).getLetter());
		if (binarySearch(word, checkWordArrayList, 0, checkWordArrayList.size()-1) != -1)
		{
			blockList.get(0).setHighlightRow(true);
			didHighlight = true;
		}

		word = Character.toString(blockList.get(1).getLetter())+Character.toString(blockList.get(5).getLetter())+Character.toString(blockList.get(9).getLetter())+Character.toString(blockList.get(13).getLetter());
		if (binarySearch(word, checkWordArrayList, 0, checkWordArrayList.size()-1) != -1)
		{
			blockList.get(1).setHighlightRow(true);
			didHighlight = true;
		}

		word = Character.toString(blockList.get(2).getLetter())+Character.toString(blockList.get(6).getLetter())+Character.toString(blockList.get(10).getLetter())+Character.toString(blockList.get(14).getLetter());
		if (binarySearch(word, checkWordArrayList, 0, checkWordArrayList.size()-1) != -1)
		{
			blockList.get(2).setHighlightRow(true);
			didHighlight = true;
		}

		word = Character.toString(blockList.get(3).getLetter())+Character.toString(blockList.get(7).getLetter())+Character.toString(blockList.get(11).getLetter())+Character.toString(blockList.get(15).getLetter());
		if (binarySearch(word, checkWordArrayList, 0, checkWordArrayList.size()-1) != -1)
		{
			blockList.get(3).setHighlightRow(true);
			didHighlight = true;
		}

		return didHighlight;
	}



	public char[][] shuffleGrid(char[][] grid, int mode)
	{
		int randomNum1;
		int randomNum2;
		char char1;
		char char2;
		
		//1 is Easy mode
		//2 is Hard mode
		//3 is Expert mode
		
		
		Random random = new Random();

		for (int i=0; i<4; i++)
		{
			for (int j=0; j<4; j++)
			{
				
				if(mode == 1 &&!((i==0&&j==0) || (i==0&&j==3) || (i==1&&j==1) || (i==1&&j==2) || (i==2&&j==1) || (i==2&&j==2) || (i==3&&j==0) || (i==3&&j==3)))
				{
					do
					{
						randomNum1 = random.nextInt(4);
						randomNum2 = random.nextInt(4);
					} while((randomNum1==0&&randomNum2==0) || (randomNum1==0&&randomNum2==3) || (randomNum1==1&&randomNum2==1) || (randomNum1==1&&randomNum2==2) || (randomNum1==2&&randomNum2==1) || (randomNum1==2&&randomNum2==2) || (randomNum1==3&&randomNum2==0) || (randomNum1==3&&randomNum2==3));
					
					char1 = grid[i][j];
					char2 = grid[randomNum1][randomNum2];
					grid[i][j] = char2;
					grid[randomNum1][randomNum2] = char1;
				} else if (mode == 2 &&!((i==0&&j==0) || (i==0&&j==3) || (i==3&&j==0) || (i==3&&j==3)))
				{
					do
					{
						randomNum1 = random.nextInt(4);
						randomNum2 = random.nextInt(4);
					} while((randomNum1==0&&randomNum2==0) || (randomNum1==0&&randomNum2==3) || (randomNum1==3&&randomNum2==0) || (randomNum1==3&&randomNum2==3));
					
					char1 = grid[i][j];
					char2 = grid[randomNum1][randomNum2];
					grid[i][j] = char2;
					grid[randomNum1][randomNum2] = char1;
				} else if (mode == 3)
				{
					randomNum1 = random.nextInt(4);
					randomNum2 = random.nextInt(4);

					char1 = grid[i][j];
					char2 = grid[randomNum1][randomNum2];
					grid[i][j] = char2;
					grid[randomNum1][randomNum2] = char1;
				}
				
			}
		}

		return grid;
	}
	
	public boolean didIWin(ArrayList<LetterBlock> blockList)
	{
		String word;
		int highlightedWordsNum = 0;

		word = Character.toString(blockList.get(0).getLetter())+Character.toString(blockList.get(1).getLetter())+Character.toString(blockList.get(2).getLetter())+Character.toString(blockList.get(3).getLetter());
		if (binarySearch(word, checkWordArrayList, 0, checkWordArrayList.size()-1) != -1)
		{
			highlightedWordsNum++;
		}

		word = Character.toString(blockList.get(4).getLetter())+Character.toString(blockList.get(5).getLetter())+Character.toString(blockList.get(6).getLetter())+Character.toString(blockList.get(7).getLetter());
		if (binarySearch(word, checkWordArrayList, 0, checkWordArrayList.size()-1) != -1)
		{
			highlightedWordsNum++;
		}

		word = Character.toString(blockList.get(8).getLetter())+Character.toString(blockList.get(9).getLetter())+Character.toString(blockList.get(10).getLetter())+Character.toString(blockList.get(11).getLetter());
		if (binarySearch(word, checkWordArrayList, 0, checkWordArrayList.size()-1) != -1)
		{
			highlightedWordsNum++;
		}

		word = Character.toString(blockList.get(12).getLetter())+Character.toString(blockList.get(13).getLetter())+Character.toString(blockList.get(14).getLetter())+Character.toString(blockList.get(15).getLetter());
		if (binarySearch(word, checkWordArrayList, 0, checkWordArrayList.size()-1) != -1)
		{
			highlightedWordsNum++;
		}

		word = Character.toString(blockList.get(0).getLetter())+Character.toString(blockList.get(4).getLetter())+Character.toString(blockList.get(8).getLetter())+Character.toString(blockList.get(12).getLetter());
		if (binarySearch(word, checkWordArrayList, 0, checkWordArrayList.size()-1) != -1)
		{
			highlightedWordsNum++;
		}

		word = Character.toString(blockList.get(1).getLetter())+Character.toString(blockList.get(5).getLetter())+Character.toString(blockList.get(9).getLetter())+Character.toString(blockList.get(13).getLetter());
		if (binarySearch(word, checkWordArrayList, 0, checkWordArrayList.size()-1) != -1)
		{
			highlightedWordsNum++;
		}

		word = Character.toString(blockList.get(2).getLetter())+Character.toString(blockList.get(6).getLetter())+Character.toString(blockList.get(10).getLetter())+Character.toString(blockList.get(14).getLetter());
		if (binarySearch(word, checkWordArrayList, 0, checkWordArrayList.size()-1) != -1)
		{
			highlightedWordsNum++;
		}

		word = Character.toString(blockList.get(3).getLetter())+Character.toString(blockList.get(7).getLetter())+Character.toString(blockList.get(11).getLetter())+Character.toString(blockList.get(15).getLetter());
		if (binarySearch(word, checkWordArrayList, 0, checkWordArrayList.size()-1) != -1)
		{
			highlightedWordsNum++;
		}

		return (highlightedWordsNum==8);	
	}

}