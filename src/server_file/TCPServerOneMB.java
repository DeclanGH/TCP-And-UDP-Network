/*
 * @author Declan Onunkwo
 * Spring 2024
 */
package server_file;

import functions.HelperFunctions;

import java.io.*;
import java.net.*;

public class TCPServerOneMB {

    static final int PORT = 26942;

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        Socket client = null;
        InputStream input = null;
        OutputStream output = null;
        DataInputStream dataInputStream = null;
        DataOutputStream dataOutputStream = null;

        try {
            serverSocket = new ServerSocket(PORT);
            String encryptedMessage;
            long key = Long.MAX_VALUE - 98765;

            System.out.println("\n--------------------------------------------------------------");

            client = serverSocket.accept();
            input = client.getInputStream();
            output = client.getOutputStream();

            String prevMessage = "";

            while (true) {
                dataInputStream = new DataInputStream(input);
                dataOutputStream = new DataOutputStream(output);

                encryptedMessage = dataInputStream.readUTF();
                dataOutputStream.writeUTF("received!");
                dataOutputStream.flush();

                // no decryption for data collection purposes

                /*key = HelperFunctions.generateRandomNumber(key);
                String decryptedMessage = HelperFunctions.decrypt(encryptedMessage,key);

                if (!decryptedMessage.equals(prevMessage) && decryptedMessage.length() != prevMessage.length()) {
                    prevMessage = decryptedMessage;
                    System.out.println("Client Message: " + decryptedMessage);
                    System.out.println("--------------------------------------------------------------");
                }*/
            }

        } catch(IOException e) {
            System.out.println("closing connection...");
            try {
                dataOutputStream.close();
                output.close();
                dataInputStream.close();
                input.close();
                client.close();
                serverSocket.close();
            } catch (IOException ex) {
                System.out.println("IO exception");
            } catch (NullPointerException ex) {
                System.out.println("no connection was initially opened...");
            }
            System.out.println("GoodBye!");
        }
    }
}