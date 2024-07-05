import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Restaurante {
    private final Lock trava = new ReentrantLock();
    private final Condition lugarDisponivel = trava.newCondition();
    private final int capacidadeRestaurante;
    private int lugaresAtualmenteOcupados = 0;

    public Restaurante(int capacidadeRestaurante) {
        this.capacidadeRestaurante = capacidadeRestaurante;
    }

    public void entrarRestaurante(int idCliente) {
        trava.lock();
        try {
            // Cliente espera até que haja um lugar disponível
            while (lugaresAtualmenteOcupados == capacidadeRestaurante) {
                System.out.println("Cliente " + idCliente + " está esperando por um lugar.");
                lugarDisponivel.await();
            }

            // Cliente ocupa um lugar
            lugaresAtualmenteOcupados++;
            System.out.println("Cliente " + idCliente + " sentou no lugar. Lugares ocupados: " + lugaresAtualmenteOcupados);

            if (lugaresAtualmenteOcupados == capacidadeRestaurante) {
                // Se todos os lugares estão ocupados, simular o jantar dos clientes
                System.out.println("Todos os lugares estão ocupados. Clientes estão jantando.");
                trava.unlock(); // Liberar a trava antes de dormir para evitar bloquear outros clientes
                try {
                    Thread.sleep(3000); // Simula o tempo do jantar
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                trava.lock(); // Re-adquirir a trava antes de modificar lugaresAtualmenteOcupados
                lugaresAtualmenteOcupados = 0; // Todos os clientes terminaram de jantar
                lugarDisponivel.signalAll(); // Acordar todos os clientes esperando por um lugar
                System.out.println("Todos os clientes terminaram de jantar e deixaram o restaurante.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            trava.unlock(); // Garantir que a trava seja liberada
        }
    }
}

class ClienteRestaurante implements Runnable {
    private final Restaurante restaurante;
    private final int idCliente;

    public ClienteRestaurante(Restaurante restaurante, int idCliente) {
        this.restaurante = restaurante;
        this.idCliente = idCliente;
    }

    @Override
    public void run() {
        restaurante.entrarRestaurante(idCliente);
    }
}

public class SimulacaoRestaurante {
    public static void main(String[] args) {
        int capacidadeRestaurante = 5;
        Restaurante restaurante = new Restaurante(capacidadeRestaurante);

        // Criar 100 clientes para simulação
        Thread[] clientes = new Thread[100];
        for (int i = 0; i < 100; i++) {
            clientes[i] = new Thread(new ClienteRestaurante(restaurante, i + 1));
        }

        // Iniciar threads dos clientes com tempos de chegada aleatórios
        for (Thread cliente : clientes) {
            cliente.start();
            try {
                Thread.sleep((int) (Math.random() * 2000)); // Tempo de chegada aleatório para cada cliente
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
