package com.example.sftp;

import com.jcraft.jsch.*;

import java.io.*;
import java.util.stream.Collectors;

import static com.example.constants.SFTPConnectConstants.*;

public class SFTPConnect {

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

    public void uploadEmailFiles() throws JSchException, SftpException {
        ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
        try {
            channelSftp.connect();

            System.out.println("Uploading activity file...");
            channelSftp.put(DESKTOP_PATH + EMAIL_ACTIVITY_FILE_GPG, SFTP_CONNECT_EMAIL_DIR, 0);

            System.out.println("Uploading Optout file...");
            channelSftp.put(DESKTOP_PATH + EMAIL_OPT_OUT_FILE_GPG, SFTP_CONNECT_EMAIL_DIR, 0);

            System.out.println("Uploading FTD file...");
            channelSftp.put(DESKTOP_PATH + EMAIL_FTD_FILE_GPG, SFTP_CONNECT_EMAIL_DIR, 0);

            System.out.println("Uploading CTL file...");
            channelSftp.put(DESKTOP_PATH + EMAIL_CTL_FILE, SFTP_CONNECT_EMAIL_DIR, 0);
        }
        finally {
            channelSftp.disconnect();
        }
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

    public boolean ifFileExists(String path) throws JSchException, SftpException {
        ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect();

        try {
            channel.lstat(path);
        } catch (SftpException e) {
            if(e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE){
                return false;
            } else {
                throw e;
            }
        } finally {
            channel.disconnect();
        }
        return true;
    }

    public void getPGPPublicKey() throws JSchException, SftpException, FileNotFoundException {
        ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect();
        channel.get("/pegafiletransfer/QA/pgp_public_key.pkr", DESKTOP_PATH+"pgp_public_key.pkr");
        channel.disconnect();
    }

    public void disconnectSession() {
        this.session.disconnect();
    }
}
