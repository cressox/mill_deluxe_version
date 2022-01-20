import javax.swing.*;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.Map;

public class Login {
    static InetAddress ip;

    static {
        try {
            ip = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    static DB_Connector db_con = new DB_Connector();
    static JFrame ROOT;
    static JButton submit_login = new JButton();
    static JButton submit_register = new JButton();
    static JButton back = new JButton();

    static JLabel login_or_register_screen;
    static JButton login = new JButton();
    static JButton register = new JButton();

    static JLabel login_screen;
    static JLabel register_screen;
    static JTextField user_label = new JTextField();
    static JTextField password_label = new JTextField();

    static JLabel info = new JLabel();

    static int user_id;

    static Lobby lobby;

    public Login() throws UnknownHostException {
    }



    public static void main(String[] args) throws SQLException {
        //db_con.wipe_all(ip);
    }

    public static void init(){
        ROOT = new JFrame();
        ROOT.setTitle("Welcome to the mill game server");
        ROOT.setSize(500, 200);
        ROOT.setLocationRelativeTo(null);

        user_label = new JTextField();
        password_label = new JTextField();
        submit_login = new JButton();
        submit_register = new JButton();

        // login or register screen //
        login_or_register_screen = new JLabel();
        add_label(info, login_or_register_screen, "welcome to the server", (int) (ROOT.getWidth()*0.25),  (int) (ROOT.getHeight()*0.60), 200, 30);
        add_btn(login, login_or_register_screen, "login", (int) (ROOT.getWidth()*0.25),  (int) (ROOT.getHeight()*0.20), 200, 30);
        add_btn(register, login_or_register_screen, "register", (int) (ROOT.getWidth()*0.25),  (int) (ROOT.getHeight()*0.45), 200, 30);

        // function of the btn //
        login.addActionListener(e -> login_fkt());
        register.addActionListener(e -> register_fkt());
    }

    public void draw(){
        init();
        ROOT.add(login_or_register_screen);
        ROOT.setVisible(true);
        ROOT.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public static void add_btn(JButton btn_to_add, JLabel label, String text, int x, int y, int width, int height){
        btn_to_add.setBounds(x,y,width,height);
        btn_to_add.setText(text);
        btn_to_add.setVisible(true);
        btn_to_add.setOpaque(true);
        label.add(btn_to_add);
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

    public static void switch_content(JLabel label_to_add, JLabel label_to_remove){
        // switch label in ROOT //
        ROOT.remove(label_to_remove);
        ROOT.add(label_to_add);

        // update ui //
        ROOT.repaint();
        ROOT.revalidate();
        label_to_add.repaint();
        label_to_add.revalidate();
    }

    public void back(){
        ROOT.dispatchEvent(new WindowEvent(ROOT, WindowEvent.WINDOW_CLOSING));
        draw();
    }

    public static void login_fkt(){
        ROOT.setTitle("login on the server");

        // prepare submit btn //
        if (submit_login.getActionListeners().length==0) {
            submit_login.addActionListener(e -> {
                try {
                    db_login(user_label.getText(), password_label.getText());
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            });
        }
        submit_login.revalidate();
        back.addActionListener(e -> switch_content(login_or_register_screen, login_screen));

        // login screen //
        login_screen = new JLabel();
        add_txt(user_label, login_screen, "username", (int) (ROOT.getWidth()*0.25), (int) (ROOT.getHeight()*0.20), 200, 30);
        add_txt(password_label, login_screen, "password", (int) (ROOT.getWidth()*0.25), (int) (ROOT.getHeight()*0.45), 200, 30);
        add_btn(submit_login, login_screen, "submit",(int) (ROOT.getWidth()*0.65),(int) (ROOT.getHeight()*0.45), 100, 30);
        add_btn(back, login_screen, "back",(int) (ROOT.getWidth()*0.65),(int) (ROOT.getHeight()*0.20), 100, 30);

        switch_content(login_screen, login_or_register_screen);
    }

    public static void register_fkt(){
        ROOT.setTitle("register on the server");

        // prepare submit btn //
        if (submit_register.getActionListeners().length==0) {
            submit_register.addActionListener(e -> {
                try {
                    db_register(user_label.getText(), password_label.getText());
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            });
        }
        submit_register.revalidate();
        back.addActionListener(e -> switch_content(login_or_register_screen, register_screen));

        // register screen //
        register_screen = new JLabel();
        add_txt(user_label, register_screen, "username", (int) (ROOT.getWidth()*0.25), (int) (ROOT.getHeight()*0.20), 200, 30);
        add_txt(password_label, register_screen, "password", (int) (ROOT.getWidth()*0.25), (int) (ROOT.getHeight()*0.45), 200, 30);
        add_btn(submit_register, register_screen, "submit",(int) (ROOT.getWidth()*0.65),(int) (ROOT.getHeight()*0.45), 100, 30);
        add_btn(back, register_screen, "back",(int) (ROOT.getWidth()*0.65),(int) (ROOT.getHeight()*0.20), 100, 30);

        switch_content(register_screen, login_or_register_screen);
    }

    public static void db_login(String username, String password) throws SQLException {
        Map<String, String> user =  db_con.getPlayerByUsername(username, ip);
        if (user==null ||user.isEmpty()){
            password_label.setText("unknown user");
        }else{
            if (!user.get("pw").equals(password)){
                System.out.println(password);
                System.out.println(user.get("pw"));
                password_label.setText("wrong password");
            }else {
                System.out.println(user.get("online"));
                if (user.get("online").equals("false")) {
                    db_con.activate_player(Integer.parseInt(user.get("id")), ip);
                    password_label.setText("logged in");
                    lobby = new Lobby(Integer.parseInt(user.get("id")));
                    lobby.draw();
                }else {
                    password_label.setText("already logged in from ip: " + user.get("ip"));
                }
            }
        }
    }

    public static void db_register(String username, String password) throws SQLException {
        Map<String, String> user =  db_con.getPlayerByUsername(username, ip);
        if (user==null || user.isEmpty()){
            int num_of_players = db_con.get_count_of_registered_players(ip);
            System.out.println(password_label.getText() + " " + user_label.getText());
            db_con.insert_player(num_of_players + 1 , "", 0, 9, ip, password, username, false);
            password_label.setText("registered");
        }else{
            password_label.setText("already registered pls log in");
        }
    }
    public int getUser_id() {
        return user_id;
    }

    public static Lobby getLobby() {
        return lobby;
    }
}
