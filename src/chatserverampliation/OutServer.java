/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserverampliation;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * This class is used to speak with the other server, this class will be trying
 * to connect with the other server 24/7 if there are not other server actually
 *
 * @author Andreu
 */
public class OutServer extends Thread {

    private final ServerProject sp;
    private final String HOST;
    private final int PORT;
    private Socket socket;

    /**
     * Constructor of Out Server
     *
     * @param sp ServerProject
     * @param HOST Host adress of the server to communicate
     * @param PORT Port of the server to communicate
     */
    public OutServer(ServerProject sp, String HOST, int PORT) {
        this.sp = sp;
        this.HOST = HOST;
        this.PORT = PORT;
    }

    /**
     * Loop that if there is not ohter sever lined, will try to connect with the
     * other server, it will do this action every 1.5 sec
     */
    @Override
    public void run() {
        while (true) {
            //Sleep 1.5 sec
            try {
                Thread.sleep(1500);
            } catch (InterruptedException ex) {
                System.out.println(ex.getMessage());
            }

            if (sp.canAddServer(HOST, PORT)) {
                tryToConnect();
            }
        }
    }

    /**
     * Open a new Socket to the other server, if the connection is succesfull..
     * Will send to the other server a 'Hi I'm the server' to communicate him
     * that the connection is done from other server, then it will add the
     * server using the ServerProhect class If the creation of the socket is
     * refused, it will jump to the excpetion, so the connection will not be
     * done
     */
    private void tryToConnect() {
        try {
            this.socket = new Socket(HOST, PORT);
            System.out.println("Connected to Server: " + HOST + ":" + PORT);
            PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true);

            //Add si no hay ningu problema
            if (sp.canAddServer(HOST, PORT)) {
                sp.addServer(socket, PORT);
            }
            out.println("$SERVER$" + sp.getMyPort());
        } catch (IOException ex) {
            System.out.println(ex.getMessage() + " - " + HOST + " : " + PORT);
        }
    }

}
