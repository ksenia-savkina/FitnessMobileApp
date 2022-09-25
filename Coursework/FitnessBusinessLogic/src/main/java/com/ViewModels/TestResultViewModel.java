package com.ViewModels;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class TestResultViewModel {
    public Integer id;

    public boolean result;

    public Timestamp date;

    public int clientId;

    public String resultStr;
}
