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
    @Autowired StoreRepository storeRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverReserved_PrepareItem(@Payload Reserved reserved){

        if(reserved.isMe()){            
            Store store = new Store();
            store.setReservationId(reserved.getId());
            store.setItemId(reserved.getItemId());        
            store.setStatus("Item Rent Started");
            store.setQty(reserved.getQty());
            storeRepository.save(store);
        }
            
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverReservationCancelled_DeleteReservation(@Payload ReservationCancelled reservationCancelled){

        if(reservationCancelled.isMe()){            
            Store store = new Store();
            store.setReservationId(reservationCancelled.getId());
            storeRepository.delete(store);
        }  
            
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
