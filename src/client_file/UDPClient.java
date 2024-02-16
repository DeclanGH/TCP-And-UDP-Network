/*
 * @author Declan Onunkwo
 * Spring 2024
 */
package client_file;

import functions.HelperFunctions;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;

public class UDPClient {

    static final int SERVER_PORT = 26945; // using any port between 26940 and 26949

    public static void main(String @NotNull [] args) {
        String serverHostName = args[0];

        DatagramChannel clientSideChannel = null;

        InetSocketAddress serverAddress;

        try {
            clientSideChannel = DatagramChannel.open();
            serverAddress = new InetSocketAddress(serverHostName, SERVER_PORT);
            clientSideChannel.connect(serverAddress);
        } catch (UnknownHostException e) {
            System.err.println("The host \"" + serverHostName + "\" is unknown.");
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't create datagram channel.");
            e.printStackTrace();
            System.exit(1);
        }

        ByteBuffer messageToSend;
        ByteBuffer dataBufferReceived = ByteBuffer.allocate(16); // to store confirmation from server

        try {
            BufferedReader stdUserInput = new BufferedReader(
                    new InputStreamReader(System.in)
            );
            String userInput;
            long key = Long.MAX_VALUE - 98765;
            System.out.println("\n--------------------------------------------------------------");

            while (true){
                userInput = stdUserInput.readLine();
                key = HelperFunctions.generateRandomNumber(key);
                String encryptedMessage = HelperFunctions.encrypt(userInput,key);
                messageToSend = ByteBuffer.wrap(encryptedMessage.getBytes());

                long startTime = System.nanoTime();
                clientSideChannel.write(messageToSend);
                clientSideChannel.receive(dataBufferReceived);
                long endTime = System.nanoTime();

                if (userInput.equals("exit!")) break;

                dataBufferReceived.flip();
                byte[] feedbackBytes = new byte[dataBufferReceived.remaining()];
                dataBufferReceived.get(feedbackBytes);

                String feedback = new String(feedbackBytes);

                long roundTripTime = endTime - startTime;
                int numberOfBitsInMessage = userInput.getBytes().length * 8;
                double timeInSeconds = roundTripTime / 1e9;
                double throughput = numberOfBitsInMessage / timeInSeconds;

                System.out.println(feedback);
                System.out.println("Round Trip Time = " + roundTripTime + " ms");
                System.out.println("Throughput = " + throughput + " bps");
                System.out.println("--------------------------------------------------------------");
                messageToSend.clear();
                dataBufferReceived.clear();
            }

            System.out.println("closing connection...");
            stdUserInput.close();
            clientSideChannel.close();
            System.out.println("GoodBye!");

        } catch (IOException e) {
            System.err.println("IO failure.");
            e.printStackTrace();
        }
    }
}