package FileTransceiver;

import java.io.*;
import java.net.Socket;

public abstract class GenericClientThread extends Thread implements Protocol.ClientToServer, Protocol.ServerToClient  {
    private Socket clientSocket;
    protected BufferedWriter bf;
    protected BufferedReader br;
    protected String data;
    private Boolean doExitThread = false;
    public GenericClientThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            this. bf = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    protected void exitThread() {
        this.doExitThread = true;
    }
    @Override
    public void run() {
        DoSomethingBeforeRun();
        while(!this.doExitThread){
            SwitchBasedOnProtocol();
        }
    }

    protected abstract void DoSomethingBeforeRun();


    protected void SendProtocol(String code,String command,String data){
        try {
            bf.write(Util.ProtocolGenerator(code,command,data));
            bf.append("\n");
            bf.flush();
            System.out.println("sending " + code + command + data);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    protected String ReadProtocol() {
        String incomingMessage= null;

        try {
            incomingMessage = br.readLine();
//            System.out.println("INSIDE OF READ 2 " + incomingMessage);

        } catch (IOException e) {
            e.printStackTrace();
        }
        if(incomingMessage != null) {
//            System.out.println(incomingMessage);
        }
//        System.out.println("INSIDE READ " + incomingMessage);
        return incomingMessage;
    }

    protected void saveFile(int fileSize,String outputDir) throws IOException {

        DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
        FileOutputStream fos = new FileOutputStream(outputDir);
        byte[] buffer = new byte[4096];

        // Send file size in separate msg
        int read = 0;
        int totalRead = 0;
        int remaining = fileSize;
        System.out.println(fileSize);
        while((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
            totalRead += read;
            remaining -= read;
            System.out.println("read " + totalRead + " bytes.");
            fos.write(buffer, 0, read);
        }
//        fos.close();
//        dis.close();
    }

    protected void sendFile(File fileObject) throws IOException {
        DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
        FileInputStream fis = new FileInputStream(fileObject);
        byte[] buffer = new byte[4096];

        while (fis.read(buffer) > 0) {
            dos.write(buffer);
        }

//        fis.close();
//        dos.close();
    }

    private void SwitchBasedOnProtocol(){
        String incomingProtocol = ReadProtocol();
        String[] codeAndCommand = new String[3];
        Util.ParseProtocol(incomingProtocol,codeAndCommand);
        data = codeAndCommand[2];
//        System.out.println(codeAndCommand[1]);
        switch(codeAndCommand[0]){
            case Protocol.SUCCESS_CODE:
                switch(codeAndCommand[1]) {

                    case Protocol.SEND_FILE_COMMAND:
//                        System.out.println("200SF");
                        Handle200SF();
                        break;
                    case Protocol.SEND_KEY_COMMAND:
                        System.out.println("200SK");
                        Handle200SK();
                        break;
                    default:
                        return;
                }
                break;
            case Protocol.OPTION_CODE:
                switch(codeAndCommand[1]) {
                    case Protocol.SEND_FILE_COMMAND:
//                        System.out.println(codeAndCommand.toString());
                        Handle204SF();
                        break;
                    case Protocol.SEND_KEY_COMMAND:
//                        System.out.println("400SK");
                        Handle204SK();
                        break;
                    default:
                        return;
                }
                break;
            case Protocol.FAIL_CODE:
                switch(codeAndCommand[1]) {
//                    case Protocol.CONNECTION_ESTABLISED_COMMAND:
////                        System.out.println(codeAndCommand.toString());
//                        Handle400CE();
//                        break;
                    case Protocol.SEND_FILE_COMMAND:
//                        System.out.println(codeAndCommand.toString());
                        Handle400SF();
                        break;
                    case Protocol.SEND_KEY_COMMAND:
//                        System.out.println("400SK");
                        Handle400SK();
                        break;
                    default:
                        return;
                }
                break;
            case Protocol.READY_TO_RECEIVE_DATA:
                switch(codeAndCommand[1]) {
                    case Protocol.SEND_FILE_COMMAND:
//                        System.out.println(codeAndCommand.toString());
                        Handle208SF();
                        break;
                    case Protocol.SEND_KEY_COMMAND:
//                        System.out.println("400SK");
                        Handle208SK();
                        break;
                    default:
                        return;
                }
                break;
            default:
                return;
        }
    }
}
