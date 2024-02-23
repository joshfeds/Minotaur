
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class Crystal {
    private static final int num_guests = 5;
    private static final boolean[] doneViewing = new boolean[num_guests];
    private static final Semaphore sem = new Semaphore(1);
    private static final Queue<Integer> order = new LinkedList<>();
    public static boolean getRandomBoolean() {
        return Math.random() < 0.5;
    }
    public static void enterShowroom(int index) throws InterruptedException {

        while (!doneViewing[index]){
            sem.acquire();
            if(order.isEmpty()){
                sem.release();
                break;
            }
            int value = order.poll();

            System.out.println("Guest # " + value + " has seen the crystal and is now leaving.");
            boolean viewStatus = getRandomBoolean();

            if(viewStatus){
                System.out.println("Guest # " + value + " does not want to see the crystal again");
                doneViewing[value] = true;
            }
            else {
                System.out.println("Guest # " + value + " does want to see the crystal again");
                order.add(value);
            }

            sem.release();
        }



    }
    public static void main(String[] args) throws InterruptedException {
        Thread[] guests = new Thread[num_guests];


        for(int i = 0; i < num_guests; i++){
            int index = i;
            guests[i] = new Thread(() -> {
                try {
                    enterShowroom(index);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        for(int i = 0; i < num_guests; i++){
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
