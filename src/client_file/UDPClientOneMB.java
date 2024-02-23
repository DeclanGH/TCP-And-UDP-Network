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

public class UDPClientOneMB {

    static final int SERVER_PORT = 26947; // using any port between 26940 and 26949
    private static final int ONE_MEGABYTE = 1048576; // 1,048,576 bytes

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

            int timeout = 5000; // milliseconds
            clientSideChannel.socket().setSoTimeout(timeout);

            mainLoop:
            while (true){
                int droppedPackets = 0;
                userInput = stdUserInput.readLine();

                int inputLength = userInput.getBytes().length;
                int numOfLoops = ONE_MEGABYTE / inputLength;
                key = HelperFunctions.generateRandomNumber(key);
                String encryptedMessage = "exit!";
                if (!userInput.equals("exit!")) encryptedMessage  = HelperFunctions.encrypt(userInput, key);
                messageToSend = ByteBuffer.wrap(encryptedMessage.getBytes());

                long totalTime = 0;
                int count = numOfLoops;

                while (count > 0) {
                    long startTime = System.nanoTime();
                    clientSideChannel.write(messageToSend);
                    if (userInput.equals("exit!")) break mainLoop;
                    messageToSend.flip();
                    try {
                        clientSideChannel.receive(dataBufferReceived);
                        long endTime = System.nanoTime();
                        totalTime += (endTime - startTime);
                        count--;
                    } catch (SocketTimeoutException e) {
                        System.out.println("Timeout occurred, resending packet...");
                        droppedPackets += 1;
                        // goes on to resend the packet without adding time or decrementing count
                    }
                    dataBufferReceived.clear();
                    key = HelperFunctions.generateRandomNumber(key);
                }

                int numberOfBitsInMessage = userInput.getBytes().length * 8 * numOfLoops;
                double timeInSeconds = totalTime / 1e9;
                double throughput = numberOfBitsInMessage / timeInSeconds;

                System.out.println("A message of " + inputLength + " bytes was sent " + numOfLoops + " times." );
                System.out.println(droppedPackets + " dropped packets.");
                System.out.println("Throughput = " + throughput + " bps");
                System.out.println("--------------------------------------------------------------");

                messageToSend.clear();
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