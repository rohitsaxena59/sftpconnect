package com.example.process;

import com.example.csv.CSVUtils;
import com.opencsv.CSVReader;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.jcajce.JcaPGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;

import java.io.*;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Date;
import java.util.Iterator;

import static com.example.constants.SFTPConnectConstants.*;

public class ProcessUtils {

    public static void encryptFile(File inputFile) throws IOException {

        System.out.println("Encrypting activity file");
        Runtime.getRuntime().exec("gpg --output " + DESKTOP_PATH + EMAIL_ACTIVITY_FILE_GPG  +
                " --encrypt --recipient user@cnoinc.com --trust-model always " +
                "--yes " + inputFile.getAbsolutePath());
    }

//    public static void encryptFile(File inputFile) {
//        try {
//
//            Security.addProvider(new BouncyCastleProvider());
//            InputStream keyStream = ProcessUtils.class.getClassLoader().getResourceAsStream(PGP_PUBLIC_KEY);
//
//            PGPPublicKey key = getPublicKey(keyStream);
//
//            OutputStream out = new DataOutputStream(new FileOutputStream(DESKTOP_PATH + "activity_java_test.pgp"));
//            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
//
//            PGPCompressedDataGenerator comData = new PGPCompressedDataGenerator(PGPCompressedDataGenerator.ZIP);
//            writeStringToLiteralData(comData.open(bOut), inputStreamToString(new FileInputStream(inputFile)));
//            comData.close();
//
//            PGPEncryptedDataGenerator cPk = new PGPEncryptedDataGenerator(PGPEncryptedDataGenerator.CAST5,false,
//                    new SecureRandom(),
//                    "BC");
//
//            cPk.addMethod(key);
//
//
//
//            byte[] bytes = bOut.toByteArray();
//
//            out = cPk.open(out, bytes.length);
//
//            out.write(bytes);
//
//            cPk.close();
//
//            out.close();
//
//        } catch (Exception e) {
//
//            e.printStackTrace();
//
//            throw new Exception(e.toString());
//
//        }
//    }

    private static PGPPublicKey getPublicKey(InputStream publicKey) throws Exception {

        PGPPublicKeyRingCollection pgpPub = new PGPPublicKeyRingCollection(
                PGPUtil.getDecoderStream(new BufferedInputStream(publicKey)), new JcaKeyFingerprintCalculator());

        PGPPublicKey key = null;
        Iterator<PGPPublicKeyRing> rIt = pgpPub.getKeyRings();
        while (key == null && rIt.hasNext()) {
            PGPPublicKeyRing kRing = rIt.next();
            Iterator<PGPPublicKey> kIt = kRing.getPublicKeys();

            while (key == null && kIt.hasNext()) {
                PGPPublicKey k = kIt.next();
                if (k.isEncryptionKey()) {
                    key = k;
                }
            }
        }
        if (key == null) {
            throw new Exception("Can't find key");
        }
        return key;
    }

    private static void writeStringToLiteralData(OutputStream out, String inString) throws IOException {

        PGPLiteralDataGenerator lData = new PGPLiteralDataGenerator();
        OutputStream pOut = lData.open(out, PGPLiteralData.BINARY, "", inString.length(), new Date());
        pOut.write(inString.getBytes());
        lData.close();
    }

    private static String inputStreamToString(InputStream in) {

        StringBuffer buf = new StringBuffer();
        try {
            InputStreamReader isr = null;
            try {
                isr = new InputStreamReader(in, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                isr = new InputStreamReader(in);
            }
            int c = 0;
            while ((c = isr.read()) != -1) {
                buf.append((char) c);
            }
            in.close();
            isr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buf.toString();
    }
}
