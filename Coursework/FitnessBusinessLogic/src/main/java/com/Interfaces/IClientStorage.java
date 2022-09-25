package com.Interfaces;

import com.BindingModels.ClientBindingModel;
import com.ViewModels.ClientViewModel;

import java.util.List;

public interface IClientStorage {
    List<ClientViewModel> getFullList();

    List<ClientViewModel> getFilteredList(ClientBindingModel model);

    ClientViewModel getElement(ClientBindingModel model);

    void update(ClientBindingModel model) throws Exception;
}