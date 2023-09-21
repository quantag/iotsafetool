package org.iotsafe.applet;

import java.security.Security;

import javax.smartcardio.CardChannel;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.iotsafe.utils.IoTSAFETools;
import org.iotsafe.utils.Tools;

public class InitializeApp {
	
	public final static byte[] FILE_0001 = Tools.hexStringToBytes("830200017302303160010121010120020040");
	public final static byte[] PRIV_KEY_0002 = Tools.hexStringToBytes("84020002740231316001024B01134E0103610107920104910200076F0101");
	public final static byte[] PUB_KEY_0002 = Tools.hexStringToBytes("85020002750231316001024B01134E0103610107920104910200076F0101");

    public InitializeApp() {

    }

    public void startInitializeApp() {
		// Initialize BC security provider
        Security.addProvider(new BouncyCastleProvider());	
        
        try {
        	
            System.out.println("--------------------------");
            System.out.println("Start IoT SAFE applet test");  
            String readerName = "OMNIKEY Smart Card Reader USB 0";
            
//            if(args.length > 0) {
//            	readerName = args[0];
//            }
		    
            // Connect to NFC device (= card) with given terminal/reader name
            CardChannel cardChannel = IoTSAFETools.connectCard(readerName);             
            System.out.println("NFC device found and connected");
            
            // Select IoT SAFE applet
            IoTSAFETools.selectIoTSAFEApplet(cardChannel);
            System.out.println("IoT SAFE applet selected");
            
            // Get version
            byte[] cardletVersion = IoTSAFETools.getVersion(cardChannel);
            System.out.println("IoT SAFE applet version: " + Tools.bytesToHexString(cardletVersion));
            
            // Store test objects
            IoTSAFETools.storeFile(cardChannel, FILE_0001);
            IoTSAFETools.storePrivKey(cardChannel, PRIV_KEY_0002);
            IoTSAFETools.storePubKey(cardChannel, PUB_KEY_0002);
            System.out.println("Test objects successfully stored");
            
            // Get auth server name
            IoTSAFETools.closeInit(cardChannel);
            System.out.println("IoT SAFE applet initialization closed");
       
        } catch (Exception e) {
            
        	System.out.println(e.getClass() + ": " + e.getMessage());
        } 
        finally {
            
        	System.out.println("End IoT SAFE applet test");
			System.out.println("------------------------");
        }

	}

}
