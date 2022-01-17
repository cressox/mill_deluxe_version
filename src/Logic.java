import javax.swing.*;
import java.net.URL;
import java.util.Arrays;

public class Logic {
    boolean my_turn = false; // cant interact

    private static Cell[] cells = new Cell[24]; // cells in the game where stones can take place
    private Player p1; // X = white;
    private Player p2; // O = black;

    private int stage = 0;
    // 0 = initial stage
    // 1 = first stage
    // 2 = second stage
    // 3 = third stage

    private int num_of_clicked_cells = 0;
    private Cell tmpCell1;
    private Cell tmpCell2;
    private boolean isMill = false;
    private boolean gameOver = false;

    public void init(int id_p1, int id_p2, String color_p1, String color_p2){
        p1 = new Player(id_p1, color_p1, "", "");
        p2 = new Player(id_p2, color_p2, "", "");
    }

    public void interpret_client_request(String data) {
        switch (stage) {
            case 1 -> {
                System.out.println();
            }
            case 2 -> {
                System.out.println();
            }
        }
    }

    Player choose_player(Player p1, Player p2){
        if ((int)(Math.random()*2) == 0){
            return p1;
        } else {
            return p2;
        }
    }

    void start_game(Muehle m){
        boolean game_is_running = true; // if game is over it switches to false
        setStage(1);
    }

    void first_stage(Cell c){
        System.out.println(p1.getStones_to_set() + "" + p2.getStones_to_set());
        if (p1.getStones_to_set() == 0 && p2.getStones_to_set() == 0){ // stage one ends and the second stage starts cause no stones to set left
            setStage(2);
            second_stage(c);
        } else{
            if (isMill){
                if (c.getStone() != null && c.getStone().getColor() != p1.getColor()){
                    if (!is_mill(c) || only_mills_in_game(p2)) {
                        p1.takeStone(c, true);
                        p2.setStones_in_game(p2.getStones_in_game() - 1);
                        isMill = false;
//                        switch_player();
                    }
                }
            }

            else if (p1.setStone(c, true)){
                if (is_mill(c)){ // mühle
                    isMill = true;
                }
//                else{
//                    switch_player();
//                }
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
//                    switch_player(); // next player

                    isMill = false;
                    if (is_blocked(p2)) {
                        winning(p1.getColor(), true);
                    }
                }
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
                            num_of_clicked_cells = 0;
                        }
                    } else if (is_blocked(p1)) {
                        winning(p2.getColor(), true);
                    }
                }
            }
        }
    }

    void third_stage(Cell c){
        second_stage(c);
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
        for (Cell c : cells){
            c.getLabel().setIcon(null);
        }

        if (color.equals("white")){
            URL imgSmartURL = this.getClass().getResource("Assets/brett_gewinn_white.png");
            ImageIcon bg = new ImageIcon(imgSmartURL);
            if (send) {
                Client.send_data("method:win, player:" + "white");
            }
        } else {
            URL imgSmartURL = this.getClass().getResource("Assets/brett_gewinn_black.png");
            ImageIcon bg = new ImageIcon(imgSmartURL);
            if (send) {
                Client.send_data("method:win, player:" + "black");
            }
        }
    }

    boolean is_in_neigbors(Cell cell_to_check, Cell cell){
        for (int tmp_neigbor : cell.getNeighbors()){ // schaut alle nachbarn von cell_to_check an
            if (cell_to_check != null){
                if (tmp_neigbor == cell_to_check.getId()) { // cell_to_chek ist in nachbarn mit drin
                    return true;
                }
            }
        }
        return false;
    }

    // GETTER AND SETTER //

    public boolean isMy_turn() {
        return my_turn;
    }

    public void setMy_turn(boolean my_turn) {
        this.my_turn = my_turn;
    }

    public static Cell[] getCells() {
        return cells;
    }

    public static void setCells(Cell[] cells) {
        Logic.cells = cells;
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

    public void setStage(int stage) {
        this.stage = stage;
    }

    public int getNum_of_clicked_cells() {
        return num_of_clicked_cells;
    }

    public void setNum_of_clicked_cells(int num_of_clicked_cells) {
        this.num_of_clicked_cells = num_of_clicked_cells;
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

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }
}
