package com.sismics.docs.core.dao;

import com.sismics.docs.core.constant.RegisterRequestStatusType;
import com.sismics.docs.core.dao.dto.RegisterRequestDto;
import com.sismics.docs.core.dao.dto.UserDto;
import com.sismics.docs.core.model.jpa.Comment;
import com.sismics.docs.core.model.jpa.RegisterRequest;
import com.sismics.util.context.ThreadLocalContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Register Request DAO
 *
 * @author IceLocke
 */
public class RegisterRequestDao {
    /**
     * Creates a new register request
     * @param request Register request
     * @return New ID
     */
    public String create(RegisterRequest request) throws Exception {
        request.setId(UUID.randomUUID().toString());

        // Check whether the user has already sent a request in processing
        // or the username / email has already existed
        EntityManager em = ThreadLocalContext.get().getEntityManager();

        // Check for duplicate request
        Query q = em.createQuery("select r from RegisterRequest r " +
                "where r.username = :username and r.status = :status");
        q.setParameter("username", request.getUsername());
        q.setParameter("status", RegisterRequestStatusType.PROCESSING);
        if (!q.getResultList().isEmpty()) {
            throw new Exception("AlreadyExistingRegisterRequest");
        }

        // Check for duplicate user
        q = em.createQuery("select u from User u where u.username = :username");
        q.setParameter("username", request.getUsername());
        if (!q.getResultList().isEmpty()) {
            throw new Exception("AlreadyExistingUsername");
        }

        request.setCreateDate(new Date());
        request.setStatus(RegisterRequestStatusType.PROCESSING);
        em.persist(request);

        return request.getId();
    }

    /**
     * Get all unprocessed register requests
     * @return List of requests
     */
    public List<RegisterRequestDto> getActiveRegisterRequests() {
        EntityManager em = ThreadLocalContext.get().getEntityManager();

        Query q = em.createQuery("select r from RegisterRequest r where r.status = :status");
        q.setParameter("status", RegisterRequestStatusType.PROCESSING);
        @SuppressWarnings("unchecked")
        List<RegisterRequest> l = q.getResultList();
        List<RegisterRequestDto> requests = new ArrayList<>();
        for (RegisterRequest r : l) {
            RegisterRequestDto dto = new RegisterRequestDto();
            dto.setId(r.getId());
            dto.setUsername(r.getUsername());
            dto.setEmail(r.getEmail());
            dto.setCreateTimestamp(r.getCreateDate().getTime());
            dto.setStorageQuota(r.getStorageQuota());
            dto.setStatus(r.getStatus());
            requests.add(dto);
        }

        return requests;
    }

    public RegisterRequest getById(String id) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        try {
            Query q = em.createQuery("select r from RegisterRequest r where r.id = :id");
            q.setParameter("id", id);
            return (RegisterRequest) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public String processRequest(String id, RegisterRequestStatusType status) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        RegisterRequest request = getById(id);
        if (request != null) {
            request.setStatus(status);
            em.persist(request);
            return request.getId();
        }
        return null;
    }
}
