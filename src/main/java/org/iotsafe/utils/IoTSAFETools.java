package org.iotsafe.utils;

import java.util.List;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;

import org.apache.commons.lang3.ArrayUtils;
import org.iotsafe.exception.CardletException;


public class IoTSAFETools {
	
	/** Card reader name */
    //private static final String READER_NAME = "OMNIKEY CardMan 5x21-CL 0";
    private static final String READER_NAME = "OMNIKEY Smart Card Reader USB 0";
    //private static String READER_NAME = "ACS ACR122U PICC Interface 0";
    //private static String READER_NAME = "ACS ACR1252 1S CL Reader PICC 0";
    //private static String READER_NAME = "NXP VirtualPCSCInterface 0";
    //!private static String READER_NAME = "Identiv uTrust 3700 F CL Reader 0";
    
	/** Smart card status word in case of success */
    public static final int STATUS_OK = 0x9000;

    /**
	 * Builds Select APDU command for given AID
	 * 
	 * @param aid Application Identifier of the applet used to build select APDU command
	 * 
	 * @return Select APDU command
	 *  
	 */
    public static byte[] getSelectAPDU(byte[] aid)
    {
    	byte[] selectAPDU = null;
    	selectAPDU = ArrayUtils.addAll(selectAPDU, IoTSAFEDefines.SELECT_APDU_HEADER); 
    	selectAPDU = ArrayUtils.add(selectAPDU, (byte)aid.length);
    	selectAPDU = ArrayUtils.addAll(selectAPDU, aid); 
    	// add Le byte
    	selectAPDU = ArrayUtils.add(selectAPDU, (byte)0);
    	
    	return selectAPDU;
    }  
    
	/**
	 * Initialize access to the given card terminal
	 * 
	 * @param name 	PCSC name of the desired terminal. If name == null, then the first available terminal is selected.
	 * 
	 * @return The selected terminal or null.
	 *  
	 */
    public static CardChannel connectCard(String name) throws CardletException
	{
		// use default reader name if none is provided
		if(name.isEmpty()) {
			name = READER_NAME;
		}
		
		System.out.println("Used card reader: " + name); 	
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
	
		int i = terminals.size() - 1;
	
		// If name == null: select first available terminal
		if (name == null)
		{
			// If at least one terminal is available, select the first one
			if (terminals.size() > 0) 									
				i = 0;							
		}
		// If name != null: find the desired terminal
		else
		{
	
			while (i >= 0)
			{
				if (terminals.get(i).getName().compareTo(name) == 0)
					break;
				i--;
			}
		}
	
		// If no matching terminal found
		if (i < 0)												
			throw new CardletException("No matching card reader found");
		
		
		// Connect to the card
		try 
		{
			Card card = terminals.get(i).connect("*");
			
			// Card not present (cannot be connected)
			if (card == null)								
			{
				throw new CardletException("NFC device not available");
			}
			
			// Get the basic logical channel to the card
			CardChannel channel = card.getBasicChannel();
			
			// Channel cannot be opened
			if (channel == null)								
			{
				throw new CardletException("NFC device not available");
			}
			
			return channel;
		} 
		catch (CardException e) {
			throw new CardletException("NFC device not available");
		}
		catch (CardletException e) {
			throw e;
		}					       
	}
	
	/**
	 * Executes provided APDU command on given card channel and returns APDU response data
	 * 
	 * @param channel Currently opened card channel
	 * @param apduCommand Command to execute
	 * 
	 * @return APDU response data
	 *  
	 */
    public static ResponseAPDU executeCommand(CardChannel channel, CommandAPDU apduCommand) throws CardletException
	{
		try {
			System.out.println("APDU-C: " + Tools.bytesToHexString(apduCommand.getBytes()));
			ResponseAPDU apduResp = channel.transmit(apduCommand);
			System.out.println("APDU-R: " + Tools.bytesToHexString(apduResp.getBytes()));  
			
			return apduResp;
		}
		catch (CardException e) {
			throw new CardletException("APDU communication with NFC device failed");
		}
		catch (CardletException e) {
			throw e;
		}
	}

