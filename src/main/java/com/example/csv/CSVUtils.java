package com.example.csv;

import com.opencsv.*;
import com.opencsv.exceptions.CsvException;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

public class CSVUtils {
    public static String CUST_FILE_NAME = "CNOF_EPS_CDH_CONTACT_pipe_test.csv";
    public static String POLICY_FILE_NAME = "CNOF_EPS_CDH_POLICY_pipe_test.csv";

    public static String CUST_FILE_VARS = "CNOF_EPS_CDH_CONTACT_NAME_";
    public static String POLICY_FILE_VARS = "CNOF_EPS_CDH_POLICY_NAME_";
    public static String[] addCustomers(String[] args, Long[] params) throws IOException, CsvException {

        String desktopPath = System.getProperty("user.home") + "/Desktop/";
        CUST_FILE_NAME = CUST_FILE_NAME.replace("NAME",args[4]);
        CUST_FILE_VARS = CUST_FILE_VARS.replace("NAME",args[4]);

        //Read from resources folder
        InputStream inputStream = CSVUtils.class.getClassLoader().getResourceAsStream(CUST_FILE_NAME);
        CSVParser parser = new CSVParserBuilder().withSeparator('|').build();
        CSVReader reader = new CSVReaderBuilder(new InputStreamReader(inputStream)).withCSVParser(parser).build();

//        File file = new File(desktopPath + CUST_FILE_NAME);
//        CSVParser parser = new CSVParserBuilder().withSeparator('|').build();
//        CSVReader reader = new CSVReaderBuilder(new FileReader(file)).withCSVParser(parser).build();

        String[] headers = reader.peek();
        int doNotEmailIndIdx =  Arrays.asList(headers).indexOf("DO_NOT_EMAIL_IND");
        int cureEmailIndIdx =  Arrays.asList(headers).indexOf("CURE_EMAIL_IND");
        int emailAddressIdx =  Arrays.asList(headers).indexOf("BEST_EMAIL_ADDRESS");

        List<String[]> csvBody = reader.readAll();

        csvBody.get(1)[0] = Long.toString(++params[0]);
        csvBody.get(2)[0] = Long.toString(++params[0]);

        csvBody.get(1)[cureEmailIndIdx] = args[2];
        csvBody.get(2)[cureEmailIndIdx] = args[2];

        csvBody.get(1)[doNotEmailIndIdx] = args[3];
        csvBody.get(2)[doNotEmailIndIdx] = args[3];

        csvBody.get(1)[emailAddressIdx] = "raghugoud@futureproofai.com";
        csvBody.get(2)[emailAddressIdx] = "raghugoud@futureproofai.com";

        reader.close();

        File outputFile = new File(CUST_FILE_NAME);

        File outputFileDesktop = new File(desktopPath + CUST_FILE_NAME);
        File outputFileVars = new File(desktopPath + CUST_FILE_VARS+args[0]+args[1]+args[2]+args[3]+".csv");
        File customerTxtFile = new File(desktopPath + "customer.txt");

        //writeToFile(csvBody, outputFile);
        writeToFile(csvBody, outputFileDesktop);
        writeToFile(csvBody, outputFileVars);
        writeToFile(Long.toString(params[0]), customerTxtFile);

        return new String[] {csvBody.get(1)[0], csvBody.get(2)[0]};
    }

    private static void writeToFile(String text, File file) throws IOException {
        FileWriter fileWriter = new FileWriter(file, false);
        fileWriter.write(text);
        fileWriter.close();
    }

    private static void writeToFile(List<String[]> csvBody, File file) throws IOException {
        //CSVWriter writer = new CSVWriter(new FileWriter(file));
        CSVWriter writer = (CSVWriter) new CSVWriterBuilder(new FileWriter(file)).withSeparator('|')
                                .withQuoteChar(CSVWriter.NO_QUOTE_CHARACTER).build();

        writer.writeAll(csvBody);
        writer.flush();
        writer.close();
        System.out.println("Generated file: " + file.getAbsolutePath());
    }

    public static String[] addPolicy(String[] customers, String[] args, Long[] params) throws IOException, CsvException {

        String desktopPath = System.getProperty("user.home") + "/Desktop/";
        POLICY_FILE_NAME = POLICY_FILE_NAME.replace("NAME",args[4]);
        POLICY_FILE_VARS = POLICY_FILE_VARS.replace("NAME",args[4]);

        //File file = new File(desktopPath + POLICY_FILE_NAME);
        //CSVParser parser = new CSVParserBuilder().withSeparator('|').build();
        //CSVReader reader = new CSVReaderBuilder(new FileReader(file)).withCSVParser(parser).build();

        InputStream inputStream = CSVUtils.class.getClassLoader().getResourceAsStream(POLICY_FILE_NAME);
        CSVParser parser = new CSVParserBuilder().withSeparator('|').build();
        CSVReader reader = new CSVReaderBuilder(new InputStreamReader(inputStream)).withCSVParser(parser).build();

        String[] headers = reader.peek();
        int activationDateIdx =  Arrays.asList(headers).indexOf("POLCY_ACTVT_DT");
        int baseProdTypeIdx =  Arrays.asList(headers).indexOf("BASE_PROD_TYPE_CD");
        int prodTypeIdx =  Arrays.asList(headers).indexOf("PROD_TYPE_CD");

        List<String[]> csvBody = reader.readAll();

        csvBody.get(1)[0] = customers[0];
        csvBody.get(2)[0] = customers[0];
        csvBody.get(3)[0] = customers[0];
        csvBody.get(4)[0] = customers[1];

        Long startPolicyId = ++params[1];

        csvBody.get(1)[1] = Long.toString(startPolicyId);
        csvBody.get(2)[1] = Long.toString(startPolicyId);
        csvBody.get(3)[1] = Long.toString(startPolicyId);
        csvBody.get(4)[1] = Long.toString(startPolicyId);

        LocalDate date;
        if(Integer.parseInt(args[0]) < 0) {
            date = LocalDate.now().minus(Math.abs(Integer.parseInt(args[0])), ChronoUnit.DAYS);
        } else if(Integer.parseInt(args[0]) > 0) {
            date = LocalDate.now().plus(Math.abs(Integer.parseInt(args[0])), ChronoUnit.DAYS);
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
        File policyTxtFile = new File(desktopPath + "policy.txt");

        //writeToFile(csvBody, outputFile);
        writeToFile(csvBody, outputFileDesktop);
        writeToFile(csvBody, outputFileVars);
        writeToFile(Long.toString(params[1]), policyTxtFile);

        return new String[] {csvBody.get(1)[1]};
    }
}
