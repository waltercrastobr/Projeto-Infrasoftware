import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BanheiroUnissex {

    public static class Banheiro {
        private static final int CAPACIDADE_MAXIMA = 3;
        private int ocupacaoAtual = 0;
        private String generoAtual = "";
        private final Lock tranca = new ReentrantLock();
        private final Condition condicao = tranca.newCondition();

        // Método para gerenciar a entrada no banheiro
        public void entrar(Pessoa pessoa) throws InterruptedException {
            tranca.lock();
            try {
                // Espera enquanto houver pessoas de outro gênero no banheiro ou estiver lotado
                while (!generoAtual.isEmpty() && !generoAtual.equals(pessoa.obterGenero()) || ocupacaoAtual >= CAPACIDADE_MAXIMA) {
                    condicao.await();
                }
                ocupacaoAtual++;
                if (ocupacaoAtual == 1) {
                    generoAtual = pessoa.obterGenero(); // Define o gênero atual ao entrar a primeira pessoa
                }
                System.out.println(pessoa.obterNome() + " (" + pessoa.obterGenero() + ") entrou no banheiro.");
                System.out.println("Ocupação Atual:" + (ocupacaoAtual));
            } finally {
                tranca.unlock();
            }
        }

        // Método para gerenciar a saída do banheiro
        public void sair(Pessoa pessoa) {
            tranca.lock();
            try {
                ocupacaoAtual--;
                System.out.println(pessoa.obterNome() + " (" + pessoa.obterGenero() + ") saiu do banheiro.");
                System.out.println("Ocupação Atual:" + (ocupacaoAtual));
                if (ocupacaoAtual == 0) {
                    generoAtual = ""; // Reseta o gênero atual quando o banheiro fica vazio
                }
                condicao.signalAll(); // Acorda todas as threads aguardando
            } finally {
                tranca.unlock();
            }
        }
    }

    // Classe Pessoa com atributos nome e gênero
    public static class Pessoa {
        private final String nome;
        private final String genero;

        public Pessoa(String nome, String genero) {
            this.nome = nome;
            this.genero = genero;
        }

        public String obterNome() {
            return nome;
        }

        public String obterGenero() {
            return genero;
        }
    }

    // Classe Runnable para simular o uso do banheiro
    public static class UsoBanheiro implements Runnable {
        private final Banheiro banheiro;
        private final Pessoa pessoa;

        public UsoBanheiro(Banheiro banheiro, Pessoa pessoa) {
            this.banheiro = banheiro;
            this.pessoa = pessoa;
        }

        // Método run que gerencia a entrada, permanência e saída do banheiro
        public void run() {
            try {
                banheiro.entrar(pessoa);
                Thread.sleep((long) (Math.random() * 500)); // Simula o tempo de uso do banheiro
                banheiro.sair(pessoa);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Banheiro banheiro = new Banheiro();

        // Criação de 50 homens e 50 mulheres
        Pessoa[] pessoas = new Pessoa[100];
        for (int i = 0; i < 50; i++) {
            pessoas[i] = new Pessoa("Homem" + (i + 1), "Masculino");
            pessoas[i + 50] = new Pessoa("Mulher" + (i + 1), "Feminino");
        }

        // Criação e inicialização de threads para cada pessoa
        Thread[] threads = new Thread[pessoas.length];
        for (int i = 0; i < pessoas.length; i++) {
            threads[i] = new Thread(new UsoBanheiro(banheiro, pessoas[i]));
        }

        // Inicia todas as threads
        for (Thread thread : threads) {
            thread.start();
        }

        // Aguarda todas as threads terminarem
        for (Thread thread : threads) {
            thread.join();
        }

    }
}
