package edu.gatech.cc.scp.mvtipbtc.repository;

import edu.gatech.cc.scp.mvtipbtc.model.TransactionAnalysis;
import edu.gatech.cc.scp.mvtipbtc.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionAnalysisRepository extends JpaRepository<TransactionAnalysis, Long> {
    List<TransactionAnalysis> findByUserOrderByCreatedAtDesc(User user);
    Optional<TransactionAnalysis> findByAddressAndUser(String address, User user);
    List<TransactionAnalysis> findByRiskLevelAndUser(String riskLevel, User user);
}

