package me.namaz.namazguide;

import android.app.Fragment;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AboutFragment extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_about, container,false);
		String versionName;
		try {
			versionName = getActivity().getPackageManager()
				    .getPackageInfo(getActivity().getPackageName(), 0).versionName;

			((TextView)rootView.findViewById(R.id.textViewVersion)).setText(getResources().getString(R.string.version) + versionName);
		} catch (NameNotFoundException e) {}
		
		
		return rootView;
	}
}
