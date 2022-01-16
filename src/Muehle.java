import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.net.URL;
import java.util.Arrays;

public class Muehle{
    boolean my_turn = false; // cant interact

    private static Cell[] cells = new Cell[24]; // cells in the game where stones can take place
    private Player p1; // X = white;
    private Player p2; // O = black;
    private int stage = 0;
    private int num_of_clicked_cells = 0;
    private Cell tmpCell1;
    private Cell tmpCell2;
    private boolean isMill = false;
    private boolean gameOver = false;

    private JFrame frame;
    private JLabel MAIN_LABEL;
    private JLabel abbruch;
    private JLabel neustart;
    private JTextArea label_p1;
    private JTextArea label_p2;
    private JTextArea label_cp;
    private JTextArea info;

    // 0 = initial stage
    // 1 = first stage
    // 2 = second stage
    // 3 = third stage

    public Muehle() {
        this.p1 = new Player("", label_p1, info, "", "");
        this.p2 = new Player("", label_p2, info, "", "");

        MAIN_LABEL = new JLabel();
        frame = new JFrame("The Mill Game");
        abbruch = new JLabel();
        neustart = new JLabel();
        label_p1 = new JTextArea();
        label_p2 = new JTextArea();
        label_cp = new JTextArea();
        info = new JTextArea();

        label_p1.setVisible(false);
        label_p2.setVisible(false);
        label_cp.setVisible(false);
        info.setVisible(false);

        if ((int)(Math.random()*2) == 0){ // player who start is randomly chosen
            setP1(p1);
            setP2(p2);
        } else {
            setP1(p2);
            setP2(p1);
        }
    }

    // METHODS //

    void start_game(Muehle m){
        boolean game_is_running = true; // if game is over it switches to false
        draw_field(m);
        setStage(1);
    }

    void first_stage(Cell c){
        System.out.println(p1.getStones_to_set() + "" + p2.getStones_to_set());
        if (p1.getStones_to_set() == 0 && p2.getStones_to_set() == 0){ // stage one ends and the second stage starts cause no stones to set left
            setStage(2);
            second_stage(c);
            update_label(label_p1, "Player black has:\n"+ p1.getStones_in_game() +" stones left in game");
            update_label(label_p2, "Player white has:\n"+ p2.getStones_in_game() +" stones left in game");
        } else{
            if (isMill){
                if (c.getStone() != null && c.getStone().getColor() != p1.getColor()){
                    if (!is_mill(c) || only_mills_in_game(p2)) {
                        p1.takeStone(c, true);
                        p2.setStones_in_game(p2.getStones_in_game() - 1);
                        update_label(p2.label, "Player " + p2.getColor() + " has:\n" + p2.getStones_in_game() + " stones left in game");
                        update_label(p1.label, "Player " + p1.getColor() + " has:\n" + p1.getStones_in_game() + " stones left in game");
                        isMill = false;
//                        switch_player();
                    } else {
                        update_label(info, "cant take stone because it is in a mill try again");
//                        System.out.println("cant take stone in cell " + c.getId());
                    }
                }
            }

            else if (p1.setStone(c, true)){
                update_label(p1.label, "Player "+ p1.getColor() +" has:\n" + p1.getStones_to_set() + " stones left to set");
                update_label(p2.label, "Player "+ p2.getColor() +" has:\n" + p2.getStones_to_set() + " stones left to set");
                if (is_mill(c)){ // mühle
                    isMill = true;
                    update_label(info, "Player " + p1.getColor() + " you have a mill! Pick a " + p2.getColor() + " stone");
                }
//                else{
//                    switch_player();
//                }
            } else {
                update_label(info, "Zelle darf nicht leer sein");
            }
        }
    }

