import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class DemoServer {
    private static ServerSocket serverSocket;
    private static Socket clientSocket;
    private static HashMap<Integer, Socket> map = new HashMap<>();
    private static int i = 0;
   // private static String messages;

    public DemoServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Waiting for new connections");
        clientSocket = serverSocket.accept();
        map.put(i++, clientSocket);
        System.out.println("Was accepted new client");
    }

    public static void connector() throws IOException {
        while (true) {

            System.out.println("Waiting for new connections");
            clientSocket = serverSocket.accept();
            System.out.println("Was accepted new client");

            map.put(i++, clientSocket);

            Thread receiver = new Thread(() ->  {
                try {
                    messageReceiver();
                } catch (IOException e) {
                    System.out.println(" Exception - " + e);
                }});
            receiver.start();
        }
    }

    public static void messageReceiver() throws IOException {
        InputStream inputStream = clientSocket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        while (true) {
            String message = reader.readLine();
            copyRoger(message.getBytes("UTF-8"));
            //messages = message;
            System.out.println("Message from client - " + message);
        }
    }

    public static void copyRoger(byte[] sms) throws IOException {
        for (int j = 0; j < map.size(); j++) {
            if (map.get(j) != null) {
                OutputStream tmp = map.get(j).getOutputStream();
                tmp.write(sms, 0, sms.length);
                tmp.write(' ');
                tmp.flush();
            }
        }
    }


    public static void messageSender() throws IOException {
       /* while (true) {
            if (messages != null) {
                for (int j = 0; j <= map.size(); j++) {
                    if (map.get(j) != null) {
                        map.get(j).getOutputStream().write(messages.getBytes("UTF-8"));
                        map.get(j).getOutputStream().flush();
                    }
                }
            }
        }*/
        while (true) {
            System.out.print("Enter you message (Server) - ");
            byte[] buff = new byte[1024];
            int read = System.in.read(buff, 0, buff.length);
            for (int j = 0; j < map.size(); j++) {
                if (map.get(j) != null) {
                    map.get(j).getOutputStream().write(buff, 0, read);
                    map.get(j).getOutputStream().flush();
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new DemoServer(8081);

        Thread sender = new Thread(() ->  {
            try {
                messageSender();
            } catch (IOException e) {
                System.out.println(" Exception - " + e);
            }});
        sender.start();

        Thread receiver = new Thread(() ->  {
            try {
                messageReceiver();
            } catch (IOException e) {
                System.out.println(" Exception - " + e);
            }});
        receiver.start();

        Thread connector = new Thread(() ->  {
            try {
                connector();
            } catch (IOException e) {
                System.out.println(" Exception - " + e);
            }});
        connector.start();
    }
}
