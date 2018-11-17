package solinger;

import io.netty.util.internal.SocketUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by ${good-yy} on 2018/11/17.
 */
public class BioClient {

    public static void startBioClient() throws IOException, InterruptedException {
        Socket socket = new Socket();
        //set soLinger
        socket.setSoLinger(true, 1000);
        socket.connect(SocketUtils.socketAddress("localhost", 8080));
        OutputStream outputStream = socket.getOutputStream();
        System.out.println("client beging write");
        outputStream.write(new byte[1024]);
        outputStream.flush();
        System.out.println("client begning close");
        try {
            synchronized (BioClient.class){
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("client closed");
        for (int i = 0; i < 100; i++) {
            Thread.sleep(100);
            System.gc();
        }
        Thread.sleep(100000);
    }
}
