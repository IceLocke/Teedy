package com.sismics.docs.core.dao;

import com.sismics.docs.core.constant.RegisterRequestStatusType;
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
    public String create(RegisterRequest request) {
        request.setId(UUID.randomUUID().toString());

        EntityManager em = ThreadLocalContext.get().getEntityManager();
        request.setCreateDate(new Date());
        em.persist(request);

        return request.getId();
    }

    /**
     * Get all unprocessed register requests
     * @return List of requests
     */
    public List<RegisterRequest> getActiveRegisterRequests() {
        EntityManager em = ThreadLocalContext.get().getEntityManager();

        Query q = em.createQuery("select r from RegisterRequest r where r.status = :status");
        q.setParameter("status", RegisterRequestStatusType.PROCESSING);
        @SuppressWarnings("unchecked")
        List<RegisterRequest> requests = q.getResultList();

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
