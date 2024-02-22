import java.util.*;
import java.util.concurrent.Semaphore;


public class Cupcake {
    public static int num_guests = 5;
    private static boolean[] eaten = new boolean[num_guests];
    private static int plateCount = 0;
    private static boolean cupcakeAvailable = true;
    private static int currentGuest;
    private static boolean isComplete = false;
    private static final Semaphore sem = new Semaphore(1);


    public static void enterLabyrinth(int index) throws InterruptedException {
        while(plateCount < num_guests){

            sem.acquire();

            if(cupcakeAvailable && !eaten[index]){
                cupcakeAvailable = false;
                eaten[index] = true;
                System.out.println("Guest #" + index + " ate the cupcake!");
            }

            if(currentGuest == 0 && !cupcakeAvailable){
                plateCount++;
                cupcakeAvailable = true;
                System.out.println("The leader counts an empty plate");
            }
            sem.release();
        }
        isComplete = true;

    }
    public static void main(String[] args) throws InterruptedException {

    Thread[] guests = new Thread[num_guests];
    int max = num_guests - 1;
    int min = 0;

    List<Integer> randList = new ArrayList<>();
    for(int i = 0; i < num_guests; i++) {
        randList.add(i);
    }
        Collections.shuffle(randList);
    for(int i = 0; i < num_guests; i++){

        int index = randList.get(i);


        guests[i] = new Thread(() -> {
            try {
                enterLabyrinth(index);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }


    for(Thread thread : guests){
        thread.start();
    }
    while(!isComplete){

        currentGuest = (int) ((Math.random() * (max - min)) + min);

    }

        System.out.println("Everyone has eaten a cupcake, time to go home!");

    }


}