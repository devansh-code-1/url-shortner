package com.urlshortener.repository;

import com.urlshortener.domain.UrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<UrlEntity, Long> {
    
    Optional<UrlEntity> findByShortCode(String shortCode);
    
    boolean existsByShortCode(String shortCode);
    
    Optional<UrlEntity> findByOriginalUrl(String originalUrl);
    
    List<UrlEntity> findByIsActiveTrue();
    
    List<UrlEntity> findByExpiresAtBefore(LocalDateTime dateTime);
    
    @Query("SELECT u FROM UrlEntity u WHERE u.isActive = true AND (u.expiresAt IS NULL OR u.expiresAt > :currentTime)")
    List<UrlEntity> findActiveAndNonExpiredUrls(@Param("currentTime") LocalDateTime currentTime);
    
    @Modifying
    @Query("UPDATE UrlEntity u SET u.clickCount = u.clickCount + 1 WHERE u.shortCode = :shortCode")
    int incrementClickCount(@Param("shortCode") String shortCode);
    
    @Modifying
    @Query("UPDATE UrlEntity u SET u.isActive = false WHERE u.expiresAt < :currentTime")
    int deactivateExpiredUrls(@Param("currentTime") LocalDateTime currentTime);
    
    @Query("SELECT COUNT(u) FROM UrlEntity u WHERE u.createdAt >= :startTime AND u.createdAt <= :endTime")
    long countUrlsCreatedBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT SUM(u.clickCount) FROM UrlEntity u WHERE u.shortCode = :shortCode")
    Long getTotalClicksByShortCode(@Param("shortCode") String shortCode);
}