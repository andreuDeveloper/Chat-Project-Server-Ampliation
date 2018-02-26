/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserverampliation;

import handlers.Server;
import handlers.Client;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Andreu
 */
public class ServerProject {

    //Server Project
    private final ServerFrame serverFrame;

    private int myPORT;
    private ServerMT serverMT;

    private final ArrayList<Client> clients;

    private final int MAX_SERVERS = 2;
    private OutServer[] outServers;
    private Server[] servers;

    /**
     * Just the constructor, initialize the server and clients list
     *
     * @param serverFrame ServerFrame
     */
    public ServerProject(ServerFrame serverFrame) {
        this.serverFrame = serverFrame;
        this.clients = new ArrayList<>();

        this.outServers = new OutServer[MAX_SERVERS];
        this.servers = new Server[MAX_SERVERS];

    }

    /**
     * Init the serverMT (for listening) and the outServer (to speak) with the
     * other server
     *
     * @param myPORT
     * @param outServerHOST
     * @param outServerPORT
     */
    public void initServerProject(int myPORT) {
        this.myPORT = myPORT;
        this.serverMT = new ServerMT(this, myPORT);
        this.serverMT.start();
    }

    /**
     * It init a specificated auxiliar server to a host and port
     * @param host Host to bind
     * @param port Port to bind
     * @param n Number of the server
     */
    public void initServerOutAux(String host, int port, int n) {
        /*
        this.serverOutAux[n] = new OutServer(this, host, port);
        this.outServer.start();
        
         */
        System.out.println("Starting ServerOut " + n);
        addMessageToLog("Starting ServerOut "+ n);
        this.outServers[n] = new OutServer(this, parseHost(host), port);
        this.outServers[n].start();
    }

    /**
     * Add the new server
     *
     * @param sock
     */
    public synchronized void addServer(Socket sock, int port) {
        boolean end = false;

        for (int i = 0; i < MAX_SERVERS && !end; i++) {
            if (servers[i] == null) {
                servers[i] = new Server(this, sock, port);
                new Thread(servers[i]).start();
                serverFrame.activateLabelServerConnected(true, servers[i].getHost(), servers[i].getPort());
                end = true;
                addMessageToLog("Server added");
            }
        }
    }

    /**
     * Check if its posible add a server
     * @param host Host of the server (IP)
     * @param port port of the server
     * @return  True if can, False if not
     */
    public synchronized boolean canAddServer(String host, int port) {
        boolean can = true;

        //If we can (for now) and the server not exists...
        for (int i = 0; (can) && (i < MAX_SERVERS); i++) {
            if (servers[i] != null) {
                if ((servers[i].getHost().equals(host)) && (servers[i].getPort() == port)) {
                    System.out.println("Server already exists");
                    return false;
                }
            }
        }

        //Check if there are not null
        for (int i = 0; i < MAX_SERVERS; i++) {
            if (servers[i] == null) {
                System.out.println("HAY UN ESPACIO DISPONIBLE");
                return true;
            }
        }
        return false;
    }

    /**
     * Remove the server
     */
    public void removeServer(Server server) {
        boolean end = false;
        for (int i = 0; i < MAX_SERVERS && !end; i++) {
            if (servers[i].equals(server)) {

                end = true;
            }
        }

        for (int i = 0; (end) && (i < MAX_SERVERS); i++) {
            if (servers[i] != null) {
                if ((servers[i].getHost().equals(server.getHost())) && (servers[i].getPort() == server.getPort())) {
                    serverFrame.activateLabelServerConnected(false, servers[i].getHost(), servers[i].getPort());

                    servers[i] = null;
                    System.out.println("Removed server");
                    addMessageToLog("Server removed");
                }
            }
        }
    }

    /**
     * Add a new client to the list using the socket
     *
     * @param sock Socket of the client - server
     */
    public synchronized void addClient(Socket sock) {
        Client c = new Client(this, sock);
        this.clients.add(c);
        new Thread(c).start();

        System.out.println("ServerProject trying to add client");
        addMessageToLog("Added a client");
    }

