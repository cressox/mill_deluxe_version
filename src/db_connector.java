import java.sql.*;

public class db_connector {
    public static void main(String[] args) throws SQLException {
        String MySQLURL = "jdbc:mysql://localhost:3306/muehle";
        String databseUserName = "root";
        String databasePassword = "root";
        Connection con = null;
        try {
            con = DriverManager.getConnection(MySQLURL, databseUserName, databasePassword);
            if (con != null) {
                System.out.println("Database connection is successful !!!!");
            }
            assert con != null;

            //CallableStatement pst = con.prepareCall("insert into player set id=0");
            CallableStatement pst = con.prepareCall("delete from player where id=0");
            pst.execute();

            pst.close();
            con.close();

            //Statement stmt = con.createStatement();
            //stmt.executeQuery("insert into 'player' (id, color, stones_in, stones_out) values (0, 'white', 0, 0)");
            //ResultSet player = stmt.executeQuery("select * from player");
//            ResultSet game = stmt.executeQuery("SELECT * FROM game");
            //int id_p = player.getInt("id");
//            int id_m = game.getInt("id");

            //System.out.println(id_m + "\t" + id_p);
            //stmt.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}