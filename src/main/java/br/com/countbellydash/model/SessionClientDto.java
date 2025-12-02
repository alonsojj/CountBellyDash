package br.com.countbellydash.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SessionClientDto {

    @JsonProperty("user_id")
    private String userId;

    private String name;
    private String email;
    private String phone;

    @JsonProperty("document_number")
    private String documentNumber;

    @JsonProperty("document_type")
    private String documentType;

    @JsonProperty("service")
    private String service;

    // getters e setters

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }
}
