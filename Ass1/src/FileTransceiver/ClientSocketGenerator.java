package FileTransceiver;

import java.io.*;
import java.net.Socket;


public class ClientSocketGenerator extends GenericClientThread{

    private File myFileToSend;


    public ClientSocketGenerator(Socket servSock, File fileToSend){
        super(servSock);
        myFileToSend = fileToSend;
        this.setName("ClientLoopThread");
    }

    /*
    * fileToSend: duong dan DAY DU cua file muon send
    * */
    public static ClientSocketGenerator createSocket(Socket servSock,File fileToSend) {
        return new ClientSocketGenerator(servSock, fileToSend);
    }


    public void Connect() {
        this.start();
    }



    @Override
    protected void DoSomethingBeforeRun() {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SendProtocol(Protocol.SUCCESS_CODE,Protocol.SEND_FILE_COMMAND,String.valueOf(myFileToSend.length()));
    }

    @Override
    public void Handle200CE() {
        try {
            sendFile(myFileToSend);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void Handle400CE() {

    }

    @Override
    public void Handle200SF() {

    }

    @Override
    public void Handle400SF() {

    }
}
