import java.net.InetAddress;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DB_Connector {
    static String MySQLURL;
    static String databseUserName;
    static String databasePassword;
    static Connection con = null;

    public static void main(String[]args) throws SQLException {
        //open_con("jdbc:mysql://localhost:3306/muehle", "root", "root");


//        Map<String, String> tmp_mill = create_game_as_map("1", "4", "5");
//        change_mill(1, tmp_mill);

//        Map<String, String> tmp_player = create_player_as_map("'1'", "'192.164.178.44'", "'cressox'", "'rte'", null, null, null, "'black'");
//        change_player(1, tmp_player);

        //close_con();
    }

    protected int get_count_of_mills(InetAddress ip) throws SQLException {
        String sql = "SELECT MAX(Id) FROM game";
        open_con("jdbc:mysql://localhost:3306/muehle", "root", "root", ip);
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        int tmp_count = rs.getInt("MAX(Id)");
        close_con(ip);
        return tmp_count;
    }

    protected int get_count_of_players(InetAddress ip) throws SQLException {
        String sql = "SELECT MAX(Id) FROM player";
        open_con("jdbc:mysql://localhost:3306/muehle", "root", "root", ip);
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        int tmp_count = rs.getInt("MAX(Id)");
        close_con(ip);
        return tmp_count;
    }

    protected void open_con(String db_url, String db_user, String db_user_pw, InetAddress ip) throws SQLException {
        MySQLURL = db_url;
        databseUserName = db_user;
        databasePassword = db_user_pw;
        con = DriverManager.getConnection(MySQLURL, databseUserName, databasePassword);
//        System.out.println("new DB_CON from CH of " + ip.getHostAddress());
    }

    protected void close_con(InetAddress ip) throws SQLException {
//        System.out.println("closed DB_CON from CH of " + ip.getHostAddress());
        con.close();
    }

    protected void insert_stone(int id) throws SQLException {
        String sql = "INSERT INTO `stone` (`id`, `in_game`, `tmp_lines`, `tmp_index`) VALUES (" + id + ", false, NULL, NULL)";
        CallableStatement pst = con.prepareCall(sql);
        pst.execute();
        pst.close();
        System.out.println(sql);
    }

    protected void insert_player(String color, int id) throws SQLException {
        Map<String, String> player = get_player(id); // check if player exists
        if (player==null || player.isEmpty()){ // player doesnt exist
            String sql = "INSERT INTO `player` (`id`, `color`, `stones_in`, `stones_out`, `ip`, `pw`, `username`, `online`) VALUES ('" + id + "', '" + color + "', '0', '9', NULL, NULL, NULL, '0')";
            CallableStatement pst = con.prepareCall(sql);
            for (int i = 1; i <= 9; i++) {
                insert_stone(i + ((id - 1) * 9));
            }
            pst.execute();
            pst.close();
            System.out.println(sql);
        } else { // player exists
            activate_player(id);
        }
    }

    protected void activate_player(int player_id) throws SQLException {
        String sql = "update player set online=true where id=" + player_id;
        CallableStatement pst = con.prepareCall(sql);
        pst.execute();
        pst.close();
        System.out.println(sql);
    }

    protected void deactivate_player(int player_id) throws SQLException {
        String sql = "update player set online=false where id=" + player_id;
        CallableStatement pst = con.prepareCall(sql);
        pst.execute();
        pst.close();
        System.out.println(sql);
    }

    protected void insert_mill(int id, int id_p1, int id_p2) throws SQLException {
        insert_player("white", id_p1);
        insert_player("black", id_p2);
        String sql = "INSERT INTO `game` (`id`, `running`, `player_one`, `player_two`) VALUES ('" + id + "', '1', '" + id_p1 + "', '" + id_p2 + "')";
        CallableStatement pst = con.prepareCall(sql);
        pst.execute();
        pst.close();
        System.out.println(sql);
    }

    protected Map get_mill(int mill_id) throws SQLException {
        // check if row exists //
        String sql = "SELECT * FROM game WHERE id = " + mill_id;

        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        if (rs==null) return null;

        Map<String, String> myResSet = new HashMap();

        while (rs.next()){
            int id = rs.getInt("id");
            boolean running = rs.getBoolean("running");
            int p1 = rs.getInt("player_one");
            int p2 = rs.getInt("player_two");

            myResSet.put("id", String.valueOf(id));
            myResSet.put("running", String.valueOf(running));
            myResSet.put("p1", String.valueOf(p1));
            myResSet.put("p2", String.valueOf(p2));
            if (id == mill_id) return myResSet;
        }

        rs.close();
        stmt.close();
        return myResSet;
    }

    protected Map get_player(int player_id) throws SQLException {
        String sql = "SELECT * FROM player WHERE id = " + player_id;

        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        if (rs==null) return null;

        Map<String, String> myResSet = new HashMap();

        while (rs.next()){
            int id = rs.getInt("id");
            String ip = rs.getString("ip");
            String pw = rs.getString("pw");
            String username = rs.getString("username");
            Boolean online = rs.getBoolean("online");
            String stones_in = rs.getString("stones_in");
            String stones_out = rs.getString("stones_out");
            String color = rs.getString("color");

            myResSet.put("id", String.valueOf(id));
            myResSet.put("ip", ip);
            myResSet.put("username", username);
            myResSet.put("pw", pw);
            myResSet.put("online", String.valueOf(online));
            myResSet.put("stones_in", stones_in);
            myResSet.put("stones_out", stones_out);
            myResSet.put("color", color);
            if (id == player_id) return myResSet;
        }

        rs.close();
        stmt.close();
        return myResSet;
    }

    protected void delete_stone(int id) throws SQLException {
        String sql = "delete from " + "stone" + " where id=" + id;
        CallableStatement pst = con.prepareCall(sql);
        pst.execute();
        pst.close();
        System.out.println(sql);
    }

    protected void delete_player(int id) throws SQLException {
        for (int i=1; i<=9; i++){
            delete_stone(i + ((id-1)*9));
        }
        String sql = "delete from " + "player" + " where id=" + id;
        CallableStatement pst = con.prepareCall(sql);
        pst.execute();
        pst.close();
        System.out.println(sql);
    }

    protected void delete_mill(int id, boolean only_game) throws SQLException {
        Map<String, String> tmp_vals = get_mill(id);
        if (!only_game){ // deleting players
            delete_player(Integer.parseInt(tmp_vals.get("p1")));
            delete_player(Integer.parseInt(tmp_vals.get("p2")));
        } else{ // setting players offline
            deactivate_player(Integer.parseInt(tmp_vals.get("p1")));
            deactivate_player(Integer.parseInt(tmp_vals.get("p2")));
        }
        String sql = "delete from " + "game" + " where id=" + id;
        CallableStatement pst = con.prepareCall(sql);
        pst.execute();
        pst.close();
        System.out.println(sql);
    }

    protected void change_player(int id, Map<String, String> values) throws SQLException {
        Map<String, String> player_values = get_player(id);
        // CURRENT VALUES //

        String sql = "update player set " +
                "id=" + player_values.get("id") +
                ", ip=" + player_values.get("ip") +
                ", username=" + player_values.get("username") +
                ", pw=" + player_values.get("pw") +
                ", online=" + player_values.get("online") +
                ", stones_out=" + player_values.get("stones_out") +
                ", stones_in=" + player_values.get("stones_in") +
                ", color=" + player_values.get("color") +
                " where player.id=" + id;
        // SQL STRING //

        if(values.get("id")!=null) sql = sql.replaceFirst(
                "id=" + player_values.get("id"),
                "id=" + values.get("id"));

        if(values.get("ip")!=null) sql = sql.replaceFirst(
                "ip=" + player_values.get("ip"),
                "ip=" + values.get("ip"));

        if(values.get("username")!=null) sql = sql.replaceFirst(
                "username=" + player_values.get("username"),
                "username=" + values.get("username"));

        if(values.get("pw")!=null) sql = sql.replaceFirst(
                "pw=" + player_values.get("pw"),
                "pw=" + values.get("pw"));

        if(values.get("online")!=null) sql = sql.replaceFirst(
                "online=" + player_values.get("online"),
                "online=" + values.get("online"));

        if(values.get("stones_in")!=null) sql = sql.replaceFirst(
                "stones_in=" + player_values.get("stones_in"),
                "stones_in=" + values.get("stones_in"));

        if(values.get("stones_out")!=null) sql = sql.replaceFirst(
                "stones_out=" + player_values.get("stones_out"),
                "stones_out=" + values.get("stones_out"));

        if(values.get("color")!=null) sql = sql.replaceFirst(
                "color=" + player_values.get("color"),
                "color=" + values.get("color"));
        // SETTING OPTIONALLY NEW VALUES //

        System.out.println(sql);

        CallableStatement pst = con.prepareCall(sql);
        pst.execute();
        pst.close();
    }

    protected void change_mill(int id, Map<String, String> values) throws SQLException {
        Map<String, String> mill_values = get_mill(id);
        // CURRENT VALUES //

        String sql = "update game set" +
                " id=" + mill_values.get("id") +
                ", player_one=" + mill_values.get("p1") +
                ", player_two=" + mill_values.get("p2") +
                " where game.id=" + id;
        // SQL STRING //

        if(values.get("id")!=null) sql = sql.replaceFirst(
                "id=" + mill_values.get("id"),
                "id=" +  values.get("id"));

        if(values.get("p1")!=null) sql = sql.replaceFirst(
                "player_one=" + mill_values.get("p1"),
                "player_one=" +  values.get("p1"));

        if(values.get("p2")!=null) sql = sql.replaceFirst(
                "player_two=" + mill_values.get("p2"),
                "player_two=" +  values.get("p2"));
        // SETTING OPTIONALLY NEW VALUES //

        CallableStatement pst = con.prepareCall(sql);
        pst.execute();
        pst.close();
    }

    protected Map<String, String> create_player_as_map(String id, String ip, String username, String pw,
                                             Boolean online, String stones_out, String stones_in, String color){
        Map<String, String> tmp_map = new HashMap<String, String>();
        tmp_map.put("id", id);
        tmp_map.put("ip", ip);
        tmp_map.put("username", username);
        tmp_map.put("pw", pw);
        tmp_map.put("online", online!=null ? String.valueOf(online) : null);
        tmp_map.put("stones_out", stones_out);
        tmp_map.put("stones_in", stones_in);
        tmp_map.put("color", color);
        return tmp_map;
    }

    protected Map<String, String> create_game_as_map(String id, String p1, String p2){
        Map<String, String> tmp_map = new HashMap<String, String>();
        tmp_map.put("id", id);
        tmp_map.put("p1", p1);
        tmp_map.put("p2", p2);
        return tmp_map;
    }

}