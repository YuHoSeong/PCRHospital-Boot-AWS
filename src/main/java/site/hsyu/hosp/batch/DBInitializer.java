package site.hsyu.hosp.batch;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import site.hsyu.hosp.domain.Hospital;
import site.hsyu.hosp.domain.HospitalRepository;

@Configuration
public class DBInitializer {

    // 스프링부트 시작시에 한번 실행됨
    @Bean
    public CommandLineRunner initDB(HospitalRepository hRepository) {

        return (args) -> {
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

            // System.out.println(totalCountDto);
            // System.out.println(totalCountDto.getResponse().getBody().getItems().getItem());

            // 3. totalCount 만큼 한번에 가져오기
            String url = "http://apis.data.go.kr/B551182/rprtHospService/getRprtHospService?serviceKey=wBH50snlYAlyFnnzx5jDPfyTSi/dc3njyf3AwfnQQDpPGvJpKC4YFGqPsw/IrapOupZkKGgf7htdzxi4Ksu3tw==&pageNo=1&numOfRows="
                    + totalCount + "&_type=json";
            ResponseDto responseDto = rt.getForObject(url, ResponseDto.class);

            List<Item> items = responseDto.getResponse().getBody().getItems().getItem();
            // System.err.println("가져온수만큼 데이터 사이즈: " + items.size());

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
                                .XPosWgs84(e.getXPosWgs84())
                                .YPosWgs84(e.getYPosWgs84())
                                .yadmNm(e.getYadmNm())
                                .ykihoEnc(e.getYkihoEnc())
                                .XPos(e.getXPos())
                                .YPos(e.getYPos())
                                .build();
                    }).collect(Collectors.toList());

            String test = rt.getForObject(url,String.class);
            JSONObject test1;
            try{
                test1 = new JSONObject(test);
                JSONArray item = test1.getJSONObject("response").getJSONObject("body").getJSONObject("items").getJSONArray("item");
                int length = item.length();
                for(int i=0; i<length;i++){
                    if(!item.getJSONObject(i).isNull("XPosWgs84")){
                        hospitals.get(i).setXPosWgs84(item.getJSONObject(i).getDouble("XPosWgs84"));
                        // hospitals.get(i).setXPosWgs84(Double.parseDouble(item.getJSONObject(i).getString("XPosWgs84")));
                    }
                    if(!item.getJSONObject(i).isNull("YPosWgs84")){
                        hospitals.get(i).setYPosWgs84(item.getJSONObject(i).getDouble("YPosWgs84"));
                        // hospitals.get(i).setYPosWgs84(Double.parseDouble(item.getJSONObject(i).getString("YPosWgs84")));
                    }
                    if(!item.getJSONObject(i).isNull("XPos")){
                        hospitals.get(i).setXPos(item.getJSONObject(i).getInt("XPos"));
                        // hospitals.get(i).setXPos(Integer.parseInt(item.getJSONObject(i).getString("XPos")));
                    }
                    if(!item.getJSONObject(i).isNull("YPos")){
                        hospitals.get(i).setYPos(item.getJSONObject(i).getInt("YPos"));
                        // hospitals.get(i).setYPos(Integer.parseInt(item.getJSONObject(i).getString("YPos")));
                    }
                }
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            // System.out.println(hospitals);
            // 삭제 테스트
            // 기존 데이터 다 삭제하기 (삭제 잘 되는지 먼저 테스트하기위해 yml - update로 변경)
            hRepository.deleteAll();

            // 배치시간에 db에 insert 하기(하루에 한번 할 예정)
            hRepository.saveAll(hospitals);
        };

    }
}
