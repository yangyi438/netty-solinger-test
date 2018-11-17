
package solinger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Administrator
 * @version 1.0
 * @date 2014年2月16日
 */
public class SimpleNioServer implements Runnable {
    private Selector selector;
    private ServerSocketChannel servChannel;
    private SocketChannel channel;
    public SimpleNioServer(int port) {
        try {
            selector = Selector.open();
            servChannel = ServerSocketChannel.open();
            servChannel.configureBlocking(false);
            servChannel.socket().bind(new InetSocketAddress(port), 1024);
            servChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void run() {
        while (true) {
            try {
                try {
                    selector.select();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectedKeys.iterator();
                SelectionKey key = null;
                while (it.hasNext()) {
                    key = it.next();
                    it.remove();
                    try {
                        handleAcceptor(key);
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (key != null) {
                            key.cancel();
                            if (key.channel() != null)
                                key.channel().close();
                        }
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }
    private void handleAcceptor(SelectionKey key) throws IOException {
        System.out.println("acceptor now");
        if (key.isAcceptable()) {
            ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
            SocketChannel sc = ssc.accept();
            sc.configureBlocking(false);
            //sc.register(key.selector(), SelectionKey.OP_READ);
            ByteBuffer allocate = ByteBuffer.allocate(1024);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (BioClient.class) {
                beginRead(key, sc);
            }
            synchronized (BioClient.class) {
                beginRead(key, sc);
            }
            try {
                allocate.put(new byte[500]);
                allocate.flip();

                int write;
                synchronized (BioClient.class) {
                    System.out.println("server Write now");
                    write = sc.write(allocate);
                }
                System.out.println("server write" + write);
            } catch (Exception e) {
                System.out.println("write exception try read now");
                synchronized (BioClient.class) {
                    beginRead(key, sc);
                }
            }
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private void beginRead(SelectionKey key, SocketChannel sc) throws IOException {
        ByteBuffer readBuffer = ByteBuffer.allocate(1024);
        int readBytes = 0;
        try {
            readBytes = sc.read(readBuffer);
        } catch (IOException e) {
             System.out.println("read exception");
            return;
        }
        System.out.println("read successful " + readBytes);
        if (readBytes > 0) {
            readBuffer.flip();
            byte[] bytes = new byte[readBuffer.remaining()];
            readBuffer.get(bytes);
        } else if (readBytes < 0) {
            key.cancel();
            sc.close();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        new Thread(new SimpleNioServer(8080)).start();
        Thread.sleep(100);
        BioClient.startBioClient();
    }

}
