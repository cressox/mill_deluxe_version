import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;

// Client class
class Client {
    // GAME VARIABLES //
    private static Cell[] cells = new Cell[24]; // cells in the game where stones can take place
    private static JFrame MAIN_FRAME = new JFrame();
    private static JLabel MAIN_LABEL = new JLabel();
    private static JLabel abbruch = new JLabel();
    private static JLabel neustart = new JLabel();

    // CLIENT VARIABLES //
    private static PrintWriter out;
    private static BufferedReader in;
    private static boolean should_receive = true;
    private static Socket socket;
    private static Muehle mill;

    // driver code
    public static void main(String[] args) throws IOException {
        draw_mill_interface();

        // establish a connection by providing host and port
        // number
        try {
            socket = new Socket("localhost", 1234);
            // writing to server
            out = new PrintWriter(
                    socket.getOutputStream(), true);

            // reading from server
            in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));

            new Thread(Client::receive_data).start(); // abgekapselt vom main thread
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void send_data(String data) {
        out.println(data); // sending information to server
        out.flush();
    }

    public static void receive_data() {
        while (should_receive) {
            try {
                String data = in.readLine();
                // interpretation //

                if (data != null) {
                    System.out.println(data);

                } else { // server dead
                    should_receive = false;
                }
            } catch (IOException e) { // could be server dead
                e.printStackTrace();
                should_receive = false;
            }
        }
    }

    static void draw_mill_interface() throws IOException {


        int WIDTH = 720;
        int HEIGHT = 720;
        MAIN_FRAME.getContentPane();
        MAIN_FRAME.setSize(WIDTH + 50, HEIGHT);
        int w = MAIN_FRAME.getWidth();
        int h = MAIN_FRAME.getHeight();
        int lh = 65;                                    //height of label set to 50px
        int lw = 65;                                    //width of label set to 50px
        int x = -15;
        int y = -20;

        BufferedImage imgSmartURL = ImageIO.read(new File("D:\\Programming\\mill_deluxe_version\\src\\muehle_brett_mit_punkten.png"));
        ImageIcon bg = new ImageIcon(imgSmartURL);
        MAIN_LABEL.setOpaque(true);
        MAIN_LABEL.setBackground(new Color(233, 220, 211));
        MAIN_LABEL.setIcon(bg);

        Border border = BorderFactory.createLineBorder(Color.BLACK, 1);     //determines frames

        abbruch.setBounds(10, 650, 100, 20);
        abbruch.setBorder(border);
        abbruch.setText("Aufgeben");
        abbruch.setVisible(true);
        MyMouseListener ml = new MyMouseListener(abbruch);
        abbruch.addMouseListener(ml);

        neustart.setBounds(10, 625, 100, 20);
        neustart.setBorder(border);
        neustart.setText("Neustart");
        neustart.setVisible(false);
        MyMouseListener ml_nst = new MyMouseListener(neustart);
        neustart.addMouseListener(ml_nst);



        for (int i = 0; i < 24; i++) { // initialise cells as objects in the field cells
            cells[i] = new Cell(i);
            cells[i].getLabel().setOpaque(true);
        }

        for (int i = 0; i <= 23; i++) { // nur temporär um zellen zu identifizieren
            MyMouseListener tmp_ml = new MyMouseListener(cells[i].getLabel(), cells[i]);
            cells[i].getLabel().addMouseListener(tmp_ml); // make labels clickable
        }

        // POSITIONING OF THE 24 LABELS //
        cells[0].getLabel().setBounds(x + 90, y + 70, lw, lh);
        cells[1].getLabel().setBounds(x + (w / 2) - (lw / 2), y + 70, lw, lh);
        cells[2].getLabel().setBounds(x + (w - 90) - (lw), y + 70, lw, lh);
        cells[3].getLabel().setBounds(x + (w / 4) - (lw / 2) + 5, y + (h / 4) - (lh / 2), lw, lh);
        cells[4].getLabel().setBounds(x + (w / 2) - (lw / 2), y + (h / 4) - (lh / 2), lw, lh);
        cells[5].getLabel().setBounds(x + (3 * w / 4) - (lw / 2) - 5, y + (h / 4) - (lh / 2), lw, lh);
        cells[6].getLabel().setBounds(x + (3 * w / 8) - (lw / 2), y + (3 * h / 8) - (lh / 2), lw, lh);
        cells[7].getLabel().setBounds(x + (w / 2) - (lw / 2), y + (3 * h / 8) - (lh / 2), lw, lh);
        cells[8].getLabel().setBounds(x + (5 * w / 8) - (lw / 2), y + (3 * h / 8) - (lh / 2), lw, lh);
        cells[9].getLabel().setBounds(x + 90, y + (h / 2) - (lh / 2), lw, lh);
        cells[10].getLabel().setBounds(x + (w / 4) - (lw / 2) + 5, y + (h / 2) - (lh / 2), lw, lh);
        cells[11].getLabel().setBounds(x + (3 * w / 8) - (lw / 2), y + (h / 2) - (lh / 2), lw, lh);
        cells[12].getLabel().setBounds(x + (5 * w / 8) - (lw / 2), y + (h / 2) - (lh / 2), lw, lh);
        cells[13].getLabel().setBounds(x + (3 * w / 4) - (lw / 2) - 10, y + (h / 2) - (lh / 2), lw, lh);
        cells[14].getLabel().setBounds(x + (w - 90) - (lw), y + (h / 2) - (lh / 2), lw, lh);
        cells[15].getLabel().setBounds(x + (3 * w / 8) - (lw / 2), y + (5 * h / 8) - (lh / 2), lw, lh);
        cells[16].getLabel().setBounds(x + (w / 2) - (lw / 2), y + (5 * h / 8) - (lh / 2), lw, lh);
        cells[17].getLabel().setBounds(x + (5 * w / 8) - (lw / 2), y + (5 * h / 8) - (lh / 2), lw, lh);
        cells[18].getLabel().setBounds(x + (w / 4) - (lw / 2) + 5, y + (3 * h / 4) - (lh / 2), lw, lh);
        cells[19].getLabel().setBounds(x + (w / 2) - (lw / 2), y + (3 * h / 4) - (lh / 2), lw, lh);
        cells[20].getLabel().setBounds(x + (3 * w / 4) - (lw / 2) - 10, y + (3 * h / 4) - (lh / 2), lw, lh);
        cells[21].getLabel().setBounds(x + 90, (h - 90) - (lh / 2) + y, lw, lh);
        cells[22].getLabel().setBounds(x + (w / 2) - (lw / 2), y + (h - 90) - (lh / 2), lw, lh);
        cells[23].getLabel().setBounds(x + (w - 90) - (lw), y + (h - 90) - (lh / 2), lw, lh);

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

        // ADD ALL TO MAIN_LABEL //
        for (int i = 0; i <= 23; i++) {
            MAIN_LABEL.add(cells[i].getLabel());
            cells[i].getLabel().setVisible(true);
        }
        MAIN_LABEL.add(abbruch);
        MAIN_LABEL.add(neustart);
        MAIN_LABEL.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        MAIN_FRAME.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        MAIN_FRAME.add(MAIN_LABEL);
        MAIN_FRAME.setVisible(true);
    }
}
