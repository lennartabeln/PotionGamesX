package com.tw0far.potiongames.handlers;
// package com.asga.potiongames.handlers;

// import java.io.File;
// import java.sql.DriverManager;
// import java.sql.ResultSet;
// import java.sql.SQLException;

// import org.bukkit.Bukkit;

// import com.asga.potiongames.models.Settings;

// import net.kyori.adventure.text.format.NamedTextColor;

// public class QueryHandler {

//     public void connect() {
//         if (Settings.activateMySQL) {
//             try {
//                 con = DriverManager.getConnection("jdbc:mysql://" + Settings.mySqlConfig.host + ":" + Settings.mySqlConfig.port + "/" + Settings.mySqlConfig.database + "?autoReconnect=true", Settings.mySqlConfig.user, Settings.mySqlConfig.password);
//                 Bukkit.getConsoleSender().sendMessage(Settings.prefix + NamedTextColor.GREEN + chatmessages.get(36));
//                 Settings.activateMySQL = true;
//             } catch (SQLException e) {
//                 Bukkit.getConsoleSender().sendMessage(Settings.prefix + NamedTextColor.RED + chatmessages.get(37) + ": " + e.getMessage());
//                 Settings.activateMySQL = false;
//             }
//         } else {
//             con = null;
//             try {
//                 File dbFile = new File(getDataFolder(), "stats.db");
//                 String url = "jdbc:sqlite:" + dbFile.getPath();
//                 con = DriverManager.getConnection(url);
//                 st = con.createStatement();
//                 Bukkit.getConsoleSender().sendMessage(Settings.prefix + NamedTextColor.GREEN + chatmessages.get(36));
//             } catch (SQLException e) {
//                 Bukkit.getConsoleSender().sendMessage(Settings.prefix + NamedTextColor.RED + chatmessages.get(37) + ": " + e.getMessage());
//             }
//         }
//     }

//     public void close() {
//         try {
//             if (con != null) {
//                 con.close();
//                 Bukkit.getConsoleSender().sendMessage(Settings.prefix + NamedTextColor.GREEN + chatmessages.get(38));
//             }
//         } catch (SQLException e) {
//             Bukkit.getConsoleSender().sendMessage(Settings.prefix + NamedTextColor.RED + chatmessages.get(39) + ": " + e.getMessage());
//         }
//     }

//     public void update(String qry) {
//         if (Settings.activateMySQL) {
//             if (con != null) {
//                 try {
//                     st = con.createStatement();
//                     st.executeUpdate(qry);
//                     st.close();
//                 } catch (SQLException e) {
//                     connect();
//                     Bukkit.getConsoleSender().sendMessage(Settings.prefix + NamedTextColor.RED + chatmessages.get(37) + ": " + e.getMessage());
//                 }
//             }
//         } else {
//             try {
//                 st.execute(qry);
//             } catch (SQLException e) {
//                 Bukkit.getConsoleSender().sendMessage(Settings.prefix + NamedTextColor.RED + chatmessages.get(37) + ": " + e.getMessage());
//             }
//         }
//     }

//     public ResultSet query(String qry) {
//         if (Settings.activateMySQL) {
//             if (con != null) {
//                 ResultSet rs = null;
//                 try {
//                     st = con.createStatement();
//                     rs = st.executeQuery(qry);
//                 } catch (SQLException e) {
//                     connect();
//                     Bukkit.getConsoleSender().sendMessage(Settings.prefix + NamedTextColor.RED + chatmessages.get(37) + ": " + e.getMessage());
//                 }
//                 return rs;
//             }
//         } else {
//             try {
//                 return st.executeQuery(qry);
//             } catch (SQLException e) {
//                 Bukkit.getConsoleSender().sendMessage(Settings.prefix + NamedTextColor.RED + chatmessages.get(37) + ": " + e.getMessage());
//             }
//         }
//         return null;
//     }

