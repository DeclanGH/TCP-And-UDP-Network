/*
 * @author Declan Onunkwo
 * Spring 2024
 */
package client_file;

import functions.HelperFunctions;
import org.jetbrains.annotations.*;

import java.io.*;
import java.net.*;

public class TCPClientOneMB {

    private static final int ONE_MEGABYTE = 1048576; // 1,048,576 bytes

    public static void main(@NotNull String[] args) {
        String serverHostName = args[0];
        int serverPortNumber = 26942;

        Socket socket = null;

        try {
            socket = new Socket(serverHostName,serverPortNumber);
        } catch (UnknownHostException e) {
            System.err.println("The host \"" + serverHostName + "\" is unknown.");
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection.");
            e.printStackTrace();
            System.exit(1);
        }

        try {
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            BufferedReader stdIn = new BufferedReader(
                    new InputStreamReader(System.in)
            );

            long key = Long.MAX_VALUE - 98765;
            System.out.println("\n--------------------------------------------------------------");

            int timeout = 5000; // milliseconds
            socket.setSoTimeout(timeout);

            mainLoop:
            while (true) {
                int droppedPackets = 0;
                String userInput = stdIn.readLine();
                int inputLength = userInput.getBytes().length;
                int numOfLoops = ONE_MEGABYTE / inputLength;
                key = HelperFunctions.generateRandomNumber(key);
                String encryption = HelperFunctions.encrypt(userInput, key);

                long totalTime = 0;
                int count = numOfLoops;

                while (count > 0){
                    long startTime = System.nanoTime();
                    output.writeUTF(encryption);
                    output.flush();
                    if (userInput.equals("exit!")) break mainLoop;
                    try {
                        if (input.readUTF().equals("received!")){
                            long endTime = System.nanoTime();
                            totalTime += endTime - startTime;
                            count -=  1;
                        }
                    } catch (SocketTimeoutException e) {
                        System.out.println("Timeout occurred, resending packet...");
                        droppedPackets += 1;
                        // goes on to resend the packet without adding time or decrementing count
                    }
                }

                double timeInSeconds = totalTime / 1e9;
                int numberOfBitsInMessage = inputLength * 8;
                double throughput = numberOfBitsInMessage / timeInSeconds;

                System.out.println("A message of " + inputLength + " bytes was sent " + numOfLoops + " times." );
                System.out.println(droppedPackets + " dropped packets.");
                System.out.println("Throughput = " + throughput + " bps");
                System.out.println("--------------------------------------------------------------");
            }

            System.out.println("closing connection...");
            output.close();
            input.close();
            stdIn.close();
            socket.close();
            System.out.println("GoodBye!");

        } catch (IOException e) {
            System.err.println("IO failure.");
            e.printStackTrace();
        }
    }
}