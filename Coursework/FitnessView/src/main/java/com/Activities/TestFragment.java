package com.Activities;

import android.app.DialogFragment;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.BindingModels.TestResultBindingModel;
import com.BusinessLogic.TestResultLogic;
import com.Implements.TestResultStorage;
import com.ViewModels.TestResultViewModel;

public class TestFragment extends DialogFragment {
    public static final String TAG_TEST_ID = "TAG_TEST_ID";

    private Integer id;

    private final TestResultLogic logic;

    public TestFragment() {
        logic = new TestResultLogic(new TestResultStorage());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle(R.string.testTitle);
        View v = inflater.inflate(R.layout.fragment_test, null);

        id = getArguments().getInt(TAG_TEST_ID);

        TextView textViewTestNumber = v.findViewById(R.id.textViewTestNumber);
        TextView textViewTestDate = v.findViewById(R.id.textViewTestDate);
        TextView textViewTestResult = v.findViewById(R.id.textViewTestResult);

        if (id != null) {
            TestResultBindingModel testResultBindingModel = new TestResultBindingModel();
            testResultBindingModel.id = id;
            TestResultViewModel view = logic.read(testResultBindingModel).get(0);
            if (view != null) {
                textViewTestNumber.setText("Тест №" + view.id);
                textViewTestDate.setText("Дата : " + view.date);
                if (view.result)
                    textViewTestResult.setText("Статус был повышен");
                else
                    textViewTestResult.setText("Статус не был изменен");
            }
        }
        return v;
    }
}