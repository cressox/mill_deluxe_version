import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MyMouseListener implements MouseListener {
    private final JLabel label;
    private final Cell cell;
    private Mill_Interface mill_interface;

    public MyMouseListener(JLabel label, Cell cell){
        this.label = label;
        this.cell = cell;
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (cell == null) return;
        Color c = new Color(255,255,255);
        if (cell.isIs_empty()) {
            label.setBackground(c);
            label.setIcon(null);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (mill_interface==null) return;
        if (cell == null) return;
        if (cell.isIs_empty()) {
            if (cell.getColor_of_player().equals("black")){
                label.setIcon(mill_interface.getIcon_black());
            }else {
                label.setIcon(mill_interface.getIcon_white());
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (!mill_interface.isMyTurn()) return;
        System.out.println(cell);
        if (cell != null) {
            System.out.println(cell.getId());
            Client.send_data(cell.getId()+cell.getColor_of_player());
        }
        else System.out.println(label.getText());
    }

    public void setMill_interface(Mill_Interface mill_interface) {
        this.mill_interface = mill_interface;
    }
}
