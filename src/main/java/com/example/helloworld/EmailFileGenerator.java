package com.example.helloworld;

import com.example.csv.CSVUtils;
import com.example.sftp.SFTPConnect;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.opencsv.exceptions.CsvException;

import java.io.IOException;

public class EmailFileGenerator {
    public static void generate(String[] args) throws JSchException, SftpException, IOException, CsvException {
        System.out.println("Starting email file generator");

        // args - customerId, status, interactionid
        if(args.length != 3) {
            throw new CsvException("Please enter all arguments");
        }
        CSVUtils.addEmailSent(args);

        SFTPConnect sftp = new SFTPConnect();
        try {
            sftp.openSession();
            sftp.uploadEmailFiles();
        } finally {
            sftp.disconnectSession();
        }
    }
}
