package br.com.countbellydash.dao;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    private static final Dotenv dotenv = Dotenv.load();

    private static final String URL = dotenv.get("DB_URL");
    private static final String USER = dotenv.get("DB_USER");
    private static final String PASSWORD = dotenv.get("DB_PASSWORD");

    public static Connection getConnection() throws SQLException {
        System.out.println("Tentando conectar ao Supabase...");
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexão com o Supabase bem-sucedida!");
            return conn;
        } catch (Exception e) {
            System.err.println("Falha na conexão com o Supabase: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            }
            catch (SQLException e) {
                System.err.println("Erro ao encerrar a connection mano: " + e.getMessage());
            }
        }
    }
}