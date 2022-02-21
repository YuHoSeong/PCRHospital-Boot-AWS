package site.hsyu.hosp.batch;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import site.hsyu.hosp.domain.Hospital;

public class HospDownloadBatchTest {
    
    // 테스트 로그 찍히는곳 - DEBUG CONSOLE Launch Java Tests 부분!!
    // 테스트 완료

    @Test
    public void start(){
        // 1. 공공데이터 다운로드
        RestTemplate rt = new RestTemplate();
        // 테이스 할때는 RUL 인코딩 사용안해도됨
        String url = "http://apis.data.go.kr/B551182/rprtHospService/getRprtHospService?serviceKey=wBH50snlYAlyFnnzx5jDPfyTSi/dc3njyf3AwfnQQDpPGvJpKC4YFGqPsw/IrapOupZkKGgf7htdzxi4Ksu3tw==&pageNo=1&numOfRows=10&_type=json";
        ResponseDto dto = rt.getForObject(url, ResponseDto.class);
        // String dto = rt.getForObject(url, String.class);
        List<Item> hospitals = dto.getResponse().getBody().getItems().getItem();
        for(Item item : hospitals){
            System.out.println(item.getYadmNm());
            System.out.println("PCR 여부 : "+item.getPcrPsblYn());
        }
    }
    
    // 공공데이터 다운로드 테스트 컬렉션 담기(전체 데이터)
    @Test
    public void download(){

        // 1. 담을 그릇 준비
        List<Hospital> hospitals = new ArrayList<>();

        // 2. api 한번 호출해서 totalcount 확인
        RestTemplate rt = new RestTemplate();
        
        // 사이즈를 1로 했더니 item이 컬렉션이 아니라서 파싱이 안되서 2로 바꿈
        int totalCount = 2;

        String totalCountUrl = "http://apis.data.go.kr/B551182/rprtHospService/getRprtHospService?serviceKey=wBH50snlYAlyFnnzx5jDPfyTSi/dc3njyf3AwfnQQDpPGvJpKC4YFGqPsw/IrapOupZkKGgf7htdzxi4Ksu3tw==&pageNo=1&numOfRows="+totalCount+"&_type=json";

        ResponseDto totalCountDto = rt.getForObject(totalCountUrl, ResponseDto.class);
        totalCount = totalCountDto.getResponse().getBody().getTotalCount();
        
        // 3. totalCount 만큼 한번에 가져오기
        String url = "http://apis.data.go.kr/B551182/rprtHospService/getRprtHospService?serviceKey=wBH50snlYAlyFnnzx5jDPfyTSi/dc3njyf3AwfnQQDpPGvJpKC4YFGqPsw/IrapOupZkKGgf7htdzxi4Ksu3tw==&pageNo=1&numOfRows="+totalCount+"&_type=json";
        ResponseDto responseDto = rt.getForObject(url, ResponseDto.class);
        
        List<Item> items = responseDto.getResponse().getBody().getItems().getItem();
        System.err.println("가져온수만큼 데이터 사이즈: "+items.size());

        // 컬렉션 복사
        hospitals = items.stream().map(
            (e)->{
                return Hospital.builder()
                .mgtStaDd(e.getMgtStaDd())
                .addr(e.getAddr())
                .pcrPsblYn(e.getPcrPsblYn())
                .ratPsblYn(e.getRatPsblYn())
                .recuClCd(e.getRecuClCd())
                .sgguCdNm(e.getSgguCdNm())
                .sidoCdNm(e.getSidoCdNm())
                .xPosWgs84(e.getXPosWgs84())
                .yPosWgs84(e.getYPosWgs84())
                .yadmNm(e.getYadmNm())
                .ykihoEnc(e.getYkihoEnc())
                .xPos(e.getXPos())
                .yPos(e.getYPos())
                .build()
                ;
            }
        ).collect(Collectors.toList());

        assertEquals(totalCount, items.size());

    }


}
