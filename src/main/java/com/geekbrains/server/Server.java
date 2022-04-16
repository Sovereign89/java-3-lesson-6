package com.geekbrains.server;

import com.geekbrains.CommonConstants;
import com.geekbrains.server.authorization.AuthService;
import com.geekbrains.server.authorization.InMemoryAuthServiceImpl;
import com.geekbrains.server.authorization.UserData;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static final Logger logger = LogManager.getLogger("Server");
    private final AuthService authService;
    private List<ClientHandler> connectedUsers;
    Connection connection = null;

    public Server() {
        logger.debug("Сервер создаётся");
        this.authService = new InMemoryAuthServiceImpl();
        try (ServerSocket server = new ServerSocket(CommonConstants.SERVER_PORT)) {
            authService.start();
            connect();
            findAll();
            connectedUsers = new ArrayList<>();
            while (true) {
                logger.info("Сервер ожидает подключения");
                Socket socket = server.accept();
                logger.info("Клиент подключился");
                new ClientHandler(this, socket);
            }
        } catch (IOException | SQLException exception) {
            logger.error("Ошибка в работе сервера");
            exception.printStackTrace();
        } finally {
            if (authService != null) {
                authService.end();
            }
        }
    }

    private void findAll() {
        logger.debug("Запущен метод поиска всех клиентов");
        try (Statement statement = connection.createStatement()) {
            try (ResultSet rs = statement.executeQuery("SELECT * FROM USERS;")) {
                while (rs.next()) {
                    UserData user = new UserData(rs.getInt("id"), rs.getString("username"), rs.getString("login"), rs.getString("password"));
                    authService.addUser(rs.getString("login"),user);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void changeNickname(int userId, String nickname) {
        logger.debug("Запущен метод смены никнейма по клиенту");
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(String.format("UPDATE USERS SET USERNAME = \"%s\" WHERE ID = %d;", nickname, userId));
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getUserIdByLogin(String login) {
        logger.debug("Запущен метод поиска идентификатора клиента по логину");
        try (Statement statement = connection.createStatement()) {
            try (ResultSet rs = statement.executeQuery(String.format("SELECT ID FROM USERS WHERE LOGIN = \"%s\";", login))) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public AuthService getAuthService() {
        return authService;
    }

    public boolean isNickNameBusy(String nickName) {
        for (ClientHandler handler : connectedUsers) {
            if (handler.getNickName().equals(nickName)) {
                return true;
            }
        }

        return false;
    }

    public synchronized void broadcastMessage(String message) {
        logger.debug("Сервер получил на отправку сообщение: " + message);
        for (ClientHandler handler : connectedUsers) {
            handler.sendMessage(message);
        }
    }

    public synchronized void privateMessage(String username, String message) {
        logger.debug("Сервер получил на отправу личное сообщение для \"" + username + "\": " + message);
        for(ClientHandler handler: connectedUsers) {
            if(handler.getNickName().equals(username)){
                handler.sendMessage("{" + username + "}: " + message);
            }
        }
    }

    private void connect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:chat.db");
        logger.debug("Успешное подключение в базе данных");
    }

    private void disconnect() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    public synchronized void addConnectedUser(ClientHandler handler) {
        connectedUsers.add(handler);
    }

    public synchronized void disconnectUser(ClientHandler handler) {
        connectedUsers.remove(handler);
    }

    public String getClients() {
        StringBuilder builder = new StringBuilder("/clients ");
        for (ClientHandler user : connectedUsers) {
            builder.append(user.getNickName()).append("\n");
        }

        return builder.toString();
    }
}
