import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Barbearia {

    public static class Barbeiro implements Runnable {
        private int numCadeiras;
        private Semaphore semaphoreCadeiras;
        private Semaphore semaphoreCorte;
        private Lock lock;
        private boolean barbeiro_dormindo;

        public Barbeiro(int numCadeiras) {
            this.numCadeiras = numCadeiras;
            this.semaphoreCadeiras = new Semaphore(numCadeiras, true); // Semaphore para controlar a quantidade de cadeiras na sala de espera
            this.semaphoreCorte = new Semaphore(1); // Semaphore para controlar a quantidade de cortes por vez
            this.lock = new ReentrantLock();
            this.barbeiro_dormindo = true;
        }


        public void cortar_cabelo(String cliente) throws InterruptedException {
            semaphoreCorte.acquire();

            System.out.println("Barbeiro está atendendo o " + cliente);
            Thread.sleep(2000); // Intervalo para simular o corte doa clientes
            System.out.println(cliente + " foi atendido!");

            semaphoreCorte.release();
        }

        public void acordar_barbeiro(String cliente) {
            lock.lock();
            try {
                System.out.println(cliente + " percebeu que o barbeiro estava dormindo e o acordou");
                this.barbeiro_dormindo = false;
            } finally {
                lock.unlock();
            }
        }

        public void entrar_sala_espera() throws InterruptedException {
            semaphoreCadeiras.acquire();
        }

        public void sair_sala_espera() throws InterruptedException {
            semaphoreCadeiras.release();
        }

        public boolean Dormindo() {
            lock.lock();
            try {
                return barbeiro_dormindo;
            } finally {
                lock.unlock();
            }
        }

        public void run() {
            System.out.println("O dono da barbearia está dormindo enquanto espera os clientes chegarem");
        }

    }

    public static class Cliente implements Runnable {
        private Barbeiro barbeiro;
        private String nome;

        public Cliente(Barbeiro barbeiro, String nome) {
            this.barbeiro = barbeiro;
            this.nome = nome;
        }

        public void run() {
            System.out.println(nome + " chegou na barbearia");
            if (barbeiro.Dormindo()) {
                barbeiro.acordar_barbeiro(nome);
            } else if (barbeiro.semaphoreCadeiras.availablePermits() == 0) {
                System.out.println(nome + " verificou que a sala de espera estava lotada e saiu da barbearia");
                return;
            }

            try {
                if (barbeiro.semaphoreCadeiras.availablePermits() == barbeiro.numCadeiras) {
                   System.out.println(nome + " sentou imediatamente para cortar o cabelo, pois não havia ninguém esperando");
                } else {
                    System.out.println(nome + " sentou na sala de espera");
                }
                barbeiro.entrar_sala_espera();
                System.out.println("Cadeiras disponíveis após a chegada do " + nome + ": " + barbeiro.semaphoreCadeiras.availablePermits());
                barbeiro.cortar_cabelo(nome);
                barbeiro.sair_sala_espera();
                System.out.println("Cadeiras disponíveis após atender " + nome + ": " + barbeiro.semaphoreCadeiras.availablePermits());
                if (barbeiro.semaphoreCadeiras.availablePermits() == barbeiro.numCadeiras) {
                System.out.println("Como não havia mais cortes sendo realizados e clientes esperando na sala de espera, o barbeiro foi dormir");
            }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Barbeiro barbeiro = new Barbeiro(6);  //Número de cadeiras = 6 pois são a 5 da sala de espera e a cadeira do corte de dentro
        Thread threadBarbeiro = new Thread(barbeiro);
        threadBarbeiro.start();

        // Criar 100 clientes para simulação
        for (int i = 1; i <= 100; i++) {
            Thread cliente = new Thread(new Cliente(barbeiro, "Cliente " + i));
            cliente.start();
            Thread.sleep((int) (Math.random() * 2000)); // Intervalo para simular chegada de novos clientes
        }

        threadBarbeiro.interrupt(); // Interrompe a thread do barbeiro após atender todos os clientes
    }
}
