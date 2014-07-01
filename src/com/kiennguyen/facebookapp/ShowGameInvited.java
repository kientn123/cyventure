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

public class ShowGameInvited extends MainFragment implements 
View.OnClickListener
{
	private TextView name;
	private TextView notes;
	private TextView admin;
	private TextView clues;
	private ListView listView;
	private Button accept;
	private Button deny;
	private Button cancel;
	private TabHost mTabHost;
	public static String gameName;
	private Firebase fb = new Firebase( "https://cyventure-test.firebaseio.com/" );
	
	public View onCreateView( LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState ) 
	{
		View rootView = inflater.inflate( R.layout.show_game_invited, container,
				false );
		name = ( TextView ) rootView.findViewById( R.id.game_name_show_game_invited );
		notes = ( TextView ) rootView.findViewById( R.id.notes_show_game_invited );
		admin = ( TextView ) rootView.findViewById( R.id.admin_show_game_invited );
		clues = ( TextView ) rootView.findViewById( R.id.clues_show_game_invited );
		listView = ( ListView ) 
				rootView.findViewById( R.id.list_players_show_game_invited ); 
		accept = ( Button ) rootView.findViewById( R.id.accept_button );
		deny = ( Button ) rootView.findViewById( R.id.deny_button );
		cancel = ( Button ) rootView.findViewById( R.id.cancel_show_game_invited );
		
		mTabHost = (TabHost) rootView.findViewById(android.R.id.tabhost);
        mTabHost.setup();
        TabSpec details = mTabHost.newTabSpec("tab1");
        details.setContent(R.id.details_show_game_invited);
        details.setIndicator("Details");
        TabSpec playerSpec = mTabHost.newTabSpec("tab2");
        playerSpec.setContent(R.id.players_show_game_invited);
        playerSpec.setIndicator("Players");
        mTabHost.addTab(details);
        mTabHost.addTab(playerSpec);
        
        ValueEventListener vel = new ValueEventListener() 
        {
			
			@Override
			public void onDataChange(DataSnapshot data) 
			{
				name.setText("Name: " + gameName);
				String note = data.child("games").child(gameName)
						.child("notes").getValue().toString();
				String id = data.child("games").child(gameName)
						.child("admin").getValue().toString();
				
				admin.setText("Admin: " + data.child("users").child(id)
						.child("name").getValue().toString());
				
		        notes.setText("Notes: " + note);
		        clues.setText("Clues: " + Long.toString(data.child("games")
		        		.child(gameName).child("clues").getChildrenCount()));
		        
		        ArrayList<String> playersString = new ArrayList<String>();
		        for (DataSnapshot player: data.child("games").child(gameName)
		        		.child("players").getChildren()) 
		        {
		        	playersString.add(data.child("users")
		        			.child(player.getName())
		        			.child("name").getValue().toString());
		        }
		        
		        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>( getActivity(),
		        		android.R.layout.simple_list_item_1, playersString );
		        listView.setAdapter(adapter2);
			}
			
			@Override
			public void onCancelled(FirebaseError arg0) {}
		};
		fb.addListenerForSingleValueEvent( vel );
		accept.setOnClickListener( this );
		deny.setOnClickListener( this );
		cancel.setOnClickListener( this );
		
		return rootView;
	}

	@Override
	public void onClick( View v ) 
	{
		switch( v.getId() ) 
		{
		case R.id.accept_button:
			// Update score-total game
			ValueEventListener vel = new ValueEventListener() {
				
				@Override
				public void onDataChange(DataSnapshot data) {
					int scoreTotalGames = Integer.parseInt(data.child("users")
							.child(Home.userid).child("scores")
							.child("totalGames").getValue().toString());

					fb.child("users")
					.child(Home.userid).child("scores")
					.child("totalGames").setValue(scoreTotalGames + 1);
					
					Fragment f = new GameProfile();
					FragmentManager fragmentManager = getFragmentManager();
					GameProfile.gameName = gameName;
					fb.child( "games" ).child( gameName ).child( "players" )
					.child( Home.userid ).child( "current" ).setValue( "0" );
					fb.child( "users" ).child( Home.userid ).child( "games" )
					.child( gameName ).child( "type" ).setValue( "in" );
			        fragmentManager.beginTransaction().replace(R.id.content_frame, f).commit();
				}
				
				@Override
				public void onCancelled(FirebaseError arg0) {}
			};
			fb.addListenerForSingleValueEvent(vel);
			break;
		case R.id.deny_button:
			Fragment f1 = new ViewGames();
			FragmentManager manager = getFragmentManager();
			fb.child( "games" ).child( gameName ).child( "players" )
			.child( Home.userid ).removeValue();
			fb.child( "users" ).child( Home.userid ).child( "games" )
			.child( gameName ).removeValue();
			manager.beginTransaction()
			.replace( R.id.content_frame, f1 ).commit();
			break;
		case R.id.cancel_show_game_invited:
			Fragment f2 = new ViewGames();
			FragmentManager manager2 = getFragmentManager();
			manager2.beginTransaction()
			.replace( R.id.content_frame, f2 ).commit();
			break;
		default: 
			break;
		}
	}

}
