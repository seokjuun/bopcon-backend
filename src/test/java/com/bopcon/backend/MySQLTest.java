package com.bopcon.backend;

import java.sql.Connection;
import java.sql.DriverManager;

public class MySQLTest {
    public static void main(String[] args) {
        String url = "jdbc:mysql://bopcon.czwmu86ms4yl.us-east-1.rds.amazonaws.com:3306/bopcon?useSSL=false&serverTimezone=UTC";
        String username = "admin";
        String password = "qwer1234!";

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            System.out.println("MySQL 연결 성공!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
