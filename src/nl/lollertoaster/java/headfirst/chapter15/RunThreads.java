package nl.lollertoaster.java.headfirst.chapter15;


public class RunThreads implements Runnable{
    public static void main(String[] args) {
        RunThreads runner = new RunThreads();

        Thread alpha = new Thread(runner);
        alpha.setName("Alpha");

        Thread beta = new Thread(runner);
        beta.setName("Beta");

        alpha.start();
        beta.start();
    }

    @Override
    public void run() {
        for (int i = 0; i < 32; i++) {
            String threadName = Thread.currentThread().getName();
            System.out.println(threadName + " is running");
        }
    }
}
