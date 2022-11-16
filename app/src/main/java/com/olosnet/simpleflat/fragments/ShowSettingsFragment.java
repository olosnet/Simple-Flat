package com.olosnet.simpleflat.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.olosnet.simpleflat.R;
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShowSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShowSettingsFragment extends Fragment {

    public ShowSettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ShowSettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ShowSettingsFragment newInstance() {
        ShowSettingsFragment fragment = new ShowSettingsFragment();
        return fragment;
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
        TabLayout tlayout = view.findViewById(R.id.setting_table_layout);
        ViewPager2 pager = view.findViewById(R.id.settings_pager);


        FAdapter adapter = new FAdapter(getActivity());
        ProfileFragment profileFragment = new ProfileFragment();
        SettingsFragment settingsFragment = new SettingsFragment();
        adapter.addFragment(settingsFragment, "Settings");
        adapter.addFragment(profileFragment, "Profiles");

        pager.setAdapter(adapter);
        pager.setCurrentItem(0);

        TabLayoutMediator mediator = new TabLayoutMediator(tlayout, pager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        tab.setText(adapter.getFragmentTitle(position));
                    }
                });
        mediator.attach();

        return view;
    }
}