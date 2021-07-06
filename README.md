![image](https://user-images.githubusercontent.com/84000863/124533256-f12bea80-de4c-11eb-90f3-f58950f5ed84.png)

# 하이마트(전자제품 대여 시스템)

본 프로젝트는 MSA/DDD/Event Storming/EDA 를 포괄하는 분석/설계/구현/운영 전단계를 커버하도록 구성한 프로젝트입니다.
이는 클라우드 네이티브 애플리케이션의 개발에 요구되는 체크포인트들을 통과하기 위한 내용을 포함합니다.

# 서비스 시나리오

기능적 요구사항
1. 고객이 제품 및 날짜를 선택하여 예약한다.
2. 예약이 되면 예약 내역이 업체에게 전달된다.
3. 예약 내역이 렌터카 업체에 전달되는 동시에, 대여 가능 수량이 변경된다.
4. 업체에서 예약 내역을 확인하여 제품을 준비한다.
5. 제품을 준비 후, 렌트된 상태로 변경된다.
6. 고객이 예약을 취소할 수 있다.
7. 고객이 제품을 반납하면 대여 가능한 수량이 증가된다.
8. 고객이 예약정보를 중간중간 조회한다.
   ex) 예약시작날짜, 예약종료날짜, 수량, 제품명 등
9. 업체는 새로운 제품을 등록할 수 있다.

비기능적 요구사항
1. 트랜잭션
    1. 예약된 주문건은 대여 가능 수량이 변경되어야 한다. Sync 호출
2. 장애격리
    1. 업체 기능이 수행되지 않더라도 예약은 365일 24시간 받을 수 있어야 한다.  Async (event-driven), Eventual Consistency
    2. 제품 등록이 과중되면 사용자를 잠시동안 받지 않고 제품 등록을 잠시후에 하도록 유도한다.  Circuit breaker
3. 성능
    1. 고객이 예약상태를 별도의 고객페이지에서 확인할 수 있어야 한다. CQRS

# 체크포인트

1. Saga
2. CQRS
3. Correlation
4. Req/Resp
5. Gateway
6. Deploy/ Pipeline
7. Circuit Breaker
8. Autoscale (HPA)
9. Zero-downtime deploy
10. Config Map / Persistence Volume
11. Polyglot
12. Self-healing (Liveness Probe)

# 분석/설계

