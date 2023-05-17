package top.baimoqilin.BaimoTokens;

import java.sql.*;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private Connection connection;
    private String host, database, username, password;
    private int port;

    @Override
    public void onEnable() {
        // load MySQL driver
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            getLogger().severe("无法连接到MySQL Driver，请检查是否安装了MySQL!");
            e.printStackTrace();
            return;
        }

        // read config file
        getConfig().options().copyDefaults(true);
        saveConfig();

        host = getConfig().getString("host");
        port = getConfig().getInt("port");
        database = getConfig().getString("database");
        username = getConfig().getString("username");
        password = getConfig().getString("password");

        // connect to MySQL database
        try {
            openConnection();
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS `baimotokens` (`uuid` VARCHAR(36) NOT NULL, `name` VARCHAR(16) NOT NULL, `token` VARCHAR(12) NOT NULL, `expires` DATETIME NOT NULL);");
        } catch (SQLException e) {
            getLogger().severe("无法操作MySQL服务器。");
            e.printStackTrace();
            return;
        }

        getLogger().info("BaimoTokens已启动");
    }

    @Override
    public void onDisable() {
        // disconnect from MySQL database
        try {
            closeConnection();
        } catch (SQLException e) {
            getLogger().severe("无法关闭对MySQL服务器的链接");
            e.printStackTrace();
        }

        getLogger().info("BaimoTokens已禁用");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("token")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "仅玩家可使用此命令！");
                return true;
            }

            Player player = (Player) sender;

            // generate random token
            StringBuilder sb = new StringBuilder();
            Random random = new Random();
            Set<Integer> usedIndexes = new HashSet<>();
            while (sb.length() < 12) {
                int index = random.nextInt(62);
                if (usedIndexes.contains(index)) {
                    continue;
                }
                usedIndexes.add(index);
                if (index < 10) {
                    sb.append(index);
                } else if (index < 36) {
                    sb.append((char) (index + 55));
                } else {
                    sb.append((char) (index + 61));
                }
            }
            String token = sb.toString();

            // insert token into MySQL database
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `baimotokens` (`uuid`, `name`, `token`, `expires`) VALUES (?, ?, ?, DATE_ADD(NOW(), INTERVAL 1 HOUR));");
                preparedStatement.setString(1, player.getUniqueId().toString());
                preparedStatement.setString(2, player.getName());
                preparedStatement.setString(3, token);
                preparedStatement.execute();
            } catch (SQLException e) {
                player.sendMessage(ChatColor.RED + "生成Token失败。请稍后再试！");
                e.printStackTrace();
                return true;
            }

            player.sendMessage(ChatColor.GREEN + "Tokens: " + token);
            player.sendMessage(ChatColor.GREEN + "它将于1小时后过期！");

            getLogger().info(player.getName() + " 生成了Token: " + token);

            return true;
        }

        return false;
    }

    private void openConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            return;
        }

        synchronized (this) {
            if (connection != null && !connection.isClosed()) {
                return;
            }

            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
        }
    }

    private void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
