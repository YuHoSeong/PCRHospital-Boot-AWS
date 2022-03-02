# PCR 검사 병원 조회 서비스

## Tech Stack
  - `Java`,`Spring Boot`,`Gradle`,`JavaScript`,`h2-DB`,`Git`,`Github`,`AWS`,`BootStrap`

## Result
  - WebSite : http://44.193.148.87/

![image](https://user-images.githubusercontent.com/82141580/155466960-1eac68be-b167-4eae-b750-6a7b23ed525d.png)

## Problem

### 문제정의
  - 공공API데이터 DB 저장시 XPos(x좌표), YPos(y좌표), XPosWgs84(위도), YPosWgs84(경도)의 데이터가 null로 저장되는 현상

### 사실수집
  - 외부API 데이터에는 모두 데이터가 존재한다.
  - DB로 저장할 객체에는 null로 담겨있다.

### 원인추론
  - 현재 XPos,YPos는 Integer, XPosWgs84,YPosWgs84는 Double 의 데이터타입으로 설정되어있다.
    데이터의 타입이 맞지 않는것일까?
    
### 조치방안
  - 외부API데이터를 String.class 로 전체 추출 후 JSON타입으로 변환하여 타입에 맞도록 저장하였다.

### 구현

```Java
// 외부 API 데이터를 restTamplate을 활용하여 String 클래스로 추출
String test = rt.getForObject(url,String.class);
JSONObject test1;
try{
  test1 = new JSONObject(test);
  JSONArray item = test1.getJSONObject("response").getJSONObject("body").getJSONObject("items").getJSONArray("item");
  int length = item.length();
  for(int i=0; i<length;i++){
    if(!item.getJSONObject(i).isNull("XPosWgs84")){
      hospitals.get(i).setXPosWgs84(item.getJSONObject(i).getDouble("XPosWgs84"));
    }
    if(!item.getJSONObject(i).isNull("YPosWgs84")){
      hospitals.get(i).setYPosWgs84(item.getJSONObject(i).getDouble("YPosWgs84"));
    }
    if(!item.getJSONObject(i).isNull("XPos")){
      hospitals.get(i).setXPos(item.getJSONObject(i).getInt("XPos"));
    }
    if(!item.getJSONObject(i).isNull("YPos")){
      hospitals.get(i).setYPos(item.getJSONObject(i).getInt("YPos"));
    }
  }
} catch (JSONException e1) {
  e1.printStackTrace();
}
```

### 문제해결
  - DB에 외부API 데이터 저장 성공
