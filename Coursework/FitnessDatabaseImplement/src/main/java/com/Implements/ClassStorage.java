package com.Implements;

import android.util.Pair;

import com.BindingModels.ClassBindingModel;
import com.DbUtil.Database;
import com.Interfaces.IClassStorage;
import com.ViewModels.ClassViewModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class ClassStorage implements IClassStorage {
    private final Connection connection;

    public ClassStorage() {
        this.connection = Database.getConnection();
    }


    @Override
    public List<ClassViewModel> getFullList() {

        String sqlStatuses = "SELECT C.id, C.name, C.date, SC.statusId, C.trainerId,"
                + " T.Name AS TrainerName, T.Surname AS TrainerSurname, S.name AS StatusName FROM CLASS C " +
                " JOIN Trainer T on C.trainerId = T.id" +
                " JOIN Status_Class SC on SC.classId = C.id" +
                " JOIN Status S on SC.statusId = S.id" +
                " ORDER BY C.id";
        String sqlClients = "SELECT C.id, CC.clientId, Cl.Name AS ClientName, Cl.Surname AS ClientSurname,"
                + " CL.Phone FROM CLASS C " +
                " JOIN Client_Class CC on C.id = CC.ClassId" +
                " JOIN Client CL on CC.clientId = CL.id" +
                " ORDER BY C.id";

        List<ClassViewModel> classViewModels = new ArrayList<>();
        Thread thread = new Thread(() -> {
            try (Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                int id = -1;
                ResultSet set = statement.executeQuery(sqlStatuses);
                ClassViewModel classViewModel = null;
                while (set.next()) {
                    int newId = set.getInt("id");

                    if (newId != id) {
                        classViewModel = new ClassViewModel();
                        classViewModel.classStatuses = new TreeMap<>();
                        classViewModel.id = newId;
                        classViewModel.name = set.getString("name");
                        classViewModel.date = set.getTimestamp("date");
                        classViewModel.trainerId = set.getInt("trainerId");
                        classViewModel.trainerFIO = set.getString("TrainerSurname") + " " + set.getString("TrainerName");
                    }

                    if (!classViewModel.classStatuses.containsKey(set.getInt("statusId")))
                        classViewModel.classStatuses.put(set.getInt("statusId"), set.getString("StatusName"));

                    if (set.next()) {
                        if (newId != set.getInt("id"))
                            classViewModels.add(classViewModel);
                        set.previous();
                    } else {
                        classViewModels.add(classViewModel);
                        break;
                    }
                    id = set.getInt("id");
                }
            } catch (Exception e) {
                System.out.print(e.getMessage());
                e.printStackTrace();
            }
            try (Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                int id = -1;
                ResultSet set = statement.executeQuery(sqlClients);
                ClassViewModel classViewModel = null;
                while (set.next()) {
                    int newId = set.getInt("id");

                    if (newId != id) {
                        for (ClassViewModel item : classViewModels) {
                            if (item.id == newId) {
                                classViewModel = item;
                                classViewModel.classClients = new TreeMap<>();
                            }
                        }
                    }

                    if (!classViewModel.classClients.containsKey(set.getInt("clientId")))
                        classViewModel.classClients.put(set.getInt("clientId"), new Pair<>(set.getString("ClientSurname")
                                + " " + set.getString("ClientName"), set.getString("Phone")));

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
        return classViewModels;
    }

    @Override
    public List<ClassViewModel> getFilteredList(ClassBindingModel model) {
        if (model == null) {
            return null;
        }
        List<ClassViewModel> classViewModels = new ArrayList<>();
        String sqlStatuses = "SELECT C.id, C.name, C.date, SC.statusId, C.trainerId,"
                + " T.Name AS TrainerName, T.Surname AS TrainerSurname, S.name AS StatusName FROM CLASS C " +
                " JOIN Trainer T on C.trainerId = T.id" +
                " JOIN Status_Class SC on SC.classId = C.id" +
                " JOIN Status S on SC.statusId = S.id";
        String sqlClients = "SELECT C.id, CC.clientId, Cl.Name AS ClientName, Cl.Surname AS ClientSurname,"
                + " CL.Phone FROM CLASS C " +
                " JOIN Client_Class CC on C.id = CC.ClassId" +
                " JOIN Client CL on CC.clientId = CL.id";
        String str;
        if (model.date != null) {
            str = " where C.date = ?" +
                    " ORDER BY C.id";
        } else {
            str = " where C.trainerId = ?" +
                    " ORDER BY C.id";
        }
        sqlStatuses += str;
        sqlClients += str;

        String finalSqlStatuses = sqlStatuses;
        String finalSqlClients = sqlClients;
        Thread thread = new Thread(() -> {
            try (PreparedStatement statement = connection.prepareStatement(finalSqlStatuses, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                if (model.date != null)
                    statement.setDate(1, new java.sql.Date(model.date.getTime()));
                else
                    statement.setInt(1, model.trainerId);
                int id = -1;
                ResultSet set = statement.executeQuery();
                ClassViewModel classViewModel = null;
                while (set.next()) {
                    int newId = set.getInt("id");

                    if (newId != id) {
                        classViewModel = new ClassViewModel();
                        classViewModel.classStatuses = new TreeMap<>();
                        classViewModel.id = newId;
                        classViewModel.name = set.getString("name");
                        classViewModel.date = set.getTimestamp("date");
                        classViewModel.trainerId = set.getInt("trainerId");
                        classViewModel.trainerFIO = set.getString("TrainerSurname") + " " + set.getString("TrainerName");
                    }

                    if (!classViewModel.classStatuses.containsKey(set.getInt("statusId")))
                        classViewModel.classStatuses.put(set.getInt("statusId"), set.getString("StatusName"));

                    if (set.next()) {
                        if (newId != set.getInt("id"))
                            classViewModels.add(classViewModel);
                        set.previous();
                    } else {
                        classViewModels.add(classViewModel);
                        break;
                    }
                    id = set.getInt("id");
                }
            } catch (Exception e) {
                System.out.print(e.getMessage());
                e.printStackTrace();
            }

            try (PreparedStatement statement = connection.prepareStatement(finalSqlClients, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                if (model.date != null)
                    statement.setDate(1, new java.sql.Date(model.date.getTime()));
                else
                    statement.setInt(1, model.trainerId);
                int id = -1;
                ResultSet set = statement.executeQuery();
                ClassViewModel classViewModel = null;
                while (set.next()) {
                    int newId = set.getInt("id");

                    if (newId != id) {
                        for (ClassViewModel item : classViewModels) {
                            if (item.id == newId) {
                                classViewModel = item;
                                classViewModel.classClients = new TreeMap<>();
                            }
                        }
                    }

                    if (!classViewModel.classClients.containsKey(set.getInt("clientId")))
                        classViewModel.classClients.put(set.getInt("clientId"), new Pair<>(set.getString("ClientSurname")
                                + " " + set.getString("ClientName"), set.getString("Phone")));

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
        return classViewModels;
    }

    @Override
    public ClassViewModel getElement(ClassBindingModel model) {
        if (model == null) {
            return null;
        }
        List<ClassViewModel> classViewModels = new ArrayList<>();

        String sqlStatuses = "SELECT C.id, C.name, C.date, SC.statusId, C.trainerId,"
                + " T.Name AS TrainerName, T.Surname AS TrainerSurname, S.name AS StatusName FROM CLASS C " +
                " JOIN Trainer T on C.trainerId = T.id" +
                " JOIN Status_Class SC on SC.classId = C.id" +
                " JOIN Status S on SC.statusId = S.id";
        String sqlClients = "SELECT C.id, CC.clientId, Cl.Name AS ClientName, Cl.Surname AS ClientSurname,"
                + " CL.Phone FROM CLASS C " +
                " JOIN Client_Class CC on C.id = CC.ClassId" +
                " JOIN Client CL on CC.clientId = CL.id";
        String str = "";
        if (model.id != null)
            str += " where C.id = ?";
        else if (model.date != null)
            str += " where C.date = ?";
        sqlStatuses += str;
        sqlClients += str;

        String finalSqlStatuses = sqlStatuses;
        String finalSqlClients = sqlClients;
        Thread thread = new Thread(() -> {
            try (PreparedStatement statement = connection.prepareStatement(finalSqlStatuses, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                if (model.id != null)
                    statement.setInt(1, model.id);
                else if (model.date != null)
                    statement.setTimestamp(1, model.date);
                int id = -1;
                ResultSet set = statement.executeQuery();
                ClassViewModel classViewModel = null;
                while (set.next()) {
                    int newId = set.getInt("id");

                    if (newId != id) {
                        classViewModel = new ClassViewModel();
                        classViewModel.classStatuses = new TreeMap<>();
                        classViewModel.id = newId;
                        classViewModel.name = set.getString("name");
                        classViewModel.date = set.getTimestamp("date");
                        classViewModel.trainerId = set.getInt("trainerId");
                        classViewModel.trainerFIO = set.getString("TrainerSurname") + " " + set.getString("TrainerName");
                    }

                    if (!classViewModel.classStatuses.containsKey(set.getInt("statusId")))
                        classViewModel.classStatuses.put(set.getInt("statusId"), set.getString("StatusName"));

                    if (set.next()) {
                        if (newId != set.getInt("id"))
                            classViewModels.add(classViewModel);
                        set.previous();
                    } else {
                        classViewModels.add(classViewModel);
                        break;
                    }
                    id = set.getInt("id");
                }
            } catch (Exception e) {
                System.out.print(e.getMessage());
                e.printStackTrace();
            }
            try (PreparedStatement statement = connection.prepareStatement(finalSqlClients, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                if (model.id != null)
                    statement.setInt(1, model.id);
                else if (model.date != null)
                    statement.setTimestamp(1, model.date);
                int id = -1;
                ResultSet set = statement.executeQuery();
                ClassViewModel classViewModel = null;
                while (set.next()) {
                    int newId = set.getInt("id");

                    if (newId != id) {
                        for (ClassViewModel item : classViewModels) {
                            if (item.id == newId) {
                                classViewModel = item;
                                classViewModel.classClients = new TreeMap<>();
                            }
                        }
                    }

                    if (!classViewModel.classClients.containsKey(set.getInt("clientId")))
                        classViewModel.classClients.put(set.getInt("clientId"), new Pair<>(set.getString("ClientSurname")
                                + " " + set.getString("ClientName"), set.getString("Phone")));

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
        if (classViewModels.size() > 0)
            return classViewModels.get(0);
        else
            return null;
    }
}