    void second_stage(Cell c){
        if (p1.getStones_in_game() <= 3 || p2.getStones_in_game() <= 3){ // stage two ends and the third stage starts cause one of the players have only 3 stones left
            setStage(3);
        }

        if (isMill && (c.getStone() != null)) { // spieler hat mühle und pot weggenommene zelle ist nicht leer
            if (!c.getStone().getColor().equals(p1.getColor())) { // darf nicht eigener stein sein
                if (!is_mill(c) || only_mills_in_game(p2)){ // darf nicht aus mühle kommen außer wenn nur mühlen sind
                    p1.takeStone(c, true); // spieler nimmt stein des anderen spielers

                    p2.setStones_in_game(p2.getStones_in_game() - 1);
                    update_label(p2.label, "Player "+ p2.getColor() +" has:\n"+p2.getStones_in_game()+" stones left in game");
//                    switch_player(); // next player

                    isMill = false;
                    if (is_blocked(p2)) {
                        winning(p1.getColor(), true);
                    }
                }
            } else{
                update_label(info, "cant take stone because it is in a mill try again");
//                System.out.println("cant take stone in cell "+c.getId());
            }
        }

        else if (isValid(c)) {
            switch (num_of_clicked_cells) {
                case 0 : {
                    if (is_blocked(p1)) {
                        winning(p2.getColor(), true);
                    }
                    else if ((c.getStone() != null)) { // erste zelle darf nicht leer sein
                        num_of_clicked_cells += 1;
                        tmpCell1 = c;
//                        System.out.println("erste zelle ist " + c.getId());
                    }
                    else {
//                        System.out.println("erste zelle darf nicht leer sein");
                        update_label(info, "erste zelle darf nicht leer sein");

                    }
                }

                case 1 : {
                    if ((c != tmpCell1) && (c.isIs_empty())) { // nicht 2 mal selbe zelle und 2. zelle muss leer sein
                        System.out.println(p1.getStones_in_game() + "" + p2.getStones_in_game());
                        if (is_in_neigbors(tmpCell1, c) || p1.getStones_in_game() <= 3 || p2.getStones_in_game() <= 3) { // zelle darf nur 1 platz weiter
                            tmpCell2 = c;
//                            System.out.println("zweite zelle ist " + c.getId());
                            num_of_clicked_cells = 0; // zähler für start und end auf null

                            if (is_in_neigbors(tmpCell1, c)){
                                p1.moveStone(tmpCell1, tmpCell2, true); // bewegen
                            }
                            else if (p1.getStones_in_game() <= 3) {
                                if (tmpCell1.getStone().getColor() == p1.getColor()){
                                    p1.moveStone(tmpCell1, tmpCell2, true); // bewegen
                                }
                            }
                            else if (p2.getStones_in_game() <= 3){
                                if (tmpCell1.getStone().getColor() == p2.getColor()){
                                    p2.moveStone(tmpCell1, tmpCell2, true); // bewegen
                                }
                            }

                            if (is_mill(tmpCell2)) { // spieler hat mühle
                                isMill = true; // spieler nimmt stein des anderen spielers
//                                System.out.println("mühle");
                            }
//                            else { // nur wenn keine  mühle darf der nächste spieler dran
//                                switch_player();
//////                                System.out.println("keine mühle");
//                            }
                        }
                        else { // invalid try
//                            System.out.println("2. zelle muss nachbar der 1. sein");
                            update_label(info, "2. zelle muss nachbar der 1. sein");
                            num_of_clicked_cells = 0;
                        }
                    } else if (is_blocked(p1)) {
                        winning(p2.getColor(), true);
                    }
                    else {
//                        System.out.println("2. zelle draf nicht die 1. sein und muss leer sein");
                        update_label(info, "2. zelle draf nicht die 1. sein und muss leer sein");
                    }
                }
            }
        }
//        System.out.println("player black has "+p1.getStones_in_game()+" stones left");
//        System.out.println("player white has "+p2.getStones_in_game()+" stones left");

        update_label(p1.label, "Player "+ p1.getColor() +" has:\n"+p1.getStones_in_game()+" stones left in game");
    }

    void third_stage(Cell c){
        second_stage(c);
    }

    void update_icon(ImageIcon i, Cell c){
        c.getLabel().setIcon(i);
    }