	/**
	 * Executes provided script of APDUs on given card channel
	 * 
	 * @param channel Currently opened card channel
	 * @param apduScript Script to execute
	 * 
	 * @return none
	 *  
	 */
    public static void executeScript(CardChannel channel, CommandAPDU[] apduScript) throws CardletException
	{
		try {
			for (int i = 0; i < apduScript.length; i++) {
	
			    CommandAPDU apduCommand = apduScript[i];
				System.out.println("APDU-C: " + Tools.bytesToHexString(apduCommand.getBytes()));
				ResponseAPDU apduResp = channel.transmit(apduCommand);
				System.out.println("APDU-R: " + Tools.bytesToHexString(apduResp.getBytes()));
			}
		}
		catch (CardException e) {
			throw new CardletException("APDU communication with NFC device failed");
		}
		catch (CardletException e) {
			throw e;
		}
	}
    
    /**
     * Selects IoT SAFE applet
     * 
     * @param channel Currently opened card channel
     * 
     * @return N/A
     *  
     */
    public static void selectIoTSAFEApplet(CardChannel channel) throws CardletException
    {       
        try {
            // select gateway cardlet
            ResponseAPDU resp = executeCommand(channel, new CommandAPDU(getSelectAPDU(IoTSAFEDefines.IOTSAFE_APPLET_AID)));
            if(resp.getSW() != STATUS_OK) 
            {
                throw new CardletException("APDU communication with NFC device failed");                            
            }
            
            return;
        }
        catch (CardletException e) {
            throw e;
        }
    }
	
	/**
     * Returns IoT SAFE applet version
     * 
     * @param channel Currently opened card channel
     * 
     * @return Applet version as byte array
     *  
     */
    public static byte[] getVersion(CardChannel channel) throws CardletException
    {       
        try {
            // get IoT SAFE applet version
            ResponseAPDU resp = executeCommand(channel, new CommandAPDU(IoTSAFEDefines.GET_VERSION_APDU_CMD));
            if(resp.getSW() != STATUS_OK) 
            {
                throw new CardletException("APDU communication with NFC device failed");                            
            }
            
            return resp.getData();
        }
        catch (CardletException e) {
            throw e;
        }
    }

    /**
     * Returns public key of IoT SAFE applet
     *
     * @param channel Currently opened card channel
     *
     * @return Public key as byte array
     *
     */
    public static byte[] getPublicKey(CardChannel channel) throws CardletException
    {
        try {
            // get public key of IoT SAFE applet
            ResponseAPDU resp = executeCommand(channel, new CommandAPDU(IoTSAFEDefines.GET_PUBLIC_KEY_APDU_HDR));
            if(resp.getSW() != STATUS_OK)
            {
                throw new CardletException("APDU communication with NFC device failed");
            }

            return resp.getData();
        }
        catch (CardletException e) {
            throw e;
        }
    }
 
    /**
     * Store private key
     * 
     * @param channel Currently opened card channel
     * 
     * @return
     *  
     */
    public static void storePrivKey(CardChannel channel, byte[] keyData) throws CardletException
    {       
    	try {
            byte[] apduBuf = null;
            apduBuf = ArrayUtils.addAll(apduBuf, IoTSAFEDefines.STORE_PRIV_KEY_APDU_HDR); 
            // total length
            apduBuf = ArrayUtils.add(apduBuf, (byte)(keyData.length));
            // data
            apduBuf = ArrayUtils.addAll(apduBuf, keyData); 
            
            ResponseAPDU resp = executeCommand(channel, new CommandAPDU(apduBuf));
            if(resp.getSW() != STATUS_OK) 
            {
                throw new CardletException("APDU communication with NFC device failed");                            
            }
        }
        catch (CardletException e) {
            throw e;
        }
    }
    
