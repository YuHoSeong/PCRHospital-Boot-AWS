package site.hsyu.hosp.batch;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
    
    // 공공데이터(JSON) 다운로드 테스트 컬렉션 담기(전체 데이터)
    @Test
    public void download(){

        // 1. 담을 그릇 준비
        List<Hospital> hospitals = new ArrayList<>();

        // 2. api 한번 호출해서 totalcount 확인
        RestTemplate rt = new RestTemplate();
        
        // 사이즈를 1로 했더니 item이 컬렉션이 아니라서 파싱이 안되서 2로 바꿈
        int totalCount = 2;

        String totalCountUrl = "http://apis.data.go.kr/B551182/rprtHospService/getRprtHospService?serviceKey=wBH50snlYAlyFnnzx5jDPfyTSi/dc3njyf3AwfnQQDpPGvJpKC4YFGqPsw/IrapOupZkKGgf7htdzxi4Ksu3tw==&pageNo=1&numOfRows="+totalCount+"&_type=json";

        // System.out.println(rt.getForObject(totalCountUrl, String.class));
        
        ResponseDto totalCountDto = rt.getForObject(totalCountUrl, ResponseDto.class);
        totalCount = totalCountDto.getResponse().getBody().getTotalCount();
        // System.err.println(totalCountDto);
        
        // 3. totalCount 만큼 한번에 가져오기
        // String url = "http://apis.data.go.kr/B551182/rprtHospService/getRprtHospService?serviceKey=wBH50snlYAlyFnnzx5jDPfyTSi/dc3njyf3AwfnQQDpPGvJpKC4YFGqPsw/IrapOupZkKGgf7htdzxi4Ksu3tw==&pageNo=1&numOfRows="+totalCount+"&_type=json";
        String url = "http://apis.data.go.kr/B551182/rprtHospService/getRprtHospService?serviceKey=wBH50snlYAlyFnnzx5jDPfyTSi/dc3njyf3AwfnQQDpPGvJpKC4YFGqPsw/IrapOupZkKGgf7htdzxi4Ksu3tw==&pageNo=1&numOfRows=10&_type=json";
        ResponseDto responseDto = rt.getForObject(url, ResponseDto.class);
        
        List<Item> items = responseDto.getResponse().getBody().getItems().getItem();
        // Item test = responseDto.getResponse().getBody().getItems().getItem().get(0);
        // System.err.println(test);
        // System.err.println("가져온수만큼 데이터 사이즈: "+items.size());

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
                .XPosWgs84(e.getXPosWgs84())
                .YPosWgs84(e.getYPosWgs84())
                .yadmNm(e.getYadmNm())
                .ykihoEnc(e.getYkihoEnc())
                .XPos(e.getXPos())
                .YPos(e.getYPos())
                .build();
            }
        ).collect(Collectors.toList());

        // System.out.println(hospitals);

        String test = rt.getForObject(url, String.class);
        JSONObject test1;
        try {
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

        System.out.println(hospitals);

        // assertEquals(totalCount, items.size());

    }

}
