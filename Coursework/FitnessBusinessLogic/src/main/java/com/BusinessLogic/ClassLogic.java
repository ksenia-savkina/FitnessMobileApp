package com.BusinessLogic;

import com.BindingModels.ClassBindingModel;
import com.Interfaces.IClassStorage;
import com.ViewModels.ClassViewModel;

import java.util.Arrays;
import java.util.List;

public class ClassLogic {
    private final IClassStorage _classStorage;

    public ClassLogic(IClassStorage classStorage) {
        _classStorage = classStorage;
    }

    public List<ClassViewModel> read(ClassBindingModel model) {
        if (model == null) {
            return _classStorage.getFullList();
        }
        if (model.id != null) {
            return Arrays.asList(_classStorage.getElement(model));
        }
        return _classStorage.getFilteredList(model);
    }
}
