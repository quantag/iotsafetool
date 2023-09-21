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
            //	readerName = args[0];
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
