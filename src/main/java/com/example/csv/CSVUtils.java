package com.example.csv;

import com.example.process.ProcessUtils;
import com.opencsv.*;
import com.opencsv.exceptions.CsvException;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static com.example.constants.SFTPConnectConstants.*;

public class CSVUtils {

    public static String[] addCustomers(String[] args, Long[] params) throws IOException, CsvException {

        CUST_FILE_VARS = CUST_FILE_VARS.replace("NAME",args[4]);

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

        csvBody.get(1)[emailAddressIdx] = "NA".equalsIgnoreCase(args[7]) ? StringUtils.EMPTY : args[7];
        csvBody.get(2)[emailAddressIdx] = "NA".equalsIgnoreCase(args[7]) ? StringUtils.EMPTY : args[7];

        reader.close();

        File outputFileDesktop = new File(CNO_PATH + CUST_FILE_NAME);
        File outputFileVars = new File(getFilePath(args, CUST_FILE_VARS));
        File customerTxtFile = new File(CNO_PATH + "customer.txt");

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
        CSVWriter writer = (CSVWriter) new CSVWriterBuilder(new FileWriter(file)).withSeparator('|')
                                .withQuoteChar(CSVWriter.NO_QUOTE_CHARACTER).build();
        writer.writeAll(csvBody);
        writer.flush();
        writer.close();
        System.out.println("Generated file: " + file.getAbsolutePath());
    }

    public static String[] addPolicy(String[] customers, String[] args, Long[] params) throws IOException, CsvException {

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

        IntStream.rangeClosed(1, 4).forEach(num -> {
            csvBody.get(num)[0] =  num == 4 ? customers[1] : customers[0];
        });

        IntStream.rangeClosed(1, 4).forEach(num -> {
            csvBody.get(num)[1] = Long.toString(num == 1 ? ++params[1] : params[1]);
        });

        try {
            LocalDate date = getDate(args[0]);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd");
            updateRows(csvBody, activationDateIdx, formatter.format(date));
        }
        catch (NumberFormatException e) {
            updateRows(csvBody, activationDateIdx, args[0]);
        }

        updateRows(csvBody, baseProdTypeIdx, args[1]);
        updateRows(csvBody, prodTypeIdx, args[1]);
        updateRows(csvBody, policyStatusIdx, args[6]);

        reader.close();

        File outputFileDesktop = new File(CNO_PATH + POLICY_FILE_NAME);
        File outputFileVars = new File(getFilePath(args, POLICY_FILE_VARS));
        File policyTxtFile = new File(CNO_PATH + "policy.txt");

        writeToFile(csvBody, outputFileDesktop);
        writeToFile(csvBody, outputFileVars);
        writeToFile(Long.toString(params[1]), policyTxtFile);

        return new String[] {csvBody.get(1)[1]};
    }

    private static void updateRows(List<String[]> csvBody, int col, String value) {
        IntStream.rangeClosed(1, 4).forEach(num -> {
            csvBody.get(num)[col] = value;
        });
    }

    public static void addEmailSent(String[] args) throws IOException, CsvException {
        InputStream inputStream = CSVUtils.class.getClassLoader().getResourceAsStream(EMAIL_ACTIVITY_FILE);
        CSVReader reader = new CSVReader(new InputStreamReader(inputStream));

        String[] headers = reader.peek();
        int customerKey =  Arrays.asList(headers).indexOf("CustomerKey");
        int status =  Arrays.asList(headers).indexOf("Action");
        int actionTimestamp =  Arrays.asList(headers).indexOf("ActionTimestamp");
        int interactionId =  Arrays.asList(headers).indexOf("ClientRequestID");

        List<String[]> csvBody = reader.readAll();

        csvBody.get(1)[customerKey] = args[0];
        csvBody.get(1)[status] = args[1];
        csvBody.get(1)[interactionId] = args[2];

        ZonedDateTime utcTime = ZonedDateTime.now(ZoneId.of("America/Chicago")).minusDays(Long.parseLong(args[3]));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss");
        csvBody.get(1)[actionTimestamp] = formatter.format(utcTime);

        reader.close();

        File emailFileDesktop = new File(CNO_PATH + EMAIL_ACTIVITY_FILE);
        File emailFileVars = new File(getFilePath(args, EMAIL_ACTIVITY_FILE));

        writeToEmailFile(csvBody, emailFileDesktop);
        writeToEmailFile(csvBody, emailFileVars);

        ProcessUtils.encryptFile(emailFileDesktop);
    }

    private static void writeToEmailFile(List<String[]> csvBody, File file) throws IOException {
        CSVWriter writer = (CSVWriter) new CSVWriterBuilder(new FileWriter(file)).build();
        writer.writeAll(csvBody);
        writer.flush();
        writer.close();
        System.out.println("Generated activity file: " + file.getAbsolutePath());
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
        return CNO_PATH + localDate + "/" + prefix + args[0]+args[1]+args[2]+args[3] + "_" + localTime +".csv";
    }
}
