package himart;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Reservation_table")
public class Reservation {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Integer qty;
    private String status;
    private Date startDate;
    private Date endDate;
    private Long itemId;
    private String name;

    @PostPersist
    public void onPostPersist(){

        boolean rslt = ReservationApplication.applicationContext.getBean(himart.external.ItemService.class)
        .modifyStock(this.getItemId(), this.getQty());

        if (rslt) {
            this.setStatus("Reserved");
            //this.setStatus(System.getenv("STATUS"));

            Reserved reserved = new Reserved();
            BeanUtils.copyProperties(this, reserved);
            reserved.publishAfterCommit();                
        } else {throw new ReservationException("No Available stock!");}

    }

    @PreRemove
    public void onPreRemove(){
        himart.external.Cancellation cancellation = new himart.external.Cancellation();
        // mappings goes here
        cancellation.setReservationId(this.getId());

        this.setStatus("Cancelled");
        ReservationCancelled reservationCancelled = new ReservationCancelled();
        BeanUtils.copyProperties(this, reservationCancelled);
        reservationCancelled.publishAfterCommit();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }




}
