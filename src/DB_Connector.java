import java.net.InetAddress;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DB_Connector {
    static String MySQLURL;
    static String databseUserName;
    static String databasePassword;
    static Connection con = null;

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

    protected int get_count_of_mills_to_join(InetAddress ip) throws SQLException {
        String sql = "SELECT * FROM game";
        open_con("jdbc:mysql://localhost:3306/muehle", "root", "root", ip);
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        int i = 0;
        while(rs.next()){
            if(rs.getInt("player_two")==-1) i++; // mill with only one player
        }
        close_con(ip);
        return i;
    }

    protected int get_count_of_registered_players(InetAddress ip) throws SQLException {
        String sql = "SELECT MAX(Id) FROM player";
        open_con("jdbc:mysql://localhost:3306/muehle", "root", "root", ip);
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        int tmp_count = rs.getInt("MAX(Id)");
        close_con(ip);
        return tmp_count;
    }

    protected int get_count_of_active_players(InetAddress ip) throws SQLException {
        String sql = "SELECT * FROM player";
        open_con("jdbc:mysql://localhost:3306/muehle", "root", "root", ip);
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        int count = 0;
        while(rs.next()){
            if(rs.getBoolean("online")) count++;
        }
        close_con(ip);
        return count;
    }

    protected int[] get_active_players(InetAddress ip) throws SQLException { // list of ids of online user
        open_con("jdbc:mysql://localhost:3306/muehle", "root", "root", ip);
        String sql = "SELECT * FROM player";
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        if (rs==null) return null;
        int[] myResSetList = new int[get_count_of_active_players(ip)];
        int i = 0;
        while (rs.next()){
            if(rs.getBoolean("online")) {
                myResSetList[i] = rs.getInt("id");
                i++;
            }
        }
        rs.close();
        stmt.close();
        return myResSetList;
    }

    protected int[] get_player_in_game_ids(InetAddress ip) throws SQLException { // list of ids of online user
        open_con("jdbc:mysql://localhost:3306/muehle", "root", "root", ip);
        String sql = "SELECT * FROM game";
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        if (rs==null) return null;
        int[] myResSetList = new int[get_count_of_mills_to_join(ip)];
        int i = 0;
        while (rs.next()){
            if(rs.getInt("player_two")==-1) { // no player two in mill
                myResSetList[i] = rs.getInt("player_one");
                i++;
            }
        }
        rs.close();
        stmt.close();
        return myResSetList;
    }

    protected void open_con(String db_url, String db_user, String db_user_pw, InetAddress ip) throws SQLException {
        MySQLURL = db_url;
        databseUserName = db_user;
        databasePassword = db_user_pw;
        con = DriverManager.getConnection(MySQLURL, databseUserName, databasePassword);
        System.out.println("new DB_CON from CH of " + ip.getHostAddress());
    }

    protected void close_con(InetAddress ip) throws SQLException {
        System.out.println("closed DB_CON from CH of " + ip.getHostAddress());
        con.close();
        con = null;
    }

    protected void insert_stone(int id, InetAddress ip) throws SQLException {
        String sql = "INSERT INTO `stone` (`id`, `in_game`, `tmp_lines`, `tmp_index`) VALUES (" + id + ", false, NULL, NULL)";
        CallableStatement pst = con.prepareCall(sql);
        pst.execute();
        pst.close();
    }

    protected void insert_player_old(String color, int id, InetAddress ip) throws SQLException {
        Map<String, String> player = get_player_by_id(id, ip); // check if player exists
        if (player==null || player.isEmpty()){ // player doesnt exist
            String sql = "INSERT INTO `player` (`id`, `color`, `stones_in`, `stones_out`, `ip`, `pw`, `username`, `online`) VALUES ('" + id + "', '" + color + "', '0', '9', NULL, NULL, NULL, '0')";
            CallableStatement pst = con.prepareCall(sql);
            for (int i = 1; i <= 9; i++) {
                insert_stone(i + ((id - 1) * 9), ip);
            }
            pst.execute();
            pst.close();
            System.out.println(sql);
        } else { // player exists
            activate_player(id, ip);
        }
    }

    protected void insert_player(int id, String color, int stones_in, int stones_out, InetAddress ip, String pw, String username, boolean online) throws SQLException {
        open_con("jdbc:mysql://localhost:3306/muehle", "root", "root", ip);
        Map<String, String> player = get_player_by_id(id, ip); // check if player exists
        if (player==null || player.isEmpty()){ // player doesnt exist
            String sql = "INSERT INTO `player` (" +
                    "`id`, `color`, `stones_in`, `stones_out`, `ip`, `pw`, `username`, `online`, `status`, `mill_id`) " +
                    "VALUES ('" + id + "', '" + color + "', '" + stones_in + "', '" + stones_out + "'," +
                    "'" + ip.getHostAddress() + "', '" + pw + "', '" + username + "', '" + (online ? 1 : 0) + "', 'offline', 0)";
            CallableStatement pst = con.prepareCall(sql);
            for (int i = 1; i <= 9; i++) {
                insert_stone(i + ((id - 1) * 9), ip);
            }
            pst.execute();
            pst.close();
            System.out.println(sql);
        } else { // player exists
            activate_player(id, ip);
        }
        this.close_con(ip);
    }

    protected void activate_player(int player_id, InetAddress ip) throws SQLException {
        open_con("jdbc:mysql://localhost:3306/muehle", "root", "root", ip);
        String sql = "update player set online=true, status='online' where id=" + player_id;
        CallableStatement pst = con.prepareCall(sql);
        pst.execute();
        pst.close();
        System.out.println(sql);
        this.close_con(ip);
    }

    protected void deactivate_player(int player_id, InetAddress ip) throws SQLException {
        open_con("jdbc:mysql://localhost:3306/muehle", "root", "root", ip);
        String sql = "update player set online=false, status='offline', mill_id='-1' where id=" + player_id;
        CallableStatement pst = con.prepareCall(sql);
        pst.execute();
        pst.close();
        System.out.println(sql);
        this.close_con(ip);
    }

    protected void insert_mill(int id, int id_p1, int id_p2, InetAddress ip) throws SQLException { // id_p2 = -1 if no player
        open_con("jdbc:mysql://localhost:3306/muehle", "root", "root", ip);
//        insert_player_old("white", id_p1, ip);
//        insert_player_old("black", id_p2, ip);
        Map<String, String> tmpMill = get_mill(id, ip);
        if (tmpMill.isEmpty() || tmpMill == null){
            String sql = "INSERT INTO `game` (`id`, `running`, `player_one`, `player_two`) VALUES ('" + id + "', '1', '" + id_p1 + "', '" + id_p2 + "')";
            CallableStatement pst = con.prepareCall(sql);
            pst.execute();
            pst.close();
            System.out.println(sql);
        }
        this.close_con(ip);
    }

    private Map get_mill(int mill_id, InetAddress ip) throws SQLException {
        open_con("jdbc:mysql://localhost:3306/muehle", "root", "root", ip);

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

    void set_player_in_game(int user_id, int mill_id_to_join, InetAddress ip) throws SQLException {
        Map<String, String> tmpMill = get_mill(mill_id_to_join, ip); // current game
        Map<String, String> newMill = create_game_as_map(tmpMill.get("id"), tmpMill.get("p1"), String.valueOf(user_id));
        System.out.println(tmpMill);
        System.out.println(newMill);
        change_mill(mill_id_to_join, newMill, ip); // update current game in db

        Map<String, String> tmpPlayer = get_player_by_id(user_id, ip); // new player
        Map<String, String> newPlayer = create_player_as_map(
                tmpPlayer.get("id"),
                tmpPlayer.get("ip"),
                tmpPlayer.get("username"),
                tmpPlayer.get("pw"),
                true,
                tmpPlayer.get("stones_out"),
                tmpPlayer.get("stones_in"),
                tmpPlayer.get("color"),
                "in game",
                String.valueOf(mill_id_to_join)
        ); // new player
        change_player(Integer.parseInt(tmpPlayer.get("id")), newPlayer, ip); // update new player in db
    }

    private Map get_player_by_id(int player_id, InetAddress ip) throws SQLException {
        if (con==null) open_con("jdbc:mysql://localhost:3306/muehle", "root", "root", ip);

        String sql = "SELECT * FROM player WHERE id = " + player_id;

        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        if (rs==null) return null;

        Map<String, String> myResSet = new HashMap();

        while (rs.next()){
            int id = rs.getInt("id");
            String tmp_ip = rs.getString("ip");
            String pw = rs.getString("pw");
            String username = rs.getString("username");
            Boolean online = rs.getBoolean("online");
            String stones_in = rs.getString("stones_in");
            String stones_out = rs.getString("stones_out");
            String color = rs.getString("color");
            String status = rs.getString("status");
            String mill_id = rs.getString("mill_id");

            myResSet.put("id", String.valueOf(id));
            myResSet.put("ip", tmp_ip);
            myResSet.put("username", username);
            myResSet.put("pw", pw);
            myResSet.put("online", String.valueOf(online));
            myResSet.put("stones_in", stones_in);
            myResSet.put("stones_out", stones_out);
            myResSet.put("color", color);
            myResSet.put("status", status);
            myResSet.put("mill_id", mill_id);
            if (id == player_id) return myResSet;
        }

        rs.close();
        stmt.close();
        return myResSet;
    }

    private Map get_player_by_username(String player_username, InetAddress ip) throws SQLException {
        open_con("jdbc:mysql://localhost:3306/muehle", "root", "root", ip);

        String sql = "SELECT * FROM player WHERE username = '" + player_username + "'";

        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        if (rs==null) return null;

        Map<String, String> myResSet = new HashMap();

        while (rs.next()){
            int id = rs.getInt("id");
            String tmp_ip = rs.getString("ip");
            String pw = rs.getString("pw");
            String username = rs.getString("username");
            Boolean online = rs.getBoolean("online");
            String stones_in = rs.getString("stones_in");
            String stones_out = rs.getString("stones_out");
            String color = rs.getString("color");
            String status = rs.getString("status");

            myResSet.put("id", String.valueOf(id));
            myResSet.put("ip", tmp_ip);
            myResSet.put("username", username);
            myResSet.put("pw", pw);
            myResSet.put("online", String.valueOf(online));
            myResSet.put("stones_in", stones_in);
            myResSet.put("stones_out", stones_out);
            myResSet.put("color", color);
            myResSet.put("status", status);
            if (username == player_username) return myResSet;
        }

        rs.close();
        stmt.close();

        return myResSet;
    }

    protected void delete_stone(int id, InetAddress ip) throws SQLException {
        String sql = "delete from " + "stone" + " where id=" + id;
        CallableStatement pst = con.prepareCall(sql);
        pst.execute();
        pst.close();
        System.out.println(sql);
    }

    protected void delete_player(int id, InetAddress ip) throws SQLException {
        open_con("jdbc:mysql://localhost:3306/muehle", "root", "root", ip);

        for (int i=1; i<=9; i++){
            delete_stone(i + ((id-1)*9), ip);
        }
        String sql = "delete from " + "player" + " where id=" + id;
        CallableStatement pst = con.prepareCall(sql);
        pst.execute();
        pst.close();
        System.out.println(sql);
        this.close_con(ip);

    }

    protected void delete_mill(int id, boolean only_game, InetAddress ip) throws SQLException {
        Map<String, String> tmp_vals = get_mill(id, ip);
        deactivate_player(Integer.parseInt(tmp_vals.get("p1")), ip);
        deactivate_player(Integer.parseInt(tmp_vals.get("p2")), ip);
        String sql = "delete from " + "game" + " where id=" + id;

        open_con("jdbc:mysql://localhost:3306/muehle", "root", "root", ip);
        CallableStatement pst = con.prepareCall(sql);
        pst.execute();
        pst.close();
        System.out.println(sql);
        this.close_con(ip);
    }

    protected void change_player(int id, Map<String, String> values, InetAddress ip) throws SQLException {
        open_con("jdbc:mysql://localhost:3306/muehle", "root", "root", ip);

        Map<String, String> player_values = get_player_by_id(id, ip);
        // CURRENT VALUES //

        String sql = "update player set " +
                "id=" + player_values.get("id") +
                ", ip='" + player_values.get("ip") +
                "', username='" + player_values.get("username") +
                "', pw='" + player_values.get("pw") +
                "', online=" + player_values.get("online") +
                ", stones_out=" + player_values.get("stones_out") +
                ", stones_in=" + player_values.get("stones_in") +
                ", color='" + (player_values.get("color")) +
                "', status='" + player_values.get("status") +
                "', mill_id=" + player_values.get("mill_id") +
                " where player.id=" + id;
        System.out.println(player_values.get("color"));
        // SQL STRING //

        if(values.get("id")!=null) sql = sql.replaceFirst(
                "id=" + player_values.get("id"),
                "id=" + values.get("id"));

        if(values.get("status")!=null) sql = sql.replaceFirst(
                "status='" + player_values.get("status"),
                "status='" + values.get("status"));

        if(values.get("mill_id")!=null) sql = sql.replaceFirst(
                "mill_id=" + player_values.get("mill_id"),
                "mill_id=" + values.get("mill_id"));

        if(values.get("ip")!=null) sql = sql.replaceFirst(
                "ip='" + player_values.get("ip"),
                "ip='" + values.get("ip"));

        if(values.get("username")!=null) sql = sql.replaceFirst(
                "username='" + player_values.get("username"),
                "username='" + values.get("username"));

        if(values.get("pw")!=null) sql = sql.replaceFirst(
                "pw='" + player_values.get("pw"),
                "pw='" + values.get("pw"));

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
                "color='" + player_values.get("color"),
                "color='" + values.get("color"));
        // SETTING OPTIONALLY NEW VALUES //

        System.out.println(sql);

        CallableStatement pst = con.prepareCall(sql);
        pst.execute();
        pst.close();
        this.close_con(ip);
    }

    protected void change_mill(int id, Map<String, String> values, InetAddress ip) throws SQLException {
        open_con("jdbc:mysql://localhost:3306/muehle", "root", "root", ip);

        Map<String, String> mill_values = get_mill(id, ip);
        // CURRENT VALUES //

        String sql = "update game set" +
                " id=" + mill_values.get("id") +
                ", player_one='" + mill_values.get("p1") +
                "', player_two='" + mill_values.get("p2") +
                "', running=" + mill_values.get("running") +
                " where game.id=" + id;
        // SQL STRING //

        if(values.get("id")!=null) sql = sql.replaceFirst(
                "id='" + mill_values.get("id"),
                "id='" +  values.get("id"));

        if(values.get("running")!=null) sql = sql.replaceFirst(
                "running=" + mill_values.get("running"),
                "running=" +  values.get("running"));

        if(values.get("p1")!=null) sql = sql.replaceFirst(
                "player_one='" + mill_values.get("p1"),
                "player_one='" +  values.get("p1"));

        if(values.get("p2")!=null) sql = sql.replaceFirst(
                "player_two='" + mill_values.get("p2"),
                "player_two='" +  values.get("p2"));
        // SETTING OPTIONALLY NEW VALUES //
        System.out.println(sql);
        CallableStatement pst = con.prepareCall(sql);
        pst.execute();
        pst.close();
        this.close_con(ip);
    }

    protected Map<String, String> create_player_as_map(String id, String ip, String username, String pw,
                                             Boolean online, String stones_out, String stones_in, String color,
                                                       String status, String mill_id){

        Map<String, String> tmp_map = new HashMap<String, String>();
        tmp_map.put("id", id);
        tmp_map.put("ip", ip);
        tmp_map.put("username", username);
        tmp_map.put("pw", pw);
        tmp_map.put("online", online!=null ? String.valueOf(online) : null);
        tmp_map.put("stones_out", stones_out);
        tmp_map.put("stones_in", stones_in);
        tmp_map.put("color", color);
        tmp_map.put("status", status);
        tmp_map.put("mill_id", mill_id);
        return tmp_map;
    }

    protected Map<String, String> create_game_as_map(String id, String p1, String p2){
        Map<String, String> tmp_map = new HashMap<String, String>();
        tmp_map.put("id", id);
        tmp_map.put("p1", p1);
        tmp_map.put("p2", p2);
        return tmp_map;
    }

    protected void wipe(InetAddress ip, String table) throws SQLException {
        open_con("jdbc:mysql://localhost:3306/muehle", "root", "root", ip);
        String sql = "delete from " + table;
        CallableStatement pst = con.prepareCall(sql);
        pst.execute();
        pst.close();
        close_con(ip);
    }

    protected void wipe_all(InetAddress ip) throws SQLException {
        wipe(ip, "game");
        wipe(ip, "player");
        wipe(ip, "stone");
    }

    // GETTER //
    public Map<String, String> getPlayerByID(int id, InetAddress ip) throws SQLException {
        if (con == null) open_con("jdbc:mysql://localhost:3306/muehle", "root", "root", ip);
        Map<String, String> tmp = get_player_by_id(id, ip);
        this.close_con(ip);
        return tmp;
    }
    public Map<String, String> getPlayerByUsername(String username, InetAddress ip) throws SQLException {
        if (con == null) open_con("jdbc:mysql://localhost:3306/muehle", "root", "root", ip);
        Map<String, String> tmp = get_player_by_username(username, ip);
        this.close_con(ip);
        return tmp;
    }

    public Map<String, String> getMillByID(int id, InetAddress ip) throws SQLException {
        if (con == null) open_con("jdbc:mysql://localhost:3306/muehle", "root", "root", ip);
        Map<String, String> tmp = get_mill(id, ip);
        this.close_con(ip);
        return tmp;
    }

}