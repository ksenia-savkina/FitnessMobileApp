package com.Activities;

import android.app.DialogFragment;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.BindingModels.ClassBindingModel;
import com.BindingModels.ClientBindingModel;
import com.BusinessLogic.ClassLogic;
import com.BusinessLogic.ClientLogic;
import com.Implements.ClassStorage;
import com.Implements.ClientStorage;
import com.ViewModels.ClassViewModel;
import com.ViewModels.ClientViewModel;

import java.util.Map;
import java.util.TreeMap;

public class ClassFragment extends DialogFragment {

    public static final String TAG_CLASS_ID = "classId";

    private final ClassLogic classLogic;

    private final ClientLogic clientLogic;

    private Integer classId;

    private Integer clientId;

    public ClassFragment() {
        classLogic = new ClassLogic(new ClassStorage());
        clientLogic = new ClientLogic(new ClientStorage());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle(R.string.classTitle);
        View v = inflater.inflate(R.layout.fragment_class, null);

        classId = getArguments().getInt(TAG_CLASS_ID);
        clientId = getArguments().getInt(AuthorizationFragment.ID);

        TextView textViewClassName = v.findViewById(R.id.textViewClassName);
        TextView textViewTrainer = v.findViewById(R.id.textViewTrainer);
        TextView textViewStatuses = v.findViewById(R.id.textViewStatuses);
        TextView textViewDate = v.findViewById(R.id.textViewDate);
        TextView textViewClients = v.findViewById(R.id.textViewClients);
        Button buttonSignUp = v.findViewById(R.id.buttonSignUp);

        ClassViewModel view = null;
        if (classId != null) {
            ClassBindingModel classBindingModel = new ClassBindingModel();
            classBindingModel.id = classId;
            view = classLogic.read(classBindingModel).get(0);
            if (view != null) {
                textViewClassName.setText(view.name);
                textViewTrainer.setText("Тренер : " + view.trainerFIO);
                String str = "Статусы : ";
                for (Map.Entry<Integer, String> status : view.classStatuses.entrySet())
                    str += status.getValue() + " ";
                textViewStatuses.setText(str);
                textViewDate.setText("Дата : " + view.date);
                if (view.classClients != null)
                    textViewClients.setText("Записалось людей : " + view.classClients.size());
                else
                    textViewClients.setText("Записи людей отсутсвуют");
            }
        }

        ClassViewModel finalView = view;
        View.OnClickListener oclBtn = v1 -> {
            Bundle args = new Bundle();
            switch (v1.getId()) {
                case R.id.buttonSignUp:
                    try {
                        if (finalView != null) {
                            ClientBindingModel client = new ClientBindingModel();
                            client.id = clientId;
                            ClientViewModel clientViewModel = clientLogic.read(client).get(0);

                            if (clientViewModel.clientUpcomingClasses == null) {
                                client.clientUpcomingClasses = new TreeMap<>();
                            } else {
                                client.clientUpcomingClasses = clientViewModel.clientUpcomingClasses;
                            }

                            client.name = clientViewModel.name;
                            client.surname = clientViewModel.surname;
                            client.birthdate = clientViewModel.birthdate;
                            client.testRequest = clientViewModel.testRequest;
                            client.statusId = clientViewModel.statusId;
                            client.phone = clientViewModel.phone;
                            client.login = clientViewModel.login;
                            client.password = clientViewModel.password;


                            client.clientUpcomingClasses.put(classId, new Pair<>(finalView.name, finalView.date));
                            clientLogic.createOrUpdate(client);
                            Toast.makeText(getContext(), R.string.SuccessfulSigned, Toast.LENGTH_LONG).show();
                        }
                        dismiss();
                    } catch (Exception ex) {
                        Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        };

        buttonSignUp.setOnClickListener(oclBtn);
        return v;
    }
}
