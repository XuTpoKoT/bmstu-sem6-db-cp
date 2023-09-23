package com.music_shop;

import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.sql.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws SQLException, IOException {
        String url = "jdbc:postgresql://localhost:5432/musicshop_db";
        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "postgres");
        Connection conn = DriverManager.getConnection(url, props);

        final String SQL_GET_PRODUCT_BY_NAME = """
            SELECT p.id
            FROM public.product p
            WHERE p.name_ = ?;
        """;
        PreparedStatement stmt = conn.prepareStatement(SQL_GET_PRODUCT_BY_NAME);
        List<String> productNames = getProductNames();


        List<String> ids = new ArrayList<>();
        int cntWarmQueries = 10;
        long startTime = ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime();
        execQueries(cntWarmQueries, productNames, stmt, ids);
        long warmTime = (ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime() - startTime) / 1000;

        int cntQueries = 10000;
        startTime = ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime();
        execQueries(cntQueries, productNames, stmt, ids);
        long normalTime = (ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime() - startTime) / 1000;

        for (String id : ids) {
            System.out.println(id);
        }

        System.out.println("Warm time " + warmTime);
        System.out.println("Normal time " + normalTime);
        String dirPath = "/home/dmitriy/bmstu/old/SEM_6/bmstu-sem6-db-cp/docs/tex/inc/csv/";
        String filename = dirPath+"usual.csv";
        String filename2 = dirPath+"indexes.csv";
        File file = new File(filename2);
        try (FileWriter fileWriter = new FileWriter(file, true)) {
            String str = productNames.size() + ", " + normalTime + "\n";
            fileWriter.write(str);
        }  catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static List<String> getProductNames() {
        List<String> productNames = new ArrayList<>();
        String path = "/home/dmitriy/bmstu/old/SEM_6/bmstu-sem6-db-cp/data/products_1000.csv";
        try (CSVReader csvReader = new CSVReader(new FileReader(path))) {
            String[] values;
            csvReader.readNext();
            while ((values = csvReader.readNext()) != null) {
                productNames.add(values[1]);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return productNames;
    }

    public static void execQueries(int cntQueries, List<String> productNames,
                                           PreparedStatement stmt, List<String> ids) throws SQLException {
        for (int i = 0; i < cntQueries; i++) {
            String name = productNames.get(new Random().nextInt(productNames.size()));
            stmt.setString(1, name);
            ResultSet resultSet = stmt.executeQuery();
            resultSet.next();
            ids.add(resultSet.getString(1));
        }
    }

}