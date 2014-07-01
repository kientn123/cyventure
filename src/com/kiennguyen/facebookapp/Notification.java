package com.kiennguyen.facebookapp;

import java.util.ArrayList;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Notification extends MainFragment
{
	// store all the notifications
	private ArrayList<String> noti = new ArrayList<String>();
	// notifications that user received
	private ArrayList<String> notiShow = new ArrayList<String>();

	private ArrayList<Integer> indexOfNotifications = new ArrayList<Integer>();
	private ArrayList<String> values = new ArrayList<String>();
	private ListView ViewList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{

		// TODO Auto-generated method stub
		final View rootView = inflater.inflate(R.layout.notification,
				container, false);
		getActivity().setTitle("Notifications");
		ViewList = (ListView) rootView
				.findViewById(R.id.listViewForNotifications);
		

		Firebase NotiRef = new Firebase("https://cyventure-test.firebaseio.com/");
		ValueEventListener vel = new ValueEventListener()
		{

			@Override
			public void onDataChange(DataSnapshot data)
			{
				// get all the notification first
				for (int index = 0; index < 6; index++)
				{
					noti.add(data.child("notifications")
							.child(Integer.toString(index)).getValue()
							.toString());
				}

				int i = 1;
				// get index of notifications
				while (data.child("users").child(Home.userid).child("notifications").hasChildren())
				{

					indexOfNotifications.add(Integer.parseInt(data
							.child("users").child(Home.userid).child("notifications")
							.child("1").child("index")
							.getValue().toString()));
					values.add(data.child("users").child(Home.userid).child("notifications")
							.child("1").child("value")
							.getValue().toString());
					i++;
				}

				for(int j = 0; j<values.size();j++)
				{
					setNoti(indexOfNotifications.get(j),values.get(j), noti);
				}
				
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						getActivity(), android.R.layout.simple_list_item_1,
						notiShow);

				ViewList.setAdapter(adapter);

				

			}

			@Override
			public void onCancelled(FirebaseError error)
			{
			}
		};
		NotiRef.addListenerForSingleValueEvent(vel);
		return rootView;

	}

	/**
	 * 
	 * @param index
	 * @param value
	 */
	public void setNoti(int index, String value,ArrayList<String> notis)
	{
		String newNoti = "";
		switch (index)
		{
		// user joined the game
		case 0:
			newNoti = value + " " + notis.get(0);
			break;
		// you lost ... game
		case 1:
			newNoti = notis.get(1) + "game " + value;
			break;
		// you won ... game
		case 2:
			newNoti = notis.get(2) + "game " + value;
			break;
		// you have unblocked ... achievement
		case 3:
			newNoti = notis.get(3) + "achievement " + value;
			break;
		// user left game
		case 4:
			String[] contents = value.split(",");
			newNoti = contents[0].substring(1, contents[0].length()) + " "
					+ notis.get(4) + " "
					+ contents[1].substring(0, contents[0].length() - 1);
		}
		notiShow.add(newNoti);

	}

}
