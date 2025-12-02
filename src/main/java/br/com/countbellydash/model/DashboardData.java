package br.com.countbellydash.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.Map;

// contrutores setando os daos que vai estar no dashboard
public class DashboardData {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private int clientesAtendidosTotal;
    private int clientesAguardando;
    private int clientesPF;
    private int clientesPJ;
    private int atendimentoHumano;
    private int reunioesAgendadas;
    private String statusConexao;
    private Map<String, SessionDto> sessionsMap; // Novo campo para armazenar as sessões

    // Construtor original
    public DashboardData(int atendidosTotal, int aguardando, int pf, int pj, int humano, int agendadas, String status) {
        this.clientesAtendidosTotal = atendidosTotal;
        this.clientesAguardando = aguardando;
        this.clientesPF = pf;
        this.clientesPJ = pj;
        this.atendimentoHumano = humano;
        this.reunioesAgendadas = agendadas;
        this.statusConexao = status;
        this.sessionsMap = Collections.emptyMap();
    }

    public DashboardData(String json) {
        // Inicializa statusConexao para evitar NullPointerException
        this.statusConexao = "ONLINE"; 
        this.sessionsMap = Collections.emptyMap();

        if (json.startsWith("CONFIG_ERROR")) {
            this.statusConexao = "ERRO: Configuração de ambiente ausente (" + json.substring(json.indexOf(":") + 2) + ")";
            return; 
        } else if (json.startsWith("API_CONNECTION_ERROR")) {
            this.statusConexao = "ERRO: Falha na conexão com a API (" + json.substring(json.indexOf(":") + 2) + ")";
            return;
        } else if (json.startsWith("API_STATUS_ERROR")) {
            this.statusConexao = "ERRO: Status inesperado da API (" + json.substring(json.indexOf(":") + 2) + ")";
            return;
        }

        Map<String, SessionDto> sessions = parseSessions(json);
        this.sessionsMap = sessions;

        this.clientesAtendidosTotal = sessions.size();

        this.clientesAguardando = (int) sessions.values().stream()
                .filter(s -> s.getState() != 14)
                .count();

        this.clientesPF = (int) sessions.values().stream()
                .filter(s -> isPessoaFisica(s.getClient()))
                .count();

        this.clientesPJ = (int) sessions.values().stream()
                .filter(s -> isPessoaJuridica(s.getClient()))
                .count();

        this.atendimentoHumano = (int) sessions.values().stream()
                .filter(s -> s.getState() == 14)
                .count();

        this.reunioesAgendadas = 0; // Este valor parece ser preenchido por outro serviço, manter 0 por enquanto.

        // Se não houve erro e as sessões estão vazias, significa que não há sessões ativas
        if (sessions.isEmpty() && !this.statusConexao.startsWith("ERRO")) {
            this.statusConexao = "ONLINE: Nenhuma sessão ativa";
        } else if (!this.statusConexao.startsWith("ERRO")) {
            this.statusConexao = "ONLINE";
        }
    }
    private boolean isPessoaFisica(SessionClientDto client) {
        if (client == null || client.getDocumentNumber() == null) return false;
        String doc = client.getDocumentNumber().replaceAll("\\D", "");
        return doc.length() == 11;
    }

    private boolean isPessoaJuridica(SessionClientDto client) {
        if (client == null || client.getDocumentNumber() == null) return false;
        String doc = client.getDocumentNumber().replaceAll("\\D", "");
        return doc.length() == 14;
    }

    public Map<String, SessionDto> parseSessions(String json) {
        try {
            Map<String, SessionDto> parsedMap = objectMapper.readValue(
                    json,
                    new TypeReference<Map<String, SessionDto>>() {}
            );
            System.out.println("DEBUG: Parsed sessions map size: " + parsedMap.size());
            return parsedMap;
        } catch (Exception e) {
            System.err.println("Erro ao parsear JSON de sessões: " + e.getMessage());
            e.printStackTrace(); // Adicionar stack trace para mais detalhes
            return Collections.emptyMap();
        }
    }
    // --- GETTERSSSSSSSSSSS
    public int getClientesAtendidosTotal() { return clientesAtendidosTotal; }
    public int getClientesAguardando() { return clientesAguardando; }
    public int getClientesPF() { return clientesPF; }
    public int getClientesPJ() { return clientesPJ; }
    public int getAtendimentoHumano() { return atendimentoHumano; }
    public int getReunioesAgendadas() { return reunioesAgendadas; }
    public String getStatusConexao() { return statusConexao; }
    public Map<String, SessionDto> getSessionsMap() { return sessionsMap; } // Novo getter

    // --- SETTERS ( Essenciais para o DAO) ---
    public void setClientesAtendidosTotal(int clientesAtendidosTotal) {
        this.clientesAtendidosTotal = clientesAtendidosTotal;
    }

    public void setClientesAguardando(int clientesAguardando) {
        this.clientesAguardando = clientesAguardando;
    }
    
    public void setClientesPF(int clientesPF) {
        this.clientesPF = clientesPF;
    }

    public void setClientesPJ(int clientesPJ) {
        this.clientesPJ = clientesPJ;
    }

    public void setAtendimentoHumano(int atendimentoHumano) {
        this.atendimentoHumano = atendimentoHumano;
    }

    public void setReunioesAgendadas(int reunioesAgendadas) {
        this.reunioesAgendadas = reunioesAgendadas;
    }
    
    public void setStatusConexao(String statusConexao) {
        this.statusConexao = statusConexao;
    }
}