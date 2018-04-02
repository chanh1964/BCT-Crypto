package FileTransceiver;



import java.io.*;
import java.net.Socket;

public class ClientSocketGenerator extends GenericClientThread{

    private File myFileToSend;
    private String myDirToSavePublicKey;

    private ClientSocketGenerator(Socket servSock, File fileToSend, String dirToSavePublicKey){
        super(servSock);
        myFileToSend = fileToSend;
        myDirToSavePublicKey = dirToSavePublicKey;
        this.setName("ClientLoopThread");
    }

    /*
     * fileToSend: duong dan DAY DU cua file muon send
     * */
    public static ClientSocketGenerator createSocket(Socket servSock,File fileToSend,String dirToSavePublicKey) {
        return new ClientSocketGenerator(servSock, fileToSend, dirToSavePublicKey);
    }


    public void Connect() {
        this.start();
    }



    @Override
    protected void DoSomethingBeforeRun() {
//        try {
//            Thread.sleep(4000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        SendProtocol(Protocol.SUCCESS_CODE,Protocol.SEND_FILE_COMMAND,String.valueOf(myFileToSend.length()));
    }

//    @Override
//    public void Handle200CE() {
//        System.out.println(this.data);
//        //Da co duoc key (lay key tu this.data)
//
//        try {
//            sendFile(myFileToSend);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
////        Thread.yield();
//        this.exitThread();
//    }

    @Override
    public void Handle200SK() {

        System.out.println("Done receiving key");
        SendProtocol(Protocol.OPTION_CODE,Protocol.SEND_FILE_COMMAND,String.valueOf(myFileToSend.length()));

    }

    @Override
    public void Handle400SK() {

    }

    @Override
    public void Handle204SK() {
        SendProtocol(Protocol.READY_TO_RECEIVE_DATA,Protocol.SEND_KEY_COMMAND,Protocol.NONE_DATA);

        try {
            saveFile(Integer.parseInt(data),myDirToSavePublicKey);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Client has received the public key of the server
        //TODO
        // - Add code to generate KEY file for RSA or whatever...
        // - Add code to encrypt KEY with the received public key
        /*
         * this.myDirToSavePublicKey: Duong dan DAY DU den public key vua duoc luu lai
         */
        SendProtocol(Protocol.SUCCESS_CODE,Protocol.SEND_KEY_COMMAND,Protocol.NONE_DATA);
        SendProtocol(Protocol.OPTION_CODE,Protocol.SEND_FILE_COMMAND, String.valueOf(myFileToSend.length()));
    }

    @Override
    public void Handle208SF() {
        try {
            sendFile(myFileToSend);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void Handle208SK() {

    }

    @Override
    public void Handle204SF() {

    }

    @Override
    public void Handle200SF() {
        System.out.println("Server has received file");
        exitThread();
    }

    @Override
    public void Handle400SF() {

    }
}
