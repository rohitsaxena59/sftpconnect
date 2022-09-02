package com.example.sftp;

import com.example.property.PropertyUtils;
import com.jcraft.jsch.*;

import java.io.*;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static com.example.constants.SFTPConnectConstants.*;

public class SFTPConnect {

    private Session session;
    private String env;

    public SFTPConnect(String env) throws IOException {
        this.env  = env;
    }

    public void openSession() throws JSchException {
        JSch jsch = new JSch();
        String privateKeyPath = CNO_PATH + getProperty("key");
        try {
            jsch.addIdentity(privateKeyPath);
            session = jsch.getSession(getProperty("user"), getProperty("host"));
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
        channel.put(CNO_PATH + CUST_FILE_NAME, getProperty("sftp.root")+"/Wellspring/",0 );
        channel.put(CNO_PATH + CUST_CTL_FILE_NAME, getProperty("sftp.root")+"/Wellspring/",0 );

        System.out.println("Uploading policy file...");
        channel.put(CNO_PATH + POLICY_FILE_NAME, getProperty("sftp.root")+"/Wellspring/",0 );
        channel.put(CNO_PATH + POLICY_CTL_FILE_NAME, getProperty("sftp.root")+"/Wellspring/",0 );

        channel.disconnect();
    }

    public void uploadQAFiles() throws JSchException, SftpException {
        ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect();

        System.out.println("Uploading customer id file...");
        channel.put(CNO_PATH +"customer.txt", getProperty("sftp.root") +"/QA/generated/customer.txt");

        System.out.println("Uploading policy id file...");
        channel.put(CNO_PATH +"policy.txt", getProperty("sftp.root") +"/QA/generated/policy.txt");

        channel.disconnect();
    }

    public void uploadEmailFiles() throws JSchException, SftpException {
        ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
        try {
            channelSftp.connect();

            System.out.println("Uploading activity file...");
            channelSftp.put(CNO_PATH + EMAIL_ACTIVITY_FILE_GPG, getProperty("sftp.root")  + SFTP_CONNECT_EMAIL_DIR, 0);

            System.out.println("Uploading Optout file...");
            channelSftp.put(CNO_PATH + EMAIL_OPT_OUT_FILE_GPG, getProperty("sftp.root")  + SFTP_CONNECT_EMAIL_DIR, 0);

            System.out.println("Uploading FTD file...");
            channelSftp.put(CNO_PATH + EMAIL_FTD_FILE_GPG, getProperty("sftp.root")  + SFTP_CONNECT_EMAIL_DIR, 0);

            System.out.println("Uploading CTL file...");
            channelSftp.put(CNO_PATH + EMAIL_CTL_FILE, getProperty("sftp.root")  + SFTP_CONNECT_EMAIL_DIR, 0);
        }
        finally {
            channelSftp.disconnect();
        }
    }

    public Long[] getParams() throws JSchException, SftpException, FileNotFoundException {

        ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect();

        channel.get(getProperty("sftp.root") + "/QA/generated/customer.txt", CNO_PATH +"customer.txt");
        String customerId = new BufferedReader(new FileReader(CNO_PATH +"customer.txt"))
                .lines().collect(Collectors.joining("\n"));

        channel.get(getProperty("sftp.root") + "/QA/generated/policy.txt", CNO_PATH +"policy.txt");
        String policyId = new BufferedReader(new FileReader(CNO_PATH +"policy.txt"))
                .lines().collect(Collectors.joining("\n"));

        channel.disconnect();
        return new Long[] {Long.parseLong(customerId), Long.parseLong(policyId)};
    }

    private String getProperty(String key) {
        return PropertyUtils.properties.getProperty(env + "." + key);
    }

    public boolean ifFilesExist(List<String> paths) throws JSchException, SftpException {
        ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect();

        try {
            for(String path : paths) {
                channel.lstat(path);
            }
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

    public void disconnectSession() {
        this.session.disconnect();
    }
}
