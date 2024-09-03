package com.olosnet.simpleflat.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.olosnet.simpleflat.R;
import com.olosnet.simpleflat.adapters.ProfileSpinAdapter;
import com.olosnet.simpleflat.buses.ConfigsBus;
import com.olosnet.simpleflat.buses.ProfilesBus;
import com.olosnet.simpleflat.common.ProfilesExclusionStrategy;
import com.olosnet.simpleflat.database.ImportProfilesType;
import com.olosnet.simpleflat.database.ProfilesModel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.io.File;
import java.util.Locale;
import java.util.Objects;

import io.reactivex.rxjava3.disposables.Disposable;

public class ProfileFragment extends Fragment {

    private final List<Disposable> subs = new ArrayList<>();
    private List<ProfilesModel> profiles;
    private ProfilesModel selectedProfile;
    private boolean firstSelection = true;
    private File profilesDirectory;
    private boolean profilesDirWritable = false;
    private String importSelectedFile = null;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        String rootOut = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
        this.profilesDirectory = new File(rootOut, "SimpleFlat");
        this.profilesDirWritable = true;

        if (!this.profilesDirectory.exists()) {
            this.profilesDirWritable = profilesDirectory.mkdirs();
        }

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
        save_button.setOnClickListener(l -> updateAndSaveSelectedProfile());

        Button load_button = view.findViewById(R.id.loadButton);
        load_button.setOnClickListener(l -> loadSelectedProfile());

        Button export_button = view.findViewById(R.id.exportButton);
        export_button.setOnClickListener(l -> exportProfiles());

        Button import_button = view.findViewById(R.id.importButton);
        import_button.setOnClickListener(l -> showImportProfilesDialog());

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

        subs.add(ProfilesBus.onImported().subscribe(value -> {
            ProfilesBus.loadRequest().onNext(true);
            execToast(R.string.import_successful);
        }));

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

        subs.add(ProfilesBus.onDeleted().subscribe(value -> {
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

    private void showImportProfilesDialog() {
        importSelectedFile = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.import_profile_dialog, (ViewGroup) getView(), false);

        builder.setTitle(R.string.profiles_files);
        builder.setView(viewInflated);
        builder.setPositiveButton(R.string.simport, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Do nothing here because we override this button later to change the close behaviour.
                //However, we still need this because on older versions of Android unless we
                //pass a handler the button doesn't get instantiated
            }
        }).setNegativeButton(R.string.cancel, ((dialogInterface, i) -> dialogInterface.cancel()));


        File[] files = profilesDirectory.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));
        String[] fstring = new String[Objects.requireNonNull(files).length];
        for (int i = 0; i < files.length; i++) {
            fstring[i] = files[i].getName();
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(requireContext(), R.layout.import_profile_dialog, R.id.profileListTextView, fstring);

        final ListView filelist = (ListView) viewInflated.findViewById(R.id.profileListView);
        filelist.setAdapter(arrayAdapter);

        filelist.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        int color = ContextCompat.getColor(requireContext(), com.google.android.material.R.color.material_on_surface_disabled);
                        view.setBackgroundColor(color);
                        importSelectedFile = (String) filelist.getItemAtPosition(i);
                    }
                }
        );


        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (importSelectedFile == null || importSelectedFile.isEmpty()) {
                    execToast(R.string.select_filename_please);
                } else {
                    dialog.dismiss();
                    showRequestOverrideProfilesDialog();
                }
            }
        });

    }

    private void showRequestOverrideProfilesDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.delete_old_profile_request)
                .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    importProfiles(true);
                })
                .setNegativeButton(R.string.no, (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    importProfiles(false);
                });

        builder.show();

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

    private void exportProfiles() {

        boolean err = false;

        if (this.profilesDirWritable) {

            // Convert profiles to JSON
            Gson gson = new GsonBuilder().setExclusionStrategies(new ProfilesExclusionStrategy()).create();
            String json = gson.toJson(profiles);

            // Export JSON
            long current_time = new Date().getTime();
            String filename = String.format(Locale.getDefault(), "profiles_exported_%d.json", current_time);

            File outFilePath = new File(profilesDirectory, filename);

            try {
                FileWriter out = new FileWriter(outFilePath);
                out.write(json);
                out.close();
            } catch (IOException e) {
                err = true;
            }

            if (this.profilesDirWritable) {
                String message = String.format(Locale.getDefault(), "%s %s", getString(R.string.profile_export_success), outFilePath.toString());
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            }
        } else {
            err = true;
        }

        if (err) {
            execToast(R.string.profile_export_failed);
        }
    }

    private void importProfiles(boolean clearOld) {
        boolean err = false;

        if (this.profilesDirWritable) {
            Gson gson = new GsonBuilder().setExclusionStrategies(new ProfilesExclusionStrategy()).create();

            File inFilePath = new File(profilesDirectory, this.importSelectedFile);
            String content = null;
            List<ProfilesModel> target = new LinkedList<ProfilesModel>();

            try {
                BufferedReader reader = new BufferedReader(new FileReader(inFilePath));
                StringBuilder stringBuilder = new StringBuilder();
                String s;
                while ((s = reader.readLine()) != null) {
                    stringBuilder.append(s);
                }
                reader.close();

                content = stringBuilder.toString();


            } catch (IOException e) {
                err = true;
            }

            try {
                Type listType = new TypeToken<List<ProfilesModel>>() {
                }.getType();
                List<ProfilesModel> iprofiles = gson.fromJson(content, listType);

                if (!profiles.isEmpty()) {
                    ImportProfilesType importProfiles = new ImportProfilesType();
                    importProfiles.models = iprofiles;
                    importProfiles.remove_old = clearOld;
                    ProfilesBus.importRequest().onNext(importProfiles);
                } else {
                    execToast(R.string.profile_import_failed_empty);
                }

            } catch (JsonSyntaxException e) {
                err = true;
            }
        } else {
            err = true;
        }

        if (err) {
            execToast(R.string.profile_import_failed);
        }
    }
}