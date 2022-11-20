package com.olosnet.simpleflat.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.olosnet.simpleflat.R;
import com.olosnet.simpleflat.adapters.FAdapter;

public class ShowSettingsFragment extends Fragment {

    public ShowSettingsFragment() {
        // Required empty public constructor
    }

    public static ShowSettingsFragment newInstance() {
        return new ShowSettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_show_settings, container, false);
        TabLayout layout = view.findViewById(R.id.setting_table_layout);
        ViewPager2 pager = view.findViewById(R.id.settings_pager);


        FAdapter adapter = new FAdapter(getActivity());
        ProfileFragment profileFragment = new ProfileFragment();
        SettingsFragment settingsFragment = new SettingsFragment();
        adapter.addFragment(settingsFragment, "Settings");
        adapter.addFragment(profileFragment, "Profiles");

        pager.setAdapter(adapter);
        pager.setCurrentItem(0);

        TabLayoutMediator mediator = new TabLayoutMediator(layout, pager,
                (tab, position) -> tab.setText(adapter.getFragmentTitle(position)));
        mediator.attach();

        return view;
    }
}