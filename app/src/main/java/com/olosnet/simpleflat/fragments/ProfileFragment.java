package com.olosnet.simpleflat.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
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
import com.olosnet.simpleflat.buses.ConfigsBus;
import com.olosnet.simpleflat.buses.ProfilesBus;
import com.olosnet.simpleflat.database.ProfilesModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.disposables.Disposable;

public class ProfileFragment extends Fragment {

    private final List<Disposable> subs = new ArrayList<>();
    private List<ProfilesModel> profiles;
    private ProfilesModel selectedProfile;
    private boolean firstSelection = true;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
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

        Button new_button = view.findViewById(R.id.newButton);
        new_button.setOnClickListener(l -> showCreateProfileDialog());

        Button delete_button = view.findViewById(R.id.deleteButton);
        delete_button.setOnClickListener(l -> showDeleteProfileDialog());

        Button save_button = view.findViewById(R.id.saveButton);
        save_button.setOnClickListener(l -> updateAndSaveSelectedProfile() );

        Button load_button = view.findViewById(R.id.loadButton);
        load_button.setOnClickListener(l -> loadSelectedProfile());

        // Spinner
        profiles = new ArrayList<>();
        ProfileSpinAdapter spinAdapter = new ProfileSpinAdapter(requireContext(),
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
            public void onNothingSelected(AdapterView<?> adapter) {
            }
        });

        // Subscription
        subs.add(ProfilesBus.onLoaded().subscribe(value -> {
            profiles.clear();
            profiles.addAll(value);
            spinAdapter.notifyDataSetChanged();
            // WA
            if (firstSelection) {
                if (profileSpinner.getCount() > 0) {
                    profileSpinner.setSelection(0);
                    selectedProfile = spinAdapter.getItem(0);
                }
                firstSelection = false;
            }
        }));

        subs.add(ProfilesBus.onCreated().subscribe(value -> {
            ProfilesBus.loadRequest().onNext(true);
            execToast(R.string.profile_created);
        }));

        subs.add(ProfilesBus.onDeleted().subscribe(value ->{
            ProfilesBus.loadRequest().onNext(true);
            execToast(R.string.profile_deleted);
        }));

        subs.add(ProfilesBus.onSaved().subscribe(value -> execToast(R.string.profile_saved)));

        return view;
    }

    private void updateAndSaveSelectedProfile() {
        if (selectedProfile != null) {
            selectedProfile.setR_value(ConfigsBus.onRedUpdated().getValue());
            selectedProfile.setG_value(ConfigsBus.onGreenUpdated().getValue());
            selectedProfile.setB_value(ConfigsBus.onBlueUpdated().getValue());
            selectedProfile.setBrightness_value(ConfigsBus.onBrightnessUpdated().getValue());
            ProfilesBus.saveRequest().onNext(selectedProfile);
        }
    }

    private void loadSelectedProfile() {
        if (selectedProfile != null) {
            ConfigsBus.writeRedRequest().onNext(selectedProfile.getR_value());
            ConfigsBus.writeGreenRequest().onNext(selectedProfile.getG_value());
            ConfigsBus.writeBlueRequest().onNext(selectedProfile.getB_value());
            ConfigsBus.writeBrightnessRequest().onNext(selectedProfile.getBrightness_value());
        }

    }

    private void createNewProfileOnCurrentData(String profileName) {
        ProfilesModel profile = new ProfilesModel();
        profile.setName(profileName);
        profile.setR_value(ConfigsBus.onRedUpdated().getValue());
        profile.setG_value(ConfigsBus.onGreenUpdated().getValue());
        profile.setB_value(ConfigsBus.onBlueUpdated().getValue());
        profile.setBrightness_value(ConfigsBus.onBrightnessUpdated().getValue());
        ProfilesBus.createRequest().onNext(profile);
    }

    private void showCreateProfileDialog() {
        // Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.create_profile_title);

        View viewInflated = LayoutInflater.from(getContext()).inflate
                (R.layout.profile_create_dialog, (ViewGroup) getView(), false);

        final EditText input = (EditText) viewInflated.findViewById(R.id.profileNameEdit);
        builder.setView(viewInflated);

        builder.setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                    String profileName = input.getText().toString();


                    if (profileName.isEmpty()) {
                        execToast(R.string.profile_name_required);
                    } else {
                        createNewProfileOnCurrentData(profileName);
                    }
                    dialogInterface.dismiss();
                })
                .setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.cancel());

        builder.show();
    }

    private void showDeleteProfileDialog() {
        if (selectedProfile != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(R.string.are_you_sure)
                    .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                        ProfilesBus.deleteRequest().onNext(selectedProfile.getId());
                        dialogInterface.dismiss();
                    })
                    .setNegativeButton(R.string.no, (dialogInterface, i) -> dialogInterface.cancel());

            builder.show();
        }
    }

    private void execToast(int resID) {
        Toast.makeText(getContext(), resID, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        for (Disposable element : subs)
            element.dispose();

        super.onDestroy();
    }
}