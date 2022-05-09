package com.example.csv;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

public class CSVUtils {

    public static boolean addCustomers(int num) throws IOException, CsvException, URISyntaxException {

        File file = new File("CNOF_EPS_CDH_CONTACT_Rohit_5_1_1.csv");

        CSVReader reader = new CSVReader(new FileReader(file));
        List<String[]> csvBody = reader.readAll();

        Long lastCustId = Long.parseLong(csvBody.get(csvBody.size()-1)[0]);
        Long startCustId = lastCustId+1;

        csvBody.get(1)[0] = Long.toString(startCustId);
        csvBody.get(2)[0] = Long.toString(startCustId+1);

        reader.close();

        File outputFile = new File("CNOF_EPS_CDH_CONTACT_Rohit_5_1_1.csv");

        String desktopPath = System.getProperty("user.home") + "/Desktop";
        File outputFileDesktop = new File(desktopPath + "/CNOF_EPS_CDH_CONTACT_Rohit_5_1_1.csv");

        writeToFile(csvBody, outputFile);
        writeToFile(csvBody, outputFileDesktop);

        return true;
    }

    private static void writeToFile(List<String[]> csvBody, File file) throws IOException {
        CSVWriter writer = new CSVWriter(new FileWriter(file));
        writer.writeAll(csvBody);

        System.out.println(csvBody.get(1)[0]+"," + csvBody.get(2)[0]);
        writer.flush();
        writer.close();
    }
}
