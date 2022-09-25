package com.ViewModels;

import java.sql.Timestamp;

public class ListViewTest {

    public Integer id;

    public Timestamp date;

    @Override
    public String toString() {
        return "Тест №" + id + " " + date;
    }
}
