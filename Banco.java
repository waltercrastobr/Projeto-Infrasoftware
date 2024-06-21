import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Banco {

    public static class Conta {
        private double saldoAtual;
        private final Lock trava = new ReentrantLock();

        public Conta(double saldoInicial) {
            this.saldoAtual = saldoInicial;
        }

        public void depositar(double valor) {
            trava.lock();
            try {
                saldoAtual += valor;
                System.out.println("NOME DO CLIENTE: " + Thread.currentThread().getName() + ", AÇÃO: " + " DEPOSITAR " + ", VALOR: " + valor + ", SALDO ATUAL: " + saldoAtual);
            } finally {
                trava.unlock();
            }
        }

        public void sacar(double valor) {
            trava.lock();
            try {
                if (valor <= saldoAtual) {
                    saldoAtual -= valor;
                    System.out.println("NOME DO CLIENTE: " + Thread.currentThread().getName() + ", AÇÃO: " + " SACAR " + ", VALOR: " + valor + ", SALDO ATUAL: " + saldoAtual);
                } else {
                    System.out.println("NOME DO CLIENTE: " + Thread.currentThread().getName() + " TENTOU SACAR: " + valor + ", SALDO INSUFICIENTE: " + saldoAtual);
                }
            } finally {
                trava.unlock();
            }
        }

        public double getSaldo() {
            return saldoAtual;
        }
    }

    public static class Cliente implements Runnable {
        private Conta conta;
        private boolean operacaoSaque;
        private double valor;

        public Cliente(Conta conta, boolean operacaoSaque, double valor) {
            this.conta = conta;
            this.operacaoSaque = operacaoSaque;
            this.valor = valor;
        }

        @Override
        public void run() {
            if (operacaoSaque) {
                conta.sacar(valor);
            } else {
                conta.depositar(valor);
            }
        }
    }

    public static void main(String[] args) {
        Conta conta = new Conta(1000);

        System.out.println("SALDO INICIAL: " + conta.getSaldo());
        System.out.println("---------------------------------------");

        Thread cliente1 = new Thread(new Cliente(conta, false, 300), "Walter");
        Thread cliente2 = new Thread(new Cliente(conta, true, 500), "Ivo");
        Thread cliente3 = new Thread(new Cliente(conta, true, 1000), "Guilherme");
        Thread cliente4 = new Thread(new Cliente(conta, false, 200), "Francisco");
        Thread cliente5 = new Thread(new Cliente(conta, true, 100), "Nilton");
        Thread cliente6 = new Thread(new Cliente(conta, false, 400), "Sofia");
        Thread cliente7 = new Thread(new Cliente(conta, true, 700), "Ana");
        Thread cliente8 = new Thread(new Cliente(conta, false, 600), "Pedro");

        cliente1.start();
        cliente2.start();
        cliente3.start();
        cliente4.start();
        cliente5.start();
        cliente6.start();
        cliente7.start();
        cliente8.start();
    }
}
