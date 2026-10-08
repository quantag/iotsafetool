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
 * Hexadecimal string and byte array conversion helpers.
 */
public class Tools {

    /**
     * Converts string to array of bytes
     *
     * @param s
     *         string as "0f1234...".
     *
     * @return converted byte array
     */
    public static byte[] hexStringToBytes(String s) {
        byte[] result = new byte[s.length() >> 1];
        for (int i = 0; i < result.length; i++) {
            result[i] = (byte) Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16);
        }
        return result;
    }

    /**
     * Convert byte array to human-readable form
     *
     * @param data
     *
     * @return readable string
     */
    public static String bytesToHexString(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            String bs = Integer.toHexString(data[i] & 0xFF).toUpperCase();
            if (bs.length() == 1) {
                sb.append(0);
            }
            sb.append(bs);
            //sb.append(" ");
        }
        return sb.toString();
    }
}
