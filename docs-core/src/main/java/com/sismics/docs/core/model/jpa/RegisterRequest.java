package com.sismics.docs.core.model.jpa;

import java.util.Date;

import com.google.common.base.MoreObjects;
import com.sismics.docs.core.constant.RegisterRequestStatusType;
import jakarta.persistence.*;

@Entity
@Table(name = "T_REGISTER_REQUEST")
public class RegisterRequest {

    @Id
    @Column(name = "RR_ID_C", length = 36)
    private String id;

    @Column(name = "RR_USERNAME_C", length = 50, nullable = false)
    private String username;

    @Column(name = "RR_PASSWORD_C", length = 100, nullable = false)
    private String password;

    @Column(name = "RR_EMAIL_C", length = 36, nullable = false)
    private String email;

    @Column(name = "RR_STORAGEQUOTA_N", nullable = false)
    private Long storageQuota;

    @Column(name = "RR_CREATEDATE_D", nullable = false)
    private Date createDate;

    @Column(name = "RR_STATUS_C", length = 18, nullable = false)
    @Enumerated(EnumType.STRING)
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

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setStorageQuota(Long storageQuota) {
        this.storageQuota = storageQuota;
    }

    public Long getStorageQuota() {
        return storageQuota;
    }

    public String getEmail() {
        return email;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setStatus(RegisterRequestStatusType status) {
        this.status = status;
    }

    public RegisterRequestStatusType getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("username", username)
                .add("email", email)
                .add("status", status)
                .toString();
    }
}
