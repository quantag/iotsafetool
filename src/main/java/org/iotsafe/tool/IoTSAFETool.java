package org.iotsafe.tool;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.iotsafe.utils.IoTSAFETools;
import org.iotsafe.utils.Tools;

import javax.smartcardio.CardChannel;
import java.security.Security;


public class IoTSAFETool {
    private String readerName = "";

    public IoTSAFETool(String readerName) {
        this.readerName = readerName;
    }

    public void runGenKeys() {
        final byte[] KEY_PAIR_0002 = Tools.hexStringToBytes("84020002");
        System.out.println("--- start GenKeys()");

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
        System.out.println("--- stop GenKeys()");
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
        System.out.println("--- start GetPublicKey()");

        // Initialize BC security provider
        Security.addProvider(new BouncyCastleProvider());

        try {
            // Connect to NFC device (= card) with given terminal/reader name
            CardChannel cardChannel = IoTSAFETools.connectCard(readerName);
            System.out.println("NFC device found and connected");

            // Select IoT SAFE applet
            IoTSAFETools.selectIoTSAFEApplet(cardChannel);
            System.out.println("IoT SAFE applet selected");
        }
        catch (Exception e) {
            System.out.println("ERROR: "+ e.getClass() +": "+ e.getMessage());
        }

        System.out.println("--- stop GetPublicKey()");
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
