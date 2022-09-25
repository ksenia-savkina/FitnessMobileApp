package com.ViewModels;

import android.util.Pair;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.TreeMap;

import lombok.Data;

@Data
public class ClientViewModel {
    public Integer id;

    public String name;

    public String surname;

    public Date birthdate;

    public boolean testRequest;

    public String phone;

    public String login;

    public String password;

    public String statusName;

    public int statusId;

    public String testRequestStr;

    public TreeMap<Integer, Pair<String, Timestamp>> clientAttendedClasses;

    public TreeMap<Integer, Pair<String, Timestamp>> clientUpcomingClasses;

    public TreeMap<Integer, Pair<String, Timestamp>> clientAvailableClasses;
}