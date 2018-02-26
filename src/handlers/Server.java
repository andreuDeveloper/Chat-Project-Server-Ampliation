/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handlers;

import chatserverampliation.ServerProject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author Andreu
 */
public class Server extends Client {

    //Atributes
    private final String host;
    private final int port;

    /**
     * Constructor to handle the server - server
     *
     * @param sp Server Project
     * @param socket
     * @param port
     */
    public Server(ServerProject sp, Socket socket, int port) {
        super(sp, socket);

        this.host = socket.getInetAddress().getHostAddress();
        this.port = port;
        System.out.println("OBJECT SERVER CREATED FOR: "+host + " : "+port);
    }

    /**
     * Bucle que se encarga de ir leyendo todos los mensajs que vengan del
     * cliente El bucle se encuentra dentro del processClient
     */
    @Override
    public void run() {
        try {
            System.out.println("Thread Server created");
            // Get I/O streams from the socket
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            processClient(in, out); // interact with a client

            // Close client connection
            socket.close();
            System.out.println(address + ") connection closed\n");
            this.sp.removeServer(this);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     *
     *
     * @param line
     */
    @Override
    protected void getMessage(String line) {
        System.out.println("Linea llegada desde servidor");
        //msg & NAME & msgContent
        String name;
        String msg;

        name = line.substring(3, line.indexOf("&")).trim();

        msg = line.substring(line.indexOf("&") + 1, line.length());
        System.out.println(name + ": " + msg);
        this.sp.doBroadcastMsgFromServer(this, line);
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

}
