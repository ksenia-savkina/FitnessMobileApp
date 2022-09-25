package com.BusinessLogic;

import com.BindingModels.TestResultBindingModel;
import com.Interfaces.ITestResultStorage;
import com.ViewModels.TestResultViewModel;

import java.util.Arrays;
import java.util.List;

public class TestResultLogic {
    private final ITestResultStorage _testResultStorage;

    public TestResultLogic(ITestResultStorage testResultStorage) {
        _testResultStorage = testResultStorage;
    }

    public List<TestResultViewModel> read(TestResultBindingModel model) {
        if (model == null) {
            return _testResultStorage.getFullList();
        }
        if (model.id != null) {
            return Arrays.asList(_testResultStorage.getElement(model));
        }
        return _testResultStorage.getFilteredList(model);
    }
}