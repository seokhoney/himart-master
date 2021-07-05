package himart;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Store_table")
public class Store {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long reservationId;
    private Long itemId;
    private Integer qty;
    private String status;
    private String name;

    @PostPersist
    public void onPostPersist(){
        ItemRented itemRented = new ItemRented();
        BeanUtils.copyProperties(this, itemRented);
        itemRented.publishAfterCommit();


    }

    @PreUpdate
    public void onPreUpdate(){
        ItemReturned itemReturned = new ItemReturned();
        BeanUtils.copyProperties(this, itemReturned);
        itemReturned.publishAfterCommit();


    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }
    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
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
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }




}