    /**
     * Remove the client of the list
     *
     * @param c Client to remove
     */
    public synchronized void removeClient(Client c) {
        this.clients.remove(c);

        System.out.println("Removed client");
        addMessageToLog("Client removed");
    }

    /**
     * Hace un broadcaset del mensaje excepto al cliente c
     *
     * @param msg Mensaje
     */
    public synchronized void doBroadcastMsgFromServer(Server s, String msg) {

        for (int i = 0; i < this.clients.size(); i++) {
            clients.get(i).sendMessage(msg);
        }

        //Si no es el servidor del que viene
        for (int i = 0; i < this.MAX_SERVERS; i++) {
            if (servers[i] != null) {
                if (!(servers[i].getHost().equals(s.getHost()) && servers[i].getPort() == s.getPort())) {
                    servers[i].sendMessage(msg);
                }
            }
        }
    }

    /**
     * Hace un broadcaset del mensaje excepto al cliente c y al otro server
     *
     * @param c Cliente c
     * @param msg Mensaje
     */
    public synchronized void doBroadcastMsgFromClient(Client c, String msg) {
        //Broadcast to all the clients except the Client c
        System.out.println("Broadcasting msg.. to " + (clients.size() - 1));
        addMessageToLog("Broadcasting msg.. to " + (clients.size() - 1));

        for (int i = 0; i < this.clients.size(); i++) {
            // debe hacer if / else --> !cliente
            if (!c.equals(clients.get(i))) {
                clients.get(i).sendMessage(msg);
            }
        }

        for (int i = 0; i < this.MAX_SERVERS; i++) {
            if (servers[i] != null) {
                servers[i].sendMessage(msg);
            }
        }
    }

    /**
     * Return the port number of out server
     * @return port number
     */
    public int getMyPort() {
        return this.myPORT;
    }

    /**
     * Parse the host to a host adress (Localhost is 127.0.0.1)
     * @param host
     * @return address
     */
    public String parseHost(String host) {
        try {
            return InetAddress.getByName(host).getHostAddress();
        } catch (UnknownHostException ex) {
            addMessageToLog("The port name not exists");
            System.out.println("The port name not exists");
            return null;
        }
    }

    //Stats
    /**
     * Put the server i to active
     * @param i Number of server
     * @return  1 if active 0 if not
     */
    public int getIsServerActive(int i) {
        if (servers[i] != null) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Get the messages sended by an specificated server
     * @param i Number of server
     * @return  number of msg
     */
    public int getMessagesSendedByServer(int i) {
        if (servers[i] != null) {
            return servers[i].getMsgSend();
        }
        return 0;
    }

   /**
     * Get the messages received by an specificated server
     * @param i Number of server
     * @return  number of msg
     */
    public int getMessagesReceivedByServer(int i) {
        if (servers[i] != null) {
            return servers[i].getMsgReceived();
        }
        return 0;
    }

    /**
     * Return our number of clients linked to our server
     * @return Number of clients
     */
    public int getNumberOfClients() {
        return this.clients.size();
    }

    /**
     * Get the number of messages sended by our clients
     * @return number of msg
     */
    public int getTotalMsgSendedByClients() {
        int sum = 0;
        for (Client c : clients) {
            sum += c.getMsgSend();
        }
        return sum;
    }

    /**
     * Get the number of messages receuved by our clients
     * @return number of msg
     */
    public int getTotalMsgReceivedByClients() {
        int sum = 0;
        for (Client c : clients) {
            sum += c.getMsgReceived();
        }
        return sum;
    }
    
    /**
     * Add a message to the log frame
     * @param msg Msg to add
     */
    public void addMessageToLog(String msg) {
        this.serverFrame.addMessageToLog(msg);
    }

}
