package com.kiennguyen.facebookapp;
/**
 * Home Class to show the Home Page when user login to the system.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class Home extends Fragment implements View.OnClickListener{
	// Declaration of instance variables
    private Button create;
    private Button join;
    private Button games;
    private Button showMap;
	private Firebase fb = new Firebase("https://cyventure-test.firebaseio.com/");
	// Make these public static, so other classes can access their values
	public static String username;
	public static String userid;
	public static int numClues;
	public static ArrayList<GraphUser> friends = new ArrayList<GraphUser>();
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Layout the view of this Fragment
    	View rootView = inflater.inflate(R.layout.home, container, false);
        getActivity().setTitle("Home");
        
        
        // Find the user's profile picture custom view
     	// Find the user's name view
        
     	// Get values of all the items in the layout
        create = (Button) rootView.findViewById(R.id.button1);
        join = (Button) rootView.findViewById(R.id.button2);
        games = (Button) rootView.findViewById(R.id.button3);
        showMap = (Button) rootView.findViewById(R.id.mapShow);
        
        // Set click event, so all the items we want can be click-able
        create.setOnClickListener(this);
        join.setOnClickListener(this);
        games.setOnClickListener(this);
        showMap.setOnClickListener(this);
        
        // Required by Facebook API to get user's information
        Session session = Session.getActiveSession();
		if (session != null && session.isOpened()) {
			//Get the user's data
			makeMeRequest(session);
		}
        
        return rootView;
    }

    // Handle CLICK events
	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
			case R.id.button1:
				Fragment f1 = new CreateGame();
				FragmentManager fragmentManager1 = getFragmentManager();
		        fragmentManager1.beginTransaction().replace(R.id.content_frame, f1).commit();
				break;
			case R.id.button2:
				 Fragment f2 = new JoinGame();
				 FragmentManager fragmentManager2 = getFragmentManager();
		         fragmentManager2.beginTransaction().replace(R.id.content_frame, f2).commit();
				break;
			case R.id.button3:
				Fragment f3 = new ViewGames();
				FragmentManager fragmentManager3 = getFragmentManager();
		        fragmentManager3.beginTransaction().replace(R.id.content_frame, f3).commit();
				break;
			case R.id.mapShow:
				Intent i = new Intent(getActivity(), FullMapActivity.class);
				startActivity(i);
			default:
				break;
	    }
	}    
	
	// Get user's data from Facebook server
	private void makeMeRequest(final Session session) {
		// Make an API call to get user data and define a new callback to handle the response
		Request request = Request.newMeRequest(session,
				new Request.GraphUserCallback() {
					
					@Override
					public void onCompleted(final GraphUser user, Response response) {
						// If the response is successful
						if (session == Session.getActiveSession()) {
							if (user != null) {
								username = user.getName();
								userid = user.getId();
								// Set the id for the ProfilePictureView view that in turn displays the profile picture
								// Set the TextView's text to their user's name
								// Event listener for firebase object 
								ValueEventListener vel = new ValueEventListener() {
									
									@Override
									public void onDataChange(DataSnapshot data) 
									{
										numClues = ( int ) data.child( "clues" ).getChildrenCount();
										// Boolean value indicates whether the user is already in the database
										boolean existed = false;
										for ( DataSnapshot child: data.child( "users" ).getChildren() ) 
										{
											if ( userid.equals( child.getName() ) ) 
											{
												existed = true;
											}
										}
										// If not, add the user's instance in the database
										if (!existed) {
											Map<String, Object> toSet = new HashMap<String, Object>();
											toSet.put( "name", user.getName() );
											fb.child( "users" ).child( user.getId() ).setValue( toSet );
											fb.child( "users" ).child(userid).child("scores")
											.child("days").setValue("0");
											fb.child( "users" ).child(userid).child("scores")
											.child("totalClues").setValue("0");
											fb.child( "users" ).child(userid).child("scores")
											.child("totalGames").setValue("0");
											fb.child( "users" ).child(userid).child("scores")
											.child("totalLosses").setValue("0");
											fb.child( "users" ).child(userid).child("scores")
											.child("totalWins").setValue("0");
										}
							
									}
									
									@Override
									public void onCancelled(FirebaseError arg0) {}
								};
								// Bind the event listener to the firebase object once
								fb.addListenerForSingleValueEvent(vel);
							}
						}
						// Don't care cases when we login not successfully
						if (response.getError() != null) {}
					}
				});
		request.executeAsync();
		Request request2 = Request.newMyFriendsRequest(session,
				new Request.GraphUserListCallback() 
		{
					
					@Override
					public void onCompleted(List<GraphUser> users, Response response) 
					{
						friends.clear();
						// TODO Auto-generated method stub
						for ( GraphUser friend: users ) 
						{
							//if ( !friends.contains( friend ) ) 
							//{
								friends.add( friend );
							//}
						}
					}
				});
		request2.executeAsync();
	}
}