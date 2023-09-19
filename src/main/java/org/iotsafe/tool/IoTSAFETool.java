package org.iotsafe.tool;

public class IoTSAFETool {

    public static void main(String[] args) {
        String argValue = "";

        if (args.length > 0) {
            argValue = args[0];
        }

        if (argValue.length() == 0)
            getHelpMessage();
        else if (argValue.matches("-?") || argValue.matches("-h") || argValue.matches("-help"))
            getHelpMessage();
        else if (argValue.matches("-gen"))
            System.out.println("generate key pair and store on card");
        else if (argValue.matches("-sign"))
            System.out.println("sign file with key on card");
        else if (argValue.matches("-getpub"))
            System.out.println("get key from card");
        else
            getHelpMessage();
    }

    private static void getHelpMessage() {
        System.out.println("IoT SAFE Tool, version 1.0");
        System.out.println("Usage: java -jar IoT_SAFE_Tool.jar [options]");
        System.out.println("where options include");
        System.out.println("  -gen");
        System.out.println("         generate key pair and store on card");
        System.out.println("  -sign");
        System.out.println("         sign file with key on card");
        System.out.println("  -getpub");
        System.out.println("         get key from card");
        System.out.println("  -? -h -help");
        System.out.println("         print this help message");
    }
}
