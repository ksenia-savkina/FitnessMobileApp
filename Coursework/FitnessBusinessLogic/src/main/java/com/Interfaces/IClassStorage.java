package com.Interfaces;

import com.BindingModels.ClassBindingModel;
import com.ViewModels.ClassViewModel;

import java.util.List;

public interface IClassStorage {
    List<ClassViewModel> getFullList();

    List<ClassViewModel> getFilteredList(ClassBindingModel model);

    ClassViewModel getElement(ClassBindingModel model);
}
