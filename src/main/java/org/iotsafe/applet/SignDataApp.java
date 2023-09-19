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
            String readerName = "";
            
            if(args.length > 0) {
            	readerName = args[0];
            } 	
		    
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
