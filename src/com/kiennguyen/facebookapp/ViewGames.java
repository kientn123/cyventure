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
import android.widget.TextView;

public class ViewGames extends MainFragment implements
View.OnClickListener 
{

	public ArrayList<String> gameNums = new ArrayList<String>();
	private ArrayList<ViewGameItem> viewGameItemList = new ArrayList<ViewGameItem>();
	private ViewGameItemAdapter viewGameItemAdapter;
	private Button back;
	
	@Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, 
    		Bundle savedInstanceState ) 
	{
        final View rootView = inflater.inflate( R.layout.view_games, container, false );
        getActivity().setTitle( "View Games" );
        
        back = ( Button ) rootView.findViewById( R.id.back_button_view_games );
        back.setOnClickListener( this );
        
        Firebase gamesDB = new Firebase( "https://cyventure-test.firebaseio.com/" );
        ValueEventListener vel = new ValueEventListener() 
        {
			
			@Override
			public void onDataChange(DataSnapshot data) 
			{
				for (DataSnapshot game: data.child( "users" ).child( Home.userid )
						.child( "games" ).getChildren()) 
				{
					String name = game.getName();
					String type = game.child( "type" ).getValue().toString();
					viewGameItemList.add( new ViewGameItem( name, type ) );
				}
		        ListView list = ( ListView ) rootView.findViewById( R.id.listView1 );
		        viewGameItemAdapter = new ViewGameItemAdapter( getActivity(), 
		        		R.layout.view_game_item, viewGameItemList );
		        list.setAdapter( viewGameItemAdapter );
			}		
			@Override
			public void onCancelled(FirebaseError error) {}
		};
        gamesDB.addListenerForSingleValueEvent( vel ); 
        return rootView;
	}
	
	@Override
	public void onClick( View v ) 
	{
		switch( v.getId() )
		{
		case R.id.back_button_view_games:
			Fragment f = new Home();
			FragmentManager manager = getFragmentManager();
			manager.beginTransaction().replace( R.id.content_frame, f ).commit();
			break;
		default:
			break;
		}
	}
	
	
	private class ViewGameItem 
	{
		String nameOfGame;
		String type;
		public ViewGameItem( String nameOfGame, String type ) 
		{
			this.nameOfGame = nameOfGame;
			this.type = type;
		}
		
		public String getName() 
		{
			return nameOfGame;
		}
		
		public String getType() 
		{
			return type;
		}
	}
	
	private class ViewGameItemAdapter extends ArrayAdapter<ViewGameItem> 
	{
		private ArrayList<ViewGameItem> viewGameList;
		
		public ViewGameItemAdapter( Context context, int textViewResourceId,
				ArrayList<ViewGameItem> viewGameList ) 
		{
			super( context, textViewResourceId, viewGameList );
			this.viewGameList = new ArrayList<ViewGameItem>();
			this.viewGameList.addAll( viewGameList );
		}
		
		private class ViewHolder {
			TextView name;
			TextView type;
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
				convertView = vi.inflate( R.layout.view_game_item, null );
				holder = new ViewHolder();
				holder.name = ( TextView ) convertView
						.findViewById( R.id.name_view_game_item );
				holder.type = ( TextView ) convertView
						.findViewById( R.id.type_view_game_item );
				convertView.setTag( holder );
				holder.name.setOnClickListener( new View.OnClickListener() 
				{
					
					@Override
					public void onClick(View v) {
						TextView tv = (TextView) v;
						ViewGameItem vgi = (ViewGameItem) tv.getTag();
						if ( vgi.getType().equals( "invited" ) ) 
						{
							Fragment f0 = new ShowGameInvited();
							FragmentManager manager0 = getFragmentManager();
							ShowGameInvited.gameName = vgi.getName();
							manager0.beginTransaction()
							.replace(R.id.content_frame, f0).commit();
						}
						else 
						{
							Fragment f = new GameProfile();
							FragmentManager fragmentManager = getFragmentManager();
							GameProfile.gameName = vgi.getName();
							fragmentManager.beginTransaction()
							.replace(R.id.content_frame, f).commit();
						}
					}
				} );
			}
			else 
			{
				holder = ( ViewHolder ) convertView.getTag();
			}
			ViewGameItem viewGameItem = viewGameList.get( position );
			holder.name.setText( viewGameItem.getName() );
			holder.type.setText( viewGameItem.getType() );
			holder.name.setTag( viewGameItem );
			return convertView;
		}
	}

	
}
