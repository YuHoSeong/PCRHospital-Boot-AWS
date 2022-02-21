package site.hsyu.hosp.batch;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import site.hsyu.hosp.domain.Hospital;
import site.hsyu.hosp.domain.HospitalRepository;

// 1주일에 한번씩 다운로드해서 DB에 변경해주기
// pcr 검사기관이 추가 될 수 있기 때문에
// 4개 병원이 있다면 4개가 DB에 insert 된다.
// 다음날에는 5개 병원이 되었다면 한개 추가하는 로직이 복잡할 것 같아서
// 그냥 4개 데이터 삭제해버리고 새로 추가해서 넣자.
// 공공데이터를 바로 바로 서비스 해주는 방식은 하루에 트래픽이 1000이라서 서비스하기 힘들것 같다.

@RequiredArgsConstructor
@Component
public class HospDownloadBatch {

    // DI
    private final HospitalRepository hospitalRepository;

    // 초 분 시 일 월 주
    @Scheduled(cron = "0 59 * * * *", zone = "Asia/Seoul") // 매 시 마다 배치함. 매시 59분 마다로 변경
    public void startBatch() {
        // System.out.println("나 1분마다 실행 됨");

        // 1. 담을 그릇 준비
        List<Hospital> hospitals = new ArrayList<>();

        // 2. api 한번 호출해서 totalcount 확인
        RestTemplate rt = new RestTemplate();

        // 사이즈를 1로 했더니 item이 컬렉션이 아니라서 파싱이 안되서 2로 바꿈
        int totalCount = 2;

        String totalCountUrl = "http://apis.data.go.kr/B551182/rprtHospService/getRprtHospService?serviceKey=wBH50snlYAlyFnnzx5jDPfyTSi/dc3njyf3AwfnQQDpPGvJpKC4YFGqPsw/IrapOupZkKGgf7htdzxi4Ksu3tw==&pageNo=1&numOfRows="
                + totalCount + "&_type=json";

        ResponseDto totalCountDto = rt.getForObject(totalCountUrl, ResponseDto.class);
        totalCount = totalCountDto.getResponse().getBody().getTotalCount();

        // 3. totalCount 만큼 한번에 가져오기
        String url = "http://apis.data.go.kr/B551182/rprtHospService/getRprtHospService?serviceKey=wBH50snlYAlyFnnzx5jDPfyTSi/dc3njyf3AwfnQQDpPGvJpKC4YFGqPsw/IrapOupZkKGgf7htdzxi4Ksu3tw==&pageNo=1&numOfRows="
                + totalCount + "&_type=json";
        ResponseDto responseDto = rt.getForObject(url, ResponseDto.class);

        List<Item> items = responseDto.getResponse().getBody().getItems().getItem();
        System.err.println("가져온수만큼 데이터 사이즈: " + items.size());

        // 컬렉션 복사
        hospitals = items.stream().map(
                (e) -> {
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
                            .build();
                }).collect(Collectors.toList());

        // 삭제 테스트
        // 기존 데이터 다 삭제하기 (삭제 잘 되는지 먼저 테스트하기위해 yml - update로 변경)
        hospitalRepository.deleteAll();;

        // 배치시간에 db에 insert 하기(하루에 한번 할 예정)
        hospitalRepository.saveAll(hospitals);
    }

}
