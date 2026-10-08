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

package org.iotsafe.utils;

/**
 * APDU headers and application identifiers for the GSMA IoT SAFE applet
 * interface.
 */
public class IoTSAFEDefines {
	
	public final static byte[] SELECT_APDU_HEADER = Tools.hexStringToBytes("00A40400");
	public final static byte[] IOTSAFE_APPLET_AID = Tools.hexStringToBytes("A00000003053F12401770101495341");

	/** Command APDU for selecting the issuer security domain */
    static final byte[] SELECT_ISD_APDU = Tools.hexStringToBytes("00A4040008A00000015100000000");  
    
    /** IoT SAFE applet APDUs */ 

    public static final byte[] GET_VERSION_APDU_CMD = Tools.hexStringToBytes("80700000");
    public static final byte[] CLOSE_INIT_APDU_CMD = Tools.hexStringToBytes("80720000");
    
    public static final byte[] STORE_PRIV_KEY_APDU_HDR = Tools.hexStringToBytes("80520000");
    public static final byte[] STORE_PUB_KEY_APDU_HDR = Tools.hexStringToBytes("80540000");
    public static final byte[] STORE_SEC_KEY_APDU_HDR = Tools.hexStringToBytes("80560000");
    public static final byte[] STORE_FILE_APDU_HDR = Tools.hexStringToBytes("805A0000");
    
    public static final byte[] GET_RANDOM_APDU_HDR = Tools.hexStringToBytes("80840000");
    public static final byte[] GEN_KEY_PAIR_APDU_HDR = Tools.hexStringToBytes("80B90000");

    //public static final byte[] GET_PUBLIC_KEY_APDU_CMD = Tools.hexStringToBytes("80CBC200"); //?
    public static final byte[] GET_PUBLIC_KEY_APDU_CMD = Tools.hexStringToBytes("80CD0000"); //?
    
    public static final byte[] GET_DATA_APP_APDU_CMD = Tools.hexStringToBytes("80CB000044");
    public static final byte[] GET_DATA_OBJ_LIST_APDU_CMD = Tools.hexStringToBytes("80CB0100");

    public static final byte[] GET_DATA_FILE_APDU_CMD = Tools.hexStringToBytes("80CBC300"); //?
    
    public static final byte[] COMP_SIGN_INIT_OPEN_SESSION_APDU_HDR = Tools.hexStringToBytes("802A0001");
    public static final byte[] COMP_SIGN_INIT_CLOSE_SESSION_APDU_CMD = Tools.hexStringToBytes("802A0101");
    public static final byte[] COMP_SIGN_UPDATE_FINAL_APDU_HDR = Tools.hexStringToBytes("802B8001");
    
    public static final byte[] VER_SIGN_INIT_OPEN_SESSION_APDU_HDR = Tools.hexStringToBytes("802C0001");
    public static final byte[] VER_SIGN_INIT_CLOSE_SESSION_APDU_CMD = Tools.hexStringToBytes("802C0101");
    public static final byte[] VER_SIGN_UPDATE_FINAL_APDU_HDR = Tools.hexStringToBytes("802D8001");
}
