package com.kiennguyen.facebookapp;


import android.support.v4.app.Fragment;

public abstract class MainFragment extends Fragment {
	
	public int dbMaxGameNum;
	public String selectedGameName;
	public String selectedGameNum;
	//public static int currentGameProgress;
	//public static int currentGameHintProgress;
	protected static int currentGameProgress;
	protected static int currentGameHintProgress;
	public static int totalClues;
	public String username = "lambacam"; //----------------------------------------------
}
