import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Random;

public class SistemaTransporte {

    public static class Onibus {
        private static final Semaphore vagas = new Semaphore(0);
        private static final Semaphore acesso = new Semaphore(1);
        private final Lock tranca = new ReentrantLock();
        private Random geradorAleatorio = new Random();
        private int passageirosAguardando = 0;
        private int passageirosEmbarcados = 0;

        // Método para gerenciar a chegada dos passageiros ao ponto de ônibus
        public void esperarOnibus(String nome, int id) throws InterruptedException {
            System.out.println(nome + id + ": chegou ao ponto de ônibus.");
            acesso.acquire();
            acesso.release();

            // Bloqueia a seção crítica para atualizar a contagem de passageiros esperando
            tranca.lock();
            try {
                passageirosAguardando++;
                if (passageirosAguardando > 50) {
                    System.out.println(nome + id + " não conseguiu embarcar e terá que esperar o próximo ônibus.");
                }
            } finally {
                tranca.unlock();
            }

            vagas.acquire();

            // Bloqueia a seção crítica para atualizar a contagem de passageiros embarcados
            tranca.lock();
            try {
                System.out.println(nome + id + ": embarcou no ônibus.");
                passageirosEmbarcados++;
                passageirosAguardando--;
            } finally {
                tranca.unlock();
            }
        }

        // Método para coordenar a chegada e partida dos ônibus
        public void coordenarTransporte() throws InterruptedException {
            for (int i = 0; i < 100; i++) {
                int tempoEspera = geradorAleatorio.nextInt(2);
                Thread.sleep((tempoEspera + 1) * 1000);

                // O ônibus chega e tranca a catraca
                acesso.acquire();
                System.out.println("O ônibus chegou.");

                // Libera vagas no ônibus (capacidade máxima de 50) e simula o tempo de embarque
                vagas.release(50);
                Thread.sleep(1000);

                // O ônibus parte com os passageiros embarcados e ajusta as vagas disponíveis
                System.out.println("O ônibus está partindo com " + passageirosEmbarcados + " passageiros.");
                vagas.acquire(50 - passageirosEmbarcados);
                passageirosEmbarcados = 0;

                // Libera a catraca
                acesso.release();
            }
        }
    }

    // Classe Passageiro que implementa Runnable para ser executada por uma thread
    public static class Passageiro implements Runnable {
        private final Onibus onibus;
        private final String nome;
        private final int id;

        public Passageiro(Onibus onibus, String nome, int id) {
            this.onibus = onibus;
            this.nome = nome;
            this.id = id;
        }

        // Método run da thread Passageiro
        public void run() {
            if (nome.equals("Passageiro")) {
                try {
                    onibus.esperarOnibus(nome, id);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    onibus.coordenarTransporte();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        Onibus onibus = new Onibus();

        // Cria e inicia a thread do motorista
        Passageiro motorista = new Passageiro(onibus, "Motorista", 0);
        Thread motoristaThread = new Thread(motorista);
        motoristaThread.start();

        // Cria e inicia as threads dos passageiros
        for (int i = 0; i < 300; i++) {
            Passageiro passageiro = new Passageiro(onibus, "Passageiro", i + 1);
            Thread passageiroThread = new Thread(passageiro);
            passageiroThread.start();
            try {
                Thread.sleep(100);  // Simula a chegada dos passageiros ao ponto em intervalos regulares
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
