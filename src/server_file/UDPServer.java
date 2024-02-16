/*
 * @author Declan Onunkwo
 * Spring 2024
 */
package server_file;

import functions.HelperFunctions;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class UDPServer {

    static final int SERVER_PORT = 26945;

    public static void main(String[] args) {

        DatagramChannel serverSideChannel = null;

        try {
            serverSideChannel = DatagramChannel.open();
            serverSideChannel.bind(new InetSocketAddress(SERVER_PORT));
        } catch (IOException e) {
            System.err.println("Couldn't create datagram channel.");
            e.printStackTrace();
            System.exit(1);
        }

        ByteBuffer dataBufferToReceive = ByteBuffer.allocate(2048);
        byte[] dataToSend = "Received!".getBytes();
        long key = Long.MAX_VALUE - 98765;

        System.out.println("\n--------------------------------------------------------------");

        while (true) {

            try {
                InetSocketAddress clientAddress = (InetSocketAddress) serverSideChannel.receive(dataBufferToReceive);
                ByteBuffer feedback = ByteBuffer.wrap(dataToSend);
                serverSideChannel.send(feedback,clientAddress);

                dataBufferToReceive.flip();
                byte[] receivedBytes = new byte[dataBufferToReceive.remaining()];
                dataBufferToReceive.get(receivedBytes);
                String receivedEncryption = new String(receivedBytes);

                key = HelperFunctions.generateRandomNumber(key);
                String decryptedMessage = HelperFunctions.decrypt(receivedEncryption, key);

                if (decryptedMessage.equals("exit!")) break;

                System.out.println("client sent : " + decryptedMessage);
                System.out.println("--------------------------------------------------------------");
                dataBufferToReceive.clear();
                feedback.clear();
            } catch (IOException e) {
                System.err.println("IO failure.");
                e.printStackTrace();
            }
        }

        try {
            System.out.println("closing connection...");
            serverSideChannel.close();
            System.out.println("GoodBye!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}