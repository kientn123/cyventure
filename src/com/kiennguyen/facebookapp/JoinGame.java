package com.kiennguyen.facebookapp;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class JoinGame extends MainFragment implements View.OnClickListener {

	Button back;
	ListView list;
	ArrayList<String> games = new ArrayList<String>();
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
        View rootView = inflater.inflate(R.layout.join_game, container, false);
        getActivity().setTitle("Join Games");
        
		list = (ListView) rootView.findViewById(R.id.listView_joinGame);
		back = (Button) rootView.findViewById(R.id.back_join_game);
		
        Firebase fb = new Firebase("https://cyventure-test.firebaseio.com/");
		
        ValueEventListener vel = new ValueEventListener() {
			
			@Override
			public void onDataChange(DataSnapshot data) {
				for (DataSnapshot aGame: data.child("games").getChildren()) {
					if (aGame.child("open").getValue().toString().equals("true")) {
						boolean isIn = false;
						for (DataSnapshot aPlayer: aGame.child("players").getChildren()) {
							if (aPlayer.getName().equals(Home.userid)) {
								isIn = true;
							}
						}
						if (!isIn) {
							games.add(aGame.getName());
						}
					}
				}
				
				
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), 
						android.R.layout.simple_list_item_1, games);
		        list.setAdapter(adapter);
		        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {  
		            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		            {  
		            	Fragment f = new ViewGameFromJoinGame();
		            	ViewGameFromJoinGame.gameName = games.get(position);
		            	//System.out.println("Game Name: " + games.get(position));
						FragmentManager fragmentManager = getFragmentManager();
				        fragmentManager.beginTransaction().replace(R.id.content_frame, f).commit();
		            }

		         });
			}
			
			@Override
			public void onCancelled(FirebaseError arg0) {}
		};
		fb.addListenerForSingleValueEvent(vel);
		back.setOnClickListener(this);
        
        return rootView;
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.back_join_game:
			Fragment f = new Home();
			FragmentManager manager = getFragmentManager();
			manager.beginTransaction().replace(R.id.content_frame, f).commit();
			break;
		default:
			break;
		}
	}
}
