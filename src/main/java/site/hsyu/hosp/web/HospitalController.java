package site.hsyu.hosp.web;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import site.hsyu.hosp.domain.Hospital;
import site.hsyu.hosp.domain.HospitalRepository;

@CrossOrigin
@RequiredArgsConstructor
@Controller
public class HospitalController {

    private static final String TAG = "HospitalController:";
    private final HospitalRepository hRepository;
    

    @GetMapping("/")
    public String index(Model model){
        
        model.addAttribute("sidoCdNms", hRepository.mFindSidoCdNm());
        model.addAttribute("sgguCdNms", hRepository.mFindSgguCdNm("강원"));

        return "index"; // templates/home.mustache 찾음
    }

    @GetMapping("/api/sggucdnm")
    //응답도 json으로 할 예정
    public @ResponseBody List<String> sggucdnm(String sidoCdNm){ //버퍼로 ajax 요청 받음.
        return hRepository.mFindSgguCdNm(sidoCdNm);
    }
    
    // http://localhost:8000/api/hospital?sidoCdNm=머시기&sgguCdNm=머시기
    @GetMapping("/api/hospital")
    //응답도 json으로 할 예정
    public @ResponseBody List<Hospital> hospitals(String sidoCdNm, String sgguCdNm){ //버퍼로 ajax 요청 받음.
        System.out.println(TAG+hRepository.mFindSidoCdNm().size());
        return hRepository.mFindHospital(sidoCdNm,sgguCdNm);
    }
}