## AS-IS 조직 (Horizontally-Aligned)
  ![image](https://user-images.githubusercontent.com/487999/79684144-2a893200-826a-11ea-9a01-79927d3a0107.png)

## TO-BE 조직 (Vertically-Aligned)
  ![image](https://user-images.githubusercontent.com/84000863/121344166-65fb2a00-c95e-11eb-97b3-8d1490beb909.png)


## Event Storming 결과
* MSAEz 로 모델링한 이벤트스토밍 결과:  http://www.msaez.io/#/storming/YDRbfy7lYeUen79PbVkrB9GD8sm2/mine/c5aa77abc9e05d36d0d00fee25b7984a


### 완성된 1차 모형

![image](https://user-images.githubusercontent.com/84000863/124533467-52ec5480-de4d-11eb-926d-d216f074ca54.png)

    - View Model 추가

### 1차 완성본에 대한 기능적/비기능적 요구사항을 커버하는지 검증

![image](https://user-images.githubusercontent.com/84000863/124543407-27269a00-de60-11eb-8d28-527c1217a163.png)

    - 고객이 제품을 예약한다 (ok)
    - 예약이 되면 업체에게 전달된다 (ok)
    - 예약 내역이 업체에 전달되는 동시에, 대여 가능 수량이 변경된다. (ok)
    - 업체에서 예약 내역을 확인하여 제품을 준비한다. (ok)
    - 제품을 준비 후, 렌트되면 렌트된 상태로 변경된다. (ok)

![image](https://user-images.githubusercontent.com/84000863/124543478-4ae9e000-de60-11eb-880c-f71055c345d9.png)

    - 고객이 예약을 취소할 수 있다 (ok)
    - 고객이 제품을 반납하면 대여 가능 수량이 변경된다. (ok)
    - 고객이 예약정보를 중간중간 조회한다. (View-green sticker 의 추가로 ok) 


### 비기능 요구사항에 대한 검증

![image](https://user-images.githubusercontent.com/84000863/124543567-78368e00-de60-11eb-84d9-35847cf63e61.png)

    - 1) 예약된 주문건은 대여 가능 수량이 변경되어야 한다. (Req/Res)
    - 2) 업체관리 기능이 수행되지 않더라도 예약은 365일 24시간 받을 수 있어야 한다. (Pub/sub)
    - 3) 제품등록 시스템이 과중되면 잠시동안 받지 않고 등록을 잠시후에 하도록 유도한다 (Circuit breaker)
    - 4) 고객이 예약상태를 별도의 고객페이지에서 확인할 수 있어야 한다 (CQRS)


## 헥사고날 아키텍처 다이어그램 도출
    
![image](https://user-images.githubusercontent.com/84000863/124543270-ee86c080-de5f-11eb-8b11-e6aec1785a70.png)

    - Chris Richardson, MSA Patterns 참고하여 Inbound adaptor와 Outbound adaptor를 구분함
    - 호출관계에서 PubSub 과 Req/Resp 를 구분함
    - 서브 도메인과 바운디드 컨텍스트의 분리:  각 팀의 KPI 별로 아래와 같이 관심 구현 스토리를 나눠가짐


# 구현:

구현한 각 서비스를 로컬에서 실행하는 방법은 아래와 같다 (각자의 포트넘버는 8081 ~ 808n 이다)

```
cd item
mvn spring-boot:run

cd reservation
mvn spring-boot:run 

cd store
mvn spring-boot:run  

cd customercenter
mvn spring-boot:run

```

## DDD 의 적용

- 각 서비스내에 도출된 핵심 Aggregate Root 객체를 Entity 로 선언하였다. 아래 Item이 그 예시이다.

```
package himart;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Item_table")
public class Item {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String name;
    private Integer stock;
    private Long itemId;

    @PostPersist
    public void onPostPersist(){
        Registered registered = new Registered();
        BeanUtils.copyProperties(this, registered);
        registered.publishAfterCommit();


    }

    @PreUpdate
    public void onPreUpdate(){
        StockModified stockModified = new StockModified();
        BeanUtils.copyProperties(this, stockModified);
        stockModified.publishAfterCommit();


    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }
}

```
- Entity Pattern 과 Repository Pattern 을 적용하여 JPA 를 통하여 다양한 데이터소스 유형 (RDB or NoSQL) 에 대한 별도의 처리가 없도록 데이터 접근 어댑터를 자동 생성하기 위하여 Spring Data REST 의 RestRepository 를 적용하였다
```
package himart;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="items", path="items")
public interface ItemRepository extends PagingAndSortingRepository<Item, Long>{
    Item findByItemId(Long itemId);

}

```
- 적용 후 REST API 의 테스트
```
# reservation 서비스의 예약처리
http POST http://localhost:8084/reservations itemId=1 name=SamsungTV55in qty=1 startDate=2021-07-05 endDate=2021-07-06

# 주문 상태 확인
http GET http://localhost:8084/reservations

```


## 폴리글랏 퍼시스턴스

item 서비스와 reservation 서비스는 h2 DB로 구현하고, 그와 달리 store 서비스의 경우 Hsql DB로 구현하여, MSA간 서로 다른 종류의 DB간에도 문제 없이 동작하여 다형성을 만족하는지 확인하였다.

- item, reservations 서비스의 pom.xml 설정

![image](https://user-images.githubusercontent.com/84000863/122320251-ed4b2d80-cf5c-11eb-85a9-e3a43e3e56d2.png)

- store 서비스의 pom.xml 설정

![image](https://user-images.githubusercontent.com/84000863/122320209-ddcbe480-cf5c-11eb-920c-4d3f86cac072.png)


## CQRS

Viewer를 별도로 구현하여 아래와 같이 view가 출력된다.

- myPage 구현

![image](https://user-images.githubusercontent.com/84000863/124533886-21c05400-de4e-11eb-86d9-eb5f250553cd.png)

- 예약 수행 후의 myPage

![image](https://user-images.githubusercontent.com/84000863/124533974-4b797b00-de4e-11eb-948d-75d843be666c.png)


## 동기식 호출(Req/Resp)

분석단계에서의 조건 중 하나로 예약(reservation)->업체(store) 간의 호출은 동기식 일관성을 유지하는 트랜잭션으로 처리하기로 하였다. 호출 프로토콜은 이미 앞서 Rest Repository 에 의해 노출되어있는 REST 서비스를 FeignClient 를 이용하여 호출하도록 한다. 

- 결제서비스를 호출하기 위하여 FeignClient를 이용하여 Service 대행 인터페이스 (Proxy) 를 구현 

```
# (reservation) ItemService.java

package himart.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="item", url="http://item:8080")
public interface ItemService {

    @RequestMapping(method= RequestMethod.GET, path="/chkAndModifyStock")
    public boolean modifyStock(@RequestParam("itemId") Long itemId,
                            @RequestParam("qty") Integer qty);

}
```

- 예약된 직후(@PostPersist) 재고수량이 업데이트 되도록 처리 (modifyStock 호출)
```
# Reservation.java

    @PostPersist
    public void onPostPersist(){

        boolean rslt = ReservationApplication.applicationContext.getBean(himart.external.ItemService.class)
        .modifyStock(this.getItemId(), this.getQty());

        if (rslt) {
            Reserved reserved = new Reserved();
            reserved.setStatus("Reserved");
            BeanUtils.copyProperties(this, reserved);
            reserved.publishAfterCommit();
        }
    }
    
```

- 재고수량은 아래와 같은 로직으로 처리
```
public boolean modifyStock(HttpServletRequest request, HttpServletResponse response)
    throws Exception {
            boolean status = false;
            Long itemId = Long.valueOf(request.getParameter("itemId"));
            int qty = Integer.parseInt(request.getParameter("qty"));

            Item item = itemRepository.findByItemId(itemId);

            if(item != null){
                    if (item.getStock() >= qty) {
                        item.setStock(item.getStock() - qty);
                        itemRepository.save(item);
                        status = true;
                    }
            }

            return status;
    }   
```

- 동기식 호출에서는 호출 시간에 따른 타임 커플링이 발생하며, 상품 시스템이 장애가 나면 예약도 못하는 것을 확인:



- 제품(item) 서비스를 잠시 내려놓음 (ctrl+c)

![image](https://user-images.githubusercontent.com/84000863/124535257-c80d5900-de50-11eb-8dfd-998002154127.png)

- 예약하기(reservation)
```
http POST http://localhost:8084/reservations itemId=1 name=SamsungTV55in qty=1 startDate=2021-07-06 endDate=2021-07-07
```
< Fail >

![image](https://user-images.githubusercontent.com/84000863/124535338-e70beb00-de50-11eb-949c-ecb161f2da5f.png)


- 상품(product) 서비스 재기동
```
cd product
mvn spring-boot:run
```

- 예약하기(booking)
```
http POST http://localhost:8084/bookings productId=1 qty=2 startDate=2021-07-03 endDate=2021-07-05
```
< Success >

![image](https://user-images.githubusercontent.com/84000863/124535526-34885800-de51-11eb-8dfb-45407c1d3769.png)

- 제품 등록 및 예약하기

![image](https://user-images.githubusercontent.com/84000863/124536052-31da3280-de52-11eb-80c1-4014b31ba1f6.png)

![image](https://user-images.githubusercontent.com/84000863/124536093-4ae2e380-de52-11eb-8453-81b1986b7722.png)

- 예약된 후 재고 수량 줄어듬 확인

![image](https://user-images.githubusercontent.com/84000863/124536118-5b935980-de52-11eb-9a31-bc48c05e064d.png)



## Gateway 적용

- gateway > applitcation.yml 설정

```
server:
  port: 8088

---

spring:
  profiles: default
  cloud:
    gateway:
      routes:
        - id: item
          uri: http://localhost:8081
          predicates:
            - Path=/items/**, /chkAndModifyStock/** 
        - id: customercenter
          uri: http://localhost:8082
          predicates:
            - Path= /myPages/**
        - id: store
          uri: http://localhost:8083
          predicates:
            - Path=/stores/** 
        - id: reservation
          uri: http://localhost:8084
          predicates:
            - Path=/reservations/** 
      globalcors:
        corsConfigurations:
          '[/**]':
            allowedOrigins:
              - "*"
            allowedMethods:
              - "*"
            allowedHeaders:
              - "*"
            allowCredentials: true


---

spring:
  profiles: docker
  cloud:
    gateway:
      routes:
        - id: item
          uri: http://item:8080
          predicates:
            - Path=/items/**, /chkAndModifyStock/** 
        - id: customercenter
          uri: http://customercenter:8080
          predicates:
            - Path= /myPages/**
        - id: store
          uri: http://store:8080
          predicates:
            - Path=/stores/** 
        - id: reservation
          uri: http://reservation:8080
          predicates:
            - Path=/reservations/** 
      globalcors:
        corsConfigurations:
          '[/**]':
            allowedOrigins:
              - "*"
            allowedMethods:
              - "*"
            allowedHeaders:
              - "*"
            allowCredentials: true

server:
  port: 8080
  
```

- gateway 테스트

```
http http://localhost:8088/reservations
```
![image](https://user-images.githubusercontent.com/84000863/124536319-bfb61d80-de52-11eb-9c84-8495ebdcdfae.png)


## 비동기식 호출(Pub/Sub)

예약(reservation)이 이루어진 후에 업체(store)에서 차를 배차하는 행위는 동기식이 아니라 비 동기식으로 처리하여 업체(store)의 배차처리를 위하여 예약이 블로킹 되지 않도록 처리한다.

- 이를 위하여 예약완료 되었음을 도메인 이벤트를 카프카로 송출한다(Publish)
 
```
   @PostPersist
    public void onPostPersist(){
        ItemRented itemRented = new ItemRented();
        BeanUtils.copyProperties(this, itemRented);
        itemRented.publishAfterCommit();
```

- 업체(store)에서는 예악완료(Booked) 이벤트에 대해서 이를 수신하여 자신의 정책을 처리하도록 PolicyHandler 를 구현한다:

```
package carrent;

...

@Service
@Service
public class PolicyHandler{
    @Autowired StoreRepository storeRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverReserved_PrepareItem(@Payload Reserved reserved){

        if(reserved.isMe()){            
            Store store = new Store();
            store.setReservationId(reserved.getId());
            store.setItemId(reserved.getItemId());        
            store.setStatus("Item Rent Started");
            store.setName(reserved.getName());
            store.setQty(reserved.getQty());
            storeRepository.save(store);
        }
            
    }
    
...

```
예약(booking) 서비스는 업체(store) 서비스와 완전히 분리되어있으며(sync transaction 없음) 이벤트 수신에 따라 처리되기 때문에, store 서비스가 유지보수로 인해 잠시 내려간 상태라도 예약을 진행해도 문제 없다.(시간적 디커플링) :
  
- 업체(store) 서비스를 잠시 내려놓음(ctrl+c)

![image](https://user-images.githubusercontent.com/84000863/124536567-2c311c80-de53-11eb-92c7-a7a910030c65.png)

- 예약하기(booking)
```
http POST http://localhost:8084/reservations itemId=2 name=SamsungTV65in qty=1 startDate=2021-07-06 endDate=2021-07-07
```

< Success >

![image](https://user-images.githubusercontent.com/84000863/124536618-3bb06580-de53-11eb-90d2-558ab9f1fe7c.png)


## Deploy / Pipeline

- 소스 가져오기
```
git clone https://github.com/seokhoney/himart-master.git
```
![image](https://user-images.githubusercontent.com/84000863/124536733-74503f00-de53-11eb-9eff-ea33dd18d34b.png)

- 빌드하기
```
cd reservation
mvn package

cd customercenter
mvn package

cd gateway
mvn package

cd item
mvn package

cd store
mvn package
```

![image](https://user-images.githubusercontent.com/84000863/124536810-a2ce1a00-de53-11eb-854e-5e614a36b974.png)

- 도커라이징(Dockerizing) : Azure Container Registry(ACR)에 Docker Image Push하기
```
cd reservation
az acr build --registry user23skccacr --image user23skccacr.azurecr.io/reservation:v1 .

cd customercenter
az acr build --registry user23skccacr --image user23skccacr.azurecr.io/customercenter:v1 .

cd gateway
az acr build --registry user23skccacr --image user23skccacr.azurecr.io/gateway:v1 .

cd item
az acr build --registry user23skccacr --image user23skccacr.azurecr.io/item:v1 .

cd store
az acr build --registry user23skccacr --image user23skccacr.azurecr.io/store:v1 . 
```
![image](https://user-images.githubusercontent.com/84000863/124536931-ddd04d80-de53-11eb-8356-57b7b49a7582.png)

- 컨테이너라이징(Containerizing) : Deployment 생성
```
cd item
kubectl apply -f kubernetes/deployment.yml

cd reservation
kubectl apply -f kubernetes/deployment.yml

cd store
kubectl apply -f kubernetes/deployment.yml

cd customercenter
kubectl apply -f kubernetes/deployment.yml

cd gateway
kubectl create deploy gateway --image=user23skccacr.azurecr.io/gateway:v1

kubectl get all
```

- 컨테이너라이징(Containerizing) : Service 생성 확인
```
cd item
kubectl apply -f kubernetes/service.yaml

cd reservation
kubectl apply -f kubernetes/service.yaml

cd store
kubectl apply -f kubernetes/service.yaml

cd customercenter
kubectl apply -f kubernetes/service.yaml

cd gateway
kubectl expose deploy gateway --type=LoadBalancer --port=8080

kubectl get all
```

![image](https://user-images.githubusercontent.com/84000863/124537006-05271a80-de54-11eb-907f-d91650a4c66e.png)


## 서킷 브레이킹(Circuit Breaking)

* 서킷 브레이킹 : Spring FeignClient + Hystrix 사용
* 시나리오는 제품 등록 시 요청이 과도한 경우 CirCuit Breaker 통한 장애 격리

Hystrix 설정: 요청처리 쓰레드에서 처리시간이 610 밀리가 초과할 경우 CirCuit Breaker Closing 설정

![image](https://user-images.githubusercontent.com/84000863/124538496-b29b2d80-de56-11eb-93d3-212dc07a6690.png)

```
# (item) application.yml 

feign:
  hystrix:
    enabled: true
 
hystrix:
  command:
    default:
      execution.isolation.thread.timeoutInMilliseconds: 610
```

피호출되는 Item의 부하처리 : 400 밀리초 + 랜덤으로 220 밀리초 추가되도록 thread sleep 조정

![image](https://user-images.githubusercontent.com/84000863/124539517-9d270300-de58-11eb-8ae0-60c0791176ed.png)

* 부하테스터 siege 툴을 통한 서킷 브레이커 동작 확인:
- 동시사용자 100명
- 30초 동안 실시

```
$ siege -c100 -t30S -v --content-type "application/json" 'http://item:8080/items POST {"itemId": "99", "stock":"5", "name":"laptop"}'
```

앞서 설정한 부하가 발생하여 Circuit Breaker가 발동, 초반에는 요청 실패처리되었으며
밀린 부하가 product에서 처리되면서 다시 요청을 받기 시작함

- Availability 가 높아진 것을 확인 (siege)

![image](https://user-images.githubusercontent.com/84000863/124540849-30f9ce80-de5b-11eb-88c5-e27866b5612e.png)


### Autoscale (HPA)
앞서 CB 는 시스템을 안정되게 운영할 수 있게 해줬지만 사용자의 요청을 100% 받아들여주지 못했기 때문에 이에 대한 보완책으로 자동화된 확장 기능을 적용하고자 한다. 


- 제품(item) 서비스에 대한 replica 를 동적으로 늘려주도록 HPA 를 설정한다. 설정은 CPU 사용량이 1프로를 넘어서면 replica 를 10개까지 늘려준다:
```
kubectl autoscale deploy item --min=1 --max=10 --cpu-percent=1

kubectl get hpa
```
![image](https://user-images.githubusercontent.com/84000863/122494497-71b4b380-d024-11eb-948d-97ef4a8628ae.png)

- CB 에서 했던 방식대로 워크로드를 30초 동안 걸어준다.
```
$ siege -c100 -t30S -v --content-type "application/json" 'http://item:8080/items POST {"itemId": "99", "stock":"5", "name":"laptop"}'
```

- 오토스케일이 어떻게 되고 있는지 모니터링을 걸어둔다:
```
watch -n 1 kubectl get pod
```

- 어느정도 시간이 흐른 후 (약 30초) 스케일 아웃이 벌어지는 것을 확인할 수 있다:

![image](https://user-images.githubusercontent.com/84000863/124541296-12e09e00-de5c-11eb-8c0f-4f7dd0b75d9c.png)

![image](https://user-images.githubusercontent.com/84000863/124541339-255ad780-de5c-11eb-9ac7-f50c69cc94b1.png)

![image](https://user-images.githubusercontent.com/84000863/124541529-93070380-de5c-11eb-8231-42122c151998.png)


## 무정지 재배포

* 먼저 무정지 재배포가 100% 되는 것인지 확인하기 위해서 Autoscaler 이나 CB 설정을 제거함

- seige 로 배포작업 직전에 워크로드를 모니터링 함.
```
siege -c10 -t120S -v --content-type "application/json" 'http://item:8080/items POST {"itemId": "99", "stock":"5", "name":"laptop"}'
```
- Readiness가 설정되지 않은 yml 파일로 배포 중 버전1에서 버전2로 업그레이드 시 서비스 요청 처리 실패
```
kubectl set image deploy item item=user23skccacr.azurecr.io/item:v2
```

![image](https://user-images.githubusercontent.com/84000863/124545098-5c80b700-de63-11eb-9dd9-60f49fb97619.png)

- deployment.yml에 readiness 옵션을 추가
```
 readinessProbe:
   tcpSocket:
     port: 8080
   initialDelaySeconds: 10
   timeoutSeconds: 2
   periodSeconds: 5
   failureThreshold: 5          
```

- readiness 옵션을 배포 옵션을 설정 한 경우 Availability가 배포기간 동안 변화가 없기 때문에 무정지 재배포가 성공한 것으로 확인됨.

![image](https://user-images.githubusercontent.com/84000863/124545528-2d1e7a00-de64-11eb-8998-a23eed32788e.png)

- 기존 버전과 새 버전의 item replicaset

![image](https://user-images.githubusercontent.com/84000863/124545486-14ae5f80-de64-11eb-948d-8dda11b6a695.png)


## ConfigMap

- Reservation 서비스의 deployment.yml 파일에 아래 항목 추가
```
env:
   - name: STATUS
     valueFrom:
       configMapKeyRef:
         name: storecm
         key: status
```

- Reservation 서비스에 configMap 설정 데이터 가져오도록 아래 항목 추가

![image](https://user-images.githubusercontent.com/84000863/124545702-7a9ae700-de64-11eb-8bc8-04a728b5e983.png)

- ConfigMap 생성 및 조회
```
kubectl create configmap storecm --from-literal=status=Reserved
kubectl get configmap storecm -o yaml
```
![image](https://user-images.githubusercontent.com/84000863/124537946-c98d5000-de55-11eb-8298-266f655cac15.png)



## Self-Healing (Liveness Probe)

- 상품(product) 서비스의 deployment.yaml에 liveness probe 옵션 추가

![image](https://user-images.githubusercontent.com/84000863/122364364-a842ed80-cf94-11eb-8a45-980901aeaf3b.png)
 
- product에 liveness 적용 확인

![image](https://user-images.githubusercontent.com/84000863/122364275-95301d80-cf94-11eb-9312-7bbcab0a00d8.png)

- product 서비스에 liveness가 발동되었고, 포트에 응답이 없기에 Restart가 발생함

![image](https://user-images.githubusercontent.com/84000863/122365346-8d24ad80-cf95-11eb-855a-e8e724d273f6.png)
