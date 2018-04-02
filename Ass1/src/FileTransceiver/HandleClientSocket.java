package FileTransceiver;

import java.io.IOException;
import java.net.Socket;

public class HandleClientSocket extends GenericClientThread {

    private String myPathToSave;

    public HandleClientSocket(Socket clientSocket,String pathToSave) {
        super(clientSocket);
        this.myPathToSave = pathToSave;
        this.setName("HandleClientThread");
        System.out.println("Handle is here");
    }


    @Override
    protected void DoSomethingBeforeRun() {

    }

    @Override
    public void Handle200SF() {
        System.out.println("READY");
        SendProtocol(Protocol.SUCCESS_CODE,Protocol.CONNECTION_ESTABLISED_COMMAND);
        try {
            saveFile(Integer.parseInt(data),myPathToSave);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void Handle400SF() {

    }

    @Override
    public void Handle200CE() {

    }

    @Override
    public void Handle400CE() {

    }
}