//     public void ConnectMySQL() {
//         if (con != null) {
//             try {
//                 update("CREATE TABLE IF NOT EXISTS Stats(UUID varchar(64), ROUNDS int, WINS int, LOSSES int, KILLS int, DEATHS int, KD double);");
//             } catch (Exception e) {
//                 update("CREATE TABLE IF NOT EXISTS Stats(UUID varchar(64), ROUNDS int, WINS int, LOSTS int, KILLS int, DEATHS int, KD double);");
//             }
//         }
//     }

//     public boolean playerExists(String uuid) {
//         try (ResultSet rs = query("SELECT * FROM Stats WHERE UUID= '" + uuid + "'")) {
//             if (rs.next()) {
//                 return rs.getString("UUID") != null;
//             }
//             return false;
//         } catch (SQLException e) {
//             Bukkit.getConsoleSender().sendMessage(Settings.prefix + NamedTextColor.RED + chatmessages.get(37) + ": " + e.getMessage());
//         }
//         return false;
//     }

//     public void createPlayer(String uuid) {
//         if (!playerExists(uuid)) {
//             try {
//                 update("INSERT INTO Stats(UUID, ROUNDS, WINS, LOSSES, KILLS, DEATHS, KD) VALUES ('" + uuid + "', '0', '0', '0', '0', '0', '0');");
//             } catch (Exception e) {
//                 update("INSERT INTO Stats(UUID, ROUNDS, WINS, LOSTS, KILLS, DEATHS, KD) VALUES ('" + uuid + "', '0', '0', '0', '0', '0', '0');");
//             }
//         }
//     }

//     public int getKills(String uuid) {
//         int i = 0;
//         if (playerExists(uuid)) {
//             try (ResultSet rs = query("SELECT * FROM Stats WHERE UUID= '" + uuid + "'")) {
//                 if ((rs.next())) {
//                     rs.getInt("KILLS");
//                 }
//                 i = rs.getInt("KILLS");
//             } catch (SQLException e) {
//                 Bukkit.getConsoleSender().sendMessage(Settings.prefix + NamedTextColor.RED + chatmessages.get(37) + ": " + e.getMessage());
//             }
//         } else {
//             createPlayer(uuid);
//             getKills(uuid);
//         }
//         return i;
//     }

//     public int getDeaths(String uuid) {
//         int i = 0;
//         if (playerExists(uuid)) {
//             try (ResultSet rs = query("SELECT * FROM Stats WHERE UUID= '" + uuid + "'")) {
//                 if ((rs.next())) {
//                     rs.getInt("DEATHS");
//                 }
//                 i = rs.getInt("DEATHS");
//             } catch (SQLException e) {
//                 Bukkit.getConsoleSender().sendMessage(Settings.prefix + NamedTextColor.RED + chatmessages.get(37) + ": " + e.getMessage());
//             }
//         } else {
//             createPlayer(uuid);
//             getDeaths(uuid);
//         }
//         return i;
//     }

//     public double getKD(String uuid) {
//         double i = 0;
//         if (playerExists(uuid)) {
//             try (ResultSet rs = query("SELECT * FROM Stats WHERE UUID= '" + uuid + "'")) {
//                 if ((rs.next())) {
//                     rs.getDouble("KD");
//                 }
//                 i = rs.getDouble("KD");
//             } catch (SQLException e) {
//                 Bukkit.getConsoleSender().sendMessage(Settings.prefix + NamedTextColor.RED + chatmessages.get(37) + ": " + e.getMessage());
//             }
//         } else {
//             createPlayer(uuid);
//             getKD(uuid);
//         }
//         return i;
//     }

//     public int getWins(String uuid) {
//         int i = 0;
//         if (playerExists(uuid)) {
//             try (ResultSet rs = query("SELECT * FROM Stats WHERE UUID= '" + uuid + "'")) {
//                 if ((rs.next())) {
//                     rs.getInt("WINS");
//                 }
//                 i = rs.getInt("WINS");
//             } catch (SQLException e) {
//                 Bukkit.getConsoleSender().sendMessage(Settings.prefix + NamedTextColor.RED + chatmessages.get(37) + ": " + e.getMessage());
//             }
//         } else {
//             createPlayer(uuid);
//             getWins(uuid);
//         }
//         return i;
//     }

