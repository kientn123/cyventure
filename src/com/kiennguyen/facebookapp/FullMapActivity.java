package com.kiennguyen.facebookapp;


import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

//import com.example.homescreen.util.SystemUiHider;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
@SuppressLint("NewApi")
public class FullMapActivity extends FragmentActivity implements 
	View.OnClickListener, GooglePlayServicesClient.ConnectionCallbacks,
	GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {

	private Button mapBackButton;
	private ImageButton mapNextItem;
	private ImageButton mapPrevItem;
	private static TextView gameName;
	private ListView curPlayersList;
	private TextView userView;
	
	private int count = 0;
	private int gameCount;
	
	private String username;
	private String mapGameName;
	private ArrayList<String> usersList;
	LocationClient mLocationClient;
	GoogleMap mMap;
	
	private double lat;
	private double lon;
	
	private int testWork = 0;
	
	public ArrayList<String> gameNames = new ArrayList<String>();
	public ArrayList<String> gamePlayers = new ArrayList<String>();

	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_map);
        if (initMap()) {
        	//Toast.makeText(this , "Ready to google map!", Toast.LENGTH_SHORT).show();
        	//mMap.setMyLocationEnabled(true);
        	mLocationClient = new LocationClient(this, this, this);
        	mLocationClient.connect();
        	gotoLocation(42.02601, -93.64836, 16);
        }
        else {
        	Toast.makeText(this, "Map not available!", Toast.LENGTH_SHORT).show();
        }
        //Put on markers for initial screen
        putMarkers(0);
        setTextView("Loading...");
		gameCount = 0;
         
    }
	 private void gotoLocation(double lat, double lng,
				float zoom) {
	    	LatLng ll = new LatLng(lat, lng);
	    	CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
	    	mMap.moveCamera(update);
		}
	

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_full_map, container, false);
		super.onCreate(savedInstanceState);
		
		//Map Screen is full-screen, not ActionBar needed
		ActionBar bar = getActionBar();
		bar.hide();

		
		//replace this one with Kien's layout
		userView = (TextView) rootView.findViewById(R.layout.activity_view_games_screen);
		curPlayersList = (ListView) rootView.findViewById(R.id.listusers);
		mapBackButton = (Button) rootView.findViewById(R.id.mapBackButton);
		mapNextItem = (ImageButton)rootView.findViewById(R.id.mapNextItem);
		mapPrevItem = (ImageButton)rootView.findViewById(R.id.mapPrevItem);
		gameName = (TextView) rootView.findViewById(R.id.mapGameNameChange);
		
	
		Intent i = getIntent();
		return rootView;
		
	}
	
	private boolean initMap() {
    	if (mMap == null) {
    		MapFragment mapFrag = 
    				(MapFragment) getFragmentManager().findFragmentById(R.id.map);
    		mMap = mapFrag.getMap();
    	}
    	return (mMap != null);
    }
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.mapNextItem:
			mMap.clear();
			if(gameCount == count && count != 0){
				count = 0;
				putMarkers(count);
			}
			else{
				putMarkers(++count);
			}
			break;
		case R.id.mapPrevItem:
			if(count == 0){
				mMap.clear();
				putMarkers(gameCount);
				count = gameCount;
			}
			else{
				mMap.clear();
				putMarkers(--count);
				
			}
			break;
		case R.id.mapBackButton:
			Fragment f = new Fragment();
			FragmentManager manager = getFragmentManager();
			manager.beginTransaction().replace( R.id.content_frame, f ).commit();
			break;
		}
		
		
	} 
	
	private void putMarkers(final int track){
		username = Home.userid;
        Firebase usersDB = new Firebase("https://cyventure-test.firebaseio.com");
        ValueEventListener vel1 = new ValueEventListener() {
			@Override
			public void onCancelled(FirebaseError arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onDataChange(DataSnapshot snap) {
				// TODO Auto-generated method stub
				for(DataSnapshot userGames: snap.child("users").child(username).child("games").getChildren()){
					gameNames.add(userGames.getName());
				}
				for(DataSnapshot allGames: snap.child("games").getChildren()){		
					if(gameNames.get(track).equals(allGames.getName())){
						for(DataSnapshot players: allGames.child("players").getChildren()){
							if(players.child("current").getValue().toString().equals("0")){
								String base = players.getName();
								String convert = (String) snap.child("users").child(base).child("name").getValue();
								gamePlayers.add(convert);
								lat = Double.parseDouble(players.child("latitude").getValue().toString());
								lon = Double.parseDouble(players.child("longitude").getValue().toString());
								mMap.addMarker(new MarkerOptions()
									.position(new LatLng(lat, lon))
									.title("Player: " + snap.child("users").child(players.getName()).child("name").getValue())
									.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
								);
							}
						}
						setTextView(gameNames.get(track));
						setUsersView(gamePlayers);
						//For efficiency purposes
						break;
					}		
				}
			}
        	
        };

        gameCount = gameNames.size() - 1;
        gamePlayers.clear();
        gameNames.clear();
        usersDB.addListenerForSingleValueEvent(vel1);
	}
	
	public void setTextView(String gametext){
		gameName = (TextView)findViewById(R.id.mapGameNameChange);
		gameName.setText(gametext);
		gameName.setTextSize(25);
	}
	
	public void setUsersView(ArrayList<String> users){
		curPlayersList = (ListView)findViewById(R.id.listusers);
		//curPlayersList.clearChoices();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, users);
        curPlayersList.setAdapter(adapter);
	}
	

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}
	
	
}
