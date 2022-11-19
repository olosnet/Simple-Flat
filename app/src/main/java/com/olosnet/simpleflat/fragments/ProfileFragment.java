package com.olosnet.simpleflat.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.olosnet.simpleflat.R;
import com.olosnet.simpleflat.dialogs.ConfirmProfileDeletionDialog;
import com.olosnet.simpleflat.dialogs.CreateProfileDialog;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        Button delete_button = view.findViewById(R.id.deleteButton);
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfirmProfileDeletionDialog delete_dialog = new ConfirmProfileDeletionDialog();

                delete_dialog.show(getActivity().getSupportFragmentManager(), "viewHolder");
            }
        });

        Button new_button = view.findViewById(R.id.newButton);
        new_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                CreateProfileDialog create_dialog = new CreateProfileDialog();
                create_dialog.show(getActivity().getSupportFragmentManager(), "viewHolder");
            }
        });
        return view;
    }
}