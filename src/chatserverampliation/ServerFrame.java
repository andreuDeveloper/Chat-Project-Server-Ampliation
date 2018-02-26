
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserverampliation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;

/**
 *
 * @author Andreu
 */
public class ServerFrame extends JFrame implements ActionListener {

    //FRAME
    private JTabbedPane jTabPane;
    private JPanel jPanelConfig;

    private JPanel jPanelStats;
    private Statistics stats;

    private JPanel jPanelLog;
    private JTextArea jTxAreaLog;
    private JScrollPane jScrollLog;

    private JLabel jLbTitle;
    private JLabel jLbAuxServer1;
    private JLabel jLbAuxServer2;
    private JLabel jLbMyServer;
    private JTextField jTxHostAuxServer[];
    private JTextField jTxPortAuxServer[];
    private JTextField jTxHostMyServer;
    private JTextField jTxPortMyServer;
    private JLabel jLbStatusServer[];
    private JButton jBtnConnect;

    //Clases
    private ServerProject serverProject;

    /**
     * Just the constructor, that creates the Frame and initialize the server
     * and clients list
     */
    public ServerFrame() {
        serverProject = new ServerProject(this);
        stats = new Statistics(this, serverProject);

        initWindow();
        initComponentsWindow();
    }

    /**
     * Action performed of the window
     *
     * @param ae
     */
    @Override
    public void actionPerformed(ActionEvent ae) {

        if (ae.getSource().equals(this.jBtnConnect)) {
            if (checkConfigurations()) {
                createConnection();
                initAuxServer(0);
                initAuxServer(1);
                new Thread(this.stats).start();
                this.jTxHostAuxServer[0].setEnabled(false);
                this.jTxPortAuxServer[0].setEnabled(false);
                this.jTxHostAuxServer[1].setEnabled(false);
                this.jTxPortAuxServer[1].setEnabled(false);
                this.jTxPortMyServer.setEnabled(false);
                this.jBtnConnect.setEnabled(false);
            } else {
                JOptionPane.showMessageDialog(null, "Wrong configuration");
            }
        }
    }

    /**
     * Activate or desactivate the labels information of connected
     * @param b Activate or desactivate
     * @param host Host IP to reference
     * @param port  Port to reference
     */
    public void activateLabelServerConnected(boolean b, String host, int port) {
        for (int i = 0; i < this.jTxHostAuxServer.length; i++) {
            if (serverProject.parseHost(jTxHostAuxServer[i].getText()).equals(host)
                    && (Integer.parseInt(jTxPortAuxServer[i].getText()) == port)) {
                if (b) {
                    this.jLbStatusServer[i].setText("SECONDARY SERVER " + (i + 1) + ": CONNECTED");
                    this.jLbStatusServer[i].setForeground(new Color(0x04d804));
                } else {
                    this.jLbStatusServer[i].setText("SECONDARY SERVER " + (i + 1) + ": DISCONNECTED");
                    this.jLbStatusServer[i].setForeground(new Color(0xff0f0f));
                }
            }
        }
    }

    /** 
     * It add a message to the console log of the frame
     * @param msg Message to add
     */
    public void addMessageToLog(String msg) {
        this.jTxAreaLog.setText(this.jTxAreaLog.getText() + "\n" + msg);
    }

