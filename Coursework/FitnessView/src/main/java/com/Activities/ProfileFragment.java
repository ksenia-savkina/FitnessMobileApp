package com.Activities;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.BindingModels.ClientBindingModel;
import com.BindingModels.TestResultBindingModel;
import com.BusinessLogic.ClientLogic;
import com.BusinessLogic.TestResultLogic;
import com.Implements.ClientStorage;
import com.Implements.TestResultStorage;
import com.ViewModels.ClientViewModel;
import com.ViewModels.ListViewTest;
import com.ViewModels.TestResultViewModel;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private static final int REQUEST_EDIT = 1;

    private final TestResultLogic testResultLogic;

    private final ClientLogic clientLogic;

    private int id;

    private ArrayAdapter<ListViewTest> adapter;

    private ListView listViewTests;

    private List<ListViewTest> list;

    private TextView textViewFIO;

    private TextView textViewStatus;

    private TextView textViewTestRequest;

    public ProfileFragment() {
        clientLogic = new ClientLogic(new ClientStorage());
        testResultLogic = new TestResultLogic(new TestResultStorage());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        super.onCreate(savedInstanceState);
        id = getArguments().getInt(AuthorizationFragment.ID);

        listViewTests = v.findViewById(R.id.listViewTests);
        listViewTests.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        textViewFIO = v.findViewById(R.id.textViewFIO);
        textViewStatus = v.findViewById(R.id.textViewStatus);
        textViewTestRequest = v.findViewById(R.id.textViewTestRequest);


        Button buttonEdit = v.findViewById(R.id.buttonEdit);
        Button buttonSignUpTest = v.findViewById(R.id.buttonSignUpTest);

        ClientViewModel finalView = loadData();
        View.OnClickListener oclBtn = v1 -> {
            Bundle args;
            switch (v1.getId()) {
                case R.id.buttonEdit:
                    args = new Bundle();
                    args.putInt(AuthorizationFragment.ID, id);
                    ManagementProfileFragment managementProfileFragment = new ManagementProfileFragment();
                    managementProfileFragment.setArguments(args);
                    managementProfileFragment.setTargetFragment(this, REQUEST_EDIT);
                    getActivity().getFragmentManager().beginTransaction()
                            .replace(R.id.container, managementProfileFragment)
                            .addToBackStack(null)
                            .commit();

                    break;
                case R.id.buttonSignUpTest:
                    try {
                        if (finalView != null) {
                            ClientBindingModel client = new ClientBindingModel();
                            client.id = id;
                            ClientViewModel clientViewModel = clientLogic.read(client).get(0);
                            client.name = clientViewModel.name;
                            client.surname = clientViewModel.surname;
                            client.birthdate = clientViewModel.birthdate;
                            client.testRequest = true;
                            client.statusId = clientViewModel.statusId;
                            client.phone = clientViewModel.phone;
                            client.login = clientViewModel.login;
                            client.password = clientViewModel.password;
                            client.clientUpcomingClasses = clientViewModel.clientUpcomingClasses;
                            clientLogic.createOrUpdate(client);
                            Toast.makeText(getContext(), R.string.SuccessfulSignedTest, Toast.LENGTH_LONG).show();
                            loadData();
                        }
                    } catch (Exception ex) {
                        Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        };

        listViewTests.setClickable(true);
        listViewTests.setOnItemClickListener((arg0, arg1, position, arg3) -> {
            DialogFragment dlgTest = new TestFragment();
            Bundle args = new Bundle();
            ListViewTest selectedItem = list.get(position);
            args.putInt(TestFragment.TAG_TEST_ID, selectedItem.id);
            dlgTest.setArguments(args);
            dlgTest.show(getFragmentManager(), "dlgTest");
        });


        buttonEdit.setOnClickListener(oclBtn);
        buttonSignUpTest.setOnClickListener(oclBtn);
        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private ClientViewModel loadData() {
        ClientBindingModel clientBindingModel = new ClientBindingModel();
        clientBindingModel.id = id;
        ClientViewModel view = null;
        try {
            view = clientLogic.read(clientBindingModel).get(0);
            textViewFIO.setText(view.surname + " " + view.name);
            textViewStatus.setText("Статус : " + view.statusName);
            if (view.testRequest)
                textViewTestRequest.setText("Заявка на тест : есть");
            else
                textViewTestRequest.setText("Заявка на тест : нет");

        } catch (Exception ex) {
            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
        TestResultBindingModel testResultBindingModel = new TestResultBindingModel();
        testResultBindingModel.clientId = id;
        try {
            List<TestResultViewModel> testResultViewModels = testResultLogic.read(testResultBindingModel);
            list = new ArrayList<>();
            for (TestResultViewModel testResultViewModel : testResultViewModels) {
                ListViewTest itemViewModel = new ListViewTest();
                itemViewModel.id = testResultViewModel.id;
                itemViewModel.date = testResultViewModel.date;
                list.add(itemViewModel);
            }
            if (list != null) {
                adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, list);
                listViewTests.setAdapter(adapter);
            }
        } catch (Exception ex) {
            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_EDIT) {
                loadData();
            }
        }
    }
}