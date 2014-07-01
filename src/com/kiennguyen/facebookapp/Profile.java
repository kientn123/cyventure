package com.kiennguyen.facebookapp;

import java.util.ArrayList;

import com.facebook.widget.ProfilePictureView;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Profile extends MainFragment
{
	private TextView username, gameW, gameP, gameL, tScore, tClue, tDays,
			totalTime;
	private int days;
	private String wons;
	private String plays;
	private String losses;
	private String tClues;
	private ProfilePictureView  profilePictureView;
	
	public ArrayList<String> gameNums = new ArrayList<String>();
	public ArrayList<String> gameNames = new ArrayList<String>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		final View v = inflater.inflate(R.layout.profile, container, false);
		getActivity().setTitle("Profile");
		// Find all the views by ID
		findTextView(v);
		
		// it will access by userId later
		Firebase fb = new Firebase("https://cyventure-test.firebaseio.com/");
		Firebase profRef = fb.child("users").child(Home.userid);
		
		ValueEventListener vel = new ValueEventListener() 
		{
			
			@Override
			public void onDataChange(DataSnapshot data) 
			{
				days = Integer.parseInt(data.child( "scores" ).child("days" )
						.getValue().toString());
				wons = data.child( "scores" ).child("totalWins" )
						.getValue().toString();
				tClues = data.child( "scores" ).child("totalClues" )
						.getValue().toString();
				plays = data.child( "scores" ).child("totalGames" )
						.getValue().toString();
				losses = data.child( "scores" ).child("totalLosses" )
						.getValue().toString();
				gameW.setText(wons);
				gameL.setText(losses);
				tDays.setText(Integer.toString(days));
				tClue.setText(tClues);
				gameP.setText(plays);
				profilePictureView.setProfileId(Home.userid);
				username.setText(Home.username);
			}
			
			@Override
			public void onCancelled(FirebaseError arg0) {}
		};
		profRef.addListenerForSingleValueEvent( vel );
		return v;
	}

	

	/**
	 * find views by each id
	 * @param v
	 */
	public void findTextView(View v)
	{
		username = (TextView) v.findViewById(R.id.name);
		gameW = (TextView) v.findViewById(R.id.gameW);
		gameP = (TextView) v.findViewById(R.id.gameP);
		gameL = (TextView)v.findViewById(R.id.gLosses);
		tClue = (TextView) v.findViewById(R.id.tClue);
		tDays = (TextView) v.findViewById(R.id.days);
		profilePictureView = (ProfilePictureView) v.findViewById(R.id.profile);
	}

	
	public void countDays()
	{
		//get the first day when user join the game update the days
	}
	// I am not sure if this is the way you get info from facebook
	// Get user's data from Facebook server
}