package com.example.helloworld;

import com.example.csv.CSVUtils;
import com.example.sftp.SFTPConnect;
import com.jcraft.jsch.*;
import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.net.URISyntaxException;

public class HelloWorld {
    public static void main(String[] args) throws JSchException, SftpException, IOException, CsvException, URISyntaxException {
        System.out.println("Starting hello world");

        String[] customers = CSVUtils.addCustomers();
        System.out.println("Customers generated: " + customers[0]+"," + customers[1]);

        String[] policies = CSVUtils.addPolicy(customers);
        System.out.println("Policy generated for above customer: " + policies[0]);

        SFTPConnect obj = new SFTPConnect();
        obj.setupJsch();
    }

}
