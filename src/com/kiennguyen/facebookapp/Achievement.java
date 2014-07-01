package com.kiennguyen.facebookapp;

import java.util.ArrayList;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Achievement extends MainFragment
{
	private int days;
	private String wons;
	private String plays;
	private String losses;
	private String tClues;

	// this is the list showing on the screen that user finished
	private ArrayList<String> achieves = new ArrayList<String>();
	// the index of achievements that user has finished
	private ArrayList<Integer> indexOfAchievement = new ArrayList<Integer>();
	// all the achievements
	private ArrayList<String> achievements = new ArrayList<String>();

	private ListView viewlist;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View rootView = inflater
				.inflate(R.layout.achievement, container, false);
		getActivity().setTitle("Achievements");
		viewlist = (ListView) rootView
				.findViewById(R.id.listViewForAchievements);

		Firebase achieRef = new Firebase("https://cyventure-test.firebaseio.com/");

		ValueEventListener vel = new ValueEventListener()
		{

			@Override
			public void onDataChange(DataSnapshot data)
			{
				for (int index = 0; index < 17; index++)
				{
					achievements.add(data.child("achievements").child(Integer.toString(index))
							.getValue().toString());
				}
				System.out.println(achievements.toString());

				days = Integer.parseInt(data.child("users").child(Home.userid).child("scores").child("days")
						.getValue().toString());
				wons = data.child("users").child(Home.userid).child("scores").child("totalWins").getValue()
						.toString();
				tClues = data.child("users").child(Home.userid).child("scores").child("totalClues").getValue()
						.toString();
				plays = data.child("users").child(Home.userid).child("scores").child("totalGames").getValue()
						.toString();
				losses = data.child("users").child(Home.userid).child("scores").child("totalLosses").getValue()
						.toString();
				checkAchievement(Integer.parseInt(tClues),
						Integer.parseInt(plays), Integer.parseInt(losses),
						days, Integer.parseInt(wons));
				System.out.println("indexofachiev: " + indexOfAchievement.toString());
				display(indexOfAchievement, achievements);
				System.out.println(achieves.toString());
				
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
						android.R.layout.simple_list_item_1, achieves);
				viewlist.setAdapter(adapter);
			}

			@Override
			public void onCancelled(FirebaseError arg0) {}
		};
		achieRef.addListenerForSingleValueEvent(vel);

		return rootView;

	}

	/**
	 * update index of achievements that user finished
	 * 
	 * @param totalClues
	 *            total clues completed
	 * @param totalGames
	 *            total game played
	 * @param totalLosses
	 *            total game loss
	 * @param totalDays
	 *            total days joined
	 * @param totalWins
	 *            total game wins
	 */
	public void checkAchievement(int totalClues, int totalGames,
			int totalLosses, int totalDays, int totalWins)
	{
		// "5DayPlay", "10DayPlay", "50DayPlay", "10ClueCompleted",
		// "30ClueCompleted", "50ClueCompleted", "70ClueCompleted",
		// "100ClueCompleted", "5GameWin", "10GameWin", "20GameWin",
		// "50GameWin", "100GameWin", "5GameLoss", "15GameLoss", "50GameLoss"

		// for days
		int daysType = totalDays / 5;
		if (daysType == 1)
		{
			indexOfAchievement.add(1);
		}
		else if (daysType == 2)
		{
			indexOfAchievement.add(1);
			indexOfAchievement.add(2);
		}
		else if (daysType == 10)
		{
			indexOfAchievement.add(1);
			indexOfAchievement.add(2);
			indexOfAchievement.add(3);
		}
		else {}

		int clueType = totalClues / 10;
		if (clueType == 1)
		{
			indexOfAchievement.add(4);
		}
		else if (clueType == 3)
		{
			indexOfAchievement.add(4);
			indexOfAchievement.add(5);
		}
		else if (clueType == 5)
		{
			indexOfAchievement.add(4);
			indexOfAchievement.add(5);
			indexOfAchievement.add(6);
		}
		else if (clueType == 7)
		{
			indexOfAchievement.add(4);
			indexOfAchievement.add(5);
			indexOfAchievement.add(6);
			indexOfAchievement.add(7);
		}
		else if (clueType == 10)
		{
			indexOfAchievement.add(4);
			indexOfAchievement.add(5);
			indexOfAchievement.add(6);
			indexOfAchievement.add(7);
			indexOfAchievement.add(8);
		}
		else {}

		// for game wins
		// 5,10,20,50,100 9-13
		int winType = totalWins / 5;
		if (winType == 1)
		{
			indexOfAchievement.add(9);
		}
		else if (winType == 2)
		{
			indexOfAchievement.add(9);
			indexOfAchievement.add(10);
		}
		else if (winType == 4)
		{
			indexOfAchievement.add(9);
			indexOfAchievement.add(10);
			indexOfAchievement.add(11);
		}
		else if (winType == 10)
		{
			indexOfAchievement.add(9);
			indexOfAchievement.add(10);
			indexOfAchievement.add(11);
			indexOfAchievement.add(12);
		}
		else if (winType == 20)
		{
			indexOfAchievement.add(9);
			indexOfAchievement.add(10);
			indexOfAchievement.add(11);
			indexOfAchievement.add(12);
			indexOfAchievement.add(13);
		}
		else {}

		// for loss game
		// 5 , 15, 50 14-16
		int lossType = totalLosses / 5;
		if (lossType == 1)
		{
			indexOfAchievement.add(14);
		}
		else if (lossType == 3)
		{
			indexOfAchievement.add(14);
			indexOfAchievement.add(15);
		}
		else if (lossType == 10)
		{
			indexOfAchievement.add(14);
			indexOfAchievement.add(15);
			indexOfAchievement.add(16);
		}
		else {}
	}

	/**
	 * When user reset achievement
	 */
	public void clean()
	{
		indexOfAchievement = new ArrayList<Integer>();
	}

	/**
	 * What shows on the screen by index of achievements that user finished
	 */
	public void display(ArrayList<Integer> indexOf,ArrayList<String> allA)
	{
		for (Integer i : indexOf)
		{
			achieves.add(allA.get(i));
		}
	}
}
