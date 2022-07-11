package com.example.helloworld;

import com.example.csv.CSVUtils;
import com.example.sftp.SFTPConnect;
import com.jcraft.jsch.*;
import com.opencsv.exceptions.CsvException;

import java.io.IOException;

public class FileGenerator {
    public static String[] generate(String[] args) throws JSchException, SftpException, IOException, CsvException {

        // args - ActivationDate, ProductType, CureEmailInd ,DoNotEmail, name, sentDate,
        // active/inactive policy, email id flag
        if(args.length != 8) {
            throw new CsvException("Please enter all arguments");
        }

        System.out.println("Starting hello world");

        SFTPConnect sftp = new SFTPConnect();
        sftp.openSession();
        Long[] params = sftp.getParams();
        System.out.println("Params: " + params[0]+","+params[1]);

        String[] customers = CSVUtils.addCustomers(args, params);
        String[] policies = CSVUtils.addPolicy(customers, args, params);
        CSVUtils.addEmailSent(customers, args);

        System.out.println("Customers generated: " + customers[0] + "," + customers[1]);
        System.out.println("Policy generated for above customer: " + policies[0]);
    
        sftp.uploadFiles();
        sftp.disconnectSession();

        return new String[] {customers[0], policies[0]};
    }
}