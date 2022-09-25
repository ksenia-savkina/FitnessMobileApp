package com.Activities;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.BindingModels.ClientBindingModel;
import com.BusinessLogic.ClientLogic;
import com.Implements.ClientStorage;
import com.ViewModels.ClientViewModel;
import com.ViewModels.ListViewClassItemViewModel;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ClassesFragment extends Fragment {

    private final ClientLogic logic;

    private int id;

    private ArrayAdapter<ListViewClassItemViewModel> adapter;

    private ListView listViewClasses;

    private List<ListViewClassItemViewModel> list;

    private TreeMap<Integer, Pair<String, Timestamp>> clientClasses;

    public ClassesFragment() {
        logic = new ClientLogic(new ClientStorage());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_classes, container, false);
        super.onCreate(savedInstanceState);
        id = getArguments().getInt(AuthorizationFragment.ID);

        listViewClasses = v.findViewById(R.id.listViewClasses);
        listViewClasses.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        loadData();

        listViewClasses.setClickable(true);
        listViewClasses.setOnItemClickListener((arg0, arg1, position, arg3) -> {
            DialogFragment dlgClass = new ClassFragment();
            Bundle args = new Bundle();
            ListViewClassItemViewModel selectedItem = list.get(position);
            args.putInt(AuthorizationFragment.ID, id);
            args.putInt(ClassFragment.TAG_CLASS_ID, selectedItem.id);
            dlgClass.setArguments(args);
            dlgClass.show(getFragmentManager(), "dlgClass");
        });

        Button buttonAttendedClasses = v.findViewById(R.id.buttonAttendedClasses);
        Button buttonUpcomingClasses = v.findViewById(R.id.buttonUpcomingClasses);

        View.OnClickListener oclBtn = v1 -> {
            Bundle args;
            switch (v1.getId()) {
                case R.id.buttonAttendedClasses:
                    args = new Bundle();
                    args.putInt(AuthorizationFragment.ID, id);
                    AttendedClassesFragment attendedClassesFragment = new AttendedClassesFragment();
                    attendedClassesFragment.setArguments(args);
                    getActivity().getFragmentManager().beginTransaction()
                            .replace(R.id.container, attendedClassesFragment)
                            .addToBackStack(null)
                            .commit();
                    break;
                case R.id.buttonUpcomingClasses:
                    args = new Bundle();
                    args.putInt(AuthorizationFragment.ID, id);
                    UpcomingClassesFragment upcomingClassesFragment = new UpcomingClassesFragment();
                    upcomingClassesFragment.setArguments(args);
                    getActivity().getFragmentManager().beginTransaction()
                            .replace(R.id.container, upcomingClassesFragment)
                            .addToBackStack(null)
                            .commit();
                    break;
            }
        };

        buttonAttendedClasses.setOnClickListener(oclBtn);
        buttonUpcomingClasses.setOnClickListener(oclBtn);
        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void loadData() {
        ClientBindingModel clientBindingModel = new ClientBindingModel();
        clientBindingModel.id = id;
        try {
            ClientViewModel view = logic.read(clientBindingModel).get(0);
            if (view != null) {
                clientClasses = view.clientAvailableClasses;
            }
            list = new ArrayList<>();
            for (Map.Entry<Integer, Pair<String, Timestamp>> cc : clientClasses.entrySet()) {
                ListViewClassItemViewModel itemViewModel = new ListViewClassItemViewModel();
                itemViewModel.id = cc.getKey();
                itemViewModel.name = cc.getValue().first;
                itemViewModel.date = cc.getValue().second;
                list.add(itemViewModel);
            }
            if (list != null) {
                adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, list);
                listViewClasses.setAdapter(adapter);
            }
        } catch (Exception ex) {
            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
