package com.kiennguyen.facebookapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;


public class CreateGame extends MainFragment implements View.OnClickListener {

	private Random rand = new Random();
	private Button create;
	private Button cancel;
	public static SeekBar slider;
	private TextView numClues;
	public static EditText gName;
	public static EditText gNote;
	public static String NAME = "name";
	public static String NOTE = "note";
	public static int NUM = 1;
	private ArrayList<Integer> nums = new ArrayList<Integer>();
	public static final String TAG = "CreatGame";
	private Firebase fb = new Firebase("https://cyventure-test.firebaseio.com/");
	private Switch open;
	private boolean isOpen;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		// Layout the view of this fragment
		View rootView = inflater.inflate(R.layout.create_game, container, false);
		// Set the title for the fragment
		getActivity().setTitle("Create Game");

		// Get all the items in the layout 
		create = (Button)rootView.findViewById(R.id.create);
		cancel = (Button)rootView.findViewById(R.id.cancel);
		slider = (SeekBar)rootView.findViewById(R.id.seekBar1);
		numClues = (TextView)rootView.findViewById(R.id.numClues);
		gName = (EditText)rootView.findViewById(R.id.nameEdit);
		gNote = (EditText)rootView.findViewById(R.id.notes);
		open = (Switch) rootView.findViewById(R.id.open);
		
		open.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked)
        			isOpen = true;
        		else
        			isOpen = false;
			}
		});
		
		slider.setMax(Home.numClues); 
		slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
		{
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
			{
				numClues.setText(Integer.toString(progress + 1));
				NUM = (progress + 1);
			}

			public void onStartTrackingTouch(SeekBar seekBar) {}
			public void onStopTrackingTouch(SeekBar seekBar) {}
		});

		// Make these items click-able
		create.setOnClickListener(this);
		cancel.setOnClickListener(this);
		// Return the RootView
		return rootView;
	}

	// Handle all the click events
	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.create:
			// --------------------------- SEND DATA
			NAME = gName.getText().toString();
			NOTE = gNote.getText().toString();
			Map<String, Object> toSet1 = new HashMap<String, Object>();
			toSet1.put("admin", Home.userid);
			toSet1.put("name", NAME);
			toSet1.put("notes", NOTE);
			toSet1.put("totalClues", NUM);
			toSet1.put("winner", "none");
			toSet1.put("open", isOpen);
			fb.child("games").child(NAME).setValue(toSet1);
			int i = 0;
			while(i < NUM)
			{
				int clueIndex = rand.nextInt(Home.numClues) + 1;
				while(nums.contains(clueIndex))
				{
					clueIndex = rand.nextInt(Home.numClues) + 1;
				}
				nums.add(clueIndex);
				i++;
			}
			for (int j=0; j<NUM; j++) {
				fb.child("games").child(NAME).child("clues").child(Integer.toString(j)).setValue(nums.get(j));
			}
			Map<String, Object> toSet2 = new HashMap<String, Object>();
			toSet2.put("type", "admin");
			fb.child("users").child(Home.userid).child("games")
			.child(NAME).setValue(toSet2);

			fb.child( "games" ).child( NAME ).child( "players" )
			.child( Home.userid ).child( "current" ).setValue( "0" );
			fb.child( "games" ).child( NAME ).child( "players" )
			.child( Home.userid ).child( "latitude" ).setValue( "0" );
			fb.child( "games" ).child( NAME ).child( "players" )
			.child( Home.userid ).child( "longitude" ).setValue( "0" );
			fb.child( "games" ).child( NAME ).child( "players" )
			.child( Home.userid ).child( "numHints" ).setValue( "5" );
			fb.child( "games" ).child( NAME ).child( "players" )
			.child( Home.userid ).child( "showHint" ).setValue( "no" );
			
			
			// Update the scores of the user
			ValueEventListener vel = new ValueEventListener() {
				
				@Override
				public void onDataChange(DataSnapshot data) {
					int scoreTotalGames = Integer.parseInt(data.child("users")
							.child(Home.userid).child("scores")
							.child("totalGames").getValue().toString());

					fb.child("users")
					.child(Home.userid).child("scores")
					.child("totalGames").setValue(scoreTotalGames + 1);
					
					Fragment f1 = new ShowGamesWeCreated();
					ShowGamesWeCreated.name = NAME;
					FragmentManager fragmentManager1 = getFragmentManager();
					fragmentManager1.beginTransaction().replace(R.id.content_frame, f1).commit();
				}
				
				@Override
				public void onCancelled(FirebaseError arg0) {}
			};
			fb.addListenerForSingleValueEvent(vel);
			break;
		case R.id.cancel:
			Fragment f2 = new Home();
			FragmentManager fragmentManager2 = getFragmentManager();
			fragmentManager2.beginTransaction().replace(R.id.content_frame, f2).commit();
			break;
		default:
			break;
		}
	}    
}
