package org.iotsafe.utils;

/**
 * Class with static helper methods
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