    /**
     * Store public key
     * 
     * @param channel Currently opened card channel
     * 
     *  
     */
    public static void storePubKey(CardChannel channel, byte[] keyData) throws CardletException
    {       
    	try {
            byte[] apduBuf = null;
            apduBuf = ArrayUtils.addAll(apduBuf, IoTSAFEDefines.STORE_PUB_KEY_APDU_HDR); 
            // total length
            apduBuf = ArrayUtils.add(apduBuf, (byte)(keyData.length));
            // data
            apduBuf = ArrayUtils.addAll(apduBuf, keyData); 
            
            ResponseAPDU resp = executeCommand(channel, new CommandAPDU(apduBuf));
            if(resp.getSW() != STATUS_OK) 
            {
                throw new CardletException("APDU communication with NFC device failed");                            
            }
        }
        catch (CardletException e) {
            throw e;
        }
    }
    
    
    /**
     * Store secret key
     * 
     * @param channel Currently opened card channel
     * 
     * @return
     *  
     */
    public static void storeSecKey(CardChannel channel, byte[] keyData) throws CardletException
    {       
    	try {
            byte[] apduBuf = null;
            apduBuf = ArrayUtils.addAll(apduBuf, IoTSAFEDefines.STORE_SEC_KEY_APDU_HDR); 
            // total length
            apduBuf = ArrayUtils.add(apduBuf, (byte)(keyData.length));
            // data
            apduBuf = ArrayUtils.addAll(apduBuf, keyData); 
            
            ResponseAPDU resp = executeCommand(channel, new CommandAPDU(apduBuf));
            if(resp.getSW() != STATUS_OK) 
            {
                throw new CardletException("APDU communication with NFC device failed");                            
            }
        }
        catch (CardletException e) {
            throw e;
        }
    }
    
    /**
     * Store file
     * 
     * @param channel Currently opened card channel
     * 
     * @return
     *  
     */
    public static void storeFile(CardChannel channel, byte[] fileData) throws CardletException
    {       
    	try {
            byte[] apduBuf = null;
            apduBuf = ArrayUtils.addAll(apduBuf, IoTSAFEDefines.STORE_FILE_APDU_HDR); 
            // total length
            apduBuf = ArrayUtils.add(apduBuf, (byte)(fileData.length));
            // data
            apduBuf = ArrayUtils.addAll(apduBuf, fileData); 
            
            ResponseAPDU resp = executeCommand(channel, new CommandAPDU(apduBuf));
            if(resp.getSW() != STATUS_OK) 
            {
                throw new CardletException("APDU communication with NFC device failed");                            
            }
        }
        catch (CardletException e) {
            throw e;
        }
    }
    
    
    /**
     * Finish IoT SAFE applet initialization
     * 
     * @param channel Currently opened card channel
     * 
     * @return N/A
     *  
     */
    public static void closeInit(CardChannel channel) throws CardletException
    {       
        try {

            ResponseAPDU resp = executeCommand(channel, new CommandAPDU(IoTSAFEDefines.CLOSE_INIT_APDU_CMD));
            if(resp.getSW() != STATUS_OK) 
            {
                throw new CardletException("APDU communication with NFC device failed");                            
            }
            
            return;
        }
        catch (CardletException e) {
            throw e;
        }
    }
    
    /**
     * Get random
     * 
     * @param channel Currently opened card channel
     * 
     * @return Generated random bytes
     *  
     */
    public static byte[] getRandom(CardChannel channel, byte numberOfBytes) throws CardletException
    {       
    	try {
            byte[] apduBuf = null;
            apduBuf = ArrayUtils.addAll(apduBuf, IoTSAFEDefines.GET_RANDOM_APDU_HDR); 
            // length of expected random bytes
            apduBuf = ArrayUtils.add(apduBuf, numberOfBytes);
            
            ResponseAPDU resp = executeCommand(channel, new CommandAPDU(apduBuf));
            if(resp.getSW() != STATUS_OK) 
            {
                throw new CardletException("APDU communication with NFC device failed");                            
            }
            
            return resp.getData();
        }
        catch (CardletException e) {
            throw e;
        }
    }
    