    private boolean only_mills_in_game(Player p){ // checkt ob nur mühlen existieren
        for (Stone s : p.getStones()){
            if (s.getCell() != null) {
                if (!is_mill(s.getCell())) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isValid(Cell c){
        if (c.getStone() == null){
            return true;
        }
        if (p1.getColor() == c.getStone().getColor()) {
            return true;
        } else {
//            System.out.println("player "+current_player.getColor()+ " pls select ur stone");
            return false;
        }
    }

    boolean is_mill(Cell c){
        String color = c.getStone().getColor();

        for (String line : c.getLines()){
            int num_of_same_stones_in_line = 0;
            for (Cell tmpCell : cells){
                if (Arrays.asList(tmpCell.getLines()).contains(line) && !tmpCell.isIs_empty()){ // cell in cells has same line and cell cant be empty
                    if ((tmpCell.getStone().getColor().equals(color))){ // same stone color
                        num_of_same_stones_in_line++; // one stone more in line
//                        System.out.println("cell "+tmpCell.getId()+" has same color on line "+line);
                    }
                }
            }
            if (num_of_same_stones_in_line == 3) {
//                System.out.println("a mil on line " + line);
                update_label(info, "Player " + p1.getColor() + " you have a mill! Pick a " + p2.getColor() + " stone");
                return true;
            }

        }

        return false;
    }

    boolean is_game_running(){
        if ((p1.getStones_in_game() <= 2) && (this.stage != 1)){ // p2 is winning
            winning(p2.getColor(), true);
            gameOver = true;
            return false;
        } else if ((p2.getStones_in_game() <= 2) && (this.stage != 1)){ // p1 is winning
            winning(p1.getColor(), true);
            gameOver = true;
            return false;
        } else { // game is still running
            return true;
        }
    }

    private boolean is_blocked(Player p){
        return false;
//        for (Stone s : p.getStones()){
//            if (s.isIs_visible()){ // sichtbarere stein wird geprüft
//                if (s.getCell() == null) continue;
//                for (int i : s.getCell().getNeighbors()){
//                    if (cells[i].isIs_empty()){
////                        System.out.println("lel");
//                        return false;
//                    }
//                }
//            }
//        }
////        System.out.println("lol");
//        return true;
    }

    void winning(String color, boolean send){
        neustart.setVisible(true);
        for (Cell c : cells){
            c.getLabel().setIcon(null);
        }



        if (color.equals("white")){
            URL imgSmartURL = this.getClass().getResource("brett_gewinn_white.png");
            ImageIcon bg = new ImageIcon(imgSmartURL);
            MAIN_LABEL.setIcon(bg);
            if (send) {
                Client.send_data("method:win, player:" + "white");
            }
        } else {
            URL imgSmartURL = this.getClass().getResource("brett_gewinn_black.png");
            ImageIcon bg = new ImageIcon(imgSmartURL);
            MAIN_LABEL.setIcon(bg);
            if (send) {
                Client.send_data("method:win, player:" + "black");
            }
        }



        //        System.out.println("Congrats "+ p.getColor() +" you won the game!");
    }

    public void draw_field(Muehle m){
        int WIDTH = 720;
        int HEIGHT = 720;

        URL imgSmartURL = this.getClass().getResource("muehle_brett_mit_punkten.png");
        ImageIcon bg = new ImageIcon(imgSmartURL);
        MAIN_LABEL.setOpaque(true);
        MAIN_LABEL.setBackground(new Color(233, 220, 211));
        MAIN_LABEL.setIcon(bg);

        Border border = BorderFactory.createLineBorder(Color.BLACK, 1);     //determines frames

        abbruch.setBounds(10, 650, 100, 20);
        abbruch.setBorder(border);
        abbruch.setText("Aufgeben");
        abbruch.setVisible(true);
        MyMouseListener ml = new MyMouseListener(abbruch, p1.getColor(), this);
        abbruch.addMouseListener(ml);

        neustart.setBounds(10, 625, 100, 20);
        neustart.setBorder(border);
        neustart.setText("Neustart");
        neustart.setVisible(false);
        MyMouseListener ml_nst = new MyMouseListener(neustart, p1.getColor(), this);
        neustart.addMouseListener(ml_nst);

        //building infolabels
        label_p1.setBounds(WIDTH+10, 50,150,200);
        label_p1.setBorder(border);
        label_p1.setText("Player black has:\n9 stones left to set");
        label_p2.setBounds(WIDTH+10, 280,150,200);
        label_p2.setBorder(border);
        label_p2.setText("Player white has:\n9 stones left to set");
        label_cp.setBounds(WIDTH+10, 510, 150, 30);
        label_cp.setBorder(border);
        label_cp.setText("Player " + p1.getColor() + " its your turn");
        info.setBounds(WIDTH+10, 540,150,100);
        info.setBorder(border);
        info.setText("What's to do?");

        label_cp.setLineWrap(true);
        label_p2.setLineWrap(true);
        label_p2.setLineWrap(true);
        info.setLineWrap(true);

        label_cp.setEditable(false);
        label_p1.setEditable(false);
        label_p2.setEditable(false);
        info.setEditable(false);

        frame.getContentPane();
        frame.setSize(WIDTH+200, HEIGHT);

        int w = frame.getWidth();
        int h = frame.getHeight();
        int lh = 50;                                    //height of label set to 50px
        int lw = 50;                                    //width of label set to 50px

        init_cells(m, WIDTH, HEIGHT);

        for(int i = 0; i<=23; i++){
            MAIN_LABEL.add(cells[i].getLabel());
//            cells[i].getLabel().setOpaque(true);
//            cells[i].getLabel().setBorder(border);      //draws frame for every cell, needed for orientation
//            cells[i].getLabel().setBackground(new Color(233, 220, 211));
        }
        MAIN_LABEL.add(abbruch);
        MAIN_LABEL.add(neustart);
        MAIN_LABEL.add(label_p1);
        MAIN_LABEL.add(label_p2);
        MAIN_LABEL.add(label_cp);
        MAIN_LABEL.add(info);
        MAIN_LABEL.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(MAIN_LABEL);
        frame.setVisible(true);
    }

    public void init_cells(Muehle m, int w, int h){
        for (int i = 0; i < 24; i++){ // initialise cells as objects in the field cells
            cells[i] = new Cell(i, m); //this
        }

        for (int i = 0; i <= 23; i++){ // nur temporär um zellen zu identifizieren
            // cells[i].getLabel().setText(String.valueOf(i));
            MyMouseListener tmp_ml = new MyMouseListener(cells[i].getLabel(), cells[i], i, p1.getColor());
            cells[i].getLabel().addMouseListener(tmp_ml); // make labels clickable
        }

        set_neighbors_of_cells();
        set_cells_in_interface(w, h);
        set_lines_of_cells();
    }

    private static void set_cells_in_interface(int w, int h){
        int x = 10;
        int y = -20;
        int lw = 50;
        int lh = 50;
        cells[0].getLabel().setBounds(x+70, y+70,lw, lh);
        cells[1].getLabel().setBounds(x+(w/2)-(lw/2), y+70, lw, lh);
        cells[2].getLabel().setBounds(x+(w-70)-(lw), y+70,lw, lh);
        cells[3].getLabel().setBounds(x+(w/4)-(lw/2), y+(h/4)-(lh/2), lw, lh);
        cells[4].getLabel().setBounds(x+(w/2)-(lw/2), y+(h/4)-(lh/2), lw, lh);
        cells[5].getLabel().setBounds(x+(3*w/4)-(lw/2), y+(h/4)-(lh/2), lw, lh);
        cells[6].getLabel().setBounds(x+(3*w/8)-(lw/2), y+(3*h/8)-(lh/2), lw, lh);
        cells[7].getLabel().setBounds(x+(w/2)-(lw/2), y+(3*h/8)-(lh/2), lw, lh);
        cells[8].getLabel().setBounds(x+(5*w/8)-(lw/2), y+(3*h/8)-(lh/2), lw, lh);
        cells[9].getLabel().setBounds(x+70, y+(h/2)-(lh/2), lw, lh);
        cells[10].getLabel().setBounds(x+(w/4)-(lw/2), y+(h/2)-(lh/2), lw, lh);
        cells[11].getLabel().setBounds(x+(3*w/8)-(lw/2), y+(h/2)-(lh/2), lw, lh);
        cells[12].getLabel().setBounds(x+(5*w/8)-(lw/2), y+(h/2)-(lh/2), lw, lh);
        cells[13].getLabel().setBounds(x+(3*w/4)-(lw/2), y+(h/2)-(lh/2), lw, lh);
        cells[14].getLabel().setBounds(x+(w-70)-(lw), y+(h/2)-(lh/2), lw, lh);
        cells[15].getLabel().setBounds(x+(3*w/8)-(lw/2), y+(5*h/8)-(lh/2), lw, lh);
        cells[16].getLabel().setBounds(x+(w/2)-(lw/2), y+(5*h/8)-(lh/2), lw, lh);
        cells[17].getLabel().setBounds(x+(5*w/8)-(lw/2), y+(5*h/8)-(lh/2), lw, lh);
        cells[18].getLabel().setBounds(x+(w/4)-(lw/2), y+(3*h/4)-(lh/2), lw, lh);
        cells[19].getLabel().setBounds(x+(w/2)-(lw/2), y+(3*h/4)-(lh/2), lw, lh);
        cells[20].getLabel().setBounds(x+(3*w/4)-(lw/2), y+(3*h/4)-(lh/2), lw, lh);
        cells[21].getLabel().setBounds(x+70, (h-90)-(lh/2)+y, lw, lh);
        cells[22].getLabel().setBounds(x+(w/2)-(lw/2), y+(h-90)-(lh/2), lw, lh);
        cells[23].getLabel().setBounds(x+(w-70)-(lw), y+(h-90)-(lh/2), lw, lh);
    }

    private static void set_neighbors_of_cells(){
        cells[0].setNeighbors(new int[]{1, 9});
        cells[1].setNeighbors(new int[]{0, 2, 4});
        cells[2].setNeighbors(new int[]{1, 14});
        cells[3].setNeighbors(new int[]{4, 10});
        cells[4].setNeighbors(new int[]{1, 3, 5, 7});
        cells[5].setNeighbors(new int[]{4, 13});
        cells[6].setNeighbors(new int[]{7, 11});
        cells[7].setNeighbors(new int[]{4, 6, 8});
        cells[8].setNeighbors(new int[]{7, 12});
        cells[9].setNeighbors(new int[]{0, 10, 21});
        cells[10].setNeighbors(new int[]{3, 9, 11, 18});
        cells[11].setNeighbors(new int[]{6, 10, 15});
        cells[12].setNeighbors(new int[]{8, 13, 17});
        cells[13].setNeighbors(new int[]{5, 12, 14, 20});
        cells[14].setNeighbors(new int[]{2, 13, 23});
        cells[15].setNeighbors(new int[]{11, 16});
        cells[16].setNeighbors(new int[]{15, 17, 19});
        cells[17].setNeighbors(new int[]{12, 16});
        cells[18].setNeighbors(new int[]{10, 19});
        cells[19].setNeighbors(new int[]{16, 18, 20, 22});
        cells[20].setNeighbors(new int[]{13, 19});
        cells[21].setNeighbors(new int[]{9, 22});
        cells[22].setNeighbors(new int[]{19, 21, 23});
        cells[23].setNeighbors(new int[]{14, 22});
    }

    private static void set_lines_of_cells(){
        cells[0].setLines(new String[]{"a", "d"});
        cells[1].setLines(new String[]{"a", "m"});
        cells[2].setLines(new String[]{"a", "b"});
        cells[3].setLines(new String[]{"e", "h"});
        cells[4].setLines(new String[]{"e", "m"});
        cells[5].setLines(new String[]{"e", "f"});
        cells[6].setLines(new String[]{"i", "l"});
        cells[7].setLines(new String[]{"i", "m"});
        cells[8].setLines(new String[]{"i", "j"});
        cells[9].setLines(new String[]{"d", "p"});
        cells[10].setLines(new String[]{"h", "p"});
        cells[11].setLines(new String[]{"l", "p"});
        cells[12].setLines(new String[]{"j", "n"});
        cells[13].setLines(new String[]{"f", "n"});
        cells[14].setLines(new String[]{"b", "n"});
        cells[15].setLines(new String[]{"k", "l"});
        cells[16].setLines(new String[]{"k", "o"});
        cells[17].setLines(new String[]{"j", "k"});
        cells[18].setLines(new String[]{"g", "h"});
        cells[19].setLines(new String[]{"g", "o"});
        cells[20].setLines(new String[]{"f", "g"});
        cells[21].setLines(new String[]{"c", "d"});
        cells[22].setLines(new String[]{"c", "o"});
        cells[23].setLines(new String[]{"c", "b"});
    }


//    void switch_player(){
//        if (getCurrent_player() == getP1()){
//            setCurrent_player(getP2());
//            setNext_player(getP1());
//        } else {
//            setCurrent_player(getP1());
//            setNext_player(getP2());
//        }
//    }

    void close_game(){
        MAIN_LABEL.removeAll();
    }

    boolean is_in_neigbors(Cell cell_to_check, Cell cell){
//        for (Cell c : cells) System.out.println(c);
        for (int tmp_neigbor : cell.getNeighbors()){ // schaut alle nachbarn von cell_to_check an
            if (cell_to_check != null){
                if (tmp_neigbor == cell_to_check.getId()) { // cell_to_chek ist in nachbarn mit drin
                    return true;
                }
            }
        }
        return false;
    }

    static void update_label(JTextArea l, String s){
        l.setText(s);
    }

    // GETTER and SETTER //


    public void setStage(int stage) {
        this.stage = stage;
    }

    public Player getP1() {
        return p1;
    }

    public void setP1(Player p1) {
        this.p1 = p1;
    }

    public Player getP2() {
        return p2;
    }

    public void setP2(Player p2) {
        this.p2 = p2;
    }

    public int getStage() {
        return stage;
    }

    public static Cell[] getCells() {
        return cells;
    }

    public static void setCells(Cell[] cells) {
        Muehle.cells = cells;
    }

    public int getNum_of_clicked_cells() {
        return num_of_clicked_cells;
    }

    public void setNum_of_clicked_cells(int num_of_clicked_cells) {
        this.num_of_clicked_cells = num_of_clicked_cells;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public boolean isMy_turn() {
        // abfrage server //
        return my_turn;
    }

    public void setMy_turn(boolean my_turn) {
        this.my_turn = my_turn;
    }

    public Cell getTmpCell1() {
        return tmpCell1;
    }

    public void setTmpCell1(Cell tmpCell1) {
        this.tmpCell1 = tmpCell1;
    }

    public Cell getTmpCell2() {
        return tmpCell2;
    }

    public void setTmpCell2(Cell tmpCell2) {
        this.tmpCell2 = tmpCell2;
    }

    public boolean isMill() {
        return isMill;
    }

    public void setMill(boolean mill) {
        isMill = mill;
    }

    public JFrame getFrame() {
        return frame;
    }

    public void setFrame(JFrame frame) {
        this.frame = frame;
    }

    public JLabel getMAIN_LABEL() {
        return MAIN_LABEL;
    }

    public void setMAIN_LABEL(JLabel MAIN_LABEL) {
        this.MAIN_LABEL = MAIN_LABEL;
    }

    public JTextArea getLabel_p1() {
        return label_p1;
    }

    public void setLabel_p1(JTextArea label_p1) {
        this.label_p1 = label_p1;
    }

    public JTextArea getLabel_p2() {
        return label_p2;
    }

    public void setLabel_p2(JTextArea label_p2) {
        this.label_p2 = label_p2;
    }

    public JTextArea getLabel_cp() {
        return label_cp;
    }

    public void setLabel_cp(JTextArea label_cp) {
        this.label_cp = label_cp;
    }

    public JTextArea getInfo() {
        return info;
    }

    public void setInfo(JTextArea info) {
        this.info = info;
    }

    public JLabel getAbbruch() {
        return abbruch;
    }

    public void setAbbruch(JLabel abbruch) {
        this.abbruch = abbruch;
    }

    public JLabel getNeustart() {
        return neustart;
    }

    public void setNeustart(JLabel neustart) {
        this.neustart = neustart;
    }
}
