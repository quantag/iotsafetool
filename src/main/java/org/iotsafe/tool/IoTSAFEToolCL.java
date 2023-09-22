package org.iotsafe.tool;

public class IoTSAFEToolCL {

    public static void main(String[] args) {
        IoTSAFETool ioTSAFETool = new IoTSAFETool("OMNIKEY Smart Card Reader USB 0");
        String argValue = "";

        if (args.length > 0) {
            argValue = args[0];
        }

        if (argValue.length() == 0)
            ioTSAFETool.getHelpMessage();
        else if (argValue.matches("-?") || argValue.matches("-h") || argValue.matches("-help"))
            ioTSAFETool.getHelpMessage();
        else if (argValue.matches("-genkeys"))
            ioTSAFETool.runGenKeys();
        else if (argValue.matches("-getpub"))
            ioTSAFETool.runGetPublicKey();
        else if (argValue.matches("-version"))
            ioTSAFETool.getAppletVersion();
        else
            ioTSAFETool.getHelpMessage();
    }

}
