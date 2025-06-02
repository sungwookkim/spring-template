package com.member;

import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Configuration
public class QueryExecute {
    private static DataSource memberDatasource;

    QueryExecute(DataSource memberDatasource) {
        QueryExecute.memberDatasource = memberDatasource;
    }

    public static void execute(String query) throws SQLException {
        try (Connection connection = memberDatasource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.execute();
        }
    }

    public static void executeQuery(String query) throws SQLException {
        try (Connection connection = memberDatasource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.executeQuery();
        }
    }

    public static void executeUpdate(String query) throws SQLException {
        try (Connection connection = memberDatasource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.executeUpdate();
        }
    }
}
