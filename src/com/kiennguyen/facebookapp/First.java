package com.kiennguyen.facebookapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class First extends Fragment
{

	@Override
	public View onCreateView(LayoutInflater inflater, 
			ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.screen, container, false);
		Button b = (Button) view.findViewById(R.id.home_screen_1);
		
		b.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), MainActivity.class);
				startActivity(i);
			}
		});
		
		return view;
	}
}
