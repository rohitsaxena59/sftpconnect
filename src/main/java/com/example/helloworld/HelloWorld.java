package com.example.helloworld;

import com.example.csv.CSVUtils;
import com.jcraft.jsch.*;
import com.opencsv.exceptions.CsvException;

import java.io.IOException;

public class HelloWorld {
    public static void main(String[] args) throws JSchException, SftpException, IOException, CsvException {
        System.out.println("Hello world args4");

        CSVUtils.addCustomers(2);
        HelloWorld obj = new HelloWorld();
        //obj.setupJsch();
    }

    private ChannelSftp setupJsch() throws JSchException, SftpException {
        JSch jsch = new JSch();
        Session session = null;
        String privateKeyPath = "/Users/rohitsaxena/Desktop/id_rsa";
        try {
            jsch.addIdentity(privateKeyPath);
            session = jsch.getSession("fpai4", "35.245.98.89", 22);
            session.setConfig("PreferredAuthentications", "publickey,gssapi-keyex,gssapi-with-mic");
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
        } catch (JSchException e) {
            throw new RuntimeException("Failed to create Jsch Session object.", e);
        }

        try {
            session.connect();
            ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();
            System.out.println("Downloading file...");
            channel.get("/home/files/Rohit/CNOF_EPS_CDH_CONTACT_Rohit_5_1_1.csv", "/Users/rohitsaxena/Desktop/");

            channel.disconnect();
            session.disconnect();
        } catch (JSchException e) {
            e.printStackTrace();
        }
        return null;
    }
}
