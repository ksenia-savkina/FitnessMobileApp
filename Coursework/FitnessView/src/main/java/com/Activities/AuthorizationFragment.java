package com.Activities;

import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.BindingModels.ClientBindingModel;
import com.BusinessLogic.ClientLogic;
import com.Implements.ClientStorage;
import com.ViewModels.ClientViewModel;

import java.util.List;

public class AuthorizationFragment extends Fragment {

    private final ClientLogic logic;
    public final static String ID = "id";

    EditText editTextLogin;
    EditText editTextPassword;

    public AuthorizationFragment() {
        logic = new ClientLogic(new ClientStorage());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_authorization, container, false);

        editTextLogin = v.findViewById(R.id.username);
        editTextPassword = v.findViewById(R.id.password);
        Button buttonEnter = v.findViewById(R.id.login);
        Button buttonCancel = v.findViewById(R.id.cancel);

        View.OnClickListener oclBtn = v1 -> {
            switch (v1.getId()) {
                case R.id.login:
                    if (checkFields()) {
                        try {
                            ClientBindingModel clientBindingModel = new ClientBindingModel();
                            clientBindingModel.login = editTextLogin.getText().toString();
                            clientBindingModel.password = editTextPassword.getText().toString();

                            List<ClientViewModel> list = logic.read(clientBindingModel);
                            if (list != null && list.size() > 0) {
                                //Intent intent = new Intent(getActivity(), ClassesActivity.class);
                                Intent intent = new Intent(getActivity(), HomeActivity.class);
                                intent.putExtra(ID, list.get(0).id);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getContext(), R.string.InvalidLogin, Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception ex) {
                            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                    break;
                case R.id.cancel:
                    getActivity().onBackPressed();
                    break;
            }
        };

        buttonEnter.setOnClickListener(oclBtn);
        buttonCancel.setOnClickListener(oclBtn);

        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean checkFields() {
        if (editTextLogin.getText().length() == 0) {
            Toast.makeText(getContext(), R.string.EmptyEditTextLogin, Toast.LENGTH_LONG).show();
            return false;
        }
        if (editTextPassword.getText().length() == 0) {
            Toast.makeText(getContext(), R.string.EmptyEditTextPassword, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
