package com.sismics.docs.core.dao.dto;

import com.sismics.docs.core.constant.RegisterRequestStatusType;

public class RegisterRequestDto {

    private String id;

    private String username;

    private String email;

    private Long storageQuota;

    private Long createTimestamp;

    private RegisterRequestStatusType status;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setStorageQuota(Long storageQuota) {
        this.storageQuota = storageQuota;
    }

    public Long getStorageQuota() {
        return storageQuota;
    }

    public void setCreateTimestamp(Long createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    public Long getCreateTimestamp() {
        return createTimestamp;
    }

    public void setStatus(RegisterRequestStatusType status) {
        this.status = status;
    }

    public RegisterRequestStatusType getStatus() {
        return status;
    }
}
