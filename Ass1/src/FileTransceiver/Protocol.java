package FileTransceiver;

public interface Protocol {
    static final String SUCCESS_CODE = "200";
    static final String FAIL_CODE = "400";
    static final String SEND_FILE_COMMAND = "SF";
    static final String CONNECTION_ESTABLISED_COMMAND = "CE";
    static final String NONE_DATA = "NONE";
    static final String PROTOCOL_SEPARATOR = "__";

    public interface ClientToServer {
        void Handle200CE();
        void Handle400CE();

    }
    public interface ServerToClient{
        void Handle200SF();
        void Handle400SF();
    }
}

