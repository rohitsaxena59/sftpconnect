package com.example.helloworld;

import com.example.csv.BulkCSVUtils;
import com.example.sftp.SFTPConnect;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.util.Arrays;


public class BulkFileGenerator {
    public static String[] generate(String[] args) throws JSchException, SftpException, IOException, CsvException {

        System.out.println("Starting bulk file generator");

        // args - ActivationDate, ProductType, CureEmailInd ,DoNotEmail, name, sentDate,
        // active/inactive policy, email address, number of customers, env, append
        if(args.length != 11) {
            throw new CsvException("Please enter all arguments");
        }

        SFTPConnect sftp = new SFTPConnect(args[9]);
        sftp.openSession();
        Long[] params = sftp.getParams();
        System.out.println("Params: " + params[0]+","+params[1]);

        String[] customers = BulkCSVUtils.addCustomers(args, params);
        String[] policies = BulkCSVUtils.addPolicy(customers, args, params);

        System.out.println("Customers generated: " + Arrays.toString(customers));
        System.out.println("Policy generated for above customer: " + Arrays.toString(policies));

        sftp.uploadQAFiles();
        sftp.disconnectSession();

        return new String[] {customers[0], policies[0]};
    }
}
