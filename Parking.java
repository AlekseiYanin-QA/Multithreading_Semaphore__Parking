import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class Parking {
    private static final boolean[] PARKING_PLACES = new boolean[5];
    private static final Semaphore semaphore = new Semaphore(PARKING_PLACES.length, true); // Позволяет 5 машинам парковаться
    private static final Random random = new Random();

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(10); // Пул для 10 машин

        // Запускаем 10 машин
        for (int i = 1; i <= 10; i++) {
            final int carNumber = i;
            executor.execute(new Car(carNumber));
        }

        executor.shutdown();
    }

    public static class Car implements Runnable {
        private final int carNumber;

        public Car(int carNumber) {
            this.carNumber = carNumber;
        }

        @Override
        public void run() {
            try {
                System.out.println("Car " + carNumber + " хочет припарковаться.");
                semaphore.acquire(); // Запрашиваем разрешение на парковку

                // Находим место и паркуемся
                parkCar(carNumber);

                // Симуляция времени, проведенного на парковке (случайное время от 1 до 5 секунд)
                int parkingTime = random.nextInt(5000) + 1000; // Время от 1 до 5 секунд
                Thread.sleep(parkingTime);
                System.out.println("Car " + carNumber + " провела " + (parkingTime / 1000) + " секунд на парковке.");

                // Убираем машину с парковки
                unparkCar(carNumber);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                semaphore.release(); // Освобождаем разрешение
            }
        }

        private void parkCar(int carNumber) {
            synchronized (PARKING_PLACES) {
                for (int i = 0; i < PARKING_PLACES.length; i++) {
                    if (!PARKING_PLACES[i]) { // Если место свободно
                        PARKING_PLACES[i] = true; // Занимаем место
                        System.out.println("Car " + carNumber + " припарковалась на месте " + (i + 1) + ".");
                        return;
                    }
                }
            }
        }

        private void unparkCar(int carNumber) {
            synchronized (PARKING_PLACES) {
                for (int i = 0; i < PARKING_PLACES.length; i++) {
                    if (PARKING_PLACES[i]) { // Если машина припаркована
                        PARKING_PLACES[i] = false; // Освобождаем место
                        System.out.println("Car " + carNumber + " уехала с места " + (i + 1) + ".");
                        return;
                    }
                }
            }
        }
    }
}
