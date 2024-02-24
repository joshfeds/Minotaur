import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Crystal {
    private static final int num_guests = 5;
    private static final boolean[] doneViewing = new boolean[num_guests];
    private static final Lock lock = new ReentrantLock();
    private static final Condition[] conditions = new Condition[num_guests];
    private static final Queue<Integer> order = new LinkedList<>();
    private static boolean isQueueEmpty = false;

    public static boolean getRandomBoolean() {
        return Math.random() < 0.5;
    }

    public static void enterShowroom(int index) throws InterruptedException {
        lock.lock();
        try {
            while (!doneViewing[index]) {

                while (order.peek() != null && order.peek() != index && !isQueueEmpty) {
                    conditions[index].await();
                }
                Integer value = order.poll();
                if (value == null) {
                    isQueueEmpty = true;
                    break;
                }
                System.out.println("Guest # " + index + " has seen the crystal and is now leaving.");
                boolean viewStatus = getRandomBoolean();
                if (viewStatus) {
                    System.out.println("Guest # " + index + " does not want to see the crystal again");
                    doneViewing[index] = true;
                } else {
                    System.out.println("Guest # " + index + " does want to see the crystal again");
                    order.add(index);
                }
                System.out.println();
                if (!order.isEmpty()) {
                    conditions[order.peek()].signal();
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < num_guests; i++) {
            conditions[i] = lock.newCondition();
        }

        Thread[] guests = new Thread[num_guests];
        for (int i = 0; i < num_guests; i++) {
            int index = i;
            guests[i] = new Thread(() -> {
                try {
                    enterShowroom(index);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        for (int i = 0; i < num_guests; i++) {
            order.add(i);
        }

        for (Thread thread : guests) {
            thread.start();
        }

        for (Thread thread : guests) {
            thread.join();
        }
        System.out.println("Everyone has seen the crystal however many times they wanted");
    }
}
