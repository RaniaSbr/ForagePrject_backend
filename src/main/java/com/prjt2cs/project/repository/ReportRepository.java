// Ajoutez ces méthodes à votre ReportRepository pour de meilleures performances

package com.prjt2cs.project.repository;

import com.prjt2cs.project.model.Puit;
import com.prjt2cs.project.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    // Méthode optimisée pour récupérer les rapports par puits avec leurs coûts
    @Query("SELECT r FROM Report r LEFT JOIN FETCH r.dailyCost LEFT JOIN FETCH r.puit WHERE r.puit.puitId = :puitId")
    List<Report> findByPuitIdWithDailyCost(@Param("puitId") String puitId);

    // Méthode optimisée pour récupérer les rapports par phase avec leurs coûts
    @Query("SELECT r FROM Report r LEFT JOIN FETCH r.dailyCost LEFT JOIN FETCH r.puit WHERE r.phase = :phase")
    List<Report> findByPhaseWithDailyCost(@Param("phase") String phase);

    // Méthode optimisée pour récupérer les rapports par puits et phase
    @Query("SELECT r FROM Report r LEFT JOIN FETCH r.dailyCost WHERE r.puit.puitId = :puitId AND r.phase = :phase")
    List<Report> findByPuitIdAndPhaseWithDailyCost(@Param("puitId") String puitId, @Param("phase") String phase);

    // Méthode pour obtenir tous les puits distincts avec leurs informations
    @Query("SELECT DISTINCT p FROM Puit p LEFT JOIN FETCH p.reports")
    List<Puit> findAllPuitsWithReports();

    // Méthode pour obtenir les IDs de puits distincts
    @Query("SELECT DISTINCT r.puit.puitId FROM Report r WHERE r.puit IS NOT NULL")
    List<String> findDistinctPuitIds();

    // Méthode pour obtenir tous les rapports avec leurs relations essentielles
    @Query("SELECT r FROM Report r LEFT JOIN FETCH r.dailyCost LEFT JOIN FETCH r.puit")
    List<Report> findAllWithEssentialRelations();

    // Méthode 1: Récupère directement la phase du rapport avec le plus grand ID
    // pour un puit
    @Query("SELECT r.phase FROM Report r WHERE r.puit.puitId = :puitId ORDER BY r.id DESC LIMIT 1")
    Optional<String> findLatestPhaseByMaxId(@Param("puitId") String puitId);

}