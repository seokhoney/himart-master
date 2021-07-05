package himart;

import himart.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PolicyHandler{
    @Autowired ItemRepository itemRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverReservationCancelled_IncreaseStock(@Payload ReservationCancelled reservationCancelled){

        if(reservationCancelled.isMe()){        
            Item item = itemRepository.findByItemId(Long.valueOf(reservationCancelled.getItemId()));
            item.setStock(item.getStock() + reservationCancelled.getQty());
            itemRepository.save(item);  
          }
            
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverItemReturned_IncreaseStock(@Payload ItemReturned itemReturned){

        if(itemReturned.isMe()){        
            Item item = itemRepository.findByItemId(Long.valueOf(itemReturned.getItemId()));
            item.setStock(item.getStock() + itemReturned.getQty());
            itemRepository.save(item);  
          }
            
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
