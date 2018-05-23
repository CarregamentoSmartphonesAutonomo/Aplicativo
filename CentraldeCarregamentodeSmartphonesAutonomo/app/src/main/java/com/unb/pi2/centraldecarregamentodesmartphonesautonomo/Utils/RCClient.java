package com.unb.pi2.centraldecarregamentodesmartphonesautonomo.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class RCClient {
    public static final String SERVER_IP = "10.3.141.1";

    Socket socket;
    PrintWriter output;
    BufferedReader input;

    public RCClient(){
        try {
            socket = new Socket(InetAddress.getByName(SERVER_IP), 4141);
            output = new PrintWriter(socket.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            write(output, "SYN"); // Synchronize
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void turn(int pulseWidth) {
        write(output, "pwAngle="+pulseWidth);
    }
    void write(PrintWriter output, String message) {
        System.out.println("Sending: " +message);
        output.println(message);
    }

    public void closeUp() {
        try {
            write(output, "FIN");
            String in = "";
            while ((in = input.readLine()) != null) {
                if (in.equals("FIN-ACK")){ // Final-Acknowledge
                    break;
                }
            }
            System.out.print("Closing socket.");
            input.close();
            output.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
