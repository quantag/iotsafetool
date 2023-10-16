package org.iotsafe.tool;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.iotsafe.utils.IoTSAFETools;
import org.iotsafe.utils.Tools;

import javax.smartcardio.CardChannel;
import java.security.Security;


public class IoTSAFETool {
    private final String readerName;

    public IoTSAFETool(String readerName) {
        this.readerName = readerName;
    }

    public void runGenKeys() {
        final byte[] KEY_PAIR_0002 = Tools.hexStringToBytes("84020002");

        // Initialize BC security provider
        Security.addProvider(new BouncyCastleProvider());

        try {
            // Connect to NFC device (= card) with given terminal/reader name
            CardChannel cardChannel = IoTSAFETools.connectCard(readerName);
            System.out.println("NFC device found and connected");

            // Select IoT SAFE applet
            IoTSAFETools.selectIoTSAFEApplet(cardChannel);
            System.out.println("IoT SAFE applet selected");

            // Generate key pair
            byte[] keyPairData = IoTSAFETools.genKeyPair(cardChannel, KEY_PAIR_0002);
            System.out.println("Generated Public Key: " + Tools.bytesToHexString(keyPairData));
        }
        catch (Exception e) {
            System.out.println("ERROR: "+ e.getClass() +": "+ e.getMessage());
        }
    }

    public void getAppletVersion() {
        // Initialize BC security provider
        Security.addProvider(new BouncyCastleProvider());

        try {
            // Connect to NFC device (= card) with given terminal/reader name
            CardChannel cardChannel = IoTSAFETools.connectCard(readerName);
            System.out.println("NFC device found and connected");

            // Select IoT SAFE applet
            IoTSAFETools.selectIoTSAFEApplet(cardChannel);
            System.out.println("IoT SAFE applet selected");

            // Get version
            byte[] cardletVersion = IoTSAFETools.getVersion(cardChannel);
            System.out.println("IoT SAFE applet version: " + Tools.bytesToHexString(cardletVersion));
        }
        catch (Exception e) {
            System.out.println("ERROR: "+ e.getClass() +": "+ e.getMessage());
        }
    }

    public void runGetPublicKey() {
        // Initialize BC security provider
        Security.addProvider(new BouncyCastleProvider());

        try {
            // Connect to NFC device (= card) with given terminal/reader name
            CardChannel cardChannel = IoTSAFETools.connectCard(readerName);
            System.out.println("NFC device found and connected");

            // Select IoT SAFE applet
            IoTSAFETools.selectIoTSAFEApplet(cardChannel);
            System.out.println("IoT SAFE applet selected");

              final byte[] sdhkkshfjsk = Tools.hexStringToBytes("8502000200");
            //final byte[] sdhkkshfjsk = Tools.hexStringToBytes("75023131");
            byte[] publicKeyData = IoTSAFETools.getPublicKey(cardChannel, sdhkkshfjsk);
            System.out.println("Public key: " + Tools.bytesToHexString(publicKeyData));
        }
        catch (Exception e) {
            System.out.println("ERROR: "+ e.getClass() +": "+ e.getMessage());
        }
    }

    public void runHmac(byte[] byteFile) {
        final byte[] COMP_SIGN_INIT_OPEN_SESSION_0002 = Tools.hexStringToBytes("84020002A1010191020001920104");
        //final byte[] DATA_TO_SIGN = Tools.hexStringToBytes("9B080102030405060708"); //original
        //final byte[] DATA_TO_SIGN = Tools.hexStringToBytes("9B08B19AB43AFE195BD6"); //my test
        final byte[] VER_SIGN_INIT_OPEN_SESSION_0002 = Tools.hexStringToBytes("85020002A1010191020001920104");

        // Initialize BC security provider
        Security.addProvider(new BouncyCastleProvider());

        try {
            // Connect to NFC device (= card) with given terminal/reader name
            CardChannel cardChannel = IoTSAFETools.connectCard(readerName);
            System.out.println("NFC device found and connected");

            // Select IoT SAFE applet
            IoTSAFETools.selectIoTSAFEApplet(cardChannel);
            System.out.println("IoT SAFE applet selected");

            // Sign data
            byte[] signature = null;
            try {
                System.out.println("Sign data...");
                IoTSAFETools.computeSignInitOpenSession(cardChannel, COMP_SIGN_INIT_OPEN_SESSION_0002);
                signature = IoTSAFETools.computeSignUpdateFinal(cardChannel, byteFile);
                //! IoTSAFETools.computeSignInitCloseSession(cardChannel);
                System.out.println("Signature: " + Tools.bytesToHexString(signature));
            }
            finally {
                IoTSAFETools.computeSignInitCloseSession(cardChannel);
            }

            // Verify signature
            System.out.println("Verify signature...");
            IoTSAFETools.verifySignInitOpenSession(cardChannel, VER_SIGN_INIT_OPEN_SESSION_0002);
            try {
                IoTSAFETools.verifySignUpdateFinal(cardChannel, byteFile, signature);
                System.out.println("Signature verification successfully completed");
            }
            catch(Exception ex) {
                System.out.println("Signature verification failed");
            }
            IoTSAFETools.verifySignInitCloseSession(cardChannel);
        }
        catch (Exception e) {
            System.out.println("ERROR: "+ e.getClass() +": "+ e.getMessage());
        }
    }

    public void getHelpMessage() {
        System.out.println("IoT SAFE Tool, version 1.0");
        System.out.println("Usage: java -jar IoT_SAFE_Tool.jar [options]");
        System.out.println("where options include");
        System.out.println("  -genkeys");
        System.out.println("         generate key pair and store on card");
        System.out.println("         return generated public key");
        System.out.println("  -getpub");
        System.out.println("         return public key from card");
        System.out.println("  -hmac <filename>");
        System.out.println("         read the file, ...");
        System.out.println("  -version");
        System.out.println("         return IoT SAFE applet version");
        System.out.println("  -? -h -help");
        System.out.println("         print this help message");
    }
}
