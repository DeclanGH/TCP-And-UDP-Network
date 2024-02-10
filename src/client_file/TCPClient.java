/*
 * @author Declan Onunkwo
 * Spring 2024
 */
package client_file;

import functions.HelperFunctions;
import org.jetbrains.annotations.*;

import java.io.*;
import java.net.*;

public class TCPClient {
    public static void main(@NotNull String[] args) {
        String serverHostName = args[0];
        int serverPortNumber = 26940;

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

            while (true) {
                String userInput = stdIn.readLine();
                key = HelperFunctions.generateRandomNumber(key);
                String encryption = HelperFunctions.encrypt(userInput, key);

                long startTime = System.nanoTime();
                output.writeUTF(encryption);
                output.flush();
                String echo = input.readUTF();
                long endTime = System.nanoTime();

                if (userInput.equals("exit!")){
                    System.out.println("closing connection...");
                    output.close();
                    input.close();
                    stdIn.close();
                    socket.close();
                    System.out.println("GoodBye!");
                    break;
                }

                long roundTripTime = endTime - startTime;
                int numberOfBitsInMessage = userInput.getBytes().length * 8;
                double timeInSeconds = roundTripTime / 1e9;
                double throughput = numberOfBitsInMessage / timeInSeconds;

                System.out.println("Input size = " + userInput.getBytes().length + " bytes" );
                System.out.println("echo: " + echo + " (" + echo.getBytes().length + " bytes)");
                System.out.println("Round Trip Time = " + roundTripTime + " ms");
                System.out.println("Throughput = " + throughput + " bps");
                System.out.println("--------------------------------------------------------------");
            }
        } catch (IOException e) {
            System.err.println("IO failure.");
            e.printStackTrace();
        }
    }
}