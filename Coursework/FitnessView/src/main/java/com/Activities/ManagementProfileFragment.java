package com.Activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.BindingModels.ClientBindingModel;
import com.BusinessLogic.ClientLogic;
import com.Implements.ClientStorage;
import com.ViewModels.ClientViewModel;

import java.sql.Date;
import java.util.Calendar;

public class ManagementProfileFragment extends Fragment {

    private final ClientLogic logic;

    private Integer id;

    private EditText editTextF_Name;
    private EditText editTextL_Name;
    private EditText editTextLogin;
    private EditText editTextPassword;
    private EditText editTextPhone;
    private EditText editTextDate;

    private Switch toggleButton;

    private Calendar dateAndTime;

    public ManagementProfileFragment() {
        logic = new ClientLogic(new ClientStorage());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_managementprofile, container, false);
        super.onCreate(savedInstanceState);
        id = getArguments().getInt(AuthorizationFragment.ID);

        editTextF_Name = v.findViewById(R.id.editTextF_Name);
        editTextL_Name = v.findViewById(R.id.editTextL_Name);
        editTextLogin = v.findViewById(R.id.editTextLogin);
        editTextPassword = v.findViewById(R.id.editTextPassword);
        editTextPhone = v.findViewById(R.id.editTextPhone);
        editTextDate = v.findViewById(R.id.editTextDate);
        toggleButton = v.findViewById(R.id.toggleButton);

        ImageButton buttonDate = v.findViewById(R.id.buttonDate);
        Button buttonSave = v.findViewById(R.id.buttonSave);
        Button buttonCancel = v.findViewById(R.id.buttonCancel);

        if (id != null) {
            try {
                ClientBindingModel model = new ClientBindingModel();
                model.id = id;
                ClientViewModel view = logic.read(model).get(0);
                if (view != null) {
                    editTextL_Name.setText(view.name);
                    editTextF_Name.setText(view.surname);
                    editTextDate.setText(DateUtils.formatDateTime(getContext(), view.birthdate.getTime(),
                            DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR
                                    | DateUtils.FORMAT_SHOW_TIME));
                    toggleButton.setChecked(view.testRequest);
                    editTextPhone.setText(view.phone);
                    editTextLogin.setText(view.login);
                    editTextPassword.setText(view.password);
                }
            } catch (Exception ex) {
                Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        dateAndTime = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener d = (view, year, monthOfYear, dayOfMonth) -> {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDateTime();
        };

        View.OnClickListener oclBtn = v1 -> {
            Bundle args;
            switch (v1.getId()) {
                case R.id.buttonDate: {

                    new DatePickerDialog(getContext(), d,
                            dateAndTime.get(Calendar.YEAR),
                            dateAndTime.get(Calendar.MONTH),
                            dateAndTime.get(Calendar.DAY_OF_MONTH))
                            .show();
                    break;
                }
                case R.id.buttonSave:
                    if (checkFields()) {
                        try {
                            ClientBindingModel model = new ClientBindingModel();
                            model.id = id;

                            ClientViewModel clientViewModel = logic.read(model).get(0);

                            model.name = editTextL_Name.getText().toString();
                            model.surname = editTextF_Name.getText().toString();
                            model.birthdate = new Date(dateAndTime.getTimeInMillis());
                            model.testRequest = toggleButton.isChecked();
                            model.phone = editTextPhone.getText().toString();
                            model.login = editTextLogin.getText().toString();
                            model.password = editTextPassword.getText().toString();
                            model.clientUpcomingClasses = clientViewModel.clientUpcomingClasses;
                            model.statusId = clientViewModel.statusId;

                            logic.createOrUpdate(model);
                            Toast.makeText(getContext(), R.string.SuccessfulAdding, Toast.LENGTH_LONG).show();
                            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, new Intent());
                            getActivity().onBackPressed();
                        } catch (Exception ex) {
                            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                    break;
                case R.id.buttonCancel:
                    getActivity().onBackPressed();
                    break;
            }
        };

        buttonSave.setOnClickListener(oclBtn);
        buttonCancel.setOnClickListener(oclBtn);
        buttonDate.setOnClickListener(oclBtn);
        return v;
    }

    private void setInitialDateTime() {
        editTextDate.setText(DateUtils.formatDateTime(getContext(),
                dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR
                        | DateUtils.FORMAT_SHOW_TIME));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean checkFields() {
        if (editTextF_Name.getText().length() == 0) {
            Toast.makeText(getContext(), R.string.EmptyEditTextF_Name, Toast.LENGTH_LONG).show();
            return false;
        }
        if (editTextL_Name.getText().length() == 0) {
            Toast.makeText(getContext(), R.string.EmptyEditTextL_Name, Toast.LENGTH_LONG).show();
            return false;
        }
        if (editTextLogin.getText().length() == 0) {
            Toast.makeText(getContext(), R.string.EmptyEditTextLogin, Toast.LENGTH_LONG).show();
            return false;
        }
        if (editTextPassword.getText().length() == 0) {
            Toast.makeText(getContext(), R.string.EmptyEditTextPassword, Toast.LENGTH_LONG).show();
            return false;
        }
        if (editTextDate.getText().length() == 0) {
            Toast.makeText(getContext(), R.string.EmptyEditDate, Toast.LENGTH_LONG).show();
            return false;
        }
        if (editTextPhone.getText().length() == 0) {
            Toast.makeText(getContext(), R.string.EmptyEditTextPhone, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}