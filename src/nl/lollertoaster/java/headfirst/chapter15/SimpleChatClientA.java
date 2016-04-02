package nl.lollertoaster.java.headfirst.chapter15;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class SimpleChatClientA {
    private JTextField outgoing;
    private PrintWriter writer;
    private Socket sock;

    public static void main(String[] args) {new SimpleChatClientA().go();}

    public void go() {
        JFrame frame = new JFrame("Ludicrously Simple Chat Client");
        frame.setSize(400, 500);

        JPanel mainPanel = new JPanel();
        frame.getContentPane().add(BorderLayout.CENTER, mainPanel);

        outgoing = new JTextField(20);
        mainPanel.add(outgoing);

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(this::onSend);
        mainPanel.add(sendButton);

        setUpNetworking();

        frame.setVisible(true);
    }

    private void setUpNetworking() {
        try {
            sock = new Socket("localhost", 5000);
            writer = new PrintWriter(sock.getOutputStream());
            System.out.println("Networking established!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onSend(ActionEvent event) {
        try {
            writer.println(outgoing.getText());
            writer.flush();
        } catch (Exception e) {e.printStackTrace();}
    }
}
