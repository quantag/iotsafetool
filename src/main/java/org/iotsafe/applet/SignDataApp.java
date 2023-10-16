package org.iotsafe.applet;

import java.security.Security;

import javax.smartcardio.CardChannel;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.iotsafe.utils.IoTSAFETools;
import org.iotsafe.utils.Tools;

public class SignDataApp {

    public final static byte[] KEY_PAIR_0002 = Tools.hexStringToBytes("84020002");

    public final static byte[] COMP_SIGN_INIT_OPEN_SESSION_0002 = Tools.hexStringToBytes("84020002A1010191020001920104");
    public final static byte[] DATA_TO_SIGN = Tools.hexStringToBytes("9B080102030405060708");
    public final static byte[] VER_SIGN_INIT_OPEN_SESSION_0002 = Tools.hexStringToBytes("85020002A1010191020001920104");

    public static void main(String[] args) {
        // Initialize BC security provider
        Security.addProvider(new BouncyCastleProvider());

        try {

            System.out.println("--------------------------");
            System.out.println("Start IoT SAFE applet test");
            String readerName = "OMNIKEY Smart Card Reader USB 0";

            //if(args.length > 0) {
            //    readerName = args[0];
            //}

            // Connect to NFC device (= card) with given terminal/reader name
            CardChannel cardChannel = IoTSAFETools.connectCard(readerName);
            System.out.println("NFC device found and connected");

            // Select IoT SAFE applet
            IoTSAFETools.selectIoTSAFEApplet(cardChannel);
            System.out.println("IoT SAFE applet selected");

            // Get version
            byte[] cardletVersion = IoTSAFETools.getVersion(cardChannel);
            System.out.println("IoT SAFE applet version: " + Tools.bytesToHexString(cardletVersion));

            // Get version
            byte[] random = IoTSAFETools.getRandom(cardChannel, (byte)16);
            System.out.println("Random bytes: " + Tools.bytesToHexString(random));

            // Generate key pair
            byte[] keyPairData = IoTSAFETools.genKeyPair(cardChannel, KEY_PAIR_0002);
            System.out.println("Generated key pair: " + Tools.bytesToHexString(keyPairData));

            // Sign data
            System.out.println("Sign data...");
            IoTSAFETools.computeSignInitOpenSession(cardChannel, COMP_SIGN_INIT_OPEN_SESSION_0002);
            byte[] signature = IoTSAFETools.computeSignUpdateFinal(cardChannel, DATA_TO_SIGN);
            IoTSAFETools.computeSignInitCloseSession(cardChannel);
            System.out.println("Signature: " + Tools.bytesToHexString(signature));

            // Verify signature
            System.out.println("Verify signature...");
            IoTSAFETools.verifySignInitOpenSession(cardChannel, VER_SIGN_INIT_OPEN_SESSION_0002);
            try {
                IoTSAFETools.verifySignUpdateFinal(cardChannel, DATA_TO_SIGN, signature);
                System.out.println("Signature verification successfully completed");
            } catch(Exception ex) {
                System.out.println("Signature verification failed");
            }
            IoTSAFETools.verifySignInitCloseSession(cardChannel);

        } catch (Exception e) {

            System.out.println(e.getClass() + ": " + e.getMessage());
        }
        finally {

            System.out.println("End IoT SAFE applet test");
            System.out.println("------------------------");
        }
    }
}
/*
--------------------------
Start IoT SAFE applet test
Used card reader: OMNIKEY Smart Card Reader USB 0
NFC device found and connected
APDU-C: 00A404000FA00000003053F1240177010149534100
APDU-R: 9000
IoT SAFE applet selected
APDU-C: 80700000
APDU-R: 01049000
IoT SAFE applet version: 0104
APDU-C: 8084000010
APDU-R: 0B0E398CCCB8D3A28053C302E2CF82BC9000
Random bytes: 0B0E398CCCB8D3A28053C302E2CF82BC
APDU-C: 80B900000484020002
APDU-R: 840200028502000234454943864104D569954216F62264F01B78ED599898D12C436A4633505DA18B008D5529E09AF283ED5E453EDC8B5492704C1B8D48895857442057335B39078AF27A9008DC14019000
Generated key pair: 840200028502000234454943864104D569954216F62264F01B78ED599898D12C436A4633505DA18B008D5529E09AF283ED5E453EDC8B5492704C1B8D48895857442057335B39078AF27A9008DC1401
Sign data...
APDU-C: 802A00010E84020002A1010191020001920104
APDU-R: 9000
APDU-C: 802B80010A9B080102030405060708
APDU-R: 3340343A117BF21D74315B9D7C4EB3EAF0CAD125AD98FF0E14A02D9554C6274A663CE3C0E9E19BEBFF3454694E95D7E1DA7AB79A9242FEE41F063245A2C6B2C91A9B9000
APDU-C: 802A0101
APDU-R: 9000
Signature: 3340343A117BF21D74315B9D7C4EB3EAF0CAD125AD98FF0E14A02D9554C6274A663CE3C0E9E19BEBFF3454694E95D7E1DA7AB79A9242FEE41F063245A2C6B2C91A9B
Verify signature...
APDU-C: 802C00010E85020002A1010191020001920104
APDU-R: 9000
APDU-C: 802D80014C9B0801020304050607083340343A117BF21D74315B9D7C4EB3EAF0CAD125AD98FF0E14A02D9554C6274A663CE3C0E9E19BEBFF3454694E95D7E1DA7AB79A9242FEE41F063245A2C6B2C91A9B
APDU-R: 9000
Signature verification successfully completed
APDU-C: 802C0101
APDU-R: 9000
End IoT SAFE applet test
------------------------
 */