package org.iotsafe.tool;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class IoTSAFEToolCL {

    public static void main(String[] args) {
        //OMNIKEY Smart Card Reader USB 0

        String terminalIndex = "0";
        String[] argsList = args;
        for(int i=0; i<args.length; i++) {
            if (argsList[i].matches("-reader")) {
                if (args.length > i+1) {
                    terminalIndex = argsList[i+1];
                    break;
                }
            }
        }

        IoTSAFETool ioTSAFETool = new IoTSAFETool(terminalIndex);

        for(int i=0; i<args.length; i++) {
            if (argsList[i].matches("-?") || argsList[i].matches("-h") || argsList[i].matches("-help")) {
                ioTSAFETool.getHelpMessage();
                break;
            }
            else if (argsList[i].matches("-list")) {
                ioTSAFETool.getCardReadersList();
                break;
            }
            else if (argsList[i].matches("-genkeys")) {
                ioTSAFETool.runGenKeys();
                break;
            }
            else if (argsList[i].matches("-getpub")) {
                ioTSAFETool.runGetPublicKey();
                break;
            }
            else if (argsList[i].matches("-hmac")) {
                //-hmac TestCase1.txt
//?                if (args.length > i+1) {
//?                    System.out.println("ERROR: no filename in command -hmac");
//?                    ioTSAFETool.getHelpMessage();
//?                }
                String fileName = argsList[i+1];
                byte[] hash = null;

                try {
                    byte[] buffer = new byte[8192];
                    int count;
                    MessageDigest digest = MessageDigest.getInstance("SHA-256");
                    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fileName));
                    while ((count = bis.read(buffer)) > 0) {
                        digest.update(buffer, 0, count);
                    }
                    bis.close();
                    hash = digest.digest();
                } catch (NoSuchAlgorithmException nsae) {
                    System.out.println("ERROR - NoSuchAlgorithmException: " + nsae);
                } catch (FileNotFoundException fnfe) {
                    System.out.println("ERROR - FileNotFoundException: " + fnfe);
                } catch (IOException ioe) {
                    System.out.println("ERROR - IOException: " + ioe);
                }

                ioTSAFETool.runHmac(hash);
                break;
            }
            else if (argsList[i].matches("-version")) {
                ioTSAFETool.getAppletVersion();
                break;
            }
        }
    }

}

/*
2.11 Generate Key Pair, page 24

--- start GenKeys()
Used card reader: OMNIKEY Smart Card Reader USB 0
NFC device found and connected
APDU-C: 00A404000FA00000003053F1240177010149534100
APDU-R: 9000
IoT SAFE applet selected
APDU-C: 80B900000484020002
APDU-R: 8402000285020002344549438641044AA6E663216362AFC2D3C8DA239C853C2E45BF429B480E6B5A6944189B212C3880E35C3223E8832EF5B56F412373920CF6A6D7C3918B1A3207792543B23BFB029000
Generated Public Key: 8402000285020002344549438641044AA6E663216362AFC2D3C8DA239C853C2E45BF429B480E6B5A6944189B212C3880E35C3223E8832EF5B56F412373920CF6A6D7C3918B1A3207792543B23BFB02
--- stop GenKeys()

Public key data: 344549438641044AA6E663216362AFC2D3C8DA239C853C2E45BF429B480E6B5A6944189B212C3880E35C3223E8832EF5B56F412373920CF6A6D7C3918B1A3207792543B23BFB02

 KEY_PAIR_0002 = Tools.hexStringToBytes("84020002");

 Used card reader: OMNIKEY Smart Card Reader USB 0
NFC device found and connected
APDU-C: 00A404000FA00000003053F1240177010149534100
APDU-R: 9000
IoT SAFE applet selected
APDU-C: 80CBC2000485020002
APDU-R: 75023131850200026001024A01014B01134E0103610107920204910200076F01019000
Generated public key: 75023131850200026001024A01014B01134E0103610107920204910200076F0101
75023131 85020002 600102 4A0101 4B0113 4E0103 610107 920204 91020007 6F0101
 */
/*
2.12 Get data – application
APDU-C: 80CB000044
APDU-R: 10011011201122334455667788112233445566778811223344556677881122334455667788B1010AB2010AB3010AB4010A61010F910200019201046F0101940102B701019000
Data application: 10011011201122334455667788112233445566778811223344556677881122334455667788B1010AB2010AB3010AB4010A61010F910200019201046F0101940102B70101
100110 - SIM Alliance version
11201122334455667788112233445566778811223344556677881122334455667788 - Applet proprietary identifier
B1010A - Max number of files
B2010A - Max number of private keys
B3010A - Max number public keys
B4010A61010F - Max number secret keys
91020001 - Supported algorithms for hash
9201046F0101 - Supported algorithms for signature
940102 - Supported algorithms for key derivation
B70101 - Maximum number of sessions
 */

/*
2.14 Get data – object list
APDU-C: 80CB0100
APDU-R: 74023131840200026001024A01014B01134E0103610107920204910200076F010175023131850200026001024A01014B01134E0103610107920204910200076F010175023232850200036001024A01004B01134E0103610107920204910200076F010173023031830200016001014A01012101012002004076023333860200046001034A01004B01A061010C9401029000
Data object list: 74023131840200026001024A01014B01134E0103610107920204910200076F010175023131850200026001024A01014B01134E0103610107920204910200076F010175023232850200036001024A01004B01134E0103610107920204910200076F010173023031830200016001014A01012101012002004076023333860200046001034A01004B01A061010C940102
2.14.4.2 Private key information structure
74023131 - Private key label
84020002 - Private key identifier
600102 - Object access conditions (for the private key)
4A0101 - Object state (for the private key)
4B0113 - Key type
4E0103 - Key specific usage
610107 - Cryptographic functions
920204 - Supported algorithms for signature (generation)
91020007 - Supported algorithms for hash
6F0101 - Supported algorithms for key agreement

2.14.4.3 Public key information structure
75023131
85020002
600102
4A0101
4B0113
4E0103
610107
920204
91020007
6F0101

75023232
85020003
600102
4A0100
4B0113
4E0103
610107
920204
91020007
6F0101
2.14.4.4 File information structure
73023031 - File label
83020001 - File ID
600101 - Object (file) access conditions
4A0101 - Object state (for the file)
210101 - File specific usage
20020040 - File size

76023333860200046001034A01004B01A061010C940102

 */