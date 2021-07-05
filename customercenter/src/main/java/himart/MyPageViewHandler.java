package himart;

import himart.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class MyPageViewHandler {


    @Autowired
    private MyPageRepository myPageRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenReserved_then_CREATE_1 (@Payload Reserved reserved) {
        try {

            if (reserved.isMe()) {
            // view 객체 생성
            MyPage myPage = new MyPage();
            // view 객체에 이벤트의 Value 를 set 함
            myPage.setReservationId(reserved.getId());
            myPage.setItemId(reserved.getItemId());
            myPage.setQty(reserved.getQty());
            myPage.setStartDate(reserved.getStartDate());
            myPage.setEndDate(reserved.getEndDate());
            myPage.setReservationStatus(reserved.getStatus());
            // view 레파지 토리에 save
            myPageRepository.save(myPage);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whenItemRented_then_UPDATE_1(@Payload ItemRented itemRented) {
        try {
            if (itemRented.isMe()){

                // view 객체 조회
                List<MyPage> myPageList = myPageRepository.findByReservationId(itemRented.getReservationId());
                for(MyPage myPage : myPageList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    myPage.setReservationStatus(itemRented.getStatus());
                    // view 레파지 토리에 save
                    myPageRepository.save(myPage);
                }
             }
            
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenItemReturned_then_UPDATE_2(@Payload ItemReturned itemReturned) {
        try {
            if (itemReturned.isMe()){

                // view 객체 조회
            List<MyPage> myPageList = myPageRepository.findByReservationId(itemReturned.getReservationId());
            for(MyPage myPage : myPageList){
                // view 객체에 이벤트의 eventDirectValue 를 set 함
                myPage.setReservationStatus(itemReturned.getStatus());
                // view 레파지 토리에 save
                myPageRepository.save(myPage);
            }
        }
            
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}