package site.hsyu.hosp.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HospitalRepository extends JpaRepository<Hospital, Integer> {
    
    // name query 사용시에 어노테이션 필요
    @Query(value = "SELECT * FROM hospital WHERE sidoCdNm =:sidoCdNm AND sgguCdNm =:sgguCdNm AND pcrPsblYn ='Y'",nativeQuery = true)
    public List<Hospital> mFindHospital(@Param("sidoCdNm")String sidoCdNm, @Param("sgguCdNm")String sgguCdNm);
    
    @Query(value = "SELECT distinct sidoCdNm FROM HOSPITAL ORDER BY SIDOCDNM",nativeQuery = true)
    public List<String> mFindSidoCdNm();
    
    @Query(value = "SELECT distinct sgguCdNm FROM HOSPITAL WHERE sidocdnm =:sidoCdNm order by sgguCdNm",nativeQuery = true)
    public List<String> mFindSgguCdNm(@Param("sidoCdNm") String sidoCdNm);

    


}
