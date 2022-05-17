package com.example.helloworld;

import com.example.csv.CSVUtils;
import com.example.sftp.SFTPConnect;
import com.jcraft.jsch.*;
import com.opencsv.exceptions.CsvException;

import java.io.IOException;

public class HelloWorld {
    public static void main(String[] args) throws JSchException, SftpException, IOException, CsvException {

        // args - ActivationDate, ProductType, CureEmailInd ,DoNotEmail, name
        if(args.length != 5) {
            throw new CsvException("Please enter all arguments");
        }

        System.out.println("Starting hello world");

        SFTPConnect sftp = new SFTPConnect();
        sftp.openSession();
        Long[] params = sftp.getParams();
        System.out.println("Params: " + params[0]+","+params[1]);

        String[] customers = CSVUtils.addCustomers(args, params);
        String[] policies = CSVUtils.addPolicy(customers, args, params);

        System.out.println("Customers generated: " + customers[0] + "," + customers[1]);
        System.out.println("Policy generated for above customer: " + policies[0]);

        sftp.uploadFiles(args);
        sftp.disconnectSession();
    }
}