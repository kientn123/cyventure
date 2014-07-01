package com.kiennguyen.facebookapp;

import java.util.ArrayList;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class ShowGamesWeCreated extends MainFragment implements View.OnClickListener{

	private TabHost mTabHost;
	private TextView gameName;
	private TextView gameNote;
	private TextView admin;
	private ListView playerList;
	private Button inviteFriends;
	private Button cancel;
	public static String name;
	private String notes;
	private ArrayList<String> cluesString = new ArrayList<String>();
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.show_games_we_created,
				container, false);
		mTabHost = (TabHost) rootView.findViewById(android.R.id.tabhost);
		gameName = (TextView) rootView.findViewById(R.id.game_name_show_game_we_created);
		gameNote = (TextView) rootView.findViewById(R.id.notes_show_game_we_created);
		admin = (TextView) rootView.findViewById(R.id.admin_show_game_we_created);
		inviteFriends = (Button) rootView.findViewById(R.id.invite_friends_show_game_we_created);
		cancel = (Button) rootView.findViewById(R.id.cancel_show_game_we_created);
		playerList = (ListView) rootView.findViewById(R.id.list_players_show_game_we_created);
		// Setup the tabs
		mTabHost.setup();
		// Make a tab called Details
		TabSpec details = mTabHost.newTabSpec("tab1");
		details.setContent(R.id.details_show_game_we_created);
		details.setIndicator("Details");
		// Make another tap called Players
		TabSpec playerSpec = mTabHost.newTabSpec("tab2");
		playerSpec.setContent(R.id.players_show_game_we_created);
		playerSpec.setIndicator("Players");
		// Add these two tabs to the TabHost
		mTabHost.addTab(details);
		mTabHost.addTab(playerSpec);
		// Set the name of the game
		gameName.setText("Name: " + name);
		Firebase fb = new Firebase("https://cyventure-test.firebaseio.com/");
		ValueEventListener vel = new ValueEventListener() {
			
			@Override
			public void onDataChange(DataSnapshot data) {
				notes = data.child("games").child(name).child("notes").getValue().toString();
				gameNote.setText("Notes: " + notes);
				
				String id = data.child("games").child(name)
						.child("admin").getValue().toString();
				
				admin.setText("Admin: " + data.child("users").child(id)
						.child("name").getValue().toString());
		        
		        ArrayList<String> playersString = new ArrayList<String>();
		        for (DataSnapshot player: data.child("games").child(name)
		        		.child("players").getChildren()) {
		        	playersString.add(data.child("users")
		        			.child(player.getName())
		        			.child("name").getValue().toString());
		        }
		        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(),
		        		android.R.layout.simple_list_item_1, playersString);
		        playerList.setAdapter(adapter2);
			}
			
			@Override
			public void onCancelled(FirebaseError arg0) {}
		};
		fb.addListenerForSingleValueEvent(vel);
		
		inviteFriends.setOnClickListener(this);
		cancel.setOnClickListener(this);
		
		return rootView;
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()) 
		{
		case R.id.invite_friends_show_game_we_created:
			Fragment f0 = new ShowFriendListToPick();
			FragmentManager manager0 = getFragmentManager();
			manager0.beginTransaction().replace(R.id.content_frame, f0).commit();
			break;
		case R.id.cancel_show_game_we_created:
			break;
		default:
			break;
		}
		
	}

}
