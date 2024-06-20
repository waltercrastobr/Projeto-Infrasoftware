import java.util.concurrent.Semaphore;

public class ControlePonte {

    public static class Ponte {
        private final Semaphore semaforo = new Semaphore(1);

        public void entrarPonte(String direcao) throws InterruptedException {
            semaforo.acquire();
            System.out.println("Um " + Thread.currentThread().getName() + " está cruzando a ponte vindo da " + direcao);
        }

        public void sairPonte(String direcao) {
            String direcaoSaida = direcao.equals("esquerda") ? "direita" : "esquerda";
            System.out.println("O " + Thread.currentThread().getName() + " saiu da ponte em direção à " + direcaoSaida);
            semaforo.release();
        }
    }

    public static class Veiculo implements Runnable {
        private Ponte ponte;
        private String direcao;

        public Veiculo(Ponte ponte, String direcao) {
            this.ponte = ponte;
            this.direcao = direcao;
        }

        @Override
        public void run() {
            try {
                ponte.entrarPonte(direcao);
                Thread.sleep(3000); // Simula o tempo de atravessar a ponte
                ponte.sairPonte(direcao);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Ponte ponte = new Ponte();

        Thread veiculo1 = new Thread(new Veiculo(ponte, "esquerda"), "Argo");
        Thread veiculo2 = new Thread(new Veiculo(ponte, "direita"), "Onix");
        Thread veiculo3 = new Thread(new Veiculo(ponte, "esquerda"), "Kwid");
        Thread veiculo4 = new Thread(new Veiculo(ponte, "direita"), "Civic");
        Thread veiculo5 = new Thread(new Veiculo(ponte, "direita"), "Pulse");
        Thread veiculo6 = new Thread(new Veiculo(ponte, "esquerda"), "Nivus");
        Thread veiculo7 = new Thread(new Veiculo(ponte, "direita"), "Jetta");

        veiculo1.start();
        veiculo2.start();
        veiculo3.start();
        veiculo4.start();
        veiculo5.start();
        veiculo6.start();
        veiculo7.start();
    }
}
