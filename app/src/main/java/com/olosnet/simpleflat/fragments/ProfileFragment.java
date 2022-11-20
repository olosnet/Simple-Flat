package com.olosnet.simpleflat.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.olosnet.simpleflat.R;
import com.olosnet.simpleflat.adapters.ProfileSpinAdapter;
import com.olosnet.simpleflat.buses.ProfilesBus;
import com.olosnet.simpleflat.buses.ConfigsBus;
import com.olosnet.simpleflat.database.ProfilesModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.disposables.Disposable;

public class ProfileFragment extends Fragment {

    private List<ProfilesModel> profiles;
    private ProfilesModel selectedProfile;
    private final List<Disposable> subs = new ArrayList<>();


    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        Button new_button = view.findViewById(R.id.newButton);
        new_button.setOnClickListener(l -> showCreateProfileDialog());

        Button delete_button = view.findViewById(R.id.deleteButton);
        delete_button.setOnClickListener(l -> showDeleteProfileDialog());

        profiles = new ArrayList<>();
        ProfileSpinAdapter spinAdapter = new ProfileSpinAdapter(getContext(),
                                                android.R.layout.simple_spinner_item,
                                                profiles);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner profileSpinner = view.findViewById(R.id.selectProfileSpinner);
        profileSpinner.setAdapter(spinAdapter);
        profileSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                selectedProfile = spinAdapter.getItem(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapter) {  }
        });

        subs.add(ProfilesBus.onLoadSubject().subscribe(value -> {
           profiles.clear();
           profiles.addAll(value);
           spinAdapter.notifyDataSetChanged();
        }));

        subs.add(ProfilesBus.onCreateSubject().subscribe(value -> {
            ProfilesBus.loadRequestSubject().onNext(1);
        }));

        subs.add(ProfilesBus.onDeleteSubject().subscribe(value -> {
            ProfilesBus.loadRequestSubject().onNext(1);
        }));

        return view;
    }

    private void createNewProfileOnCurrentData(String profileName)
    {
        Log.i("PROFILE", "Save profile with name" + profileName);

        int currentR = ConfigsBus.rSubject().getValue();
        int currentG = ConfigsBus.gSubject().getValue();
        int currentB = ConfigsBus.bSubject().getValue();
        float currentBrightness = ConfigsBus.brightnessSubject().getValue();

        ProfilesModel profile = new ProfilesModel();
        profile.setName(profileName);
        profile.setR_value(currentR);
        profile.setG_value(currentG);
        profile.setB_value(currentB);
        profile.setBrightness_value(currentBrightness);

        ProfilesBus.createRequestSubject().onNext(profile);
    }

    private void showCreateProfileDialog() {
        // Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.create_profile_title);

        // Input text
        EditText input = new EditText(builder.getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setPadding(16, 0, 16, 0);
        builder.setView(input);

        builder.setMessage(R.string.insert_profile_name)
                .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                    String profileName = input.getText().toString();


                    if (profileName.isEmpty()) {
                        Toast.makeText(getContext(),
                                R.string.profile_name_required, Toast.LENGTH_LONG).show();
                    }
                    else {
                        createNewProfileOnCurrentData(profileName);
                    }
                    dialogInterface.dismiss();
                })
                .setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.cancel());

        builder.show();
    }

    private void showDeleteProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.are_you_sure)
                .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                    ProfilesBus.deleteRequestSubject().onNext(selectedProfile.getId());
                    dialogInterface.dismiss();
                })
                .setNegativeButton(R.string.no, (dialogInterface, i) -> dialogInterface.cancel());

        builder.show();
    }

    @Override
    public void onDestroy() {
        for (Disposable element : subs)
            element.dispose();

        super.onDestroy();
    }
}