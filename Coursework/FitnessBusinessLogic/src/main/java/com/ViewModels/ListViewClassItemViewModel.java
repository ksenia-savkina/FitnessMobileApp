package com.ViewModels;

import java.sql.Timestamp;

public class ListViewClassItemViewModel {

    public Integer id;

    public String name;

    public Timestamp date;

    @Override
    public String toString() {
        return name + " " + date;
    }
}
