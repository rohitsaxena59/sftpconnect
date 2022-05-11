package com.example.csv;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

public class CSVUtils {
    public static final String CUST_FILE_NAME = "CNOF_EPS_CDH_CONTACT_Rohit_5_1_1.csv";
    public static final String POLICY_FILE_NAME = "CNOF_EPS_CDH_POLICY_Rohit_5_1_1.csv";

    public static final String CUST_FILE_VARS = "CNOF_EPS_CDH_CONTACT_Rohit_";
    public static final String POLICY_FILE_VARS = "CNOF_EPS_CDH_POLICY_Rohit_";

    public static final String PROD_TYPE = "GBL";
    public static String[] addCustomers(String[] args) throws IOException, CsvException {

        String desktopPath = System.getProperty("user.home") + "/Desktop/";

        // Read from resources folder
        //InputStream inputStream = CSVUtils.class.getClassLoader().getResourceAsStream(CUST_FILE_NAME);
        //CSVReader reader = new CSVReader(new InputStreamReader(inputStream));

        File file = new File(desktopPath + CUST_FILE_NAME);
        CSVReader reader = new CSVReader(new FileReader(file));

        String[] headers = reader.peek();
        int doNotEmailIndIdx =  Arrays.asList(headers).indexOf("DO_NOT_EMAIL_IND");
        int cureEmailIndIdx =  Arrays.asList(headers).indexOf("CURE_EMAIL_IND");
        int emailAddressIdx =  Arrays.asList(headers).indexOf("BEST_EMAIL_ADDRESS");

        List<String[]> csvBody = reader.readAll();

        Long lastCustId = Long.parseLong(csvBody.get(csvBody.size()-1)[0]);
        Long startCustId = lastCustId+1;

        csvBody.get(1)[0] = Long.toString(startCustId);
        csvBody.get(2)[0] = Long.toString(startCustId+1);

        csvBody.get(1)[doNotEmailIndIdx] = args[2];
        csvBody.get(2)[doNotEmailIndIdx] = args[2];

        csvBody.get(1)[cureEmailIndIdx] = args[3];
        csvBody.get(2)[cureEmailIndIdx] = args[3];

        reader.close();

        File outputFile = new File(CUST_FILE_NAME);

        File outputFileDesktop = new File(desktopPath + CUST_FILE_NAME);
        File outputFileVars = new File(desktopPath + CUST_FILE_VARS+args[0]+args[1]+args[2]+args[3]+".csv");

        //writeToFile(csvBody, outputFile);
        writeToFile(csvBody, outputFileDesktop);
        writeToFile(csvBody, outputFileVars);

        return new String[] {csvBody.get(1)[0], csvBody.get(2)[0]};
    }

    private static void writeToFile(List<String[]> csvBody, File file) throws IOException {
        CSVWriter writer = new CSVWriter(new FileWriter(file));
        writer.writeAll(csvBody);
        writer.flush();
        writer.close();
    }

    public static String[] addPolicy(String[] customers, String[] args) throws IOException, CsvException {

        String desktopPath = System.getProperty("user.home") + "/Desktop/";

        File file = new File(desktopPath + POLICY_FILE_NAME);
        CSVReader reader = new CSVReader(new FileReader(file));

        //InputStream inputStream = CSVUtils.class.getClassLoader().getResourceAsStream(POLICY_FILE_NAME);
        //CSVReader reader = new CSVReader(new InputStreamReader(inputStream));

        String[] headers = reader.peek();
        int activationDateIdx =  Arrays.asList(headers).indexOf("POLCY_ACTVT_DT");
        int baseProdTypeIdx =  Arrays.asList(headers).indexOf("BASE_PROD_TYPE_CD");
        int prodTypeIdx =  Arrays.asList(headers).indexOf("PROD_TYPE_CD");

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

        LocalDate date;
        if(Integer.parseInt(args[0]) < 0) {
            date = LocalDate.now().minus(Math.abs(Integer.parseInt(args[0])), ChronoUnit.DAYS);
        } else if(Integer.parseInt(args[0]) > 0) {
            date = LocalDate.now().plus(1, ChronoUnit.DAYS);
        } else {
            date = LocalDate.now();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd");
        csvBody.get(1)[activationDateIdx] = formatter.format(date);
        csvBody.get(2)[activationDateIdx] = formatter.format(date);
        csvBody.get(3)[activationDateIdx] = formatter.format(date);
        csvBody.get(4)[activationDateIdx] = formatter.format(date);

        csvBody.get(1)[baseProdTypeIdx] = args[1];
        csvBody.get(2)[baseProdTypeIdx] = args[1];
        csvBody.get(3)[baseProdTypeIdx] = args[1];
        csvBody.get(4)[baseProdTypeIdx] = args[1];

        csvBody.get(1)[prodTypeIdx] = args[1];
        csvBody.get(2)[prodTypeIdx] = args[1];
        csvBody.get(3)[prodTypeIdx] = args[1];
        csvBody.get(4)[prodTypeIdx] = args[1];

        reader.close();

        File outputFile = new File(POLICY_FILE_NAME);


        File outputFileDesktop = new File(desktopPath + POLICY_FILE_NAME);
        File outputFileVars = new File(desktopPath + POLICY_FILE_VARS+args[0]+args[1]+args[2]+args[3]+".csv");

        //writeToFile(csvBody, outputFile);
        writeToFile(csvBody, outputFileDesktop);
        writeToFile(csvBody, outputFileVars);

        return new String[] {csvBody.get(1)[1]};
    }
}
