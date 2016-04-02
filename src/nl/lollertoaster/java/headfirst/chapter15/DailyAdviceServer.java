package nl.lollertoaster.java.headfirst.chapter15;


import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class DailyAdviceServer {
    private String[] adviceList = {
            "Take smaller bites",
            "Go for the tight jeans. No they do NOT make you look fat!",
            "One word: inappropriate",
            "Just for today, be honest. Tell your boss what you *really* think",
            "You might want to rethink that haircut"
    };

    public static void main(String[] args) {new DailyAdviceServer().go();}

    private void go() {
        try {
            ServerSocket serverSocket = new ServerSocket(4242);

            //noinspection InfiniteLoopStatement
            while (true) {
                Socket socket = serverSocket.accept();

                PrintWriter writer = new PrintWriter(socket.getOutputStream());

                String advice = getAdvice();
                System.out.println(advice);

                writer.println(advice);
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getAdvice() {
        int random = (int)(Math.random() * adviceList.length);
        return adviceList[random];
    }
}
