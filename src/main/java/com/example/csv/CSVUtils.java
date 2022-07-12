package com.example.csv;

import com.example.process.ProcessUtils;
import com.example.sftp.SFTPConnect;
import com.opencsv.*;
import com.opencsv.exceptions.CsvException;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import static com.example.constants.SFTPConnectConstants.*;

public class CSVUtils {

    public static String[] addCustomers(String[] args, Long[] params) throws IOException, CsvException {

        //CUST_FILE_NAME = CUST_FILE_NAME.replace("NAME",args[4]);
        CUST_FILE_VARS = CUST_FILE_VARS.replace("NAME",args[4]);

        //Read from resources folder
        InputStream inputStream = CSVUtils.class.getClassLoader().getResourceAsStream(CUST_FILE_NAME);
        CSVParser parser = new CSVParserBuilder().withSeparator('|').build();
        CSVReader reader = new CSVReaderBuilder(new InputStreamReader(inputStream)).withCSVParser(parser).build();

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

        csvBody.get(1)[emailAddressIdx] = "N".equalsIgnoreCase(args[7]) ? StringUtils.EMPTY : "raghugoud@futureproofai.com";
        csvBody.get(2)[emailAddressIdx] = "N".equalsIgnoreCase(args[7]) ? StringUtils.EMPTY : "raghugoud@futureproofai.com";

        reader.close();

        File outputFile = new File(CUST_FILE_NAME);

        File outputFileDesktop = new File(DESKTOP_PATH + CUST_FILE_NAME);
        File outputFileVars = new File(DESKTOP_PATH + CUST_FILE_VARS+args[0]+args[1]+args[2]+args[3]+".csv");
        File customerTxtFile = new File(DESKTOP_PATH + "customer.txt");

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

        //POLICY_FILE_NAME = POLICY_FILE_NAME.replace("NAME",args[4]);
        POLICY_FILE_VARS = POLICY_FILE_VARS.replace("NAME",args[4]);

        InputStream inputStream = CSVUtils.class.getClassLoader().getResourceAsStream(POLICY_FILE_NAME);
        CSVParser parser = new CSVParserBuilder().withSeparator('|').build();
        CSVReader reader = new CSVReaderBuilder(new InputStreamReader(inputStream)).withCSVParser(parser).build();

        String[] headers = reader.peek();
        int activationDateIdx =  Arrays.asList(headers).indexOf("POLCY_ACTVT_DT");
        int baseProdTypeIdx =  Arrays.asList(headers).indexOf("BASE_PROD_TYPE_CD");
        int prodTypeIdx =  Arrays.asList(headers).indexOf("PROD_TYPE_CD");
        int policyStatusIdx =  Arrays.asList(headers).indexOf("POLICY_STATUS_CD");

        List<String[]> csvBody = reader.readAll();

        csvBody.get(1)[0] = customers[0];
        csvBody.get(2)[0] = customers[0];
        csvBody.get(3)[0] = customers[0];
        csvBody.get(4)[0] = customers[1];

        csvBody.get(1)[1] = Long.toString(++params[1]);
        csvBody.get(2)[1] = Long.toString(params[1]);
        csvBody.get(3)[1] = Long.toString(params[1]);
        csvBody.get(4)[1] = Long.toString(params[1]);

        LocalDate date = getDate(args[0]);
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

        csvBody.get(1)[policyStatusIdx] = args[6];
        csvBody.get(2)[policyStatusIdx] = args[6];
        csvBody.get(3)[policyStatusIdx] = args[6];
        csvBody.get(4)[policyStatusIdx] = args[6];

        reader.close();

        File outputFileDesktop = new File(DESKTOP_PATH + POLICY_FILE_NAME);
        File outputFileVars = new File(DESKTOP_PATH + POLICY_FILE_VARS+args[0]+args[1]+args[2]+args[3]+".csv");
        File policyTxtFile = new File(DESKTOP_PATH + "policy.txt");

        writeToFile(csvBody, outputFileDesktop);
        writeToFile(csvBody, outputFileVars);
        writeToFile(Long.toString(params[1]), policyTxtFile);

        return new String[] {csvBody.get(1)[1]};
    }

    public static void addEmailSent(String[] args) throws IOException, CsvException {
        InputStream inputStream = CSVUtils.class.getClassLoader().getResourceAsStream(EMAIL_ACTIVITY_FILE);
        CSVReader reader = new CSVReader(new InputStreamReader(inputStream));

        String[] headers = reader.peek();
        int deploymentDate =  Arrays.asList(headers).indexOf("DeploymentDate_M");
        int customerKey =  Arrays.asList(headers).indexOf("CustomerKey");
        int status =  Arrays.asList(headers).indexOf("Action");
        int actionTimestamp =  Arrays.asList(headers).indexOf("OriginalActionTimestamp");
        int interactionId =  Arrays.asList(headers).indexOf("ClientRequestID");

        List<String[]> csvBody = reader.readAll();

        csvBody.get(1)[customerKey] = args[0];
        csvBody.get(1)[status] = args[1];
        csvBody.get(1)[interactionId] = args[2];

        ZonedDateTime utcTime = ZonedDateTime.now(ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd'T'HH:mm:ss");
        csvBody.get(1)[actionTimestamp] = csvBody.get(1)[deploymentDate] = formatter.format(utcTime);

        reader.close();

        File outputFileDesktop = new File(DESKTOP_PATH + EMAIL_ACTIVITY_FILE);

        CSVWriter writer = (CSVWriter) new CSVWriterBuilder(new FileWriter(outputFileDesktop)).
                withQuoteChar(CSVParser.NULL_CHARACTER).build();
        writer.writeAll(csvBody);
        writer.flush();
        writer.close();
        System.out.println("Generated activity file: " + outputFileDesktop.getAbsolutePath());

        ProcessUtils.encryptFile(outputFileDesktop);
    }

    private static LocalDate getDate(String days) {
        LocalDate date;
        if(Integer.parseInt(days) < 0) {
            date = LocalDate.now().minus(Math.abs(Integer.parseInt(days)), ChronoUnit.DAYS);
        } else if(Integer.parseInt(days) > 0) {
            date = LocalDate.now().plus(Math.abs(Integer.parseInt(days)), ChronoUnit.DAYS);
        } else {
            date = LocalDate.now();
        }
        return date;
    }
}
