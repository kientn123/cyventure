package com.kiennguyen.facebookapp;

import java.util.ArrayList;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

public class GameProfile extends MainFragment implements View.OnClickListener 
{
	
	private Button bClue;
	private Button bEnd;
	private TextView tName;
	private TextView tNotes;
	private TabHost mTabHost;
	public static String gameName;
	private ListView listView;
	private TextView currentClue;
	private TextView totalClues;
	private TextView winner;
	private String win;
	
	public View onCreateView( LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState ) 
	{
        View rootView = inflater.inflate( R.layout.game_profile, container, false );
        getActivity().setTitle( "Game Profile" );
        
        bClue = (Button)rootView.findViewById( R.id.gBClues );
        bEnd = (Button)rootView.findViewById( R.id.gBEnd );
        tName = (TextView)rootView.findViewById( R.id.gName );
        tNotes = (TextView)rootView.findViewById( R.id.gNotes );
        listView = ( ListView ) rootView.findViewById( R.id.listView_game_profile );
        currentClue = ( TextView ) rootView.findViewById( R.id.gProgress );
        totalClues = ( TextView ) rootView.findViewById( R.id.gHints );
        winner = (TextView) rootView.findViewById(R.id.gPlayers);
        
        mTabHost = ( TabHost ) rootView.findViewById( android.R.id.tabhost );
        mTabHost.setup();
        TabSpec details = mTabHost.newTabSpec( "tab1" );
        details.setContent( R.id.details_game_profile );
        details.setIndicator( "Details" );
        TabSpec playerSpec = mTabHost.newTabSpec( "tab2" );
        playerSpec.setContent( R.id.players_game_profile );
        playerSpec.setIndicator( "Players" );
        mTabHost.addTab( details );
        mTabHost.addTab( playerSpec );
        
        Firebase fb = new Firebase( "https://cyventure-test.firebaseio.com/" );
        ValueEventListener vel = new ValueEventListener() 
        {
			
			@Override
			public void onDataChange( DataSnapshot data ) 
			{
				tName.setText( gameName );
				String note = data.child( "games" ).child( gameName )
						.child( "notes" ).getValue().toString();
		        tNotes.setText( note );
		        bClue.setText( "Go" );
		        bEnd.setText( "Back To View Game" );
		        
		        int current = Integer.parseInt(data.child( "games" ).child( gameName )
		        		.child( "players" ).child( Home.userid ).child( "current" )
		        		.getValue().toString());
		        currentClue.setText( Integer.toString( current+1 ) );
		        String total = Integer.toString( ( int ) data.child( "games" )
		        		.child( gameName )
		        		.child( "clues" ).getChildrenCount() ); 
		        totalClues.setText(total);
		        
		        ArrayList<GameProfileItem> playerItems = new ArrayList<GameProfileItem>();
		        for ( DataSnapshot player: data.child( "games" ).child( gameName )
		        		.child( "players" ).getChildren() ) {
		        	String aName = data.child( "users" )
		        			.child( player.getName() )
		        			.child( "name" ).getValue().toString();
		        	String numCurrent = player.child("current").getValue().toString();
		        	String currentEach;
		        	if (Integer.parseInt(numCurrent) == -1) {
		        		currentEach = "invited";
		        	}
		        	else {
			        	currentEach = numCurrent + "/" + total;
		        	}
		        	GameProfileItem item = new GameProfileItem(aName, currentEach);
		        	playerItems.add(item);
		        }
		        
		        GameProfileItemAdapter adapter2 = new GameProfileItemAdapter( getActivity(),
		        		R.layout.game_profile_item, playerItems );
		        listView.setAdapter(adapter2);
		        win = data.child("games").child(gameName)
		        		.child("winner").getValue().toString();
		        if (!win.equals("none")) {
		        	winner.setText(win + " won the game");
		        }
			}
			
			@Override
			public void onCancelled(FirebaseError arg0) {}
		};
		fb.addListenerForSingleValueEvent( vel );
		bClue.setOnClickListener( this );
        bEnd.setOnClickListener( this );
        
       
    	
        return rootView;
    }
	
	@Override
	public void onClick( View v ) 
	{
		switch( v.getId() )
		{
			case R.id.gBClues:
				if (!win.equals("none")) {
					Toast.makeText(getActivity(), win + " won the game",
							Toast.LENGTH_SHORT).show();
				}
				else {
					Fragment f1 = new DisplayGame();
					FragmentManager fragmentManager1 = getFragmentManager();
					DisplayGame.gameName = gameName;
			        fragmentManager1.beginTransaction()
			        .replace( R.id.content_frame, f1 ).commit();
				}
				break;
			case R.id.gBEnd:
				Fragment f2 = new ViewGames();
				FragmentManager fragmentManager2 = getFragmentManager();
		        fragmentManager2.beginTransaction()
		        .replace( R.id.content_frame, f2 ).commit();
				break;
			default:
				break;
	    }
	}    
	
	private class GameProfileItem 
	{
		String nameOfPlayer;
		String current;
		public GameProfileItem( String nameOfPlayer, String current ) 
		{
			this.nameOfPlayer = nameOfPlayer;
			this.current = current;
		}
		
		public String getName() 
		{
			return nameOfPlayer;
		}
		
		public String getType() 
		{
			return current;
		}
	}
	
	
	
	private class GameProfileItemAdapter extends ArrayAdapter<GameProfileItem> 
	{
		private ArrayList<GameProfileItem> gameProfileList;
		
		public GameProfileItemAdapter( Context context, int textViewResourceId,
				ArrayList<GameProfileItem> gameProfileList ) 
		{
			super( context, textViewResourceId, gameProfileList );
			this.gameProfileList = new ArrayList<GameProfileItem>();
			this.gameProfileList.addAll( gameProfileList );
		}
		
		private class ViewHolder {
			TextView name;
			TextView current;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) 
		{
			ViewHolder holder = null;
			Log.v( "ConvertView", String.valueOf( position ) );
			if ( convertView == null ) 
			{
				LayoutInflater vi = (LayoutInflater) getActivity()
						.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
				convertView = vi.inflate( R.layout.game_profile_item, null );
				holder = new ViewHolder();
				holder.name = ( TextView ) convertView
						.findViewById( R.id.name_game_profile_item );
				holder.current = ( TextView ) convertView
						.findViewById( R.id.current_game_profile_item );
				convertView.setTag( holder );
			}
			else 
			{
				holder = ( ViewHolder ) convertView.getTag();
			}
			GameProfileItem viewGameItem = gameProfileList.get( position );
			holder.name.setText( viewGameItem.getName() );
			holder.current.setText( viewGameItem.getType() );
			return convertView;
		}
	}
}
