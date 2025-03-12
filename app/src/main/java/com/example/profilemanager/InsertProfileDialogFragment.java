package com.example.profilemanager;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class InsertProfileDialogFragment extends DialogFragment {

    private EditText editName, editSurname, editId, editGpa;
    private DatabaseHelper dbHelper;
    private OnProfileAddedListener listener;

    public interface OnProfileAddedListener {
        void onProfileAdded();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnProfileAddedListener) {
            listener = (OnProfileAddedListener) context;
        } else {
            throw new RuntimeException(context + " must implement OnProfileAddedListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(requireContext(), R.style.CustomDialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_insert_profile, null);
        dialog.setContentView(view);

        editName = view.findViewById(R.id.editName);
        editSurname = view.findViewById(R.id.editSurname);
        editId = view.findViewById(R.id.editId);
        editGpa = view.findViewById(R.id.editGpa);

        editName.setCustomSelectionActionModeCallback(null);
        editSurname.setCustomSelectionActionModeCallback(null);
        editId.setCustomSelectionActionModeCallback(null);
        editGpa.setCustomSelectionActionModeCallback(null);

        Button saveButton = view.findViewById(R.id.saveButton);
        Button cancelButton = view.findViewById(R.id.cancelButton);

        dbHelper = getActivity() != null ? new DatabaseHelper(getActivity()) : null;

        saveButton.setOnClickListener(v -> saveProfile());
        cancelButton.setOnClickListener(v -> {
            closeKeyboard();
            dismiss();
        });

        return dialog;
    }

    private void saveProfile() {
        if (dbHelper == null) {
            Toast.makeText(getContext(), "Database error", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = editName.getText().toString().trim();
        String surname = editSurname.getText().toString().trim();
        String id = editId.getText().toString().trim();
        String gpa = editGpa.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(surname) || TextUtils.isEmpty(id) || TextUtils.isEmpty(gpa)) {
            Toast.makeText(getContext(), "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!name.matches("^[A-Za-z]+$") || !surname.matches("^[A-Za-z]+$")) {
            Toast.makeText(getContext(), "Name and Surname must contain only letters!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!id.matches("\\d{8}")) {
            Toast.makeText(getContext(), "ID must be an 8-digit number!", Toast.LENGTH_SHORT).show();
            return;
        }

        int profileId;
        try {
            profileId = Integer.parseInt(id);
            if (profileId < 10000000 || profileId > 99999999) {
                Toast.makeText(getContext(), "Profile ID must be between 10000000 and 99999999!", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid Profile ID format!", Toast.LENGTH_SHORT).show();
            return;
        }

        float gpaValue;
        try {
            gpaValue = Float.parseFloat(gpa);
            if (!(gpaValue >= 0 && gpaValue <= 4.4)) {
                Toast.makeText(getContext(), "GPA must be between 0 and 4.4!", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid GPA format!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dbHelper.isProfileIdExists(profileId)) {
            Toast.makeText(getContext(), "Profile ID already exists!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dbHelper.addProfile(profileId, name, surname, gpaValue)) {
            Toast.makeText(getContext(), "Profile added successfully!", Toast.LENGTH_SHORT).show();
            if (listener != null) listener.onProfileAdded();
            closeKeyboard();
            dismiss();
        } else {
            Toast.makeText(getContext(), "Failed to add profile", Toast.LENGTH_SHORT).show();
        }
    }

    private void closeKeyboard() {
        View view = getDialog() != null ? getDialog().getCurrentFocus() : null;
        if (view != null && getContext() != null) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}