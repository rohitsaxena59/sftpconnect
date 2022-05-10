package com.example.sftp;

import com.jcraft.jsch.*;

public class SFTPConnect {

    public ChannelSftp setupJsch() throws JSchException, SftpException {
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
