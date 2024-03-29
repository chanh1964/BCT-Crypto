package FileTransceiver;

import javax.imageio.IIOException;
import java.io.File;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ServerSocketGenerator extends Thread{

    public final static int DEFAULT_SOCKET_PORT = 13267;  // you may change this
    public final static String DEFAULT_SAVE_PATH = "";
    private static String myDirToSavePublicKey = null;
    private static File myPublicKey = null;
    private static long keySize = 0;
    private static int fileCounter = 0;

    private ServerSocket servSock = null;
    private int mySocketPortNumber = DEFAULT_SOCKET_PORT;
    private String mySavePath;
    private static InetAddress ip;

    static {
        try {
            ip = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public static void UpdateFileCounter(){
        ServerSocketGenerator.fileCounter++;
    }

    private ServerSocketGenerator(int socketPortNumber, String savePath, File myPublicKey, String dirToSavePublicKey){
        mySocketPortNumber = socketPortNumber;
        mySavePath = savePath;
        ServerSocketGenerator.myPublicKey = myPublicKey;
        ServerSocketGenerator.keySize = myPublicKey.length();
        myDirToSavePublicKey = dirToSavePublicKey;
        this.setName("ServerLoopThread");
    }

    public static ServerSocketGenerator createSocket(int socketPortNumber, String savePath,File key,String dirToSavePublicKey) {
        return new ServerSocketGenerator(socketPortNumber,savePath,key,dirToSavePublicKey);
    }

    public static ServerSocketGenerator createSocket(File key, String dirToSavePublicKey) {
        return new ServerSocketGenerator(DEFAULT_SOCKET_PORT,DEFAULT_SAVE_PATH,key, dirToSavePublicKey);
    }

    public static void updatePublicKey(File publicKey) {
        ServerSocketGenerator.myPublicKey = publicKey;
        ServerSocketGenerator.keySize = publicKey.length();
    }
    public static File getPublicKey() {
        return ServerSocketGenerator.myPublicKey;
    }
    public static String getDirToSavePublicKey(){
        return ServerSocketGenerator.myDirToSavePublicKey;
    }
    public void Start() {
        this.start();
    }

    public int getMySocketPortNumber() {
        return mySocketPortNumber;
    }
    public void Disconnect(){
        this.interrupt();
    }
    @Override
    public void run() {
        try {
            servSock = new ServerSocket(this.mySocketPortNumber);
            System.out.println("Socket created succesfully at port " + this.mySocketPortNumber + " of " + ip.getHostAddress());
            while (true ){
                try {
                    Socket clientSocket = servSock.accept();
                    System.out.println("Got connection!");
//                    BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
//                    bf.append("You are connected!").append("\n");
//                    bf.flush();
                    HandleClientSocket handleClientSocket =  new HandleClientSocket(clientSocket, mySavePath+ String.valueOf(fileCounter++),ServerSocketGenerator.myPublicKey);
                    handleClientSocket.start();
                } catch (IIOException e) {
                    e.printStackTrace();
                }
            }
//            System.out.println("Server Shutting down");
//            Thread.yield();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    //DEV FUNC

    public ServerSocket getServSock() {
        return servSock;
    }

}
