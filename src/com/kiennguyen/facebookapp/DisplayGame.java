package com.kiennguyen.facebookapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import android.content.Context;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.Location;

public class DisplayGame extends MainFragment implements View.OnClickListener,
LocationListener {
	
	private Button checkin;
	private Button hintButton;
	private Button back;
	//private static int randomNum;
	private TextView clueNo;
	private TextView description;
	private TextView hint;
	LocationManager locationManager;
	private boolean answerright = false;
	private String cluePosition;
	private int totalClues;
	private int current;
	public static String gameName;
	private String currentPosition;
	private String provider;
	private double currentLat, currentLng;
	private String winner;
	
	private Firebase fb = new Firebase( "https://cyventure-test.firebaseio.com/" );
	
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState ) 
	{
        View rootView = inflater.inflate( R.layout.display_game, container, false );
        getActivity().setTitle( "Game" );	
        // Connect to LocationClient
        locationManager = (LocationManager) getActivity()
        		.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);
        currentLat = location.getLatitude();
        currentLng = location.getLongitude();
		
		//Initialize values
		description = ( TextView )rootView.findViewById( R.id.textView3 );
		checkin = ( Button ) rootView.findViewById( R.id.startDate_b );
		back = ( Button ) rootView.findViewById( R.id.leave );
		hintButton = (Button) rootView.findViewById(R.id.hint);
		clueNo = ( TextView ) rootView.findViewById( R.id.textView2 ); 
		hint = (TextView) rootView.findViewById(R.id.textView4);

		checkin.setOnClickListener( this );
		back.setOnClickListener( this );
		hintButton.setOnClickListener(this);
		
		ValueEventListener vel = new ValueEventListener() 
		{
			
			@Override
			public void onDataChange(DataSnapshot data) 
			{
				currentPosition = data.child( "games" ).child( gameName )
						.child( "players" ).child( Home.userid ).child( "current" )
						.getValue().toString();
				cluePosition = data.child( "games" ).child( gameName )
						.child( "clues" ).child( currentPosition ).getValue().toString();
				String descrip = data.child( "clues" ).child( cluePosition )
						.child( "clue1" ).getValue().toString();
				description.setText(descrip);
				clueNo.setText(Integer.toString( Integer.parseInt( currentPosition ) + 1 ) );
				
				String hintSignal = data.child("games").child(gameName)
						.child("players").child(Home.userid).child("showHint")
						.getValue().toString();
				if (hintSignal.equals("yes")) {
					String h = data.child( "clues" ).child( cluePosition )
							.child( "clue2" ).getValue().toString();
					hint.setText("hint: " + h);
				}
			}
			
			@Override
			public void onCancelled(FirebaseError arg0) {}
		};
		fb.addListenerForSingleValueEvent( vel );
		
		return rootView;
	}
	
	@Override
	public void onClick( View v ) 
	{
		switch( v.getId() )
		{
			case R.id.startDate_b: //CHECKIN
				ValueEventListener vel1 = new ValueEventListener() 
				{
					
					@Override
					public void onDataChange(DataSnapshot data) {
						winner = data.child("games").child(gameName)
								.child("winner").getValue().toString();
						if (!winner.equals("none")) {
							Toast.makeText(getActivity(), winner + " won the game",
									Toast.LENGTH_SHORT).show();
						}
						else {
							double lat = Double.parseDouble(data.child("clues")
									.child(cluePosition).child("latitude").getValue().toString());
							double lng = Double.parseDouble(data.child("clues")
									.child(cluePosition).child("longitude").getValue().toString());
							System.out.println("destination: " + lat + ", " + lng);
							answerright = decideLocation(lat, lng);
							
							// Update recently checked in location of this player
							fb.child("games").child(gameName).child("players")
							.child(Home.userid).child("latitude").setValue(currentLat);
							fb.child("games").child(gameName).child("players")
							.child(Home.userid).child("longitude").setValue(currentLng);
							
							totalClues = (int) data.child("games").child(gameName)
									.child("clues").getChildrenCount();
							current = Integer.parseInt(currentPosition) + 1;
							if (answerright) {
								if (current < totalClues) {
									int scoreTotalClues = Integer.parseInt(data.child("users")
											.child(Home.userid).child("scores")
											.child("totalClues").getValue().toString());
			
									fb.child("users")
									.child(Home.userid).child("scores")
									.child("totalClues").setValue(scoreTotalClues + 1);
									fb.child("games").child(gameName)
									.child("players").child(Home.userid)
									.child("current").setValue(Integer.toString(current));
									
									fb.child("games").child(gameName).child("players")
									.child(Home.userid).child("showHint").setValue("no");
									
									Fragment f = new DisplayGame();
									FragmentManager manager = getFragmentManager();
									manager.beginTransaction()
									.replace(R.id.content_frame, f).commit();
								}
								else {
									
									int scoreTotalClues = Integer.parseInt(data.child("users")
											.child(Home.userid).child("scores")
											.child("totalClues").getValue().toString());
									
									int scoreTotalWins = Integer.parseInt(data.child("users")
											.child(Home.userid).child("scores")
											.child("totalWins").getValue().toString());
									
			
									fb.child("users")
									.child(Home.userid).child("scores")
									.child("totalClues").setValue(scoreTotalClues + 1);
									
									fb.child("users")
									.child(Home.userid).child("scores")
									.child("totalWins").setValue(scoreTotalWins + 1);
									
									for (DataSnapshot player: data.child("games").child(gameName)
											.child("players").getChildren()) {
										int scoreLosses = Integer.parseInt(data.child("users")
												.child(player.getName()).child("scores")
												.child("totalLosses").getValue().toString());
										fb.child("users")
										.child(player.getName()).child("scores")
										.child("totalLosses").setValue(scoreLosses + 1);
									}
									
									
									fb.child("games").child(gameName)
									.child("players").child(Home.userid)
									.child("current").setValue(Integer.toString(current));
									Toast.makeText(getActivity(), "YOU WIN!",
											Toast.LENGTH_SHORT).show();
									fb.child("games").child(gameName)
									.child("winner").setValue(Home.username);
								}
							}
						}
					}

					@Override
					public void onCancelled(FirebaseError arg0) {}
				};
				fb.addListenerForSingleValueEvent( vel1 );
				break;
			case R.id.leave:
				Fragment f2 = new GameProfile();
				FragmentManager fragmentManager2 = getFragmentManager();
		        fragmentManager2.beginTransaction().replace(R.id.content_frame, f2).commit();
				break;
			case R.id.hint:
				ValueEventListener vel2 = new ValueEventListener() {
					
					// Get the clue2 from database as a hint and display it
					// Players have only 5 chances to display hints in a games
					@Override
					public void onDataChange(DataSnapshot data) {
						int numHints = Integer.parseInt(data.child("games").child(gameName)
								.child("players").child(Home.userid)
								.child("numHints").getValue().toString());
						String hintSignal = data.child("games").child(gameName)
								.child("players").child(Home.userid).child("showHint")
								.getValue().toString();
						if (hintSignal.equals("no") &&
								numHints > 0) {
							String h = data.child( "clues" ).child( cluePosition )
									.child( "clue2" ).getValue().toString();
							hint.setText("hint: " + h);
							fb.child("games").child(gameName).child("players")
							.child(Home.userid).child("numHints").setValue(numHints - 1);
							fb.child("games").child(gameName).child("players")
							.child(Home.userid).child("showHint").setValue("yes");
						}
						else {
							if (hintSignal.equals("no") &&
									numHints == 0) {
								Toast.makeText(getActivity(),
										"You have used all 5 hints you have in this game",
										Toast.LENGTH_SHORT).show();
							}
						}
					}
					
					@Override
					public void onCancelled(FirebaseError arg0) {}
				};
				fb.addListenerForSingleValueEvent(vel2);
				break;
			default:
				break;
	    }
	}    
	private boolean decideLocation(double lat, double lng) {
		System.out.println("current: " + currentLat + ", " + currentLng);
		if (((currentLat <= lat+0.0007) && (currentLat >= lat-0.0007)) && 
				((currentLng <= lng+0.0007) && (currentLng >= lng-0.0007))) {
				Toast.makeText(getActivity(), "You get it right", Toast.LENGTH_SHORT).show();
				return true;
			}
		else {
			Toast.makeText(getActivity(), "Wrong location", Toast.LENGTH_SHORT).show();
			return false;
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		locationManager.requestLocationUpdates(provider, 400, 1, this);
	}
	
	@Override
	public void onPause() {
	    super.onPause();
	    locationManager.removeUpdates(this);
	}

	@Override
	public void onLocationChanged(Location location) {
		currentLat = location.getLatitude();
		currentLng = location.getLongitude();
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
}
