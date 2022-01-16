import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Objects;

public class Player {
    ImageIcon icon;
    ImageIcon iconActive;
    private String imgURL;
    private String imgURLActive;
    private Stone[] stones = new Stone[9];
    private int stones_to_set = 9;
    private int stones_in_game = 0;
    private String color;
    private Color COLOR = new Color(233, 220, 211);

    private Client client = new Client();

    JTextArea label = new JTextArea();
    JTextArea info = new JTextArea();

    public Player(String color, JTextArea label, JTextArea info, String imgUrl, String imgURLActive) {
        this.color = color;
        this.label = label;
        this.info = info;
        for (int i = 0; i < 9; i++){ // initialise stones as objects in the field stones
            stones[i] = new Stone(i+1, color, this);
        }
        icon = new ImageIcon(Objects.requireNonNull(this.getClass().getResource(imgUrl)));
        iconActive = new ImageIcon(Objects.requireNonNull(this.getClass().getResource(imgURLActive)));
    }

    // METHODS //

    public boolean setStone(Cell c, boolean send){ // zelle die angeklickt wurde muss mit übergeben werden um sie zuordnen zu können
        if (getStones_in_game() < 9 && c.isIs_empty()){
            stones[getStones_in_game()].setIs_set(true); // stein ist siochtbar
            stones[getStones_in_game()].setCell(c); // ordnet stein die zelle zu

            c.setIs_empty(false); // zelle ist nicht mehr leer
            c.setStone(stones[getStones_in_game()]); // ordnet zelle den stein zu

            selectStone(c, false);

            setStones_in_game(getStones_in_game() + 1);
            setStones_to_set(getStones_to_set() - 1);

            if (send) {
                if (c.getMill().is_mill(c)){
                    Client.send_data("method:set, cell:" + c.getId() + ", player:" + color + "action:mill");
                } else {
                    Client.send_data("method:set, cell:" + c.getId() + ", player:" + color);
                }

            }

            return true;

        } else {
            return false;
        }
    }

    void selectStone(Cell c, boolean selected){
        c.getLabel().setIcon(selected ? iconActive : icon);
        c.getStone().setIs_visible(selected);
    }

    void moveStone(Cell start_cell, Cell end_cell, boolean send){
        start_cell.getStone().setCell(end_cell); // verknüpft neue zelle mit stein
        end_cell.setStone(start_cell.getStone()); // verknüpft den stein mit der neuen zelle
        end_cell.setIs_empty(false); // end zelle ist nun nicht mehr leer
        start_cell.setIs_empty(true); // start zelle ist nun leer

        end_cell.getLabel().setBackground(start_cell.getLabel().getBackground()); // visuelle änderung
        end_cell.getLabel().setIcon(start_cell.getLabel().getIcon());
        start_cell.getLabel().setBackground(COLOR);
        start_cell.getLabel().setIcon(null);


//        System.out.println(start_cell.getLabel().getText());

        start_cell.setStone(null);

        if (send) {
            if (end_cell.getMill().is_mill(end_cell)){
                Client.send_data("method:move, start_cell:" + start_cell.getId() + ", end_cell:" + end_cell.getId() + ", player:" + color + "action:mill");
            } else {
                Client.send_data("method:move, start_cell:" + start_cell.getId() + ", end_cell:" + end_cell.getId() + ", player:" + color);
            }

        }
    }

    void takeStone(Cell c, boolean send){
        if (send){
            Client.send_data("method:take, cell:" + c.getId() + ", player:" + color);
        }

        c.getStone().setCell(null); // löscht im stein die zelle
        c.setIs_empty(true); // zelle ist wieder leer
        c.setStone(null); // löscht stein
        c.getLabel().setBackground(COLOR);
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

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
