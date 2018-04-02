package FileTransceiver;

import java.io.IOException;
import java.net.Socket;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class HandleClientSocket extends GenericClientThread {

    private String myPathToSave;
    private File myPublicKey;
    public HandleClientSocket(Socket clientSocket,String pathToSave, File publicKey) {
        super(clientSocket);
        this.myPathToSave = pathToSave;
        this.myPublicKey = publicKey;
        this.setName("HandleClientThread");
        System.out.println("Handle is here");
    }


    @Override
    protected void DoSomethingBeforeRun() {
        SendProtocol(Protocol.OPTION_CODE,Protocol.SEND_KEY_COMMAND,String.valueOf(myPublicKey.length()));
    }

    @Override
    public void Handle208SK() {
        try {
            sendFile(myPublicKey);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SendProtocol(Protocol.SUCCESS_CODE,Protocol.SEND_KEY_COMMAND,Protocol.NONE_DATA);
    }

    @Override
    public void Handle204SF() {
        SendProtocol(Protocol.READY_TO_RECEIVE_DATA,Protocol.SEND_FILE_COMMAND,Protocol.NONE_DATA);
        try {
            saveFile(Integer.parseInt(data),myPathToSave);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SendProtocol(Protocol.SUCCESS_CODE,Protocol.SEND_FILE_COMMAND,Protocol.NONE_DATA);
        //Server has received the file
        //TODO
        //- Add code to process file after receiving
        /*
         * this.pathToSave: Duong dan DAY DU den File vua duoc luu lai
         */
        this.exitThread();
    }

    @Override
    public void Handle200SF() {
    }

    @Override
    public void Handle400SF() {

    }


    @Override
    public void Handle200SK() {
        System.out.println("Client received Key");
    }

    @Override
    public void Handle400SK() {

    }

    @Override
    public void Handle204SK() {

    }

    @Override
    public void Handle208SF() {

    }
}



