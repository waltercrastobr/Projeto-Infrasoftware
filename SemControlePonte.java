public class SemControlePonte {

    public static class Ponte {
        public void atravessarPonte(String direcao) throws InterruptedException {
            System.out.println("Um " + Thread.currentThread().getName() + " está cruzando a ponte vindo da " + direcao);
            Thread.sleep(3000); // Simula o tempo de atravessar a ponte
            String direcaoSaida = direcao.equals("esquerda") ? "direita" : "esquerda";
            System.out.println("O " + Thread.currentThread().getName() + " terminou de cruzar a ponte e foi para a " + direcaoSaida);
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
                ponte.atravessarPonte(direcao);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Ponte ponte = new Ponte();

        Thread veiculo1 = new Thread(new Veiculo(ponte, "esquerda"), "Virtus");
        Thread veiculo2 = new Thread(new Veiculo(ponte, "direita"), "Hb20");
        Thread veiculo3 = new Thread(new Veiculo(ponte, "esquerda"), "Sandero");
        Thread veiculo4 = new Thread(new Veiculo(ponte, "direita"), "Logan");
        Thread veiculo5 = new Thread(new Veiculo(ponte, "esquerda"), "Gol");
        Thread veiculo6 = new Thread(new Veiculo(ponte, "direita"), "Polo");

        veiculo1.start();
        veiculo2.start();
        veiculo3.start();
        veiculo4.start();
        veiculo5.start();
        veiculo6.start();
    }
}
