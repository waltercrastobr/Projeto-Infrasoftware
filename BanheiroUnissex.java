import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;

// Classe que representa o banheiro unissex
class Banheiro {
    private final int capacidadeMaxima; // Capacidade máxima do banheiro
    private int numeroPessoas; // Número de pessoas atualmente no banheiro
    private String generoAtual; // Gênero das pessoas atualmente no banheiro
    private final Semaphore semaphore; // Controle de acesso baseado na capacidade
    private final Lock lock; // Lock para controle de concorrência

    // Construtor do banheiro, inicializa os atributos
    public Banheiro(int capacidade) {
        this.capacidadeMaxima = capacidade;
        this.numeroPessoas = 0;
        this.generoAtual = null;
        this.semaphore = new Semaphore(capacidade, true);
        this.lock = new ReentrantLock(true);
    }

    // Método para uma pessoa tentar entrar no banheiro
    public void entrar(String generoPessoa, int id) throws InterruptedException {
        lock.lock();
        try {
            // Verifica se o banheiro está ocupado por outro gênero
            if (generoAtual != null && !generoPessoa.equals(generoAtual)) {
                System.out.println("Pessoa " + id + " (" + generoPessoa + ")" +
                        " não conseguiu entrar pois tem " + generoAtual + " lá dentro.");
            }
            // Espera até que haja espaço e o gênero seja compatível
            while (numeroPessoas == capacidadeMaxima ||
                    (generoAtual != null && !generoPessoa.equals(generoAtual))) {
                lock.unlock();
                Thread.sleep(50); // Aguarda antes de tentar novamente
                lock.lock();
            }
            semaphore.acquire(); // Adquire uma permissão para entrar
            numeroPessoas++;
            generoAtual = generoPessoa;
            System.out.println("Pessoa " + id + " (" + generoPessoa + ")" +
                    " entrou no banheiro (" + numeroPessoas + " de " + capacidadeMaxima + ")");
        } finally {
            lock.unlock();
        }
    }

    // Método para uma pessoa sair do banheiro
    public void sair(String generoPessoa, int id) {
        lock.lock();
        try {
            numeroPessoas--;
            System.out.println("Pessoa " + id + " (" + generoPessoa + ")" +
                    " saiu do banheiro (" + numeroPessoas + " de " + capacidadeMaxima + ")");
            // Se o banheiro ficar vazio, reseta o gênero atual
            if (numeroPessoas == 0) {
                generoAtual = null;
            }
            semaphore.release(); // Libera uma permissão
        } finally {
            lock.unlock();
        }
    }
}

// Classe que representa uma pessoa tentando usar o banheiro
class Pessoa implements Runnable {
    private final Banheiro banheiro;
    private final String generoPessoa;
    private int id;

    // Construtor da pessoa, inicializa os atributos
    public Pessoa(Banheiro banheiro, String generoPessoa, int id) {
        this.banheiro = banheiro;
        this.generoPessoa = generoPessoa;
        this.id = id;
    }

    // Método executado pela thread, simulando a entrada e saída do banheiro
    public void run() {
        try {
            banheiro.entrar(generoPessoa, id); // Tenta entrar no banheiro
            Thread.sleep((long) (Math.random() * 1000)); // Simula o tempo dentro do banheiro
            banheiro.sair(generoPessoa, id); // Sai do banheiro
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

// Classe principal para execução do programa
public class BanheiroUnissex {
    public static void main(String[] args) {
        Banheiro banheiro = new Banheiro(3); // Cria um banheiro com capacidade para 3 pessoas

        // Cria e inicia 10 threads, cada uma representando uma pessoa
        for (int i = 1; i <= 10; i++) {
            Pessoa pessoa = new Pessoa(banheiro, i % 2 == 0 ? "homem" : "mulher", i);
            new Thread(pessoa).start();
        }
    }
}
