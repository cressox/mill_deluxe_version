import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MyMouseListener implements MouseListener {
    private JLabel label;
    private Cell cell;
    private int id;
    private String color;
    private Muehle mill;

    public MyMouseListener(JLabel label, Cell cell, int id, String color) {
        this.label = label;
        this.cell = cell;
        this.id = id;
        this.color = color;
    }
    public MyMouseListener(JLabel label, String color, Muehle mill){
        this.label = label;
        this.color = color;
        this.mill = mill;
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (cell != null){
            if (cell.getStone()!=null){
                cell.getStone().getPlayer().selectStone(cell, false);
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (cell != null){
            if (cell.getStone()!=null){
                cell.getStone().getPlayer().selectStone(cell, true);
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (label.getText().equals("Aufgeben")){
            Client.send_data("method:aufgeben, player:" + (color.equals("white") ? "black" : "white"));
            mill.getNeustart().setVisible(true);
        }
        else if (label.getText().equals("Neustart")){
            mill.close_game();
            mill.start_game(mill);
            Client.send_data("method:neustart, player:" + (color.equals("white") ? "black" : "white"));
        }
        else if (!cell.getMill().isGameOver()) { // game needs to be running
            if (cell.getMill().isMy_turn()) { // my turn
                System.out.println("clicked" + cell.getMill().getStage());

                int stage = cell.getMill().getStage();
                if (stage == 1) cell.getMill().first_stage(cell);
                else if (stage == 2) cell.getMill().second_stage(cell);
                else if (stage == 3) {
                    cell.getMill().third_stage(cell);
                    cell.getMill().is_game_running();
                }
            }
        }
        else {
            System.out.println("cant click game is over");
        }
    }
}
