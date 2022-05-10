package com.example.csv;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import java.io.*;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

public class CSVUtils {
    public static final String CUST_FILE_NAME = "CNOF_EPS_CDH_CONTACT_Rohit_5_1_1.csv";
    public static final String POLICY_FILE_NAME = "CNOF_EPS_CDH_POLICY_Rohit_5_1_1.csv";
    public static String[] addCustomers() throws IOException, CsvException, URISyntaxException {

        File file = new File(CUST_FILE_NAME);

        CSVReader headerReader = new CSVReader(new FileReader(file));

        String[] headers = headerReader.readNext();
        int doNotEmailIndIdx =  Arrays.asList(headers).indexOf("DO_NOT_EMAIL_IND");
        int cureEmailIndIdx =  Arrays.asList(headers).indexOf("CURE_EMAIL_IND");
        headerReader.close();

        CSVReader reader = new CSVReader(new FileReader(file));
        List<String[]> csvBody = reader.readAll();

        Long lastCustId = Long.parseLong(csvBody.get(csvBody.size()-1)[0]);
        Long startCustId = lastCustId+1;

        csvBody.get(1)[0] = Long.toString(startCustId);
        csvBody.get(2)[0] = Long.toString(startCustId+1);

        csvBody.get(1)[doNotEmailIndIdx] = "N";
        csvBody.get(2)[doNotEmailIndIdx] = "N";

        csvBody.get(1)[cureEmailIndIdx] = "Y";
        csvBody.get(2)[cureEmailIndIdx] = "Y";

        reader.close();

        File outputFile = new File(CUST_FILE_NAME);

        String desktopPath = System.getProperty("user.home") + "/Desktop/";
        File outputFileDesktop = new File(desktopPath + CUST_FILE_NAME);

        writeToFile(csvBody, outputFile);
        writeToFile(csvBody, outputFileDesktop);

        return new String[] {csvBody.get(1)[0], csvBody.get(2)[0]};
    }

    private static void writeToFile(List<String[]> csvBody, File file) throws IOException {
        CSVWriter writer = new CSVWriter(new FileWriter(file));
        writer.writeAll(csvBody);
        writer.flush();
        writer.close();
    }

    public static String[] addPolicy(String[] customers) throws IOException, CsvException {

        File file = new File(POLICY_FILE_NAME);

        CSVReader headerReader = new CSVReader(new FileReader(file));

        String[] headers = headerReader.readNext();
        int activationDateIdx =  Arrays.asList(headers).indexOf("POLCY_ACTVT_DT");
        headerReader.close();

        CSVReader reader = new CSVReader(new FileReader(file));
        List<String[]> csvBody = reader.readAll();

        csvBody.get(1)[0] = customers[0];
        csvBody.get(2)[0] = customers[0];
        csvBody.get(3)[0] = customers[0];
        csvBody.get(4)[0] = customers[1];

        Long lastPolicyId = Long.parseLong(csvBody.get(csvBody.size()-1)[1]);
        Long startPolicyId = lastPolicyId + 1;

        csvBody.get(1)[1] = Long.toString(startPolicyId);
        csvBody.get(2)[1] = Long.toString(startPolicyId);
        csvBody.get(3)[1] = Long.toString(startPolicyId);
        csvBody.get(4)[1] = Long.toString(startPolicyId);

        LocalDate date = LocalDate.now().minus(1, ChronoUnit.DAYS);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd");
        csvBody.get(1)[activationDateIdx] = formatter.format(date);
        csvBody.get(2)[activationDateIdx] = formatter.format(date);
        csvBody.get(3)[activationDateIdx] = formatter.format(date);
        csvBody.get(4)[activationDateIdx] = formatter.format(date);

        reader.close();

        File outputFile = new File(POLICY_FILE_NAME);

        String desktopPath = System.getProperty("user.home") + "/Desktop/";
        File outputFileDesktop = new File(desktopPath + POLICY_FILE_NAME);

        writeToFile(csvBody, outputFile);
        writeToFile(csvBody, outputFileDesktop);

        return new String[] {csvBody.get(1)[1]};
    }
}
