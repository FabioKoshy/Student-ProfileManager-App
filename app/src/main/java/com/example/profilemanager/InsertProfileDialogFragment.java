package com.example.profilemanager;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class InsertProfileDialogFragment extends DialogFragment {

    private DatabaseHelper dbHelper;
    private EditText etName, etSurname, etProfileID, etGPA;
    private Context context;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        context = getActivity();
        dbHelper = new DatabaseHelper(context);

        // Inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_insert_profile, null);
        etName = view.findViewById(R.id.et_profile_name);
        etSurname = view.findViewById(R.id.et_profile_surname);
        etProfileID = view.findViewById(R.id.et_profile_id);
        etGPA = view.findViewById(R.id.et_profile_gpa);
        Button btnSave = view.findViewById(R.id.btn_save);
        Button btnCancel = view.findViewById(R.id.btn_cancel);

        // Save Button Click
        btnSave.setOnClickListener(v -> saveProfile());

        // Cancel Button Click
        btnCancel.setOnClickListener(v -> dismiss());

        // Create dialog
        Dialog dialog = new Dialog(context);
        dialog.setContentView(view);
        dialog.setCancelable(true);
        return dialog;
    }

    private void saveProfile() {
        String name = etName.getText().toString().trim();
        String surname = etSurname.getText().toString().trim();
        String idText = etProfileID.getText().toString().trim();
        String gpaText = etGPA.getText().toString().trim();

        // Input validation
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(surname) || TextUtils.isEmpty(idText) || TextUtils.isEmpty(gpaText)) {
            Toast.makeText(context, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        int profileID;
        float gpa;
        try {
            profileID = Integer.parseInt(idText);
            gpa = Float.parseFloat(gpaText);

            if (profileID < 10000000 || profileID > 99999999) {
                Toast.makeText(context, "Profile ID must be 8 digits!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (gpa < 0 || gpa > 4.3) {
                Toast.makeText(context, "GPA must be between 0 and 4.3!", Toast.LENGTH_SHORT).show();
                return;
            }

        } catch (NumberFormatException e) {
            Toast.makeText(context, "Invalid number format!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Insert into database
        if (dbHelper.insertProfile(profileID, name, surname, gpa)) {
            Toast.makeText(context, "Profile added successfully!", Toast.LENGTH_SHORT).show();
            dismiss();
        } else {
            Toast.makeText(context, "Profile ID already exists!", Toast.LENGTH_SHORT).show();
        }
    }
}
