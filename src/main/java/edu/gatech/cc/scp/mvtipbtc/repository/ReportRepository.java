package edu.gatech.cc.scp.mvtipbtc.repository;

import edu.gatech.cc.scp.mvtipbtc.model.Report;
import edu.gatech.cc.scp.mvtipbtc.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByUserOrderByCreatedAtDesc(User user);
    List<Report> findByAddressAndUser(String address, User user);
}

