import java.util.Arrays;

public class Logic {
    int id;
    String isWinner; // cant interact

    private static final Cell[] cells = new Cell[24]; // cells in the game where stones can take place
    private Player p1; // X = white;
    private Player p2; // O = black;

    private int stage = 1;
    // 0 = initial stage
    // 1 = first stage
    // 2 = second stage
    // 3 = third stage

    private int num_of_clicked_cells = 0;
    private Cell tmpCell1;
    public boolean isMill = false;
    public boolean gameOver = false;

    public Logic(int id) {
        this.id = id;
    }

    public void init(int id_p1, int id_p2){
        p1 = new Player(id_p1, "white", "D:\\Programming\\mill_deluxe_version\\src\\Assets\\white.png", "D:\\Programming\\mill_deluxe_version\\src\\Assets\\white-red.png");
        p2 = new Player(id_p2, "black", "D:\\Programming\\mill_deluxe_version\\src\\Assets\\black.png", "D:\\Programming\\mill_deluxe_version\\src\\Assets\\black-red.png");
        init_cells();
    }

    void init_cells(){
        // initialise cells as objects in the field cells
        for (int i = 0; i < 24; i++) {
            cells[i] = new Cell(i);
        }

        // neighbors of 24 label //
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

        // lines of 24 label //
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

    public boolean interpret_client_request(String data) {
        System.out.println("mill now cr: " + isMill);
        int cell_id;
        String cell_player_color;
        if (data.contains("white")) {
            cell_id = Integer.parseInt(data.replace("white", ""));
            cell_player_color = "white";
        } else {
            cell_id = Integer.parseInt(data.replace("black", ""));
            cell_player_color = "black";
        }
        switch (stage) {
            case 1 -> {
                System.out.println(data);
                System.out.println(Arrays.toString(cells));
                first_stage(cells[cell_id], cell_player_color);
                is_game_running();
            }
            case 2 -> {
                System.out.println(data);
                System.out.println(Arrays.toString(cells));
                second_stage(cells[cell_id], cell_player_color);
                is_game_running();
            }
        }
        return true;
    }

    public String current_game_state_as_string(){
        StringBuilder result_string = new StringBuilder();
        for (Cell cell:cells){
            if (cell.getStone() == null){
                result_string.append("n");
            }
            else if (cell.getStone().getColor().equals("white")){
                result_string.append("w");
            }
            else {
                result_string.append("b");
            }
        }
        return result_string.toString();
    }

    public int count_char(char countChar){
        String tmp = current_game_state_as_string();
        int count=0;
        for (char ch : tmp.toCharArray()){
            if (ch==countChar) count++;
        }
        return count;
    }

    void first_stage(Cell c, String cell_player_color){
        System.out.println(p1.getStones_to_set() + "" + p2.getStones_to_set());
        if (p1.getStones_to_set() == 0 && p2.getStones_to_set() == 0){ // stage one ends and the second stage starts cause no stones to set left
            setStage(2);
            second_stage(c, cell_player_color);
        } else{
            if (isMill){
                if (c.getStone() != null && (cell_player_color.equals("white"))){
                    if (!is_mill(c) || only_mills_in_game(p2, cell_player_color)) {
                        p1.take_stone(c);
                        p2.setStones_in_game(p2.getStones_in_game() - 1);
                        //isMill = false;
                    }
                } else {
                    if (!is_mill(c) || only_mills_in_game(p1, cell_player_color)) {
                        p2.take_stone(c);
                        p1.setStones_in_game(p1.getStones_in_game() - 1);
                        //isMill = false;
                    }
                }
            }
            else if (cell_player_color.equals("white")){
                if(p1.set_stone(c)) {
                    System.out.println(cell_player_color + " p1");
                    if (is_mill(c)) { // mühle
                        this.isMill = true;
                        System.out.println("now mill" + isMill);
                    }
                }
            }else {
                if(p2.set_stone(c)) {
                    System.out.println(cell_player_color + " p2");
                    if (is_mill(c)) { // mühle
                        this.isMill = true;
                        System.out.println("now mill " + isMill);

                    }
                }
            }
        }
        System.out.println("p1: sig: " + p1.getStones_in_game());
        System.out.println("p2: sig: " + p2.getStones_in_game());
    }

    void second_stage(Cell c, String cell_player_color){
        System.out.println("p1: sig: " + p1.getStones_in_game());
        System.out.println("p2: sig: " + p2.getStones_in_game());
        if (isMill && (c.getStone() != null)) { // spieler hat mühle und pot weggenommene zelle ist nicht leer
            if (!c.getStone().getColor().equals("white")) { // darf nicht eigener stein sein
                if (!is_mill(c) || only_mills_in_game(p2, cell_player_color)){ // darf nicht aus mühle kommen außer wenn nur mühlen sind
                    p1.take_stone(c); // spieler nimmt stein des anderen spielers
                    p2.setStones_in_game(p2.getStones_in_game() - 1);
                    if (is_blocked(p2)) {
                        winning(p1.getColor());
                    }
                }
            } else if (!is_mill(c) || only_mills_in_game(p1, cell_player_color)){ // darf nicht aus mühle kommen außer wenn nur mühlen sind
                p2.take_stone(c); // spieler nimmt stein des anderen spielers
                p1.setStones_in_game(p1.getStones_in_game() - 1);
                if (is_blocked(p1)) {
                    winning(p2.getColor());
                }
            }
        }

        else if (isValid(c, cell_player_color)) {
            switch (num_of_clicked_cells) {
                case 0 : {
                    if (cell_player_color.equals("white")) {
                        if (is_blocked(p1)) {
                            winning(p2.getColor());
                        } else if ((c.getStone() != null)) { // erste zelle darf nicht leer sein
                            num_of_clicked_cells += 1;
                            tmpCell1 = c;
                        }
                    }else{
                        if (is_blocked(p2)) {
                            winning(p1.getColor());
                        }
                        else if ((c.getStone() != null)) { // erste zelle darf nicht leer sein
                            num_of_clicked_cells += 1;
                            tmpCell1 = c;
                        }
                    }
                }

                case 1 : {
                    if ((c != tmpCell1) && (c.isIs_empty())) { // nicht 2 mal selbe zelle und 2. zelle muss leer sein
                        if (is_in_neigbors(tmpCell1, c) || p1.getStones_in_game() <= 3 || p2.getStones_in_game() <= 3) { // zelle darf nur 1 platz weiter
                            num_of_clicked_cells = 0; // zähler für start und end auf null

                            if (cell_player_color.equals("white")){
                                if (is_in_neigbors(tmpCell1, c)){
                                    p1.move_stone(tmpCell1, c); // bewegen
                                }
                                else if (count_char('w')<=3) {
                                    if (tmpCell1.getStone().getColor().equals(p1.getColor())){
                                        p1.move_stone(tmpCell1, c); // bewegen
                                    }
                                }
                            }else{
                                if (is_in_neigbors(tmpCell1, c)){
                                    p2.move_stone(tmpCell1, c); // bewegen
                                }
                                else if (count_char('b')<=3) {
                                    if (tmpCell1.getStone().getColor().equals(p2.getColor())){
                                        p2.move_stone(tmpCell1, c); // bewegen
                                    }
                                }
                            }

                            if (is_mill(c)) { // spieler hat mühle
                                isMill = true; // spieler nimmt stein des anderen spielers
//                                System.out.println("mühle");
                            }
                        }
                        else { // invalid try
                            num_of_clicked_cells = 0;
                        }
                    } else if (cell_player_color.equals("white")){
                        if (is_blocked(p1)) {
                            winning(p2.getColor());
                        }
                    } else {
                        if (is_blocked(p2)) {
                            winning(p1.getColor());
                        }
                    }
                }
            }
        }
    }

    private boolean only_mills_in_game(Player p, String cell_player_color) { // checkt ob nur mühlen existieren
        String game_state = current_game_state_as_string();
        char[] gs = game_state.toCharArray();
        int i = 0;
        for (char c : gs) {
            System.out.println("id: " + i + " " + c);
            if (c == 'n') {
                i++;
                continue;
            }
            if (c == 'w' && p.getColor().equals("white")) {
                System.out.println(game_state + " i am b");
                if (!is_mill(cells[i])) return false;
            }
            if (c == 'b' && p.getColor().equals("black")) {
                System.out.println(game_state + " i am w");
                if (!is_mill(cells[i])) return false;
            }
            i++;
        }
        System.out.println("only mills");
        return true;
    }

    private boolean isValid(Cell c, String cell_player_color){
        if (c.getStone() == null){
            return true;
        }
        if (cell_player_color.equals("white")){
            return p1.getColor().equals(c.getStone().getColor());
        }else{
            return p2.getColor().equals(c.getStone().getColor());
        }
    }

    boolean is_mill(Cell c){
        String game_state = current_game_state_as_string();
        char[] gs = game_state.toCharArray();
        String[] ls = c.getLines();
        for (String line : ls){
            int m = 0;
            System.out.println("m=0");
            for (Cell cell : cells){
                if (cell.getLines()[0].equals(line) || cell.getLines()[1].equals(line)){
                    if (gs[cell.getId()]==gs[c.getId()]){
                        System.out.println("Cell num " + cell.getId() + ": " + gs[cell.getId()]);
                        m++;
                    }
                }
            }
            if (m==3) {
                System.out.println("its in a mill " + c.getId());
                return true;
            }
        }
        System.out.println("its not in a mill " + c.getId());
        return false;
    }

    void is_game_running(){
        if ((p1.getStones_in_game() <= 2) && (this.stage != 1)){ // p2 is winning
            winning(p2.getColor());
            gameOver = true;
            isWinner = "black";
        } else if ((p2.getStones_in_game() <= 2) && (this.stage != 1)){ // p1 is winning
            winning(p1.getColor());
            gameOver = true;
            isWinner = "white";
        }

    }

    private boolean is_blocked(Player p){
        return false;
//        String game_state = current_game_state_as_string();
//        char[] gs = game_state.toCharArray();
//        int i = 0;
//        for (char c : gs){
//            if (c=='n') continue;
//            if (c=='w' && p.getColor().equals("white")){
//                int [] tmp = cells[i].getNeighbors();
//                for (int t : tmp){
//                    if (cells[t].isIs_empty()) return true;
//                }
//            }
//            if (c=='b' && p.getColor().equals("black")){
//                int [] tmp = cells[i].getNeighbors();
//                for (int t : tmp){
//                    if (cells[t].isIs_empty()) return true;
//                }
//            }
//            i++;
    }

    void winning(String color){
        if (color.equals("white")){
            gameOver = true;
            isWinner = "white";
        } else {
            gameOver = true;
            isWinner = "black";
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

    public void setStage(int stage) {
        this.stage = stage;
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

    public String getIsWinner() {
        return isWinner;
    }
}
