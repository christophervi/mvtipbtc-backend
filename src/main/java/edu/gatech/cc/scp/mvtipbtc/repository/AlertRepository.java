package edu.gatech.cc.scp.mvtipbtc.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.gatech.cc.scp.mvtipbtc.model.Alert;
import edu.gatech.cc.scp.mvtipbtc.model.User;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByUserOrderByTimestampDesc(User user);
    List<Alert> findByRiskLevelAndUserOrderByTimestampDesc(String riskLevel, User user);
    long countByUserAndRiskLevel(User user, String riskLevel);
    Optional<Alert> findByIdAndUser(Long id, User user);
}

