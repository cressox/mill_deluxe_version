import javax.swing.*;

public class Login {
    static JFrame ROOT;
    static JButton submit = new JButton();
    static JButton back = new JButton();

    static JLabel login_or_register;
    static JButton login = new JButton();
    static JButton register = new JButton();

    static JLabel login_screen;
    static JLabel register_screen;
    static JTextField user = new JTextField();
    static JTextField password = new JTextField();

    public static void main(String[] args) {
        draw();
    }

    public static void init(){
        ROOT = new JFrame();
        ROOT.setTitle("Welcome to the mill game server");
        ROOT.setSize(500, 200);
        ROOT.setLocationRelativeTo(null);

        user = new JTextField();
        password = new JTextField();
        submit = new JButton();

        // login or register screen //
        login_or_register = new JLabel();
        add_btn(login, login_or_register, "login", (int) (ROOT.getWidth()*0.25),  (int) (ROOT.getHeight()*0.20), 200, 30);
        add_btn(register, login_or_register, "register", (int) (ROOT.getWidth()*0.25),  (int) (ROOT.getHeight()*0.45), 200, 30);

        // function of the btn //
        login.addActionListener(e -> login_fkt());
        register.addActionListener(e -> register_fkt());
    }

    public static void draw(){
        init();
        ROOT.add(login_or_register);
        ROOT.setVisible(true);
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
        label_to_add.repaint();
        label_to_add.revalidate();
    }

    public static void login_fkt(){
        // prepare submit btn //
        submit.addActionListener(e -> submit_fkt(false));
        back.addActionListener(e -> switch_content(login_or_register, login_screen));

        // login screen //
        login_screen = new JLabel();
        add_txt(user, login_screen, "username", (int) (ROOT.getWidth()*0.25), (int) (ROOT.getHeight()*0.20), 200, 30);
        add_txt(password, login_screen, "password", (int) (ROOT.getWidth()*0.25), (int) (ROOT.getHeight()*0.45), 200, 30);
        add_btn(submit, login_screen, "submit",(int) (ROOT.getWidth()*0.65),(int) (ROOT.getHeight()*0.45), 100, 30);
        add_btn(back, login_screen, "back",(int) (ROOT.getWidth()*0.65),(int) (ROOT.getHeight()*0.20), 100, 30);

        switch_content(login_screen, login_or_register);
    }

    public static void register_fkt(){
        // prepare submit btn //
        submit.addActionListener(e -> submit_fkt(true));
        back.addActionListener(e -> switch_content(login_or_register, register_screen));

        // register screen //
        register_screen = new JLabel();
        add_txt(user, register_screen, "username", (int) (ROOT.getWidth()*0.25), (int) (ROOT.getHeight()*0.20), 200, 30);
        add_txt(password, register_screen, "password", (int) (ROOT.getWidth()*0.25), (int) (ROOT.getHeight()*0.45), 200, 30);
        add_btn(submit, register_screen, "submit",(int) (ROOT.getWidth()*0.65),(int) (ROOT.getHeight()*0.45), 100, 30);
        add_btn(back, register_screen, "back",(int) (ROOT.getWidth()*0.65),(int) (ROOT.getHeight()*0.20), 100, 30);

        switch_content(register_screen, login_or_register);
    }

    public static void submit_fkt(boolean register){

    }
}
