/*
 * Copyright 2023-2026 Quantag IT Solutions GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.iotsafe.applet;

import java.security.Security;

import javax.smartcardio.CardChannel;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.iotsafe.utils.IoTSAFETools;
import org.iotsafe.utils.Tools;

/**
 * Standalone example: connects to a card, selects the applet and runs the
 * initialisation sequence.
 */
public class InitializeApp {

    public final static byte[] FILE_0001 = Tools.hexStringToBytes("830200017302303160010121010120020040");
    public final static byte[] PRIV_KEY_0002 = Tools.hexStringToBytes("84020002740231316001024B01134E0103610107920104910200076F0101");
    public final static byte[] PUB_KEY_0002 = Tools.hexStringToBytes("85020002750231316001024B01134E0103610107920104910200076F0101");

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
APDU-C: 805A000012830200017302303160010121010120020040
APDU-R: 9000
APDU-C: 805200001E84020002740231316001024B01134E0103610107920104910200076F0101
APDU-R: 9000
APDU-C: 805400001E85020002750231316001024B01134E0103610107920104910200076F0101
APDU-R: 9000
Test objects successfully stored
APDU-C: 80720000
APDU-R: 9000
IoT SAFE applet initialization closed
End IoT SAFE applet test
------------------------
 */