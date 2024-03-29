package com.example.constants;

public class SFTPConnectConstants {
    public static String CUST_FILE_VARS = "CNOF_EPS_CDH_CONTACT_NAME_";
    public static String POLICY_FILE_VARS = "CNOF_EPS_CDH_POLICY_NAME_";

    public static final String CNO_PATH = System.getenv("CNO_HOME");

    public static final String CUST_FILE_NAME = "CNOF_EPS_CDH_CONTACT_pipe_test.csv";
    public static final String POLICY_FILE_NAME = "CNOF_EPS_CDH_POLICY_pipe_test.csv";
    public static final String CUST_CTL_FILE_NAME = "CNOF_EPS_CDH_CONTACT_pipe_test.ctl";
    public static final String POLICY_CTL_FILE_NAME = "CNOF_EPS_CDH_POLICY_pipe_test.ctl";

    public static final String EMAIL_ACTIVITY_FILE = "ColonialPenn_Activity__New.csv";
    public static final String EMAIL_ACTIVITY_FILE_GPG = "ColonialPenn_Activity__New.pgp";
    public static final String EMAIL_OPT_OUT_FILE_GPG = "ColonialPenn_OptOut_New.pgp";
    public static final String EMAIL_FTD_FILE_GPG = "ColonialPenn_FTD_New.pgp";
    public static final String EMAIL_CTL_FILE = "ColonialPenn_Extracts_xxxxxxxx.pgp";

    public static final String SFTP_CONNECT_EMAIL_DIR = "/Email/";
    public static final String SFTP_CONNECT_WELLSPRING_DIR = "/pegafiletransfer/Wellspring/";
}
