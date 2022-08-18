package com.example.csv;

import com.example.property.PropertyUtils;
import com.opencsv.*;
import com.opencsv.exceptions.CsvException;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static com.example.constants.SFTPConnectConstants.*;
import static com.example.constants.SFTPConnectConstants.CNO_PATH;

public class BulkCSVUtils {

    public static String[] addCustomers(String[] args, Long[] params) throws IOException, CsvException {

        CUST_FILE_VARS = CUST_FILE_VARS.replace("NAME", args[4]);
        List<String> customers = new ArrayList<String>();

        InputStream inputStream = CSVUtils.class.getClassLoader().getResourceAsStream(CUST_FILE_NAME);
        CSVParser parser = new CSVParserBuilder().withSeparator('|').build();
        CSVReader reader = new CSVReaderBuilder(new InputStreamReader(inputStream)).withCSVParser(parser).build();

        String[] headers = reader.peek();
        int doNotEmailIndIdx = Arrays.asList(headers).indexOf("DO_NOT_EMAIL_IND");
        int cureEmailIndIdx = Arrays.asList(headers).indexOf("CURE_EMAIL_IND");
        int emailAddressIdx = Arrays.asList(headers).indexOf("BEST_EMAIL_ADDRESS");

        List<String[]> csvBody = reader.readAll();

        CSVWriter writer = (CSVWriter) new CSVWriterBuilder(new FileWriter(CNO_PATH + CUST_FILE_NAME,
                Boolean.parseBoolean(args[10]))).withSeparator('|').withQuoteChar(CSVWriter.NO_QUOTE_CHARACTER).build();

        if(!Boolean.parseBoolean(args[10])) {
            writer.writeNext(csvBody.get(0));
        }

        IntStream.rangeClosed(1, Integer.parseInt(args[8])).forEach(num -> {

            csvBody.get(1)[0] = append(++params[0],args[9]);
            csvBody.get(2)[0] = append(++params[0],args[9]);

            csvBody.get(1)[cureEmailIndIdx] = args[2];
            csvBody.get(2)[cureEmailIndIdx] = args[2];

            csvBody.get(1)[doNotEmailIndIdx] = args[3];
            csvBody.get(2)[doNotEmailIndIdx] = args[3];

            csvBody.get(1)[emailAddressIdx] = "NA".equalsIgnoreCase(args[7]) ? StringUtils.EMPTY : args[7];
            csvBody.get(2)[emailAddressIdx] = "NA".equalsIgnoreCase(args[7]) ? StringUtils.EMPTY : args[7];

            writer.writeNext(csvBody.get(1));
            writer.writeNext(csvBody.get(2));

            customers.add(csvBody.get(1)[0]);
        });

        reader.close();
        writer.flush();
        writer.close();

        File customerTxtFile = new File(CNO_PATH + "customer.txt");
        writeToFile(Long.toString(params[0]), customerTxtFile);

        return customers.stream().toArray(String[]::new);
    }

    public static String[] addPolicy(String[] customers, String[] args, Long[] params) throws IOException, CsvException {

        POLICY_FILE_VARS = POLICY_FILE_VARS.replace("NAME",args[4]);
        List<String> policies = new ArrayList<>();

        InputStream inputStream = CSVUtils.class.getClassLoader().getResourceAsStream(POLICY_FILE_NAME);
        CSVParser parser = new CSVParserBuilder().withSeparator('|').build();
        CSVReader reader = new CSVReaderBuilder(new InputStreamReader(inputStream)).withCSVParser(parser).build();

        String[] headers = reader.peek();
        int activationDateIdx =  Arrays.asList(headers).indexOf("POLCY_ACTVT_DT");
        int baseProdTypeIdx =  Arrays.asList(headers).indexOf("BASE_PROD_TYPE_CD");
        int prodTypeIdx =  Arrays.asList(headers).indexOf("PROD_TYPE_CD");
        int policyStatusIdx =  Arrays.asList(headers).indexOf("POLICY_STATUS_CD");

        List<String[]> csvBody = reader.readAll();

        CSVWriter writer = (CSVWriter) new CSVWriterBuilder(new FileWriter(CNO_PATH + POLICY_FILE_NAME,
                Boolean.parseBoolean(args[10]))).withSeparator('|')
                .withQuoteChar(CSVWriter.NO_QUOTE_CHARACTER).build();

        if(!Boolean.parseBoolean(args[10])) {
            writer.writeNext(csvBody.get(0));
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd");

        IntStream.rangeClosed(1, Integer.parseInt(args[8])).forEach(custNum -> {
            IntStream.rangeClosed(1, 4).forEach(num -> {

                csvBody.get(num)[0] =  num == 4 ? append(extract(customers[custNum-1],args[9])+1, args[9]) : customers[custNum-1];
                csvBody.get(num)[1] = Long.toString(num == 1 ? ++params[1] : params[1]);
                csvBody.get(num)[baseProdTypeIdx] = args[1];
                csvBody.get(num)[prodTypeIdx] = args[1];
                csvBody.get(num)[policyStatusIdx] = args[6];
                csvBody.get(num)[activationDateIdx] = isDateParseable(args[0]) ? formatter.format(getDate(args[0])) : args[0];

                writer.writeNext(csvBody.get(num));
            });
            policies.add(csvBody.get(1)[1]);
        });

        reader.close();
        writer.close();

        File policyTxtFile = new File(CNO_PATH + "policy.txt");
        writeToFile(Long.toString(params[1]), policyTxtFile);

        return policies.stream().toArray(String[]::new);
    }

    private static Long extract(String customer, String env) {
        return Long.parseLong(customer.replace(PropertyUtils.properties.getProperty(env + ".customer.prefix"), StringUtils.EMPTY));
    }

    private static String append(Long customer, String env) {
        return PropertyUtils.properties.getProperty(env + ".customer.prefix") + customer;
    }

    private static boolean isDateParseable(String days) {
        try {
            Integer.parseInt(days);
        }
        catch (NumberFormatException e) {
            return false;
        }
        return true;
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

    private static String getFilePath(String[] args, String prefix) {
        String localDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYY-MM-dd"));
        File dir = new File(CNO_PATH + localDate);
        if (!dir.exists()){
            dir.mkdirs();
        }
        String localTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH_mm_ss"));
        return CNO_PATH + localDate + "/" + prefix + "_" + localTime +".csv";
    }

    private static void writeToFile(String text, File file) throws IOException {
        FileWriter fileWriter = new FileWriter(file, false);
        fileWriter.write(text);
        fileWriter.close();
    }
}
