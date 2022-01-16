//import javax.swing.*;
//import javax.swing.border.Border;
//import java.awt.*;
//import java.util.Arrays;
//
//public class Muehle {
//    private static Cell[] cells = new Cell[24]; // cells in the game where stones can take place
//    private static Player p1 = new Player(0); // 0 = white;
//    private static Player p2 = new Player(1); // 1 = black;
//    private int stage = 0;
//    private Player current_player = null;
//    private int num_of_clicked_cells = 0;
//    private Cell tmpCell1;
//    private Cell tmpCell2;
//    private boolean isMill = false;
//
//    // 0 = initial stage
//    // 1 = first stage
//    // 2 = second stage
//    // 3 = third stage
//
//    // METHODS //
//
//    void start_game(Muehle m){
//        boolean game_is_running = true; // if game is over it switches to false
//        draw_field(m);
//        setStage(1);
//    }
//
//    void first_stage(Cell c){
//        if (p1.getStones_to_set() == 0 && p2.getStones_to_set() == 0){ // stage one ends and the second stage starts cause no stones to set left
//            setStage(2);
//            second_stage(c);
//        } else{
//            current_player.setStone(c);
//        }
//
//
//    }
//
//    void second_stage(Cell c){
//        if ((c.getStone() != null) || (num_of_clicked_cells == 1)){ // erste geklickzte zelle darf nicht leer sein
//            if (isMill){ // spieler hat mühle
//                current_player.takeStone(c); // spieler nimmt stein des anderen spielers
//                switch_current_player(); // next player
//                isMill = false;
//            }
//
//            if (p1.getStones_in_game() <= 3 || p2.getStones_in_game() <= 3){ // stage two ends and the third stage starts cause one of the players have only 3 stones left
//                setStage(3);
//            }
//
//            if (num_of_clicked_cells == 0){ // start zelle wird gesetzt
//                num_of_clicked_cells += 1;
//                tmpCell1 = c;
//                System.out.println("erste zelle ist "+c.getId());
//
//            } else if (num_of_clicked_cells == 1 && c != tmpCell1 && c.isIs_empty()) { // end zelle wird gesetzt und darf nicht start zelle sein
//
//                if (is_in_neigbors(tmpCell1, c)){ // zelle darf nur 1 platz weiter
//                    tmpCell2 = c;
//                    System.out.println("zweite zelle ist "+c.getId());
//                    num_of_clicked_cells = 0; // zähler für start und end auf null
//
//                    current_player.moveStone(tmpCell1, tmpCell2); // bewegen
//
//                    if (is_mill(tmpCell2)){ // spieler hat mühle
//                        isMill = true; // spieler nimmt stein des anderen spielers
//                    } else { // nur wenn keine  mühle darf der nächste spieler dran
//                        switch_current_player();
//                    }
//                } else { // invalid try
//                    num_of_clicked_cells = 0;
//                }
//            }
//        }
//
//
//    }
//
//    void third_stage(Cell c){
//
//        // zellen sind klickbar springen //
//
//    }
//
//    private boolean is_mill(Cell c){
//        int color = c.getStone().getColor();
//        boolean mill = true;
//
//        for (String line : c.getLines()){
//            for (Cell tmpCell : cells){
//                if (Arrays.asList(tmpCell.getLines()).contains(line) && !tmpCell.isIs_empty()){ // cell in cells has same line and cell cant be empty
//                    if ((tmpCell.getStone().getColor() != color)){ // not same stone color
//                        System.out.println("not a mil on line " + line);
//                        mill = false;
//                        break;
//                    }
//                }
//            }
//            if (mill) {
//                System.out.println("a mil on line " + line);
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
//            winning(p2);
//            return false;
//
//        } else if ((p2.getStones_in_game() <= 2) && (this.stage != 1)){ // p1 is winning
//            winning(p1);
//            return false;
//
//        } else { // game is still running
//            return true;
//        }
//    }
//
//    private static void winning(Player p){
//        System.out.println("Congrats "+ p +" you won the game!");
//    }
//
//    public void draw_field(Muehle m){
//        JFrame frame = new JFrame("The Mill Game");
//        JPanel panel = new JPanel();
//        int WIDTH = 720;
//        int HEIGHT = 720;
//
//        frame.getContentPane();
//        frame.setSize(WIDTH, HEIGHT);
//        Border border = BorderFactory.createLineBorder(Color.BLACK, 1);     //determines frames
//
//        int w = frame.getWidth();
//        int h = frame.getHeight();
//        int lh = 50;                                    //height of label set to 50px
//        int lw = 50;                                    //width of label set to 50px
//
//        init_cells(m, WIDTH, HEIGHT);
//        //draw_rects();                                 //don't know yet if needed
//
//        panel.setLayout(null);
//
//        for(int i = 0; i<=23; i++){
//            panel.add(cells[i].getLabel());
//            cells[i].getLabel().setBorder(border);      //draws frame for every cell, needed for orientation
//        }
//
//        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        frame.add(panel);
//        frame.setVisible(true);
//    }
//
//    public void init_cells(Muehle m, int w, int h){
//        for (int i = 0; i < 24; i++){ // initialise cells as objects in the field cells
//            cells[i] = new Cell(i, m);
//        }
//
//        for (int i = 0; i <= 23; i++){ // nur temporär um zellen zu identifizieren
//            // cells[i].getLabel().setText(String.valueOf(i));
//            MyMouseListener tmp_ml = new MyMouseListener(cells[i].getLabel(), cells[i], i);
//            cells[i].getLabel().addMouseListener(tmp_ml); // make labels clickable
//        }
//
//        set_neighbors_of_cells();
//        set_cells_in_interface(w, h);
//        set_lines_of_cells();
//    }
//
//    private static void set_cells_in_interface(int w, int h){
//        int lw = 50;
//        int lh = 50;
//        cells[0].getLabel().setBounds(70, 70,lw, lh);
//        cells[1].getLabel().setBounds((w/2)-(lw/2), 70, lw, lh);
//        cells[2].getLabel().setBounds((w-70)-(lw), 70,lw, lh);
//        cells[3].getLabel().setBounds((w/4)-(lw/2), (h/4)-(lh/2), lw, lh);
//        cells[4].getLabel().setBounds((w/2)-(lw/2), (h/4)-(lh/2), lw, lh);
//        cells[5].getLabel().setBounds((3*w/4)-(lw/2), (h/4)-(lh/2), lw, lh);
//        cells[6].getLabel().setBounds((3*w/8)-(lw/2), (3*h/8)-(lh/2), lw, lh);
//        cells[7].getLabel().setBounds((w/2)-(lw/2), (3*h/8)-(lh/2), lw, lh);
//        cells[8].getLabel().setBounds((5*w/8)-(lw/2), (3*h/8)-(lh/2), lw, lh);
//        cells[9].getLabel().setBounds(70, (h/2)-(lh/2), lw, lh);
//        cells[10].getLabel().setBounds((w/4)-(lw/2), (h/2)-(lh/2), lw, lh);
//        cells[11].getLabel().setBounds((3*w/8)-(lw/2), (h/2)-(lh/2), lw, lh);
//        cells[12].getLabel().setBounds((5*w/8)-(lw/2), (h/2)-(lh/2), lw, lh);
//        cells[13].getLabel().setBounds((3*w/4)-(lw/2), (h/2)-(lh/2), lw, lh);
//        cells[14].getLabel().setBounds((w-70)-(lw), (h/2)-(lh/2), lw, lh);
//        cells[15].getLabel().setBounds((3*w/8)-(lw/2), (5*h/8)-(lh/2), lw, lh);
//        cells[16].getLabel().setBounds((w/2)-(lw/2), (5*h/8)-(lh/2), lw, lh);
//        cells[17].getLabel().setBounds((5*w/8)-(lw/2), (5*h/8)-(lh/2), lw, lh);
//        cells[18].getLabel().setBounds((w/4)-(lw/2), (3*h/4)-(lh/2), lw, lh);
//        cells[19].getLabel().setBounds((w/2)-(lw/2), (3*h/4)-(lh/2), lw, lh);
//        cells[20].getLabel().setBounds((3*w/4)-(lw/2), (3*h/4)-(lh/2), lw, lh);
//        cells[21].getLabel().setBounds(70, (h-90)-(lh/2), lw, lh);
//        cells[22].getLabel().setBounds((w/2)-(lw/2), (h-90)-(lh/2), lw, lh);
//        cells[23].getLabel().setBounds((w-70)-(lw), (h-90)-(lh/2), lw, lh);
//    }
//
//    private static void set_neighbors_of_cells(){
//        cells[0].setNeighbors(new int[]{1, 9});
//        cells[1].setNeighbors(new int[]{0, 2, 4});
//        cells[2].setNeighbors(new int[]{1, 14});
//        cells[3].setNeighbors(new int[]{4, 10});
//        cells[4].setNeighbors(new int[]{3, 5, 7});
//        cells[5].setNeighbors(new int[]{4, 13});
//        cells[6].setNeighbors(new int[]{7, 11});
//        cells[7].setNeighbors(new int[]{4, 6, 8});
//        cells[8].setNeighbors(new int[]{7, 12});
//        cells[9].setNeighbors(new int[]{0, 10, 21});
//        cells[10].setNeighbors(new int[]{3, 9, 11, 18});
//        cells[11].setNeighbors(new int[]{6, 10, 15});
//        cells[12].setNeighbors(new int[]{8, 13, 17});
//        cells[13].setNeighbors(new int[]{5, 12, 14, 20});
//        cells[14].setNeighbors(new int[]{2, 23});
//        cells[15].setNeighbors(new int[]{11, 16});
//        cells[16].setNeighbors(new int[]{15, 17});
//        cells[17].setNeighbors(new int[]{12, 16});
//        cells[18].setNeighbors(new int[]{10, 19});
//        cells[19].setNeighbors(new int[]{16, 18, 20, 22});
//        cells[20].setNeighbors(new int[]{13, 19});
//        cells[21].setNeighbors(new int[]{15, 22});
//        cells[22].setNeighbors(new int[]{21, 23});
//        cells[23].setNeighbors(new int[]{14, 22});
//    }
//
//    private static void set_lines_of_cells(){
//        cells[0].setLines(new String[]{"a", "d"});
//        cells[1].setLines(new String[]{"a", "m"});
//        cells[2].setLines(new String[]{"a", "b"});
//        cells[3].setLines(new String[]{"e", "h"});
//        cells[4].setLines(new String[]{"e", "m"});
//        cells[5].setLines(new String[]{"e", "f"});
//        cells[6].setLines(new String[]{"i", "l"});
//        cells[7].setLines(new String[]{"i", "m"});
//        cells[8].setLines(new String[]{"i", "j"});
//        cells[9].setLines(new String[]{"d", "p"});
//        cells[10].setLines(new String[]{"h", "p"});
//        cells[11].setLines(new String[]{"l", "p"});
//        cells[12].setLines(new String[]{"i", "n"});
//        cells[13].setLines(new String[]{"f", "n"});
//        cells[14].setLines(new String[]{"b", "n"});
//        cells[15].setLines(new String[]{"k", "l"});
//        cells[16].setLines(new String[]{"k", "o"});
//        cells[17].setLines(new String[]{"i", "k"});
//        cells[18].setLines(new String[]{"g", "h"});
//        cells[19].setLines(new String[]{"g", "o"});
//        cells[20].setLines(new String[]{"f", "g"});
//        cells[21].setLines(new String[]{"c", "d"});
//        cells[22].setLines(new String[]{"c", "o"});
//        cells[23].setLines(new String[]{"c", "b"});
//    }
//
//    void switch_current_player(){
//        if (getCurrent_player() == getP1()){
//            setCurrent_player(getP2());
//        } else {
//            setCurrent_player(getP1());
//        }
//    }
//
//    boolean is_in_neigbors(Cell cell_to_check, Cell cell){
//        for (Cell c : cells) System.out.println(c);
//        for (int tmp_neigbor : cell.getNeighbors()){ // schaut alle nachbarn von cell_to_check an
//            if (tmp_neigbor == cell_to_check.getId()) { // cell_to_chek ist in nachbarn mit drin
//                return true;
//            }
//        }
//        return false;
//    }
//
//
//    // GETTER and SETTER //
//
//
//    public void setStage(int stage) {
//        this.stage = stage;
//    }
//
//    public static Player getP1() {
//        return p1;
//    }
//
//    public static void setP1(Player p1) {
//        Muehle.p1 = p1;
//    }
//
//    public static Player getP2() {
//        return p2;
//    }
//
//    public static void setP2(Player p2) {
//        Muehle.p2 = p2;
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
//    public Player getCurrent_player() {
//        return current_player;
//    }
//
//    public void setCurrent_player(Player current_player) {
//        this.current_player = current_player;
//    }
//
//    public int getNum_of_clicked_cells() {
//        return num_of_clicked_cells;
//    }
//
//    public void setNum_of_clicked_cells(int num_of_clicked_cells) {
//        this.num_of_clicked_cells = num_of_clicked_cells;
//    }
//}
