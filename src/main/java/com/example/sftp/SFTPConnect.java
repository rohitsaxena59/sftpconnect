package com.example.sftp;

import com.jcraft.jsch.*;

public class SFTPConnect {

    private static final String DESKTOP_PATH = System.getProperty("user.home") + "/Desktop/";
    public static String CUST_FILE_NAME = "CNOF_EPS_CDH_CONTACT_NAME_5_1_1.csv";
    public static String POLICY_FILE_NAME = "CNOF_EPS_CDH_POLICY_NAME_5_1_1.csv";

    public ChannelSftp setupJsch(String[] args) throws JSchException, SftpException {
        CUST_FILE_NAME = CUST_FILE_NAME.replace("NAME",args[4]);
        POLICY_FILE_NAME = POLICY_FILE_NAME.replace("NAME",args[4]);

        JSch jsch = new JSch();
        Session session = null;
        String privateKeyPath = DESKTOP_PATH + "id_rsa";
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

            System.out.println("Uploading customer file...");
            channel.put(DESKTOP_PATH + CUST_FILE_NAME, "/home/files/" + args[4] +"/",0 );

            System.out.println("Uploading policy file...");
            channel.put(DESKTOP_PATH + POLICY_FILE_NAME, "/home/files/" + args[4] +"/",0 );

            channel.disconnect();
            session.disconnect();
        } catch (JSchException e) {
            e.printStackTrace();
        }
        return null;
    }
}