    /**
     * Returns an ImageIcon, or null if the path was invalid.
     */
    private ImageIcon createImageIcon(String path) {
        if (path != null) {
            return new ImageIcon(path);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    /**
     * Check if the configurations are ok
     * @return True if ok, no if not
     */
    private boolean checkConfigurations() {
        //Check incomplete fields and that are port numbers
        try {
            if ((jTxHostMyServer.getText().length() > 0) && (jTxPortMyServer.getText().length() == 4)) {
                Integer.parseInt(jTxPortMyServer.getText());
            } else {
                return false;
            }

            for (int i = 0; i < jTxHostAuxServer.length; i++) {
                if ((jTxHostAuxServer[i].getText().length() > 0) && (jTxPortAuxServer[i].getText().length() == 4)) {
                    Integer.parseInt(jTxPortAuxServer[i].getText());
                } else {
                    return false;
                }
            }

            //Check not repeated
            if (jTxPortAuxServer[0].getText().equals(jTxPortMyServer.getText())
                    && jTxHostAuxServer[0].getText().equals(jTxHostMyServer.getText())) {
                return false;
            }
            if (jTxPortAuxServer[1].getText().equals(jTxPortMyServer.getText())
                    && jTxHostAuxServer[1].getText().equals(jTxHostMyServer.getText())) {
                return false;
            }
            if (jTxPortAuxServer[0].getText().equals(jTxPortAuxServer[1].getText())
                    && jTxHostAuxServer[0].getText().equals(jTxHostAuxServer[1].getText())) {
                return false;
            }

        } catch (Exception e) {
            return false;
        }

        return true;
    }

    /**
     * Init the basic things of the window
     */
    private void initWindow() {
        this.setTitle("Server");
        this.setSize(800, 450);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
    }

    /**
     * Init the components of the windows
     */
    private void initComponentsWindow() {
        //TabPane
        this.jTabPane = new JTabbedPane();

        this.jPanelConfig = new JPanel();
        this.jPanelConfig.setLayout(new GridBagLayout());
        this.jPanelConfig.setBackground(new Color(0x263238));

        this.jPanelStats = new JPanel();
        this.jPanelStats.setLayout(new BorderLayout());

        this.jPanelLog = new JPanel();
        this.jPanelLog.setLayout(new BorderLayout());

        jTabPane.addTab("  CONFIGURATION  ", createImageIcon("./img/settings.png"), jPanelConfig);
        jTabPane.addTab("  STATS  ", createImageIcon("./img/stats.png"), jPanelStats);
        jTabPane.addTab("  LOG  ", createImageIcon("./img/log.png"), jPanelLog);

        Container cpanel = this.getContentPane();
        cpanel.add(jTabPane);

        //Components CONFIG
        this.jLbTitle = new JLabel("SERVER CONFIGURATION");
        this.jLbTitle.setHorizontalAlignment(0);
        this.jLbTitle.setForeground(Color.white);
        this.jLbTitle.setFont(new Font("Arial", 1, 25));
        this.jLbAuxServer1 = new JLabel("AUX SERVER 1");
        this.jLbAuxServer1.setHorizontalAlignment(0);
        this.jLbAuxServer1.setForeground(Color.white);
        this.jLbAuxServer1.setFont(new Font("Arial", 1, 15));
        this.jLbAuxServer2 = new JLabel("AUX SERVER 2");
        this.jLbAuxServer2.setHorizontalAlignment(0);
        this.jLbAuxServer2.setForeground(Color.white);
        this.jLbAuxServer2.setFont(new Font("Arial", 1, 15));
        this.jLbMyServer = new JLabel("My Server");
        this.jLbMyServer.setHorizontalAlignment(0);
        this.jLbMyServer.setForeground(Color.white);
        this.jLbMyServer.setFont(new Font("Arial", 1, 15));
        Font f = new Font("Arial", 1, 14);
        this.jTxHostAuxServer = new JTextField[2];
        this.jTxPortAuxServer = new JTextField[2];
        this.jTxHostAuxServer[0] = new JTextField("localhost");
        this.jTxHostAuxServer[0].setHorizontalAlignment(0);
        this.jTxHostAuxServer[0].setFont(f);
        this.jTxHostAuxServer[1] = new JTextField("localhost");
        this.jTxHostAuxServer[1].setHorizontalAlignment(0);
        this.jTxHostAuxServer[1].setFont(f);
        this.jTxPortAuxServer[0] = new JTextField("7777");
        this.jTxPortAuxServer[0].setHorizontalAlignment(0);
        this.jTxPortAuxServer[0].setFont(f);
        this.jTxPortAuxServer[1] = new JTextField("9999");
        this.jTxPortAuxServer[1].setHorizontalAlignment(0);
        this.jTxPortAuxServer[1].setFont(f);
        this.jTxHostMyServer = new JTextField("localhost");
        this.jTxHostMyServer.setEnabled(false);
        this.jTxHostMyServer.setHorizontalAlignment(0);
        this.jTxHostMyServer.setFont(new Font("Arial", 3, 13));
        this.jTxPortMyServer = new JTextField("8888");
        this.jTxPortMyServer.setHorizontalAlignment(0);
        this.jTxPortMyServer.setFont(f);
        this.jLbStatusServer = new JLabel[2];
        this.jLbStatusServer[0] = new JLabel("SECONDARY SERVER 1: DISCONNECTED");
        this.jLbStatusServer[0].setHorizontalAlignment(0);
        this.jLbStatusServer[0].setFont(new Font("Arial", 3, 14));
        this.jLbStatusServer[0].setForeground(new Color(0xff0f0f));
        this.jLbStatusServer[1] = new JLabel("SECONDARY SERVER 2: DISCONNECTED");
        this.jLbStatusServer[1].setHorizontalAlignment(0);
        this.jLbStatusServer[1].setFont(new Font("Arial", 3, 14));
        this.jLbStatusServer[1].setForeground(new Color(0xff0f0f));
        this.jBtnConnect = new JButton("CONNECT");
        this.jBtnConnect.addActionListener(this);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1; //Esto es como ser redimensiona 
        c.weightx = 1;

        //Titulo
        c.weighty = 1;
        c.insets = new Insets(10, 0, 10, 0);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 5;
        c.gridheight = 1;
        this.jPanelConfig.add(this.jLbTitle, c);

        //LABELS SERVERS
        c.weighty = 0.5;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 1;
        this.jPanelConfig.add(this.jLbAuxServer1, c);
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        c.gridheight = 1;
        this.jPanelConfig.add(this.jLbAuxServer2, c);
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 1;
        c.gridheight = 1;
        this.jPanelConfig.add(this.jLbMyServer, c);

        //TextFields HOST
        c.insets = new Insets(10, 5, 10, 5);
        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 1;
        this.jPanelConfig.add(this.jTxHostAuxServer[0], c);
        c.gridx = 1;
        c.gridy = 2;
        c.gridwidth = 1;
        c.gridheight = 1;
        this.jPanelConfig.add(this.jTxHostAuxServer[1], c);
        c.gridx = 1;
        c.gridy = 3;
        c.gridwidth = 1;
        c.gridheight = 1;
        this.jPanelConfig.add(this.jTxHostMyServer, c);

        //TextFields PORT
        c.gridx = 2;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 1;
        this.jPanelConfig.add(this.jTxPortAuxServer[0], c);
        c.gridx = 2;
        c.gridy = 2;
        c.gridwidth = 1;
        c.gridheight = 1;
        this.jPanelConfig.add(this.jTxPortAuxServer[1], c);
        c.gridx = 2;
        c.gridy = 3;
        c.gridwidth = 1;
        c.gridheight = 1;
        this.jPanelConfig.add(this.jTxPortMyServer, c);

        //lABELS
        c.insets = new Insets(10, 30, 10, 0);
        c.gridx = 3;
        c.gridy = 1;
        c.gridwidth = 2;
        c.gridheight = 1;
        this.jPanelConfig.add(this.jLbStatusServer[0], c);
        c.insets = new Insets(10, 30, 10, 0);
        c.gridx = 3;
        c.gridy = 2;
        c.gridwidth = 2;
        c.gridheight = 1;
        this.jPanelConfig.add(this.jLbStatusServer[1], c);

        c.insets = new Insets(10, 30, 10, 15);
        c.gridx = 3;
        c.gridy = 3;
        c.gridwidth = 2;
        c.gridheight = 1;
        this.jPanelConfig.add(this.jBtnConnect, c);

        //STATS
        this.jPanelStats.add(this.stats.getTableHeader(), BorderLayout.NORTH);
        this.jPanelStats.add(this.stats, BorderLayout.CENTER);

        //Components LOG
        this.jTxAreaLog = new JTextArea();
        this.jTxAreaLog.setFont(new Font("Arial", Font.PLAIN, 20));
        this.jTxAreaLog.setBackground(new Color(0x263238));
        this.jTxAreaLog.setForeground(Color.WHITE);
        this.jTxAreaLog.setEditable(false);
        this.jTxAreaLog.setLineWrap(true);
        this.jTxAreaLog.setWrapStyleWord(true);
        this.jTxAreaLog.setBorder(BorderFactory.createCompoundBorder(
                jTxAreaLog.getBorder(),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        DefaultCaret caret = (DefaultCaret) jTxAreaLog.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        this.jScrollLog = new JScrollPane(jTxAreaLog, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        this.jPanelLog.add(jScrollLog, BorderLayout.CENTER);
    }

    /**
     * Createa connection main to the rendebo port
     */
    private void createConnection() {
        addMessageToLog("Trying to create a connection.. ");
        int myPort = Integer.parseInt(this.jTxPortMyServer.getText());
        this.serverProject.initServerProject(myPort);

        System.out.println("Configurated");
        System.out.println("MY PORT-> " + myPort);
        this.setTitle("Server - (localhost : " + myPort + ")");
        addMessageToLog("Configurated at localhost : " + myPort);
    }

    /**
     * Init the specificated auxiliar server
     * @param n Number of the server 
     */
    private void initAuxServer(int n) {
        String host;
        int port;
        switch (n) {
            case 0:

                host = jTxHostAuxServer[0].getText();
                port = Integer.parseInt(jTxPortAuxServer[0].getText());

                this.serverProject.initServerOutAux(host, port, n);
                addMessageToLog("Creating outServer " + (n + 1) + " - " + host + ":" + port);

                break;
            case 1:

                host = jTxHostAuxServer[1].getText();
                port = Integer.parseInt(jTxPortAuxServer[1].getText());

                this.serverProject.initServerOutAux(host, port, n);
                addMessageToLog("Creating outServer " + (n + 1) + " - " + host + ":" + port);

                break;
        }
    }
}
