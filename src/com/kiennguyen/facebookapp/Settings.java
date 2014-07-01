package com.kiennguyen.facebookapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class Settings extends Fragment implements View.OnClickListener{
    private Button about;
    private Button logout;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.settings, container, false);
        getActivity().setTitle("Settings");
        
        about = (Button) rootView.findViewById(R.id.button1);
        logout = (Button) rootView.findViewById(R.id.button2);
  
        about.setOnClickListener(this);
        logout.setOnClickListener(this);
        
        return rootView;
    }

	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
			case R.id.button1:
				break;
			case R.id.button2:
				break;
			case R.id.button3:
				break;
			default:
				break;
	    }
	}  
}