    /**
     * Generate key pair
     * 
     * @param channel Currently opened card channel
     * 
     * @return Generated key pai data
     *  
     */
    public static byte[] genKeyPair(CardChannel channel, byte[] keyPairInput) throws CardletException
    {       
    	try {
            byte[] apduBuf = null;
            apduBuf = ArrayUtils.addAll(apduBuf, IoTSAFEDefines.GEN_KEY_PAIR_APDU_HDR); 
            // total length
            apduBuf = ArrayUtils.add(apduBuf, (byte)(keyPairInput.length));
            // data
            apduBuf = ArrayUtils.addAll(apduBuf, keyPairInput);
            
            ResponseAPDU resp = executeCommand(channel, new CommandAPDU(apduBuf));
            if(resp.getSW() != STATUS_OK) 
            {
                throw new CardletException("APDU communication with NFC device failed");                            
            }
            
            return resp.getData();
        }
        catch (CardletException e) {
            throw e;
        }
    }
    
    /**
     * Compute signature init - open session
     * 
     * @param channel Currently opened card channel
     * 
     * @return N/A
     *  
     */
    public static void computeSignInitOpenSession(CardChannel channel, byte[] initData) throws CardletException
    {       
        try {

        	byte[] apduBuf = null;
            apduBuf = ArrayUtils.addAll(apduBuf, IoTSAFEDefines.COMP_SIGN_INIT_OPEN_SESSION_APDU_HDR); 
            // total length
            apduBuf = ArrayUtils.add(apduBuf, (byte)(initData.length));
            // data
            apduBuf = ArrayUtils.addAll(apduBuf, initData);
            
            ResponseAPDU resp = executeCommand(channel, new CommandAPDU(apduBuf));
            if(resp.getSW() != STATUS_OK) 
            {
                throw new CardletException("APDU communication with NFC device failed");                            
            }
            
            return;
        }
        catch (CardletException e) {
            throw e;
        }
    }
    
    /**
     * Compute signature init - close session
     * 
     * @param channel Currently opened card channel
     * 
     * @return N/A
     *  
     */
    public static void computeSignInitCloseSession(CardChannel channel) throws CardletException
    {       
        try {

        	byte[] apduBuf = null;
            apduBuf = ArrayUtils.addAll(apduBuf, IoTSAFEDefines.COMP_SIGN_INIT_CLOSE_SESSION_APDU_CMD); 
            
            ResponseAPDU resp = executeCommand(channel, new CommandAPDU(apduBuf));
            if(resp.getSW() != STATUS_OK) 
            {
                throw new CardletException("APDU communication with NFC device failed");                            
            }
            
            return;
        }
        catch (CardletException e) {
            throw e;
        }
    }
    
    /**
     * Compute signature update - final call
     * 
     * @param channel Currently opened card channel
     * 
     * @return N/A
     *  
     */
    public static byte[] computeSignUpdateFinal(CardChannel channel, byte[] inputData) throws CardletException
    {       
        try {

        	byte[] apduBuf = null;
            apduBuf = ArrayUtils.addAll(apduBuf, IoTSAFEDefines.COMP_SIGN_UPDATE_FINAL_APDU_HDR); 
            // total length
            apduBuf = ArrayUtils.add(apduBuf, (byte)(inputData.length));
            // data
            apduBuf = ArrayUtils.addAll(apduBuf, inputData);
            
            ResponseAPDU resp = executeCommand(channel, new CommandAPDU(apduBuf));
            if(resp.getSW() != STATUS_OK) 
            {
                throw new CardletException("APDU communication with NFC device failed");                            
            }
            
            return resp.getData();
        }
        catch (CardletException e) {
            throw e;
        }
    }
    
