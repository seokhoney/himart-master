package himart;

import himart.config.kafka.KafkaProcessor;

import java.util.Optional;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PolicyHandler{
    @Autowired ReservationRepository reservationRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverItemRented_UpdateStatus(@Payload ItemRented itemRented){

        if(itemRented.isMe()){        
            Optional<Reservation> optionalReservation = reservationRepository.findById(itemRented.getReservationId());
            Reservation reservation = optionalReservation.get();
            reservation.setStatus("Rented");
            reservationRepository.save(reservation);
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverItemReturned_UpdateStatus(@Payload ItemReturned itemReturned){

        if(itemReturned.isMe()){  
            Optional<Reservation> optionalReservation = reservationRepository.findById(itemReturned.getReservationId());
            Reservation reservation = optionalReservation.get();
            reservation.setStatus("Returned");
            reservationRepository.save(reservation);
          }
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
