package com.example.sftp;

import com.jcraft.jsch.*;

public class SFTPConnect {

    private static final String DESKTOP_PATH = System.getProperty("user.home") + "/Desktop/";
    public static final String CUST_FILE_NAME = "CNOF_EPS_CDH_CONTACT_Rohit_5_1_1.csv";
    public static final String POLICY_FILE_NAME = "CNOF_EPS_CDH_POLICY_Rohit_5_1_1.csv";

    public ChannelSftp setupJsch() throws JSchException, SftpException {
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
            channel.put(DESKTOP_PATH + CUST_FILE_NAME, "/home/files/Rohit/",0 );

            System.out.println("Uploading policy file...");
            channel.put(DESKTOP_PATH + POLICY_FILE_NAME, "/home/files/Rohit/",0 );

            channel.disconnect();
            session.disconnect();
        } catch (JSchException e) {
            e.printStackTrace();
        }
        return null;
    }
}
