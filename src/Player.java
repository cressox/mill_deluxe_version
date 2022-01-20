import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class Player {
    int id;
    private String color;
    private int stones_to_set = 9;
    private int stones_in_game = 0;

    // for logic //
    private Stone[] stones = new Stone[9];

    // GUI //
    ImageIcon icon;
    ImageIcon iconActive;
    private String imgURL;
    private String imgURLActive;
    JTextArea label = new JTextArea();
    JTextArea info = new JTextArea();
    private Color COLOR_OF_GUI = new Color(233, 220, 211);

    public Player(int id, String color, String imgUrl, String imgURLActive) {
        this.id = id;
        this.color = color;
        this.label = label;
        this.info = info;
        for (int i=1; i<=9; i++){ // initialise stones as objects in the field stones
            stones[i-1] = new Stone(i + ((id-1)*9), color, this);
        }
        icon = new ImageIcon(imgUrl);
        iconActive = new ImageIcon(imgURLActive);
    }

    // METHODS //
    public boolean set_stone(Cell c, String cell_player_color){ // zelle die angeklickt wurde muss mit übergeben werden um sie zuordnen zu können
        if (getStones_in_game() < 9 && c.isIs_empty()){
            stones[getStones_in_game()].setIs_set(true); // stein ist sichtbar
            stones[getStones_in_game()].setCell(c); // ordnet stein die zelle zu

            c.setIs_empty(false); // zelle ist nicht mehr leer
            c.setStone(stones[getStones_in_game()]); // ordnet zelle den stein zu

            select_stone(c, false);

            setStones_in_game(getStones_in_game() + 1);
            setStones_to_set(getStones_to_set() - 1);

            return true;

        } else {
            return false;
        }
    }

    void select_stone(Cell c, boolean selected){
        c.getLabel().setIcon(selected ? iconActive : icon);
        c.getStone().setIs_visible(selected);
    }

    void move_stone(Cell start_cell, Cell end_cell){
        start_cell.getStone().setCell(end_cell); // verknüpft neue zelle mit stein
        end_cell.setStone(start_cell.getStone()); // verknüpft den stein mit der neuen zelle
        end_cell.setIs_empty(false); // end zelle ist nun nicht mehr leer
        start_cell.setIs_empty(true); // start zelle ist nun leer

        end_cell.getLabel().setBackground(start_cell.getLabel().getBackground()); // visuelle änderung
        end_cell.getLabel().setIcon(start_cell.getLabel().getIcon());
        start_cell.getLabel().setBackground(COLOR_OF_GUI);
        start_cell.getLabel().setIcon(null);


//        System.out.println(start_cell.getLabel().getText());

        start_cell.setStone(null);
    }

    void take_stone(Cell c){
        c.getStone().setCell(null); // löscht im stein die zelle
        c.setIs_empty(true); // zelle ist wieder leer
        c.setStone(null); // löscht stein
        c.getLabel().setBackground(COLOR_OF_GUI);
        c.getLabel().setIcon(null);
    }

    // MAGIC METHODS //
    @Override
    public String toString() {
        return "Player{" +
                "stones_to_set=" + stones_to_set +
                ", stones_in_game=" + stones_in_game +
                ", color=" + color +
                ", stones=" + Arrays.toString(stones) +
                '}';
    }

    // GETTER and SETTER //
    public Stone[] getStones() {
        return stones;
    }

    public void setStones(Stone[] stones) {
        this.stones = stones;
    }

    public int getStones_to_set() {
        return stones_to_set;
    }

    public void setStones_to_set(int stones_to_set) {
        this.stones_to_set = stones_to_set;
    }

    public int getStones_in_game() {
        return stones_in_game;
    }

    public void setStones_in_game(int stones_in_game) {
        this.stones_in_game = stones_in_game;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
