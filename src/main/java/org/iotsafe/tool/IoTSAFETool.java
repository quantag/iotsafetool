package org.iotsafe.tool;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.iotsafe.exception.CardletException;
import org.iotsafe.utils.IoTSAFETools;
import org.iotsafe.utils.Tools;

import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;
import java.security.Security;
import java.util.List;


public class IoTSAFETool {
    private final String readerName;

    public IoTSAFETool(String readerIndex) {
        // Get the list of available terminals
        TerminalFactory factory = TerminalFactory.getDefault();
        List<CardTerminal> terminals;
        try
        {
            terminals = factory.terminals().list();
        }
        catch (CardException e) {
            throw new CardletException("No card reader available");
        }
        // get terminal name by index
        this.readerName = terminals.get(Integer.parseInt(readerIndex)).getName();
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

    public void getCardReadersList() {
        // Get the list of available terminals
        TerminalFactory factory = TerminalFactory.getDefault();
        List<CardTerminal> terminals;
        try
        {
            terminals = factory.terminals().list();
        }
        catch (CardException e) {
            throw new CardletException("No card reader available");
        }

        //print the list
        int t = 0;
        System.out.println("List of available card readers:");
        for(CardTerminal terminal : terminals) {
            System.out.println("   "+ t +" : "+ terminal.getName());
            t++;
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

        //byte[] byteFile = Tools.hexStringToBytes("B19AB43AFE195BD6");
        byte[] addBytes = Tools.hexStringToBytes("9B08");
        byte[] finalByteFile = new byte[addBytes.length + 8]; //byteFile.length
        System.arraycopy(addBytes, 0, finalByteFile, 0, addBytes.length);
        System.arraycopy(byteFile, 0, finalByteFile, addBytes.length, 8); //byteFile.length

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
                signature = IoTSAFETools.computeSignUpdateFinal(cardChannel, finalByteFile);
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
                IoTSAFETools.verifySignUpdateFinal(cardChannel, finalByteFile, signature);
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
        /*
        Used card reader: OMNIKEY Smart Card Reader USB 0
        NFC device found and connected
        APDU-C: 00A404000FA00000003053F1240177010149534100
        APDU-R: 9000
        IoT SAFE applet selected
        Sign data...
        APDU-C: 802A00010E84020002A1010191020001920104
        APDU-R: 9000
        APDU-C: 802B80010A9B08B19AB43AFE195BD6
        APDU-R: 3340AD6A1161249BBBF7632E2A94D12C06091EFEAEB7C11BADBE54E1FC61B41DEAB0F7A0558B8DB45B984BD6E4500900BF94830D1A95AB199504F3F731C2024CEF819000
        Signature: 3340AD6A1161249BBBF7632E2A94D12C06091EFEAEB7C11BADBE54E1FC61B41DEAB0F7A0558B8DB45B984BD6E4500900BF94830D1A95AB199504F3F731C2024CEF81
        APDU-C: 802A0101
        APDU-R: 9000
        Verify signature...
        APDU-C: 802C00010E85020002A1010191020001920104
        APDU-R: 9000
        APDU-C: 802D80014C9B08B19AB43AFE195BD63340AD6A1161249BBBF7632E2A94D12C06091EFEAEB7C11BADBE54E1FC61B41DEAB0F7A0558B8DB45B984BD6E4500900BF94830D1A95AB199504F3F731C2024CEF81
        APDU-R: 9000
        Signature verification successfully completed
        APDU-C: 802C0101
        APDU-R: 9000
         */
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
        System.out.println("  -list");
        System.out.println("         return list of available card readers with index");
        System.out.println("  -reader <card reader index>");
        System.out.println("         set card reader by its index");
        System.out.println("  -version");
        System.out.println("         return IoT SAFE applet version");
        System.out.println("  -? -h -help");
        System.out.println("         print this help message");
    }
}
