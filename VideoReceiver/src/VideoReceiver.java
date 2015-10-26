import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class VideoReceiver {
	public static final int SERVERPORT = 8901;

	public static void main(String[] args) {
	    try {
	        System.out.println("S: Connecting...");

	        ServerSocket serverSocket = new ServerSocket(SERVERPORT);
	        System.out.println("S: Socket Established...");

	        Socket client = serverSocket.accept();
	        System.out.println("S: Receiving...");

	        ObjectInputStream put = new ObjectInputStream(
	                client.getInputStream());

	        String s = "/Users/alyshehata/Documents/test.mp4";
	        String path =  s;
	        System.out.println("The requested file is : " + s);
	        File f = new File(path);
	        if (!f.exists()){
	        	f.createNewFile();
	        }
	        	FileOutputStream fis = new FileOutputStream(f);

	            byte[] buf = new byte[1024];
	            int read;
	            while ((read = put.read(buf, 0, 1024)) != -1) {
	                fis.write(buf, 0, read);
	                fis.flush();
	            }

	            System.out.println("File transfered");
	            client.close();
	            serverSocket.close();
	            fis.close();

	    } catch (Exception e) {
	        System.out.println("S: Error");
	        e.printStackTrace();
	    }

	}

}
