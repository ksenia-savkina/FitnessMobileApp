package com.Implements;

import android.util.Pair;

import com.BindingModels.ClientBindingModel;
import com.DbUtil.Database;
import com.Interfaces.IClientStorage;
import com.ViewModels.ClientViewModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class ClientStorage implements IClientStorage {
    private final Connection connection;

    public ClientStorage() {
        this.connection = Database.getConnection();
    }

    @Override
    public List<ClientViewModel> getFullList() {
        List<ClientViewModel> clientViewModels = new ArrayList<>();

        String sql = "SELECT CL.id, CL.name, CL.surname, CL.birthdate, CL.testRequest, CL.phone, CL.Login, " +
                " CL.Password, CL.StatusId, S.name AS StatusName" +
                " FROM CLIENT CL JOIN Status S on S.id = CL.StatusId" +
                " ORDER BY CL.testRequest";
        Thread thread = new Thread(() -> {
            try (Statement statement = connection.createStatement()) {
                ResultSet set = statement.executeQuery(sql);
                while (set.next()) {
                    ClientViewModel clientViewModel = new ClientViewModel();
                    clientViewModel.id = set.getInt(1);
                    clientViewModel.name = set.getString(2);
                    clientViewModel.surname = set.getString(3);
                    clientViewModel.birthdate = set.getDate(4);
                    clientViewModel.testRequest = set.getBoolean(5);
                    clientViewModel.phone = set.getString(6);
                    clientViewModel.login = set.getString(7);
                    clientViewModel.password = set.getString(8);
                    clientViewModel.statusId = set.getInt(9);
                    clientViewModel.statusName = set.getString(10);
                    clientViewModels.add(clientViewModel);
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
        return clientViewModels;
    }

    @Override
    public List<ClientViewModel> getFilteredList(ClientBindingModel model) {
        if (model == null) {
            return null;
        }
        List<ClientViewModel> clientViewModels = new ArrayList<>();
        String sql = "SELECT CL.id, CL.name, CL.surname, CL.birthdate, CL.testRequest, CL.phone, CL.Login, " +
                " CL.Password, CL.StatusId, S.name AS StatusName, C.id as ClassId, C.name as ClassName, C.date FROM CLIENT CL" +
                " JOIN Status S on S.id = CL.StatusId" +
                " JOIN Status_Class SC on SC.StatusId = S.id " +
                " JOIN Class C on SC.classId = C.id" +
                " WHERE cl.StatusId = SC.StatusId and CL.login = ? and cl.password = ? and date > NOW()";
        Thread thread = new Thread(() -> {
            try {
                PreparedStatement statement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                statement.setString(1, model.login);
                statement.setString(2, model.password);
                int id = -1;
                ResultSet set = statement.executeQuery();
                ClientViewModel clientViewModel = null;
                while (set.next()) {
                    int newId = set.getInt("id");
                    if (newId != id) {
                        clientViewModel = new ClientViewModel();
                        clientViewModel.clientAvailableClasses = new TreeMap<>();
                        clientViewModel.id = set.getInt(1);
                        clientViewModel.name = set.getString(2);
                        clientViewModel.surname = set.getString(3);
                        clientViewModel.birthdate = set.getDate(4);
                        clientViewModel.testRequest = set.getBoolean(5);
                        clientViewModel.phone = set.getString(6);
                        clientViewModel.login = set.getString(7);
                        clientViewModel.password = set.getString(8);
                        clientViewModel.statusId = set.getInt(9);
                        clientViewModel.statusName = set.getString(10);
                    }

                    clientViewModel.clientAvailableClasses.put(set.getInt("classId"),
                            new Pair<>(set.getString("className"), set.getTimestamp("date")));

                    if (set.next()) {
                        if (newId != set.getInt("id"))
                            clientViewModels.add(clientViewModel);
                        set.previous();
                    } else {
                        clientViewModels.add(clientViewModel);
                        break;
                    }
                    id = set.getInt("id");
                }
                String sql2 = "SELECT CL.id, CL.name, CL.surname, CL.birthdate, CL.testRequest, CL.phone, CL.Login, " +
                        " CL.Password, CL.StatusId, C.id as ClassId, C.name as ClassName, C.date FROM CLIENT CL" +
                        " JOIN Client_Class CC on CL.id = CC.ClientId" +
                        " JOIN Class C on CC.classId = C.id" +
                        " WHERE CL.login = ? and cl.password = ?";
                String sqlUpcoming = sql2 + " and date > NOW()";
                String sqlAttended = sql2 + " and date <= NOW()";

                statement = connection.prepareStatement(sqlAttended, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                statement.setString(1, model.login);
                statement.setString(2, model.password);
                id = -1;
                set = statement.executeQuery();
                clientViewModel = null;
                while (set.next()) {
                    int newId = set.getInt("id");

                    if (newId != id) {
                        for (ClientViewModel item : clientViewModels) {
                            if (item.id == newId) {
                                clientViewModel = item;
                                clientViewModel.clientAttendedClasses = new TreeMap<>();
                            }
                        }
                    }

                    clientViewModel.clientAttendedClasses.put(set.getInt("classId"),
                            new Pair<>(set.getString("className"), set.getTimestamp("date")));
                    if (set.next())
                        set.previous();
                    else
                        break;
                    id = set.getInt("id");
                }

                statement = connection.prepareStatement(sqlUpcoming, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                statement.setString(1, model.login);
                statement.setString(2, model.password);
                id = -1;
                set = statement.executeQuery();
                clientViewModel = null;
                while (set.next()) {
                    int newId = set.getInt("id");

                    if (newId != id) {
                        for (ClientViewModel item : clientViewModels) {
                            if (item.id == newId) {
                                clientViewModel = item;
                                clientViewModel.clientUpcomingClasses = new TreeMap<>();
                            }
                        }
                    }

                    clientViewModel.clientUpcomingClasses.put(set.getInt("classId"),
                            new Pair<>(set.getString("className"), set.getTimestamp("date")));
                    if (set.next())
                        set.previous();
                    else
                        break;
                    id = set.getInt("id");
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
        return clientViewModels;
    }

    @Override
    public ClientViewModel getElement(ClientBindingModel model) {
        if (model == null) {
            return null;
        }
        List<ClientViewModel> clientViewModels = new ArrayList<>();
        String sql = "SELECT CL.id, CL.name, CL.surname, CL.birthdate, CL.testRequest, CL.phone, CL.Login, " +
                " CL.Password, CL.StatusId, S.name AS StatusName, C.id as ClassId, C.name as ClassName, C.date FROM CLIENT CL" +
                " JOIN Status S on S.id = CL.StatusId" +
                " JOIN Status_Class SC on SC.StatusId = S.id " +
                " JOIN Class C on SC.classId = C.id" +
                " WHERE cl.StatusId = SC.StatusId and CL.id = ? and date > NOW()";
        Thread thread = new Thread(() -> {
            try {
                PreparedStatement statement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                statement.setInt(1, model.id);
                int id = -1;
                ResultSet set = statement.executeQuery();
                ClientViewModel clientViewModel = null;
                while (set.next()) {
                    int newId = set.getInt("id");
                    if (newId != id) {
                        clientViewModel = new ClientViewModel();
                        clientViewModel.clientAvailableClasses = new TreeMap<>();
                        clientViewModel.id = set.getInt(1);
                        clientViewModel.name = set.getString(2);
                        clientViewModel.surname = set.getString(3);
                        clientViewModel.birthdate = set.getDate(4);
                        clientViewModel.testRequest = set.getBoolean(5);
                        clientViewModel.phone = set.getString(6);
                        clientViewModel.login = set.getString(7);
                        clientViewModel.password = set.getString(8);
                        clientViewModel.statusId = set.getInt(9);
                        clientViewModel.statusName = set.getString(10);
                    }

                    clientViewModel.clientAvailableClasses.put(set.getInt("classId"),
                            new Pair<>(set.getString("className"), set.getTimestamp("date")));

                    if (set.next()) {
                        if (newId != set.getInt("id"))
                            clientViewModels.add(clientViewModel);
                        set.previous();
                    } else {
                        clientViewModels.add(clientViewModel);
                        break;
                    }
                    id = set.getInt("id");
                }
                String sql2 = "SELECT CL.id, CL.name, CL.surname, CL.birthdate, CL.testRequest, CL.phone, CL.Login, " +
                        " CL.Password, CL.StatusId, C.id as ClassId, C.name as ClassName, C.date FROM CLIENT CL" +
                        " JOIN Client_Class CC on CL.id = CC.ClientId" +
                        " JOIN Class C on CC.classId = C.id" +
                        " WHERE CL.id = ?";
                String sqlUpcoming = sql2 + " and date > NOW()";
                String sqlAttended = sql2 + " and date <= NOW()";

                statement = connection.prepareStatement(sqlAttended, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                statement.setInt(1, model.id);
                id = -1;
                set = statement.executeQuery();
                clientViewModel = null;
                while (set.next()) {
                    int newId = set.getInt("id");

                    if (newId != id) {
                        for (ClientViewModel item : clientViewModels) {
                            if (item.id == newId) {
                                clientViewModel = item;
                                clientViewModel.clientAttendedClasses = new TreeMap<>();
                            }
                        }
                    }

                    clientViewModel.clientAttendedClasses.put(set.getInt("classId"),
                            new Pair<>(set.getString("className"), set.getTimestamp("date")));
                    if (set.next())
                        set.previous();
                    else
                        break;
                    id = set.getInt("id");
                }

                statement = connection.prepareStatement(sqlUpcoming, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                statement.setInt(1, model.id);
                id = -1;
                set = statement.executeQuery();
                clientViewModel = null;
                while (set.next()) {
                    int newId = set.getInt("id");

                    if (newId != id) {
                        for (ClientViewModel item : clientViewModels) {
                            if (item.id == newId) {
                                clientViewModel = item;
                                clientViewModel.clientUpcomingClasses = new TreeMap<>();
                            }
                        }
                    }

                    clientViewModel.clientUpcomingClasses.put(set.getInt("classId"),
                            new Pair<>(set.getString("className"), set.getTimestamp("date")));
                    if (set.next())
                        set.previous();
                    else
                        break;
                    id = set.getInt("id");
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
        if (clientViewModels.size() > 0)
            return clientViewModels.get(0);
        return null;
    }

    @Override
    public void update(ClientBindingModel model) throws Exception {
        ClientViewModel element = getElement(model);
        if (element == null) {
            throw new Exception("Клиент не найден");
        }
        try {
            Thread thread = new Thread(() -> {
                try {
                    String sql = "SELECT * FROM Client_Class WHERE clientId = ?";
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setInt(1, model.id);
                    ResultSet set = statement.executeQuery();
                    while (set.next()) {
                        int classId = set.getInt("classId");
                        if (!model.clientUpcomingClasses.containsKey(classId)) {
                            sql = "DELETE FROM Client_Class WHERE ClassId = ? and ClientId = ?";
                            statement = connection.prepareStatement(sql);
                            statement.setInt(1, classId);
                            statement.setInt(2, model.id);
                            statement.executeUpdate();
                        } else {
                            model.clientUpcomingClasses.remove(classId);
                        }
                    }
                    sql = "UPDATE CLIENT SET name = (?), surname = (?), birthdate = (?), testRequest = (?)," +
                            " phone = (?), login = (?), password = (?) WHERE Id = ?";
                    statement = connection.prepareStatement(sql);
                    statement.setString(1, model.name);
                    statement.setString(2, model.surname);
                    statement.setDate(3, model.birthdate);
                    statement.setBoolean(4, model.testRequest);
                    statement.setString(5, model.phone);
                    statement.setString(6, model.login);
                    statement.setString(7, model.password);
                    statement.setInt(8, model.id);
                    statement.executeUpdate();

                    for (Map.Entry<Integer, Pair<String, Timestamp>> entry : model.clientUpcomingClasses.entrySet()) {
                        String sql2 = "INSERT INTO Client_Class VALUES ((?), (?))";
                        statement = connection.prepareStatement(sql2);
                        statement.setInt(1, model.id);
                        statement.setInt(2, entry.getKey());
                        statement.executeUpdate();
                    }

                    connection.commit();
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
