package com.kiennguyen.facebookapp;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.location.Location;

public class PlayingGameTest extends FragmentActivity implements
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener, LocationListener{
	
	private TextView question;
	private TextView num;
	private Button checkin;
	private boolean answerRight;
	private int numCluesLeft;
	private double destinationLatitude;
	private double destinationLongitude;
	DataSnapshot datasnapshot;
	LocationClient mLocationClient;
	
	Firebase game = new Firebase("https://cyventure-test.firebaseio.com/");
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.playing_game_test);
		
		mLocationClient = new LocationClient(this, this, this);
     	mLocationClient.connect();
     	
		question = (TextView) findViewById(R.id.question);
		num = (TextView) findViewById(R.id.num);
		checkin = (Button) findViewById(R.id.checkin);
		
		game.addValueEventListener(new ValueEventListener() {
			
			@Override
			public void onDataChange(DataSnapshot data) {
				datasnapshot = data;
				numCluesLeft = Integer.parseInt(datasnapshot.child("games").child("brig").child("totalClues").getValue().toString());
				num.setText(Integer.toString(numCluesLeft));
				String firstClue = datasnapshot.child("games").child("brig").child("clues").child("0").getValue().toString();
				String firstQuestion = datasnapshot.child("clues").child(firstClue).child("clue1").getValue().toString();
				question.setText(firstQuestion);
				destinationLatitude = Double.parseDouble(datasnapshot.child("clues").child(firstClue).child("latitude").getValue().toString());
				destinationLongitude = Double.parseDouble(datasnapshot.child("clues").child(firstClue).child("longitude").getValue().toString());
				Log.v("destination", Double.toString(destinationLatitude) + ", " + Double.toString(destinationLongitude));
			}
			
			@Override
			public void onCancelled(FirebaseError arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		
		checkin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				answerRight = decideLocation(destinationLatitude, destinationLongitude);
				if (answerRight && (numCluesLeft > 0)) {
					numCluesLeft--;
					num.setText(Integer.toString(numCluesLeft));
					String secondClue = datasnapshot.child("games").child("brig").child("clues").child("1").getValue().toString();
					String secondQuestion = datasnapshot.child("clues").child(secondClue).child("clue1").getValue().toString();
					question.setText(secondQuestion);
					destinationLatitude = Double.parseDouble(datasnapshot.child("clues").child(secondClue).child("latitude").getValue().toString());
					destinationLongitude = Double.parseDouble(datasnapshot.child("clues").child(secondClue).child("longitude").getValue().toString());
					Log.v("destination", Double.toString(destinationLatitude) + ", " + Double.toString(destinationLongitude));
				}
			}
		});
	}
	
	private boolean decideLocation(double lat, double lng) {
		Location currentLocation = mLocationClient.getLastLocation();
		if (currentLocation == null) {
			Toast.makeText(this, "Current location isn't available", Toast.LENGTH_SHORT).show();
			return false;
		}
		else {
			Log.v("currentLocation", currentLocation.getLatitude() + ", " + currentLocation.getLongitude());
			double currentLat = currentLocation.getLatitude(); 
			double currentLng = currentLocation.getLongitude();
			if ((currentLat <= lat+0.0005 && currentLat >= lat-0.0005) && 
					(currentLng <= lng+0.0005 && currentLng >= lng-0.0005)) {
					Toast.makeText(this, "You get it right", Toast.LENGTH_LONG).show();
					return true;
				}
			else {
				Toast.makeText(this, "Wrong location", Toast.LENGTH_LONG).show();
				return false;
			}
		}
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
