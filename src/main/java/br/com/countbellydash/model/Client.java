package br.com.countbellydash.model;

public class Client {
    private String userId;
    private String name;
    private DocumentType documentType;
    private String documentNumber;

    public Client(String userId, String name, DocumentType documentType, String documentNumber) {
        this.userId = userId;
        this.name = name;
        this.documentType = documentType;
        this.documentNumber = documentNumber;
    }

    // Getters
    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    // Setters (if needed, but for a model often immutable or set via constructor)
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    @Override
    public String toString() {
        return "Client{"
               + "userId='" + userId + "'\n" +
               ", name='" + name + "'\n" +
               ", documentType=" + documentType + "\n" +
               ", documentNumber='" + documentNumber + "'\n" +
               '}';
    }
}
