package com.BusinessLogic;

import com.BindingModels.ClientBindingModel;
import com.Interfaces.IClientStorage;
import com.ViewModels.ClientViewModel;

import java.util.Arrays;
import java.util.List;

public class ClientLogic {
    private final IClientStorage _clientStorage;

    public ClientLogic(IClientStorage clientStorage) {
        _clientStorage = clientStorage;
    }

    public List<ClientViewModel> read(ClientBindingModel model) {
        if (model == null) {
            return _clientStorage.getFullList();
        }
        if (model.id != null) {
            return Arrays.asList(_clientStorage.getElement(model));
        }
        return _clientStorage.getFilteredList(model);
    }

    public void createOrUpdate(ClientBindingModel model) throws Exception {
        ClientBindingModel clientBindingModel = new ClientBindingModel();
        clientBindingModel.id = null;
        clientBindingModel.phone = model.phone;
        clientBindingModel.login = model.login;
        ClientViewModel element = _clientStorage.getElement(clientBindingModel);
        if (element != null && element.id != model.id) {
            throw new Exception("Уже есть пользователь с такими данными");
        }
        if (model.id != null) {
            _clientStorage.update(model);
        }
    }
}
