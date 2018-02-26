/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserverampliation;

import java.awt.Dimension;
import java.awt.Font;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 *
 * @author Andreu
 */
public class Statistics extends JTable implements Runnable {

    private ServerFrame serverFrame;
    private ServerProject serverProject;

    private DefaultTableModel model;
    private String[] header = {"#", "SERVER 1", "SERVER 2", "CLIENTS"};
    private String[][] data = {
        {"Active", "0", "0", "0"},
        {"Messages send from", "0", "0", "0"},
        {"Messages received from", "0", "0", "0"}
    };

    /**
     * Constructor
     * @param serverFrame Linked Frame
     * @param serverProject Linked Project to extract the things
     */
    public Statistics(ServerFrame serverFrame, ServerProject serverProject) {
        this.serverFrame = serverFrame;
        this.serverProject = serverProject;
        initTable();
        refresh();
    }

    /**
     * Thread life method
     */
    @Override
    public void run() {
        while (true) {
            refresh();
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(Statistics.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    /**
     * R
     */
    private void refresh() {
        model.setValueAt(serverProject.getIsServerActive(0), 0, 1);
        model.setValueAt(serverProject.getIsServerActive(1), 0, 2);
        model.setValueAt(serverProject.getNumberOfClients(), 0, 3);

//        model.setValueAt("a", 0, 1);
//        model.setValueAt("b", 0, 2);
//        model.setValueAt("c", 0, 3);

        model.setValueAt(serverProject.getMessagesSendedByServer(0), 1, 1);
        model.setValueAt(serverProject.getMessagesSendedByServer(1), 1, 2);
        model.setValueAt(serverProject.getTotalMsgSendedByClients(), 1, 3);

        model.setValueAt(serverProject.getMessagesReceivedByServer(0), 2, 1);
        model.setValueAt(serverProject.getMessagesReceivedByServer(1), 2, 2);
        model.setValueAt(serverProject.getTotalMsgReceivedByClients(), 2, 3);

    }

    /**
     * Init the main aspects of the table
     */
    private void initTable() {
        model = new DefaultTableModel(data, header) {
            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        };
        this.setModel(model);
        this.getTableHeader().setFont(new Font("Arial", Font.BOLD, 15));
        this.getTableHeader().setPreferredSize(new Dimension(200, 50));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        TableColumn column;
        for (int i = 0; i < this.getColumnCount(); i++) {
            column = this.getColumnModel().getColumn(i);
            column.setPreferredWidth(200);
            column.setCellRenderer(centerRenderer);
        }
        this.setRowHeight(50);

    }

}
