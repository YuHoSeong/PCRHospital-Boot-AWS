package site.hsyu.hosp.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface HospitalRepository extends JpaRepository<Hospital, Integer> {
    
    @Query(value = "SELECT * FROM hospital WHERE sidoCdNm = :sidoCdNm AND sgguCdNm = :sgguCdNm",nativeQuery = true)
    public List<Hospital> mFindHospital(String sidoCdNm, String sgguCdNm);
}
