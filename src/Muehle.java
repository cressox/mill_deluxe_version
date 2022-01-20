//import javax.swing.*;
//import java.net.URL;
//import java.util.Arrays;
//
//public class Muehle{
//    boolean my_turn = false; // cant interact
//
//    private static Cell[] cells = new Cell[24]; // cells in the game where stones can take place
//    private Player p1; // X = white;
//    private Player p2; // O = black;
//    private int stage = 0;
//    private int num_of_clicked_cells = 0;
//    private Cell tmpCell1;
//    private Cell tmpCell2;
//    private boolean isMill = false;
//    private boolean gameOver = false;
//
//    private JFrame frame;
//    private JLabel MAIN_LABEL;
//    private JLabel abbruch;
//    private JLabel neustart;
//    private JTextArea label_p1;
//    private JTextArea label_p2;
//    private JTextArea label_cp;
//    private JTextArea info;
//
//    // 0 = initial stage
//    // 1 = first stage
//    // 2 = second stage
//    // 3 = third stage
//
//    public Muehle() {
//        this.p1 = new Player(0,"", "", "");
//        this.p2 = new Player(1,"", "", "");
//
//        MAIN_LABEL = new JLabel();
//        frame = new JFrame("The Mill Game");
//        abbruch = new JLabel();
//        neustart = new JLabel();
//        label_p1 = new JTextArea();
//        label_p2 = new JTextArea();
//        label_cp = new JTextArea();
//        info = new JTextArea();
//
//        label_p1.setVisible(false);
//        label_p2.setVisible(false);
//        label_cp.setVisible(false);
//        info.setVisible(false);
//
//        if ((int)(Math.random()*2) == 0){ // player who start is randomly chosen
//            setP1(p1);
//            setP2(p2);
//        } else {
//            setP1(p2);
//            setP2(p1);
//        }
//    }
//
//    // METHODS //
//
//    void start_game(Muehle m){
//        boolean game_is_running = true; // if game is over it switches to false
//        setStage(1);
//    }
//
//    void first_stage(Cell c){
//        System.out.println(p1.getStones_to_set() + "" + p2.getStones_to_set());
//        if (p1.getStones_to_set() == 0 && p2.getStones_to_set() == 0){ // stage one ends and the second stage starts cause no stones to set left
//            setStage(2);
//            second_stage(c);
//            update_label(label_p1, "Player black has:\n"+ p1.getStones_in_game() +" stones left in game");
//            update_label(label_p2, "Player white has:\n"+ p2.getStones_in_game() +" stones left in game");
//        } else{
//            if (isMill){
//                if (c.getStone() != null && c.getStone().getColor() != p1.getColor()){
//                    if (!is_mill(c) || only_mills_in_game(p2)) {
//                        p1.take_stone(c);
//                        p2.setStones_in_game(p2.getStones_in_game() - 1);
//                        update_label(p2.label, "Player " + p2.getColor() + " has:\n" + p2.getStones_in_game() + " stones left in game");
//                        update_label(p1.label, "Player " + p1.getColor() + " has:\n" + p1.getStones_in_game() + " stones left in game");
//                        isMill = false;
////                        switch_player();
//                    } else {
//                        update_label(info, "cant take stone because it is in a mill try again");
////                        System.out.println("cant take stone in cell " + c.getId());
//                    }
//                }
//            }
//
//            else if (p1.set_stone(c)){
//                update_label(p1.label, "Player "+ p1.getColor() +" has:\n" + p1.getStones_to_set() + " stones left to set");
//                update_label(p2.label, "Player "+ p2.getColor() +" has:\n" + p2.getStones_to_set() + " stones left to set");
//                if (is_mill(c)){ // mühle
//                    isMill = true;
//                    update_label(info, "Player " + p1.getColor() + " you have a mill! Pick a " + p2.getColor() + " stone");
//                }
////                else{
////                    switch_player();
////                }
//            } else {
//                update_label(info, "Zelle darf nicht leer sein");
//            }
//        }
//    }
//
//    void second_stage(Cell c){
//        if (p1.getStones_in_game() <= 3 || p2.getStones_in_game() <= 3){ // stage two ends and the third stage starts cause one of the players have only 3 stones left
//            setStage(3);
//        }
//
//        if (isMill && (c.getStone() != null)) { // spieler hat mühle und pot weggenommene zelle ist nicht leer
//            if (!c.getStone().getColor().equals(p1.getColor())) { // darf nicht eigener stein sein
//                if (!is_mill(c) || only_mills_in_game(p2)){ // darf nicht aus mühle kommen außer wenn nur mühlen sind
//                    p1.take_stone(c); // spieler nimmt stein des anderen spielers
//
//                    p2.setStones_in_game(p2.getStones_in_game() - 1);
//                    update_label(p2.label, "Player "+ p2.getColor() +" has:\n"+p2.getStones_in_game()+" stones left in game");
////                    switch_player(); // next player
//
//                    isMill = false;
//                    if (is_blocked(p2)) {
//                        winning(p1.getColor(), true);
//                    }
//                }
//            } else{
//                update_label(info, "cant take stone because it is in a mill try again");
////                System.out.println("cant take stone in cell "+c.getId());
//            }
//        }
//
//        else if (isValid(c)) {
//            switch (num_of_clicked_cells) {
//                case 0 : {
//                    if (is_blocked(p1)) {
//                        winning(p2.getColor(), true);
//                    }
//                    else if ((c.getStone() != null)) { // erste zelle darf nicht leer sein
//                        num_of_clicked_cells += 1;
//                        tmpCell1 = c;
////                        System.out.println("erste zelle ist " + c.getId());
//                    }
//                    else {
////                        System.out.println("erste zelle darf nicht leer sein");
//                        update_label(info, "erste zelle darf nicht leer sein");
//
//                    }
//                }
//
//                case 1 : {
//                    if ((c != tmpCell1) && (c.isIs_empty())) { // nicht 2 mal selbe zelle und 2. zelle muss leer sein
//                        System.out.println(p1.getStones_in_game() + "" + p2.getStones_in_game());
//                        if (is_in_neigbors(tmpCell1, c) || p1.getStones_in_game() <= 3 || p2.getStones_in_game() <= 3) { // zelle darf nur 1 platz weiter
//                            tmpCell2 = c;
////                            System.out.println("zweite zelle ist " + c.getId());
//                            num_of_clicked_cells = 0; // zähler für start und end auf null
//
//                            if (is_in_neigbors(tmpCell1, c)){
//                                p1.move_stone(tmpCell1, tmpCell2); // bewegen
//                            }
//                            else if (p1.getStones_in_game() <= 3) {
//                                if (tmpCell1.getStone().getColor() == p1.getColor()){
//                                    p1.move_stone(tmpCell1, tmpCell2); // bewegen
//                                }
//                            }
//                            else if (p2.getStones_in_game() <= 3){
//                                if (tmpCell1.getStone().getColor() == p2.getColor()){
//                                    p2.move_stone(tmpCell1, tmpCell2); // bewegen
//                                }
//                            }
//
//                            if (is_mill(tmpCell2)) { // spieler hat mühle
//                                isMill = true; // spieler nimmt stein des anderen spielers
////                                System.out.println("mühle");
//                            }
////                            else { // nur wenn keine  mühle darf der nächste spieler dran
////                                switch_player();
////////                                System.out.println("keine mühle");
////                            }
//                        }
//                        else { // invalid try
////                            System.out.println("2. zelle muss nachbar der 1. sein");
//                            update_label(info, "2. zelle muss nachbar der 1. sein");
//                            num_of_clicked_cells = 0;
//                        }
//                    } else if (is_blocked(p1)) {
//                        winning(p2.getColor(), true);
//                    }
//                    else {
////                        System.out.println("2. zelle draf nicht die 1. sein und muss leer sein");
//                        update_label(info, "2. zelle draf nicht die 1. sein und muss leer sein");
//                    }
//                }
//            }
//        }
////        System.out.println("player black has "+p1.getStones_in_game()+" stones left");
////        System.out.println("player white has "+p2.getStones_in_game()+" stones left");
//
//        update_label(p1.label, "Player "+ p1.getColor() +" has:\n"+p1.getStones_in_game()+" stones left in game");
//    }
//
//    void third_stage(Cell c){
//        second_stage(c);
//    }
//
//    private boolean only_mills_in_game(Player p){ // checkt ob nur mühlen existieren
//        for (Stone s : p.getStones()){
//            if (s.getCell() != null) {
//                if (!is_mill(s.getCell())) {
//                    return false;
//                }
//            }
//        }
//        return true;
//    }
//
//    private boolean isValid(Cell c){
//        if (c.getStone() == null){
//            return true;
//        }
//        if (p1.getColor() == c.getStone().getColor()) {
//            return true;
//        } else {
////            System.out.println("player "+current_player.getColor()+ " pls select ur stone");
//            return false;
//        }
//    }
//
//    boolean is_mill(Cell c){
//        String color = c.getStone().getColor();
//
//        for (String line : c.getLines()){
//            int num_of_same_stones_in_line = 0;
//            for (Cell tmpCell : cells){
//                if (Arrays.asList(tmpCell.getLines()).contains(line) && !tmpCell.isIs_empty()){ // cell in cells has same line and cell cant be empty
//                    if ((tmpCell.getStone().getColor().equals(color))){ // same stone color
//                        num_of_same_stones_in_line++; // one stone more in line
////                        System.out.println("cell "+tmpCell.getId()+" has same color on line "+line);
//                    }
//                }
//            }
//            if (num_of_same_stones_in_line == 3) {
////                System.out.println("a mil on line " + line);
//                update_label(info, "Player " + p1.getColor() + " you have a mill! Pick a " + p2.getColor() + " stone");
//                return true;
//            }
//
//        }
//
//        return false;
//    }
//
//    boolean is_game_running(){
//        if ((p1.getStones_in_game() <= 2) && (this.stage != 1)){ // p2 is winning
//            winning(p2.getColor(), true);
//            gameOver = true;
//            return false;
//        } else if ((p2.getStones_in_game() <= 2) && (this.stage != 1)){ // p1 is winning
//            winning(p1.getColor(), true);
//            gameOver = true;
//            return false;
//        } else { // game is still running
//            return true;
//        }
//    }
//
//    private boolean is_blocked(Player p){
//        return false;
////        for (Stone s : p.getStones()){
////            if (s.isIs_visible()){ // sichtbarere stein wird geprüft
////                if (s.getCell() == null) continue;
////                for (int i : s.getCell().getNeighbors()){
////                    if (cells[i].isIs_empty()){
//////                        System.out.println("lel");
////                        return false;
////                    }
////                }
////            }
////        }
//////        System.out.println("lol");
////        return true;
//    }
//
//    void winning(String color, boolean send){
//        neustart.setVisible(true);
//        for (Cell c : cells){
//            c.getLabel().setIcon(null);
//        }
//
//        if (color.equals("white")){
//            URL imgSmartURL = this.getClass().getResource("Assets/brett_gewinn_white.png");
//            ImageIcon bg = new ImageIcon(imgSmartURL);
//            MAIN_LABEL.setIcon(bg);
//            if (send) {
//                Client.send_data("method:win, player:" + "white");
//            }
//        } else {
//            URL imgSmartURL = this.getClass().getResource("Assets/brett_gewinn_black.png");
//            ImageIcon bg = new ImageIcon(imgSmartURL);
//            MAIN_LABEL.setIcon(bg);
//            if (send) {
//                Client.send_data("method:win, player:" + "black");
//            }
//        }
//    }
//
//    boolean is_in_neigbors(Cell cell_to_check, Cell cell){
//        for (int tmp_neigbor : cell.getNeighbors()){ // schaut alle nachbarn von cell_to_check an
//            if (cell_to_check != null){
//                if (tmp_neigbor == cell_to_check.getId()) { // cell_to_chek ist in nachbarn mit drin
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
//
//    static void update_label(JTextArea l, String s){
//        l.setText(s);
//    }
//
//    // GETTER and SETTER //
//
//
//    public void setStage(int stage) {
//        this.stage = stage;
//    }
//
//    public Player getP1() {
//        return p1;
//    }
//
//    public void setP1(Player p1) {
//        this.p1 = p1;
//    }
//
//    public Player getP2() {
//        return p2;
//    }
//
//    public void setP2(Player p2) {
//        this.p2 = p2;
//    }
//
//    public int getStage() {
//        return stage;
//    }
//
//    public static Cell[] getCells() {
//        return cells;
//    }
//
//    public static void setCells(Cell[] cells) {
//        Muehle.cells = cells;
//    }
//
//    public int getNum_of_clicked_cells() {
//        return num_of_clicked_cells;
//    }
//
//    public void setNum_of_clicked_cells(int num_of_clicked_cells) {
//        this.num_of_clicked_cells = num_of_clicked_cells;
//    }
//
//    public boolean isGameOver() {
//        return gameOver;
//    }
//
//    public void setGameOver(boolean gameOver) {
//        this.gameOver = gameOver;
//    }
//
//    public boolean isMy_turn() {
//        // abfrage server //
//        return my_turn;
//    }
//
//    public void setMy_turn(boolean my_turn) {
//        this.my_turn = my_turn;
//    }
//
//    public Cell getTmpCell1() {
//        return tmpCell1;
//    }
//
//    public void setTmpCell1(Cell tmpCell1) {
//        this.tmpCell1 = tmpCell1;
//    }
//
//    public Cell getTmpCell2() {
//        return tmpCell2;
//    }
//
//    public void setTmpCell2(Cell tmpCell2) {
//        this.tmpCell2 = tmpCell2;
//    }
//
//    public boolean isMill() {
//        return isMill;
//    }
//
//    public void setMill(boolean mill) {
//        isMill = mill;
//    }
//
//    public JFrame getFrame() {
//        return frame;
//    }
//
//    public void setFrame(JFrame frame) {
//        this.frame = frame;
//    }
//
//    public JLabel getMAIN_LABEL() {
//        return MAIN_LABEL;
//    }
//
//    public void setMAIN_LABEL(JLabel MAIN_LABEL) {
//        this.MAIN_LABEL = MAIN_LABEL;
//    }
//
//    public JTextArea getLabel_p1() {
//        return label_p1;
//    }
//
//    public void setLabel_p1(JTextArea label_p1) {
//        this.label_p1 = label_p1;
//    }
//
//    public JTextArea getLabel_p2() {
//        return label_p2;
//    }
//
//    public void setLabel_p2(JTextArea label_p2) {
//        this.label_p2 = label_p2;
//    }
//
//    public JTextArea getLabel_cp() {
//        return label_cp;
//    }
//
//    public void setLabel_cp(JTextArea label_cp) {
//        this.label_cp = label_cp;
//    }
//
//    public JTextArea getInfo() {
//        return info;
//    }
//
//    public void setInfo(JTextArea info) {
//        this.info = info;
//    }
//
//    public JLabel getAbbruch() {
//        return abbruch;
//    }
//
//    public void setAbbruch(JLabel abbruch) {
//        this.abbruch = abbruch;
//    }
//
//    public JLabel getNeustart() {
//        return neustart;
//    }
//
//    public void setNeustart(JLabel neustart) {
//        this.neustart = neustart;
//    }
//}
