package nl.lollertoaster.java.headfirst.chapter15;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


public class DailyAdviceClient {
    public static void main(String[] args) {
        new DailyAdviceClient().go();
    }

    private void go() {
        try {
            Socket s = new Socket("127.0.0.1", 4242);

            InputStreamReader streamReader = new InputStreamReader(s.getInputStream());
            BufferedReader reader = new BufferedReader(streamReader);

            String advice = reader.readLine();
            System.out.println("Today you should: " + advice);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}