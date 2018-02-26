/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handlers;

import chatserverampliation.ServerProject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author Andreu
 */
public class Client implements Runnable {

    //Atributes
    protected ServerProject sp;
    protected final Socket socket;
    protected final String address;
    protected BufferedReader in;
    protected PrintWriter out;

    protected int msgReceived;
    protected int msgSend;

    /**
     * /Constructor of the cliend handler
     *
     * @param sp Server Project
     * @param socket Socker of the client . server
     */
    public Client(ServerProject sp, Socket socket) {
        System.out.println("Something Created");
        this.sp = sp;
        this.socket = socket;
        this.address = this.socket.getInetAddress().getHostAddress();
    }

    /**
     * Bucle que se encarga de ir leyendo todos los mensajs que vengan del
     * cliente El bucle se encuentra dentro del processClient
     */
    @Override
    public void run() {
        try {
            System.out.println("Thread Client created");
            // Get I/O streams from the socket
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            processClient(in, out); // interact with a client

            // Close client connection
            socket.close();
            System.out.println(address + ") connection closed\n");
            this.sp.removeClient(this);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * This method decodify the message sent by the client and do the pertinent
     * things
     *
     * @param in In of the client
     * @param out Out of the client
     */
    public void processClient(BufferedReader in, PrintWriter out) {
        String line;
        boolean done = false;
        try {
            while (!done) {
                if ((line = in.readLine()) == null) {
                    done = true;
                } else {
                    this.msgReceived++;
                    System.out.println("Client sent something");
                    line = line.trim();
                    if (line.length() >= 3) {
                        switch (line.substring(0, 3).toLowerCase()) {
                            case "bye":
                                done = true;
                                break;
                            case "msg":
                                getMessage(line);
                                break;
                            default:
                                System.out.println("Ignoring input line");
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * Get message of the client and broadcast it to the other and the other
     * server
     *
     * @param line
     */
    protected void getMessage(String line) {
        //msg & NAME & msgContent
        String name;
        String msg;

        name = line.substring(3, line.indexOf("&")).trim();

        msg = line.substring(line.indexOf("&") + 1, line.length());
        System.out.println(name + ": " + msg);

        this.sp.doBroadcastMsgFromClient(this, line);
    }

    /**
     * Comunicate a message with the client
     *
     * @param line
     */
    public void sendMessage(String line) {
        out.println(line);
        this.msgSend++;
    }

    /**
     * Return the number of msg sended by this client
     * @return Number of msg
     */
    public int getMsgSend() {
        return this.msgSend;
    }

    /**
     * Return the number of msg received by this client
     * @return Number of msg
     */
    public int getMsgReceived() {
        return this.msgReceived;
    }

}
