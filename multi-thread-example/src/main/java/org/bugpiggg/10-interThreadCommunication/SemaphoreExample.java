import java.util.concurrent.Semaphore;

public class SemaphoreExample {
    private static final Semaphore semaphore = new Semaphore(2); // Allows 2 permits

    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            new Thread(new Task(i)).start();
        }
    }

    static class Task implements Runnable {
        private final int id;

        Task(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                System.out.println("Thread " + id + " is trying to acquire a permit.");
                semaphore.acquire();
                System.out.println("Thread " + id + " has acquired a permit.");

                // Simulating some work
                Thread.sleep(2000);

                System.out.println("Thread " + id + " is releasing the permit.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                semaphore.release();
                System.out.println("Thread " + id + " has released the permit.");
            }
        }
    }
}
