/*
 * @author Declan Onunkwo
 * Spring 2024
 */
package server_file;

import functions.HelperFunctions;

import java.io.*;
import java.net.*;

public class TCPServer {
    static final int PORT = 26940;
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            String encryptedMessage;
            long key = Long.MAX_VALUE - 98765;

            System.out.println("\n--------------------------------------------------------------");

            Socket client = serverSocket.accept();
            InputStream input = client.getInputStream();
            OutputStream output = client.getOutputStream();

            while (true) {
                DataInputStream dataInputStream = new DataInputStream(input);
                DataOutputStream dataOutputStream = new DataOutputStream(output);

                dataOutputStream.writeUTF("Message was received!");
                dataOutputStream.flush();

                key = HelperFunctions.generateRandomNumber(key);
                encryptedMessage = dataInputStream.readUTF();

                String decryptedMessage = HelperFunctions.decrypt(encryptedMessage,key);

                if (decryptedMessage.equals("exit!")) {
                    System.out.println("closing connection...");
                    dataOutputStream.close();
                    output.close();
                    dataInputStream.close();
                    input.close();
                    client.close();
                    break;
                }

                System.out.println("Client Message: " + decryptedMessage);
                System.out.println("--------------------------------------------------------------");
            }

            serverSocket.close();
            System.out.println("GoodBye!");
        } catch(IOException e) {
            System.err.println("IO failure.");
            e.printStackTrace();
        }
    }
}