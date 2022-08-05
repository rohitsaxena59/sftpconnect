package com.example.process;

import java.io.*;

import static com.example.constants.SFTPConnectConstants.*;

public class ProcessUtils {

    public static void encryptFile(File inputFile) throws IOException {

        System.out.println("Encrypting activity file");
        Runtime.getRuntime().exec("gpg --output " + CNO_PATH + EMAIL_ACTIVITY_FILE_GPG  +
                " --encrypt --recipient user@cnoinc.com --trust-model always " +
                "--yes " + inputFile.getAbsolutePath());
    }
}
