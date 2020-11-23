# 1. 개요
- 카카오페이 사전과제 **<span style="color:blue">카카오페이 뿌리기</span>** 기능을 구현하였습니다.


# 2. 기술명세
- 언어 : Java 1.8
- 프레임워크 : spring boot 2.3.1
- 의존성 & 빌드 관리 : gradle
- Persistence : JPA
- Database : H2 (in memory)
- OAS : swagger

> H2 database 웹콘솔 보기
- H2 웹console 접속경로는 다음과 같습니다. [http://localhost:8080/h2-console/](http://localhost:8080/h2-console/)  
<img src="https://user-images.githubusercontent.com/61044774/85590819-b0b56080-b67f-11ea-8415-3eb50f5b82b8.jpg" width="90%"></img>

- Driver Class : org.h2.Driver
- JDBC URL : jdbc:h2:mem:testdb
- User Name : sa
- Password : [없음]

> swagger API명세 페이지 보기
- 어플리케이션 기동 후 아래와 같이 [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) 접속하여 API페이지를 조회할 수 있습니다.  
<img src="https://user-images.githubusercontent.com/61044774/99792057-e6e10880-2b69-11eb-9425-bccbe7126d75.jpg" width="90%"></img>

# 3. 개발 요구사항
> HTTP Header를 통해 뿌리기/받기에 대한 사용자 정보 및 대화방 정보가 전달된다.
- 뿌리기 : 사용자는 뿌리기 금액, 인원을 지정하여 뿌리기 기능을 수행한다.
  * 뿌리기 요청건에 대하여 고유 Token을 발급한다.
  * 뿌릴금액은 인원수에 맞추어 자동으로 분배한다.
- 받기 : 사용자는 뿌리기에서 받은 Token으로 분배된 금액을 받을 수 있다.
  * 받기 요청에 대하여 금액을 회신한다.
- 조회 : 뿌리기 사용자는 자신의 뿌리기 정보를 조회할 수 있다.
  * 뿌린 시각, 뿌린 금액, 받기 완료된 금액, 받기 완료된 정보 ([받은 금액, 받은사용자 아이디] 리스트)를 회신한다.


# 4. 뿌리기 테이블 설계
뿌리기 서비스를 구현하기 위한 기본 테이블 설계입니다.
<br>
## 4.1 DISTRIBUTE (뿌리기정보)
- 뿌리기에 대한 기본정보가 담겨있는 테이블.  
<img src="https://user-images.githubusercontent.com/61044774/99785806-0162b400-2b61-11eb-9438-060b693d5839.jpg" width="70%"></img>
  * ID : 뿌리기에 대한 고유정보
  * USER_ID : 뿌리기 사용자정보
  * AMOUNT : 뿌리기 금액정보
  * DIST_COUNT : 뿌리기 인원정보
  * TOKEN : 뿌리기 시에 발행되는 고유 Token정보
  * RECEIVE_LIMIT_TIME : 뿌리기 받기에 대한 유효시간
  * QUERY_LIMIT_TIME : 뿌리기 조회에 대한 유효시간

## 4.2 DISTRIBUTE_DETAIL (뿌리기 상세정보)
- 뿌리기에서 분배되는 정보가 담겨있는 테이블. 뿌리기 받기에 대한 세부정보가 담겨있다.
<img src="https://user-images.githubusercontent.com/61044774/99786686-21df3e00-2b62-11eb-8c44-6cf7cbfd3a2c.jpg" width="66%"></img>
  * ID : 뿌리기상세에 대한 고유정보
  * DIST_ID : 뿌리기에 대한 고유정보 (DISTRIBUTE테이블의 ID컬럼과 매핑된다.)
  * USER_ID : 뿌리기를 받은 사용자정보
  * AMOUNT : 뿌리기를 받은 금액정보
  * RECEIVE_TIME : 뿌리기 금액을 받은 시간
  * RECEIVE_YN : 뿌리기를 받은 여부

# 5. 실행

## 실행 하기

> 소스 main Application 실행하기
- com.milkit.app.DemoApplication 을 IDE에서 run하여 바로 실행할 수 있습니다.
 <img src="https://user-images.githubusercontent.com/61044774/98205672-de8aaa00-1f7b-11eb-8a54-2ea4ad48cad6.jpg" width="90%"></img>


# 6. 문제해결 전략

## 6. 1 받기 동시성 이슈해결
- 뿌리기 토큰을 가지고 여러 사용자가 동시에 받기를 시도할 때, 아직 받기가 설정되지 않은 데이터 row에 동시에 접근하여 업데이트 될 가능성이 있습니다.
때문에 SELECT FOR UPDATE와 같은 배타적 잠금을 사용하여 해결하고자 하였습니다.
```java

@PersistenceContext
EntityManager entityManager;

@Transactional
public DistributeDetail updateReceive(Long distID, String userID) throws Exception {
    TypedQuery<DistributeDetail> query = entityManager.createQuery("SELECT d FROM DistributeDetail d WHERE DIST_ID = :distID AND RECEIVE_YN = 'N'", DistributeDetail.class);
    query.setParameter("distID", distID);
    query.setMaxResults(1);
    query.setLockMode(LockModeType.PESSIMISTIC_WRITE);		//배타적 잠금
    query.setHint("javax.persistence.lock.timeout", "3000");

    DistributeDetail distributeDetail = query.getSingleResult();
        .
        .
        .
```

## 6. 2 뿌릴 금액을 인원수에 맞게 분배 구현
- 뿌릴 금액을 인원수에 맞게 분배하는 로직은 다음과 같이 개발하였습니다.
```java
public List<DistributeDetail> getDistributeDetail(Distribute distribute) {
    long totalAmount = distribute.getAmount();              /*  뿌리기 전체금액    */
    int distCount = distribute.getDistCount();              /*  뿌리기 인원수    */

    for(int i=0; i<distCount; i++) {
        int denominator = (distCount-i);                    /*  분배값을 구하기 위한 분모(루프가 수행됨에 따라 1씩 감소)    */

        long detailAmount = totalAmount/denominator;        /*  분배금액을 구하기 위해 뿌리기 전체금액에서 분모로 나눔   */
        long remainderAmount = totalAmount%denominator;     /*  분배금액 동일하게 떨어지지 않을 수 있기 때문에 나머지 금액을 구함   */
        if(remainderAmount > 0) {                           /*  나머지 금액이 있다면 분매금액에 더하기를 함    */
            detailAmount = detailAmount+remainderAmount;
        }
        totalAmount = totalAmount - detailAmount;           /*  전체뿌리기 금액에서 분백금액을 빼줌 */

        DistributeDetail distributeDetail = DistributeDetail
            .builder()
            .distID(distribute.getId())
            .amount(detailAmount)                           /*  분배금액 세팅 */
            .receiveYN("N")
            .build();
        .
        .
        .
}
```

## 6. 3 각 기능에 대한 제약사항 Validate 구현
- RequestValidateService 인터페이스를 구현한 ReceiveRequestValidateDelegateServiceImpl 과 같은 클래스로 제약사항을 확인하는 기능을 구현 하였습니다. (*아래 구현 예시)
제약사항을 검출한 후 ServiceException을 throw 합니다.

```java
public class ReceiveRequestValidateDelegateServiceImpl extends AbstractRequestValidateDelegateServiceImpl<ReceiveRequest> {

  @Override
  protected ReceiveRequest addedValidate(ReceiveRequest request) throws Exception {
        .
        .
        .
    Distribute distribute = distributeService.getDistribute(request.getToken());

    //받기 요청 사용자가 뿌리기를 수행한 사용자일 경우
    String distributeUserID = distribute.getUserID();
    if( request.getUserID().equals(distributeUserID) ) {
        throw new ServiceException(ErrorCodeEnum.NotReiveDistributeUserException.getCode(), new String[]{distributeUserID});
    }
        .
        .
        .
}
```

## 6. 4 기능 및 예외사항에 대한 테스트 수행
- 뿌리기 서비스의 기능 및 예외사항을 테스트 하기 위하여 38개의 테스트 항목을 작성하였습니다.

  ### 6.4.1 서비스 테스트 예제
  - 기능테스트
  ```java
  @Test
  @DisplayName("1. 자신의 뿌리기 정보를 조회한다.")
  public void query_test() throws Exception {
          .
          .
          .
    Distribute result = queryHandlerService.query(queryRequest);
    List<DistributeDetail> distributeDetail = result.getDetail();

    assertTrue(
        result.getAmount().equals(amount) &&
        distributeDetail.size() == 2 &&
        result.getReceiveAmount().equals(receive1+receive2)
    );
  }
  ```
  - 예외테스트
  ```java
  @Test
  @DisplayName("2. 자신의 뿌리기가 아닌 정보를 조회할 경우 예외를 테스트한다.")
  public void query_not_own_distribute_exception_test() throws Exception {
          .
          .
          .
    ServiceException exception = assertThrows(ServiceException.class, () -> {
        QueryRequest queryRequest = queryRequestValidateDelegateService.process(headers, request);
        Distribute result = queryHandlerService.query(queryRequest);
    });

    assertTrue( exception.getCode().equals("613"));
    );
  }
  ```

  ### 6.4.2 API 테스트 예제
  - 기능테스트
  ```java
  @Test
  @DisplayName("1. 뿌리기 토큰으로 금액을 받는다.")
  public void receive_test() throws Exception {
          .
          .
          .
      ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.put("/api/pay/receive")
        .header(AppCommon.DIST_USER_HEADER_STRING, receiveUserID)
        .header(AppCommon.DIST_ROOM_HEADER_STRING, roomID)
        .content(content).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("code").value("0"))
            .andExpect(jsonPath("message").value("성공했습니다"))
            .andExpect(jsonPath("value").value(greaterThan(0)))
            ;
  }
  ```
  - 예외테스트
  ```java
  @Test
  @DisplayName("2. 동일 사용자가 뿌리기를 다시 받을 경우 예외를 테스트한다.")
  public void receive_manytime_exception_test() throws Exception {
          .
          .
          .
      ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.put("/api/pay/receive")
        .header(AppCommon.DIST_USER_HEADER_STRING, receiveUserID)
        .header(AppCommon.DIST_ROOM_HEADER_STRING, roomID)
        .content(content).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("code").value("610"))
            .andExpect(jsonPath("value").isEmpty())
            ;
  }
  ```

# 7. API 명세
아래의 정보는 어플리케이션 기동 후 swagger를 ([http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)) 통해서도 확인하실 수 있습니다.

> Tips2 : API Response 형식

| 구분    | 내용             | 비고                             |
| :------ | :--------------- | :------------------------------- |
| code    | 응답코드         | 0 : 성공, 그 외 코드 : 실패                 |
| message | 메시지           | 성공 혹은 실패 시의 메시지 |
| value    | 결과 값 |                                  |

- 성공 시 (*예제)
```javascript

{
  "code": "0",                /*  응답코드 */
  "message": "성공했습니다",    /*  메시지 */
  "value": "6eW"              /*  뿌리기 토큰정보 */
}
```

- 실패 시 (*예제)
```javascript
{
  "code": "611",
  "message": "받기 유효시간이 초과하였습니다. 받기유효시간:2020-11-20 19:33:19.412, 받기요청시간:Fri Nov 20 19:34:19 KST 2020",
  "value": null
}
```

## 7.1 뿌리기 API
- 사용자는 임의의 방에 뿌릴 금액 및 뿌릴 인원을 지정하여 뿌리기는 할 수 있습니다.

  * URL : POST http://localhost:8080/api/pay/distribute
  * 요청 Body
  ```javascript

  {
    "amount" : 5000,    /*  뿌릴 금액 */
    "distCount" : 3     /*  뿌릴 인원 */
  }

  ```
  * 정상 응답 Body
  ```javascript

  {
    "code": "0",                /*  응답코드 */
    "message": "성공했습니다",    /*  메시지 */
    "value": "6eW"              /*  뿌리기 토큰정보 */
  }

  ```

## 7.2 받기 API
- 사용자는 뿌리기에서 생성된 토큰을 통해 받기를 할 수 있습니다.

  * URL : PUT http://localhost:8080/api/pay/receive
  * 요청 Body


  ```javascript
  {
    "token" : "6eW"         /*  뿌리기 토큰정보  */
  }
  ```
  * 정상 응답 Body
  ```javascript
  {
    "code": "0",
    "message": "성공했습니다",
    "value": 1667         /*  뿌리기 금액정보  */
  }
  ```


- 예외 1. 동일 사용자가 뿌리기를 다시 받을 경우 예외
  * 응답 Body
  ```javascript
  {
    "code": "610",
    "message": "사용자가 이미 뿌리기 금액을 받았습니다. 뿌리기ID:1, 사용자ID:01023684318",
    "value": null         
  }
  ```

- 예외 2. 뿌리기한 사용자가 받기 시도를 할 경우 예외
  * 응답 Body
  ```javascript
  {
    "code": "608",
    "message": "자신이 뿌린 금액은 받을 수 없습니다. 사용자ID:01066849318",
    "value": null         
  }
  ```

- 예외 3. 뿌리기와 다른방의 사용자가 받을 경우 예외
  * 응답 Body
  ```javascript
  {
    "code": "609",
    "message": "뿌린이가 호출된 대화방과 동일한 대화방에 속한 사용자만이 받을 수 있습니다. 뿌리기대화방ID:dw3e31w32td2, 요청대화방ID:tr3SruT5X8",
    "value": null         
  }
  ```

- 예외 4. 뿌리기 받기의 시간이 초과한 경우 예외
  * 응답 Body
  ```javascript
  {
    "code": "611",
    "message": "받기 유효시간이 초과하였습니다. 받기유효시간:2020-11-22 12:26:23.865, 받기요청시간:Sun Nov 22 12:26:25 KST 2020",
    "value": null         
  }
  ```

- 예외 5. 이미 모든사용자가 받기가 완료된 후 사용자가 받기를 시도할 경우 예외
  * 응답 Body
  ```javascript
  {
    "code": "612",
    "message": "이미 모든 인원이 받기가 완료 되었습니다.",
    "value": null         
  }
  ```

## 7.3 조회 API
- 사용자는 자신이 뿌린 뿌리기 정보를 조회할 수 있습니다.

  * URL : GET http://localhost:8080/api/pay/query/[뿌리기토큰정보]
  * 요청 Body
  ```javascript
  N/A
  ```
  * 정상 응답 Body
  ```javascript
  {
    "code": "0",
    "message": "성공했습니다",
    "value": {
      "amount": 5000,                       /*  뿌린금액  */
      "distTime": "2020-11-20 18:24:46",    /*  뿌린시간  */
      "receiveAmount": 1667,                /*  받은금액  */
      "detail": [                           /*  상세리스트  */
        {
          "userID": "01112345678",          /*  받은사용자  */
          "amount": 1667                    /*  받은금액  */
        }
      ]
    }
  }
  ```

- 예외 1. 자신의 뿌리기가 아닌 정보를 조회할 경우 예외
  * 응답 Body
  ```javascript
  {
    "code": "613",
    "message": "자신의 뿌리기 정보만 조회를 할 수 있습니다. 조회사용자ID:09832345678, 뿌리기사용자ID:01019434678",
    "value": null         
  }
  ```

- 예외 2. 뿌리기 조회시간을 초과했을 경우 예외
  * 응답 Body
  ```javascript
  {
    "code": "614",
    "message": "조회하기 유효시간이 초과하였습니다. 조회하기유효시간:2020-11-22 12:30:38.691, 조회하기요청시간:Sun Nov 22 12:32:38 KST 2020",
    "value": null         
  }
  ```
