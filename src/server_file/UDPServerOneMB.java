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

public class UDPServerOneMB {

    static final int SERVER_PORT = 26947;

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

                // for data collection purposes, the validation would be commented out

                byte[] receivedBytes = new byte[dataBufferToReceive.remaining()];
                dataBufferToReceive.get(receivedBytes);
                String receivedEncryption = new String(receivedBytes);

                /*key = HelperFunctions.generateRandomNumber(key);
                String decryptedMessage = HelperFunctions.decrypt(receivedEncryption, key);*/

                if (receivedEncryption.equals("exit!")) break;

                /*System.out.println("client sent : " + decryptedMessage);
                System.out.println("--------------------------------------------------------------");*/
                dataBufferToReceive.clear();
                feedback.clear();
            } catch (IOException e) {
                System.out.println("IO failure.");
            }
        }

        System.out.println("closing connection...");
        try {
            serverSideChannel.close();
        } catch (IOException ex) {
            System.out.println("socket was not open...");;
        }
        System.out.println("GoodBye!");
    }
}