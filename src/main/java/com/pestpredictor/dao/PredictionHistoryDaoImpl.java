package com.pestpredictor.dao;

import com.pestpredictor.model.PredictionHistory;
import com.pestpredictor.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hibernate implementation of PredictionHistoryDao.
 */
@Repository
public class PredictionHistoryDaoImpl implements PredictionHistoryDao {

    private static final Logger logger = Logger.getLogger(PredictionHistoryDaoImpl.class.getName());

    @Override
    public PredictionHistory save(PredictionHistory history) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(history);
            tx.commit();
            logger.info("PredictionHistory saved for user: " + history.getUser().getUsername());
            return history;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.log(Level.SEVERE, "Error saving prediction history", e);
            throw new RuntimeException("Failed to save prediction history", e);
        }
    }

    @Override
    public Optional<PredictionHistory> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            PredictionHistory history = session.get(PredictionHistory.class, id);
            return Optional.ofNullable(history);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding prediction history by id", e);
            return Optional.empty();
        }
    }

    @Override
    public List<PredictionHistory> findByUserId(Long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<PredictionHistory> query = session.createQuery(
                    "FROM PredictionHistory ph WHERE ph.user.id = :userId ORDER BY ph.createdAt DESC",
                    PredictionHistory.class);
            query.setParameter("userId", userId);
            return query.getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding prediction history by userId", e);
            throw new RuntimeException("Failed to retrieve prediction history", e);
        }
    }

    @Override
    public List<PredictionHistory> findByUserId(Long userId, int limit) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<PredictionHistory> query = session.createQuery(
                    "FROM PredictionHistory ph WHERE ph.user.id = :userId ORDER BY ph.createdAt DESC",
                    PredictionHistory.class);
            query.setParameter("userId", userId);
            query.setMaxResults(limit);
            return query.getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding limited prediction history", e);
            throw new RuntimeException("Failed to retrieve prediction history", e);
        }
    }

    @Override
    public List<PredictionHistory> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM PredictionHistory ph ORDER BY ph.createdAt DESC",
                    PredictionHistory.class).getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error fetching all prediction histories", e);
            throw new RuntimeException("Failed to retrieve all prediction history", e);
        }
    }

    @Override
    public void delete(Long id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            PredictionHistory history = session.get(PredictionHistory.class, id);
            if (history != null) {
                session.remove(history);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.log(Level.SEVERE, "Error deleting prediction history", e);
            throw new RuntimeException("Failed to delete prediction history", e);
        }
    }

    @Override
    public long countByUserId(Long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createQuery(
                    "SELECT COUNT(ph) FROM PredictionHistory ph WHERE ph.user.id = :userId",
                    Long.class)
                    .setParameter("userId", userId)
                    .uniqueResult();
            return count != null ? count : 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error counting prediction histories", e);
            return 0;
        }
    }
}