//     public int getLosses(String uuid) {
//         int i = 0;
//         if (playerExists(uuid)) {
//             try (ResultSet rs = query("SELECT * FROM Stats WHERE UUID= '" + uuid + "'")) {
//                 if ((rs.next())) {
//                     try {
//                         rs.getInt("LOSSES");
//                     } catch (Exception e) {
//                         rs.getInt("LOSTS");
//                     }
//                 }
//                 try {
//                     i = rs.getInt("LOSSES");
//                 } catch (Exception e) {
//                     i = rs.getInt("LOSTS");
//                 }
//             } catch (Exception e) {
//                 Bukkit.getConsoleSender().sendMessage(Settings.prefix + NamedTextColor.RED + chatmessages.get(37) + ": " + e.getMessage());
//             }
//         } else {
//             createPlayer(uuid);
//             getLosses(uuid);
//         }
//         return i;
//     }

//     public int getRounds(String uuid) {
//         int i = 0;
//         if (playerExists(uuid)) {
//             try (ResultSet rs = query("SELECT * FROM Stats WHERE UUID= '" + uuid + "'")) {
//                 if ((rs.next())) {
//                     rs.getInt("ROUNDS");
//                 }
//                 i = rs.getInt("ROUNDS");
//             } catch (SQLException e) {
//                 Bukkit.getConsoleSender().sendMessage(Settings.prefix + NamedTextColor.RED + chatmessages.get(37) + ": " + e.getMessage());
//             }
//         } else {
//             createPlayer(uuid);
//             getRounds(uuid);
//         }
//         return i;
//     }

//     public void setKills(String uuid, int kills) {
//         if (playerExists(uuid)) {
//             update("UPDATE Stats SET KILLS= '" + kills + "' WHERE UUID= '" + uuid + "';");
//         } else {
//             createPlayer(uuid);
//             setKills(uuid, kills);
//         }
//     }

//     public void setDeaths(String uuid, int deaths) {
//         if (playerExists(uuid)) {
//             update("UPDATE Stats SET DEATHS= '" + deaths + "' WHERE UUID= '" + uuid + "';");
//         } else {
//             createPlayer(uuid);
//             setDeaths(uuid, deaths);
//         }
//     }

//     public void setKD(String uuid, double kd) {
//         if (playerExists(uuid)) {
//             int kills = getKills(uuid);
//             int deaths = getDeaths(uuid);
//             if (deaths != 0) {
//                 kd = ((double) kills) / ((double) deaths);
//             } else {
//                 kd = kills;
//             }
//             kd = kd * 1000;
//             kd = Math.round(kd);
//             kd = kd / 1000;
//             update("UPDATE Stats SET KD= '" + kd + "' WHERE UUID= '" + uuid + "';");
//         } else {
//             createPlayer(uuid);
//             setKD(uuid, kd);
//         }
//     }

//     public void setWins(String uuid, int wins) {
//         if (playerExists(uuid)) {
//             update("UPDATE Stats SET WINS= '" + wins + "' WHERE UUID= '" + uuid + "';");
//         } else {
//             createPlayer(uuid);
//             setWins(uuid, wins);
//         }
//     }

//     public void setLosses(String uuid, int losses) {
//         if (playerExists(uuid)) {
//             try {
//                 update("UPDATE Stats SET LOSSES= '" + losses + "' WHERE UUID= '" + uuid + "';");
//             } catch (Exception e) {
//                 update("UPDATE Stats SET LOSTS= '" + losses + "' WHERE UUID= '" + uuid + "';");
//             }
//         } else {
//             createPlayer(uuid);
//             setLosses(uuid, losses);
//         }
//     }

//     public void setRounds(String uuid, int rounds) {
//         if (playerExists(uuid)) {
//             int wins = getWins(uuid);
//             int losses = getLosses(uuid);
//             rounds = wins + losses;
//             update("UPDATE Stats SET ROUNDS= '" + rounds + "' WHERE UUID= '" + uuid + "';");
//         } else {
//             createPlayer(uuid);
//             setRounds(uuid, rounds);
//         }
//     }
// }