    /**
     * Verify signature init - open session
     * 
     * @param channel Currently opened card channel
     * 
     * @return N/A
     *  
     */
    public static void verifySignInitOpenSession(CardChannel channel, byte[] initData) throws CardletException
    {       
        try {

        	byte[] apduBuf = null;
            apduBuf = ArrayUtils.addAll(apduBuf, IoTSAFEDefines.VER_SIGN_INIT_OPEN_SESSION_APDU_HDR); 
            // total length
            apduBuf = ArrayUtils.add(apduBuf, (byte)(initData.length));
            // data
            apduBuf = ArrayUtils.addAll(apduBuf, initData);
            
            ResponseAPDU resp = executeCommand(channel, new CommandAPDU(apduBuf));
            if(resp.getSW() != STATUS_OK) 
            {
                throw new CardletException("APDU communication with NFC device failed");                            
            }
        }
        catch (CardletException e) {
            throw e;
        }
    }
    
    /**
     * Verify signature init - close session
     * 
     * @param channel Currently opened card channel
     * 
     * @return N/A
     *  
     */
    public static void verifySignInitCloseSession(CardChannel channel) throws CardletException
    {       
        try {

        	byte[] apduBuf = null;
            apduBuf = ArrayUtils.addAll(apduBuf, IoTSAFEDefines.VER_SIGN_INIT_CLOSE_SESSION_APDU_CMD); 
            
            ResponseAPDU resp = executeCommand(channel, new CommandAPDU(apduBuf));
            if(resp.getSW() != STATUS_OK) 
            {
                throw new CardletException("APDU communication with NFC device failed");                            
            }
        }
        catch (CardletException e) {
            throw e;
        }
    }
    
    /**
     * Verify signature update - final call
     * 
     * @param channel Currently opened card channel
     * 
     * @return N/A
     *  
     */
    public static void verifySignUpdateFinal(CardChannel channel, byte[] inputData, byte[] signature) throws CardletException
    {       
        try {

        	byte[] apduBuf = null;
            apduBuf = ArrayUtils.addAll(apduBuf, IoTSAFEDefines.VER_SIGN_UPDATE_FINAL_APDU_HDR); 
            // total length
            apduBuf = ArrayUtils.add(apduBuf, (byte)(inputData.length + signature.length));
            // data
            apduBuf = ArrayUtils.addAll(apduBuf, inputData);
            // signature
            apduBuf = ArrayUtils.addAll(apduBuf, signature);
            
            ResponseAPDU resp = executeCommand(channel, new CommandAPDU(apduBuf));
            if(resp.getSW() != STATUS_OK) 
            {
                throw new CardletException("APDU communication with NFC device failed");                            
            }           
        }
        catch (CardletException e) {
            throw e;
        }
    }
    
    /**
     * Returns data application
     * 
     * @param channel Currently opened card channel
     * 
     * @return Response data as byte array
     *  
     */
    public static byte[] getDataApplication(CardChannel channel) throws CardletException
    {       
        try {
            // get IoT SAFE applet version
            ResponseAPDU resp = executeCommand(channel, new CommandAPDU(IoTSAFEDefines.GET_DATA_APP_APDU_CMD));
            if(resp.getSW() != STATUS_OK) 
            {
                throw new CardletException("APDU communication with NFC device failed");                            
            }
            
            return resp.getData();
        }
        catch (CardletException e) {
            throw e;
        }
    }
    
    /**
     * Returns data object list
     * 
     * @param channel Currently opened card channel
     * 
     * @return Response data as byte array
     *  
     */
    public static byte[] getDataObjectList(CardChannel channel) throws CardletException
    {       
        try {
            // get IoT SAFE applet version
            ResponseAPDU resp = executeCommand(channel, new CommandAPDU(IoTSAFEDefines.GET_DATA_OBJ_LIST_APDU_CMD));
            if(resp.getSW() != STATUS_OK) 
            {
                throw new CardletException("APDU communication with NFC device failed");                            
            }
            
            return resp.getData();
        }
        catch (CardletException e) {
            throw e;
        }
    }
}
