package com.example.sftp;

import com.jcraft.jsch.*;

import java.io.*;
import java.util.stream.Collectors;

public class SFTPConnect {

    private static final String DESKTOP_PATH = System.getProperty("user.home") + "/Desktop/";
    public static String CUST_FILE_NAME = "CNOF_EPS_CDH_CONTACT_pipe_test.csv";
    public static String POLICY_FILE_NAME = "CNOF_EPS_CDH_POLICY_pipe_test.csv";

    public static String CUST_CTL_FILE_NAME = "CNOF_EPS_CDH_CONTACT_pipe_test.ctl";
    public static String POLICY_CTL_FILE_NAME = "CNOF_EPS_CDH_POLICY_pipe_test.ctl";

    private Session session;

    public void openSession() throws JSchException {
        JSch jsch = new JSch();
        String privateKeyPath = DESKTOP_PATH + "FPAI_private_key.txt";
        try {
            jsch.addIdentity(privateKeyPath);
            session = jsch.getSession("sftp-user", "cnollc-cnocdh-stg1-sftp.pegacloud.net");
            session.setConfig("PreferredAuthentications", "publickey,gssapi-keyex,gssapi-with-mic");
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
        } catch (JSchException e) {
            throw new RuntimeException("Failed to create Jsch Session object.", e);
        }
        session.connect();
    }

    public void uploadFiles() throws JSchException, SftpException {

        ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect();

        System.out.println("Uploading customer file...");
        channel.put(DESKTOP_PATH + CUST_FILE_NAME, "/pegafiletransfer/Wellspring/",0 );
        channel.put(DESKTOP_PATH + CUST_CTL_FILE_NAME, "/pegafiletransfer/Wellspring/",0 );

        System.out.println("Uploading policy file...");
        channel.put(DESKTOP_PATH + POLICY_FILE_NAME, "/pegafiletransfer/Wellspring/",0 );
        channel.put(DESKTOP_PATH + POLICY_CTL_FILE_NAME, "/pegafiletransfer/Wellspring/",0 );

        System.out.println("Uploading customer id file...");
        channel.put(DESKTOP_PATH+"customer.txt", "/pegafiletransfer/QA/generated/customer.txt");

        System.out.println("Uploading policy id file...");
        channel.put(DESKTOP_PATH+"policy.txt", "/pegafiletransfer/QA/generated/policy.txt");

        channel.disconnect();
    }

    public Long[] getParams() throws JSchException, SftpException, FileNotFoundException {

        ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect();

        channel.get("/pegafiletransfer/QA/generated/customer.txt", DESKTOP_PATH+"customer.txt");
        String customerId = new BufferedReader(new FileReader(DESKTOP_PATH+"customer.txt"))
                .lines().collect(Collectors.joining("\n"));

        channel.get("/pegafiletransfer/QA/generated/policy.txt", DESKTOP_PATH+"policy.txt");
        String policyId = new BufferedReader(new FileReader(DESKTOP_PATH+"policy.txt"))
                .lines().collect(Collectors.joining("\n"));

        channel.disconnect();
        return new Long[] {Long.parseLong(customerId), Long.parseLong(policyId)};
    }

    public void disconnectSession() {
        this.session.disconnect();
    }
}
