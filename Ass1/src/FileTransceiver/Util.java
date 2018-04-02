public class Util {
    /*
    Generate protocol based on code + command
    Protocol form: [code]__[command]
    ex: 200__SF
    */
    static String ProtocolGenerator(String code, String command ){
        return code + Protocol.PROTOCOL_SEPARATOR + command + Protocol.PROTOCOL_SEPARATOR + Protocol.NONE_DATA;
    }

    static String ProtocolGenerator(String code, String command, String data) {
        return code + Protocol.PROTOCOL_SEPARATOR + command + Protocol.PROTOCOL_SEPARATOR + data;
    }
    static void ParseProtocol(String incomingProtocol, String[] result){
//        System.out.println("FULL: " + incomingProtocol);
        String[] temp = incomingProtocol.split("[__]");
        result[0] = temp[0];
        result[1] = temp[2];
        result[2] = temp[4];
    }
}
