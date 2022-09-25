package com.BindingModels;

import android.util.Pair;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.TreeMap;

public class ClientBindingModel {
    public Integer id;

    public String name;

    public String surname;

    public Date birthdate;

    public boolean testRequest;

    public String phone;

    public String login;

    public String password;

    public int statusId;

    public TreeMap<Integer, Pair<String, Timestamp>> clientUpcomingClasses;
}