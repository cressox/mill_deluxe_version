public class tester {
    public static void main(String[] args) {
        Muehle m = new Muehle();

        if ((int)(Math.random()*2) == 0){ // player who start is randomly chosen
            m.setP1(m.getP1());
        } else {
            m.setP1(m.getP2());
        }

        m.start_game(m);

        }
}
