package FileTransceiver;
import java.io.File;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Main {
    public final static int DEFAULT_SERVER_SOCKET_PORT = 13267;  // you may change this
    private static InetAddress ip;
    private final static String DEFAULT_DIR_TO_KEY = "/home/minhtriet/bk/mmanm/src/MyPublicKey/KEYFILE.txt";
    private final static String DEFAULT_DIR_TO_SAVE_FILE = "/home/minhtriet/bk/mmanm/src/RecievedFile/";
    private final static File DEFAULT_FILE_TO_SEND1 = new File ("/home/minhtriet/bk/mmanm/src/FileToSend1/sending1.txt");
    private final static File DEFAULT_FILE_TO_SEND2 = new File ("/home/minhtriet/bk/mmanm/src/FileToSend2/sending2.txt");
    private final static String DEFAULT_DIR_TO_SAVE_PUBLIC_KEY = "/home/minhtriet/bk/mmanm/src/PublicKey/";

    static {
        try {
            ip = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        //Server phai duoc start khi chuong trinh chay. Server se chay tren 1 thread rieng.
        //Nhiem vu duy nhat cua thread nay la bat cac truy cap tu nhieu client den cung luc.


        //When program starts, start server thread
        //USERINPUT
        /*
        * SERVER_PORT: port cua server
        * DIR_TO_SAVE_FILE: duong dan DAY DU cua noi luu file ser nhan
        * DIR_TO_KEY: duong day DAY DU cua public key cua server nay
        * DIR_TO_SAVE_PUBLIC_KEY: duong dan DAY DU cua noi luu cac public key tu cac server khac
        */
        ServerSocketGenerator serverThead = ServerSocketGenerator.createSocket(DEFAULT_SERVER_SOCKET_PORT, DEFAULT_DIR_TO_SAVE_FILE,new File(DEFAULT_DIR_TO_KEY),DEFAULT_DIR_TO_SAVE_PUBLIC_KEY);
        serverThead.Start();

        //When user click send file, create socket to connect to server
        //USERINPUT
        /*
         * HOST_ADDRESS: host address cua server se nhan file
         * SERVER_SOCKET_PORT: port cua server se nhan file
         */
        Socket servSock1 = null;
        Socket servSock2 = null;

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        try {
        servSock1 = new Socket(InetAddress.getLocalHost().getHostAddress(),DEFAULT_SERVER_SOCKET_PORT);
        servSock2 = new Socket(InetAddress.getLocalHost().getHostAddress(),DEFAULT_SERVER_SOCKET_PORT);

        } catch (Exception e) {
            e.printStackTrace();
        }

        //Start client thread to send file
        //USERINPUT
        /*
         * DIR_TO_SAVE_FILE: duong dan DAY DU cua noi luu file ser nhan
         * DIR_TO_KEY: duong day DAY DU cua public key cua server nay
         * DIR_TO_SAVE_PUBLIC_KEY: duong dan DAY DU cua noi luu cac public key tu cac server khac
         */
        //CODEINPUT
        /*
        * servSock: socket de ket noi voi server se nhan file da duoc tao o tren
        */
        String publicKey1Name = "Key1";
        String publicKey2Name = "Key2";
        ClientSocketGenerator clientThread1 = ClientSocketGenerator.createSocket(servSock1, DEFAULT_FILE_TO_SEND1,DEFAULT_DIR_TO_SAVE_PUBLIC_KEY+publicKey1Name);
        clientThread1.Connect();
        System.out.println("Ready to start 2");
        //Start another client thread to send file
        ClientSocketGenerator clientThread2 = ClientSocketGenerator.createSocket(servSock2, DEFAULT_FILE_TO_SEND2,DEFAULT_DIR_TO_SAVE_PUBLIC_KEY+publicKey2Name);
        clientThread2.Connect();


    }
}
