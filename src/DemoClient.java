import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class DemoClient {

    private static Socket socket;

    public DemoClient(int port, String host) throws IOException {
        socket = new Socket(host, port);
        System.out.println("Connected");
    }

    public static void messageSender() throws IOException {
        while (true) {
            System.out.print("Enter you message (Client) - ");
            byte[] buff = new byte[1024];
            int read = System.in.read(buff, 0, buff.length);
            socket.getOutputStream().write(buff, 0, read);
        }
    }

    public static void messageReceiver() throws IOException {
        InputStream inputStream = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        while (true) {
            String message = reader.readLine();
            System.out.println("Message from server - " + message);
        }
    }

    public static void main(String[] args) throws IOException {
        new DemoClient(8081, "127.0.0.2");

        Thread receiver = new Thread(() ->  {
            try {
                messageReceiver();
            } catch (IOException e) {
                System.out.println(" Exception - " + e);
            }});
        receiver.start();

        Thread sender = new Thread(() ->  {
            try {
                messageSender();
            } catch (IOException e) {
                System.out.println(" Exception - " + e);
            }});
        sender.start();
    }
}
