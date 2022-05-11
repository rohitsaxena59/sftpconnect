package com.example.helloworld;

import com.example.csv.CSVUtils;
import com.example.sftp.SFTPConnect;
import com.jcraft.jsch.*;
import com.opencsv.exceptions.CsvException;

import java.io.IOException;

public class HelloWorld {
    public static void main(String[] args) throws JSchException, SftpException, IOException, CsvException {

        // args - ActivationDate, ProductType, CureEmailInd ,DoNotEmail, customerEmail
        if(args.length != 4) {
            throw new CsvException("Please enter all arguments");
        }

        System.out.println("Starting hello world");

        String[] customers = CSVUtils.addCustomers(args);
        System.out.println("Customers generated: " + customers[0] + "," + customers[1]);

        String[] policies = CSVUtils.addPolicy(customers, args);
        System.out.println("Policy generated for above customer: " + policies[0]);

        SFTPConnect obj = new SFTPConnect();
        //obj.setupJsch();
    }
}