import java.util.*;
import java.util.concurrent.Semaphore;


public class Cupcake {
    public static int num_guests = 10;
    private static boolean[] eaten = new boolean[num_guests];
    private static int plateCount = 0;
    private static boolean cupcakeAvailable = true;
    private static int currentGuest;
    private static boolean isComplete = false;
    private static final Semaphore sem = new Semaphore(1);
    private static int totalEntered = 0;

    public static void enterLabyrinth() throws InterruptedException {
        int max = num_guests;
        int min = 0;
        totalEntered++;
        while (plateCount < num_guests) {

            sem.acquire();
            currentGuest = (int) ((Math.random() * (max - min)) + min);

            if (cupcakeAvailable && !eaten[currentGuest]) {
                cupcakeAvailable = false;
                eaten[currentGuest] = true;
                System.out.println("Guest #" + currentGuest + " ate the cupcake!");
            }

            if (currentGuest == 0 && !cupcakeAvailable) {
                plateCount++;
                cupcakeAvailable = true;
                System.out.println("The leader counts an empty plate and replaces it with a cupcake");

            }

            if(eaten[currentGuest] && cupcakeAvailable){
                System.out.println("Guest #" + currentGuest + " can't eat anymore");
            }
            sem.release();


        }

    }

    public static void main(String[] args) throws InterruptedException {

        Thread[] guests = new Thread[num_guests];

        List<Integer> randList = new ArrayList<>();
        for (int i = 0; i < num_guests; i++) {
            randList.add(i);
        }
        Collections.shuffle(randList);
        for (int i = 0; i < num_guests; i++) {
            int index = randList.get(i);
            guests[index] = new Thread(() -> {
                try {
                    enterLabyrinth();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        for (Thread thread : guests) {
            thread.start();
        }


        for (Thread thread : guests) {
            thread.join();
        }

        System.out.println("Everyone has eaten a cupcake, time to go home!");

    }
}
