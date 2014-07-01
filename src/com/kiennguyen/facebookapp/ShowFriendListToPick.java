package com.kiennguyen.facebookapp;

import java.util.ArrayList;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class ShowFriendListToPick extends MainFragment implements
		View.OnClickListener {

	private ListView list;
	private Button inviteFriends;
	private Button cancel;
	private ArrayList<FriendItem> friendItemList = new ArrayList<FriendItem>();
	private MyFriendItemAdapter myFriendItemAdapter;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.show_friend_list_to_pick,
				container, false);
		list = (ListView) rootView
				.findViewById(R.id.listView_show_friend_list_to_pick);
		inviteFriends = (Button) rootView
				.findViewById(R.id.invite_show_friend_list_to_pick);
		cancel = (Button) rootView
				.findViewById(R.id.cancel_show_friend_list_to_pick);
		Firebase fb = new Firebase("https://cyventure-test.firebaseio.com/");
		ValueEventListener vel = new ValueEventListener() {

			@Override
			public void onCancelled(FirebaseError arg0) {
			}

			@Override
			public void onDataChange(DataSnapshot data) {
				for (GraphUser friend : Home.friends) {
					boolean existed = false;
					for (DataSnapshot child : data.child("users").getChildren()) {
						if (friend.getId().equals(child.getName())) {
							existed = true;
						}
					}
					if (existed) {
						friendItemList.add(new FriendItem(friend, false));
					}
				}
				myFriendItemAdapter = new MyFriendItemAdapter(getActivity(),
						R.layout.friends_pick, friendItemList);
				list.setAdapter(myFriendItemAdapter);
			}

		};
		fb.addListenerForSingleValueEvent(vel);

		inviteFriends.setOnClickListener(this);
		cancel.setOnClickListener(this);
		return rootView;

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.invite_show_friend_list_to_pick:
			Firebase fb= new Firebase("https://cyventure-test.firebaseio.com/");
			ArrayList<FriendItem> friendList = myFriendItemAdapter.friendList;
			for (int i = 0; i < friendList.size(); i++) {
				FriendItem friendItem = friendList.get(i);
				if (friendItem.isSelected()) {
					fb.child("games").child(ShowGamesWeCreated.name).child("players")
					.child(friendItem.getGraphUser().getId())
					.child("current").setValue("-1");
					
					fb.child("games").child(ShowGamesWeCreated.name).child("players")
					.child(friendItem.getGraphUser().getId())
					.child("latitude").setValue("0");
					
					fb.child("games").child(ShowGamesWeCreated.name).child("players")
					.child(friendItem.getGraphUser().getId())
					.child("longitude").setValue("0");
					
					fb.child("games").child(ShowGamesWeCreated.name).child("players")
					.child(friendItem.getGraphUser().getId())
					.child("numHints").setValue("5");
					
					fb.child("games").child(ShowGamesWeCreated.name).child("players")
					.child(friendItem.getGraphUser().getId())
					.child("showHint").setValue("no");
					
					fb.child("users").child(friendItem.getGraphUser().getId())
					.child("games").child(ShowGamesWeCreated.name).child("type")
					.setValue("invited");
				}
			}
			Fragment f0 = new ShowGamesWeCreated();
			FragmentManager manager0 = getFragmentManager();
			manager0.beginTransaction().replace(R.id.content_frame, f0).commit();
			break;
		case R.id.cancel_show_friend_list_to_pick:
			break;
		default:
			break;
		}
	}

	private class FriendItem {

		GraphUser user;
		boolean selected = false;

		public FriendItem(GraphUser user, boolean selected) {
			this.user = user;
			this.selected = selected;
		}

		public GraphUser getGraphUser() {
			return user;
		}

		public boolean isSelected() {
			return selected;
		}

		public void setSelected(boolean selected) {
			this.selected = selected;
		}
	}

	private class MyFriendItemAdapter extends ArrayAdapter<FriendItem> {
		private ArrayList<FriendItem> friendList;

		public MyFriendItemAdapter(Context context, int textViewResourceId,
				ArrayList<FriendItem> friendList) {
			super(context, textViewResourceId, friendList);
			this.friendList = new ArrayList<FriendItem>();
			this.friendList.addAll(friendList);
		}

		private class ViewHolder {
			TextView userName;
			ProfilePictureView profilePictureView;
			CheckBox checkBox;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			Log.v("ConvertView", String.valueOf(position));

			if (convertView == null) {
				LayoutInflater vi = (LayoutInflater) getActivity()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = vi.inflate(R.layout.friends_pick, null);

				holder = new ViewHolder();
				holder.userName = (TextView) convertView
						.findViewById(R.id.player_friends_pick);
				holder.profilePictureView = (ProfilePictureView) convertView
						.findViewById(R.id.picture_friends_pick);
				holder.checkBox = (CheckBox) convertView
						.findViewById(R.id.checkbox_friend_pick);
				convertView.setTag(holder);
				holder.checkBox.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						CheckBox cb = (CheckBox) v;
						FriendItem friendItem = (FriendItem) cb.getTag();
						friendItem.setSelected(cb.isChecked());
					}
				});
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			FriendItem friendItem = friendList.get(position);
			holder.userName.setText(friendItem.getGraphUser().getName());
			holder.profilePictureView.setProfileId(friendItem.getGraphUser()
					.getId());
			holder.checkBox.setChecked(friendItem.isSelected());
			holder.checkBox.setTag(friendItem);

			return convertView;
		}
	}
}
