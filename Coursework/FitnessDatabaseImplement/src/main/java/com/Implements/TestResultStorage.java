package com.Implements;

import com.BindingModels.TestResultBindingModel;
import com.DbUtil.Database;
import com.Interfaces.ITestResultStorage;
import com.ViewModels.TestResultViewModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TestResultStorage implements ITestResultStorage {

    private final Connection connection;

    public TestResultStorage() {
        this.connection = Database.getConnection();
    }

    @Override
    public List<TestResultViewModel> getFullList() {
        List<TestResultViewModel> testResultViewModels = new ArrayList<>();
        String sql = "SELECT * FROM TESTRESULT ORDER BY ID";
        Thread thread = new Thread(() -> {
            try (Statement statement = connection.createStatement()) {
                ResultSet set = statement.executeQuery(sql);
                while (set.next()) {
                    TestResultViewModel testResultViewModel = new TestResultViewModel();
                    testResultViewModel.id = set.getInt(1);
                    testResultViewModel.result = set.getBoolean(2);
                    testResultViewModel.date = set.getTimestamp(3);
                    testResultViewModel.clientId = set.getInt(4);
                    testResultViewModels.add(testResultViewModel);
                }
            } catch (Exception e) {
                System.out.print(e.getMessage());
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return testResultViewModels;
    }

    @Override
    public List<TestResultViewModel> getFilteredList(TestResultBindingModel model) {
        if (model == null) {
            return null;
        }
        List<TestResultViewModel> testResultViewModels = new ArrayList<>();
        String sql = "SELECT * FROM TESTRESULT WHERE clientId = ?";
        Thread thread = new Thread(() -> {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, model.clientId);
                ResultSet set = statement.executeQuery();
                while (set.next()) {
                    TestResultViewModel testResultViewModel = new TestResultViewModel();
                    testResultViewModel.id = set.getInt(1);
                    testResultViewModel.result = set.getBoolean(2);
                    testResultViewModel.date = set.getTimestamp(3);
                    testResultViewModel.clientId = set.getInt(4);
                    testResultViewModels.add(testResultViewModel);
                }
            } catch (Exception e) {
                System.out.print(e.getMessage());
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return testResultViewModels;
    }

    @Override
    public TestResultViewModel getElement(TestResultBindingModel model) {
        if (model == null) {
            return null;
        }
        List<TestResultViewModel> testResultViewModels = new ArrayList<>();
        Thread thread = new Thread(() -> {
            String sql = "SELECT * FROM TESTRESULT";
            if (model.id != null)
                sql += " WHERE id = ?";
            else if (model.date != null)
                sql += " WHERE date = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                if (model.id != null)
                    statement.setInt(1, model.id);
                else if (model.date != null)
                    statement.setTimestamp(1, model.date);
                ResultSet set = statement.executeQuery();
                while (set.next()) {
                    TestResultViewModel testResultViewModel = new TestResultViewModel();
                    testResultViewModel.id = set.getInt(1);
                    testResultViewModel.result = set.getBoolean(2);
                    testResultViewModel.date = set.getTimestamp(3);
                    testResultViewModel.clientId = set.getInt(4);
                    testResultViewModels.add(testResultViewModel);
                }
            } catch (Exception e) {
                System.out.print(e.getMessage());
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (testResultViewModels.size() > 0)
            return testResultViewModels.get(0);
        return null;
    }
}
