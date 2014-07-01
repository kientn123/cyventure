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
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

public class ViewGameFromJoinGame extends MainFragment
implements View.OnClickListener {
	
	private TextView name;
	private TextView admin;
	private TextView total;
	private Button join;
	private Button cancel;
	private ListView listView;
	private TabHost mTabHost;
	private Firebase fb = new Firebase("https://cyventure-test.firebaseio.com/");
	public static String gameName;
	private ArrayList<String> players = new ArrayList<String>();
	
	public View onCreateView( LayoutInflater inflater, ViewGroup container, 
    		Bundle savedInstanceState ) 
	{
		View rootView = inflater.inflate( R.layout.view_game_from_join_game,
				container, false );
		
		name = (TextView) rootView.findViewById(R.id.name_view_g_from_join_g);
		admin = (TextView) rootView.findViewById(R.id.admin_view_g_from_join_g);
		total = (TextView) rootView.findViewById(R.id.total_view_g_from_join_g);
		join = (Button) rootView.findViewById(R.id.join_view_g_from_join_g);
		cancel = (Button) rootView.findViewById(R.id.cancel_view_g_from_join_g);
		listView = (ListView) rootView
				.findViewById(R.id.listView_view_game_from_join_game);
		
		mTabHost = (TabHost) rootView.findViewById(android.R.id.tabhost);
		mTabHost.setup();
		
		TabSpec details = mTabHost.newTabSpec("tab1");
		details.setContent(R.id.details_view_game_from_join_game);
		details.setIndicator("Details");
		
		TabSpec playerSpec = mTabHost.newTabSpec("tab2");
		playerSpec.setContent(R.id.players_view_game_from_join_game);
		playerSpec.setIndicator("Players");
		
		mTabHost.addTab(details);
		mTabHost.addTab(playerSpec);
		
		ValueEventListener vel = new ValueEventListener() {
			
			@Override
			public void onDataChange(DataSnapshot data) {
				name.setText("Name: " + gameName);
				String adId = data.child("games").child(gameName)
						.child("admin").getValue().toString();
				String ad = data.child("users").child(adId)
						.child("name").getValue().toString();
				admin.setText("Admin: " + ad);
				int totalClues = (int) data.child("games").child(gameName)
						.child("clues").getChildrenCount();
				total.setText("Number of Clues: " + Integer.toString(totalClues));
				
				for (DataSnapshot player: data.child("games")
						.child(gameName).child("players").getChildren()) {
					String playerName = data.child("users").child(player.getName())
							.child("name").getValue().toString();
					players.add(playerName);
				}
				
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), 
						android.R.layout.simple_list_item_1, players);
				listView.setAdapter(adapter);
			}
			
			@Override
			public void onCancelled(FirebaseError arg0) {}
		};
		fb.addListenerForSingleValueEvent(vel);
		
		join.setOnClickListener(this);
		cancel.setOnClickListener(this);
		return rootView;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.join_view_g_from_join_g:
			ValueEventListener vel = new ValueEventListener() {
				
				@Override
				public void onDataChange(DataSnapshot data) {
					int scoreTotalGames = Integer.parseInt(data.child("users")
							.child(Home.userid).child("scores")
							.child("totalGames").getValue().toString());

					fb.child("users")
					.child(Home.userid).child("scores")
					.child("totalGames").setValue(scoreTotalGames + 1);
					
					fb.child("users").child(Home.userid).child("games")
					.child(gameName).child("type").setValue("in");
					
					fb.child("games").child(gameName).child("players")
					.child(Home.userid).child("current").setValue("0");
					
					fb.child("games").child(gameName).child("players")
					.child(Home.userid).child("latitude").setValue("0");
					
					fb.child("games").child(gameName).child("players")
					.child(Home.userid).child("longitude").setValue("0");
					
					fb.child("games").child(gameName).child("players")
					.child(Home.userid).child("numHints").setValue("5");
					
					Fragment f = new GameProfile();
					FragmentManager manager = getFragmentManager();
					GameProfile.gameName = gameName;
					manager.beginTransaction().replace(R.id.content_frame, f).commit();
				}
				
				@Override
				public void onCancelled(FirebaseError arg0) {}
			};
			fb.addListenerForSingleValueEvent(vel);
			break;
		case R.id.cancel_view_g_from_join_g:
			Fragment f1 = new JoinGame();
			FragmentManager manager1 = getFragmentManager();
			manager1.beginTransaction().replace(R.id.content_frame, f1).commit();
			break;
		default:
			break;
		}
	}

}
