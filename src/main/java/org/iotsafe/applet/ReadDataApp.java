package org.iotsafe.applet;

import java.security.Security;

import javax.smartcardio.CardChannel;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.iotsafe.utils.IoTSAFETools;
import org.iotsafe.utils.Tools;

public class ReadDataApp {

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

            // Get data application
            byte[] dataApp = IoTSAFETools.getDataApplication(cardChannel);
            System.out.println("Data application: " + Tools.bytesToHexString(dataApp));

            // Get data application
            byte[] dataObjList = IoTSAFETools.getDataObjectList(cardChannel);
            System.out.println("Data object list: " + Tools.bytesToHexString(dataObjList));

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
APDU-C: 80CB000044
APDU-R: 10011011201122334455667788112233445566778811223344556677881122334455667788B1010AB2010AB3010AB4010A61010F910200019201046F0101940102B701019000
Data application: 10011011201122334455667788112233445566778811223344556677881122334455667788B1010AB2010AB3010AB4010A61010F910200019201046F0101940102B70101
APDU-C: 80CB0100
APDU-R: 74023131840200026001024A01014B01134E0103610107920204910200076F010175023131850200026001024A01014B01134E0103610107920204910200076F010173023031830200016001014A0101210101200200409000
Data object list: 74023131840200026001024A01014B01134E0103610107920204910200076F010175023131850200026001024A01014B01134E0103610107920204910200076F010173023031830200016001014A010121010120020040
End IoT SAFE applet test
------------------------
 */