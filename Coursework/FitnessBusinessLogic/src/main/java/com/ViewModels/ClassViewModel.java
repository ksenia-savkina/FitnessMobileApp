package com.ViewModels;

import android.util.Pair;

import java.sql.Timestamp;
import java.util.TreeMap;

import lombok.Data;

@Data
public class ClassViewModel {
    public Integer id;

    public String name;

    public Timestamp date;

    public String trainerFIO;

    public int trainerId;

    public TreeMap<Integer, Pair<String, String>> classClients;

    public TreeMap<Integer, String> classStatuses;
}