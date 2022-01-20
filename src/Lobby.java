import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;

public class Lobby{
    static InetAddress ip;
    static ClientHandler[] online_users;
    static DB_Connector db_con = new DB_Connector();

    static Mill_Interface mill_interface;

    static int user_id=-1;

    public Lobby(int user_id) {
        this.user_id = user_id;
    }

    static {
        try {
            ip = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    static JFrame ROOT = new JFrame();
    static JPanel games_panel = new JPanel();
    static JScrollPane scroll_pane_games;
    static JPanel LEFT_panel = new JPanel();

    static JPanel RIGHT_panel = new JPanel();
    static JScrollPane scroll_pane_players;
    static JTable table;

    static JButton start_btn = new JButton();


    static String[][] data;

    public static void main(String[] args) throws SQLException {
        db_con.wipe_all(ip);
//        draw();
    }

    static void draw() throws SQLException {
        ROOT.setSize(900, 500);
        init();
        Map<String, String> player = db_con.getPlayerByID(user_id, ip);
        ROOT.setTitle("Welcome " + player.get("username") + " in the lobby choose a game to join!");
        ROOT.getContentPane().setLayout(null);

        ROOT.add(LEFT_panel);
        ROOT.add(RIGHT_panel);
        ROOT.add(start_btn);
        ROOT.getContentPane().setBackground(new Color(238, 238, 238));
        ROOT.setLocationRelativeTo(null);
        ROOT.setVisible(true);
        ROOT.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        ROOT.repaint();
        ROOT.revalidate();
    }

    static void init() throws SQLException {
        double margin_top_left = 0.025;
        int margin_tl = (int) (ROOT.getWidth()*margin_top_left);

        create_start_btn();
        create_table_of_online_user();
        create_table_of_games();

        LEFT_panel.setOpaque(true);
        LEFT_panel.setBounds(margin_tl, margin_tl, 550, 320);
        RIGHT_panel.setOpaque(false);
        RIGHT_panel.setBounds(margin_tl+570, margin_tl, 270, 320);

    }

    static void create_start_btn(){
        // start btn //
        start_btn.setBounds(10,350, 860, 100);
        start_btn.setText("start a new mill game and wait for players to join");
        start_btn.setOpaque(true);
        start_btn.setVisible(true);
        start_btn.addActionListener(e -> {
            Client.send_data("start");
            try {
                update_all();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    static void create_table_of_games() throws SQLException {
        games_panel.setLayout(new GridLayout());
//        db_con.insert_mill(1, 1, -1, ip);
//        db_con.insert_mill(2, 2, -1, ip);
//        db_con.insert_mill(3, 3, -1, ip);
//        db_con.insert_mill(4, 4, -1, ip);
//        db_con.insert_mill(5, 5, -1, ip);
        int[] players_waiting = db_con.get_player_in_game_ids(ip);


        set_waiting_games_in_label(games_panel, players_waiting);
        scroll_pane_games = new JScrollPane(games_panel, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scroll_pane_games.setPreferredSize(new Dimension(540, 300));
        add_scroll_pane(scroll_pane_games, LEFT_panel);
    }

    static void create_table_of_online_user() throws SQLException {
        // db active players //
        int count_online_users = db_con.get_count_of_active_players(ip);
        int[] db_online_users = db_con.get_active_players(ip);

        data = new String[count_online_users][2];
        String[] columnNames = { "user", "status" };

        for (int i = 0; i < count_online_users; i++){
            Map<String,String> tmpMap = db_con.getPlayerByID(db_online_users[i], ip);
            data[i][0] = tmpMap.get("username");
            data[i][1] = tmpMap.get("status");
        }
        System.out.println(Arrays.deepToString(data));
        table = new JTable(data, columnNames);
        table.setSize(100,200);
        scroll_pane_players = new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll_pane_players.setPreferredSize(new Dimension(250, 300));
        add_scroll_pane(scroll_pane_players, RIGHT_panel);
    }

    static void set_waiting_games_in_label(JPanel label, int[] player_ids_in_game_waiting) throws SQLException {
        int i = 0;
        for (int player_id : player_ids_in_game_waiting) {
            Map<String, String> tmpPlayer = db_con.getPlayerByID(player_id, ip);
            if (tmpPlayer.get("mill_id") == null) { // no game running with this player
            } else {
                // picture from https://appoftheday.downloadastro.com/wp-content/uploads/2019/09/The-Mill-App-Icon.png date: 20.01.2022 - 11:45 //
                add_btn(new JButton(),
                        games_panel,
                        ("<html><img src=\"https://appoftheday.downloadastro.com/wp-content/uploads/2019/09/The-Mill-App-Icon.png\"" +
                                "width=\"150\" height=\"150\"><br/>join mill game " + tmpPlayer.get("mill_id") + " now!<br/" +
                                "<br/><br/>player:<br/>" + tmpPlayer.get("username") + "<br/><br/>is waiting 1/2</html>"),
                        Integer.parseInt((tmpPlayer.get("mill_id"))));
                i++;
            }
        }
    }
    public static void add_btn(JButton btn_to_add, JPanel panel, String text, int mill_id){
        btn_to_add.setText(text);
        btn_to_add.setVisible(true);
        btn_to_add.setOpaque(true);
        btn_to_add.addActionListener(e -> {
            try {
                btn_fkt(mill_id, user_id);
            } catch (SQLException | IOException throwables) {
                throwables.printStackTrace();
            }
        });
        panel.add(btn_to_add);
    }

    static void btn_fkt(int mill_id_to_join, int user_who_wants_to_join_id) throws SQLException, IOException { // join function
        System.out.println(mill_id_to_join + " " + user_who_wants_to_join_id);
        db_con.set_player_in_game(user_who_wants_to_join_id, mill_id_to_join, ip);
        update_all();
//        mill_interface = new Mill_Interface();
//        mill_interface.draw();

        // CONNECT TO SERVER //
        // needs create function too //

        Client.send_data("millId=" + mill_id_to_join);
        Client.send_data("join");
    }

    public static void update_all() throws SQLException {
        Component[] components = games_panel.getComponents();
        for (Component component : components) {
            games_panel.remove(component);
        }
        games_panel.revalidate();
        games_panel.repaint();
        int[] players_waiting = db_con.get_player_in_game_ids(ip);
        set_waiting_games_in_label(games_panel, players_waiting);

        ROOT.revalidate();
        ROOT.repaint();
        games_panel.revalidate();
        games_panel.repaint();
        scroll_pane_games.revalidate();
        scroll_pane_games.repaint();
        LEFT_panel.revalidate();
        LEFT_panel.repaint();
        RIGHT_panel.revalidate();
        RIGHT_panel.repaint();
        scroll_pane_players.revalidate();
        scroll_pane_players.repaint();
        table.revalidate();
        table.repaint();
    }

    public static void add_txt(JTextField txt_to_add, JLabel label, String text, int x, int y, int width, int height){
        txt_to_add.setBounds(x,y,width,height);
        txt_to_add.setText(text);
        txt_to_add.setVisible(true);
        txt_to_add.setOpaque(true);
        label.add(txt_to_add);

    }
    public static void add_label(JLabel label_to_add, JLabel label, String text, int x, int y, int width, int height){
        label_to_add.setBounds(x,y,width,height);
        label_to_add.setText(text);
        label_to_add.setVisible(true);
        label_to_add.setOpaque(true);
        label.add(label_to_add);

    }
    public static void add_scroll_pane(JScrollPane scroll_pane_to_add, JPanel panel){
        scroll_pane_to_add.setVisible(true);
        scroll_pane_to_add.setOpaque(true);
        panel.add(scroll_pane_to_add);
    }

    public static int getUser_id() {
        return user_id;
    }
}
