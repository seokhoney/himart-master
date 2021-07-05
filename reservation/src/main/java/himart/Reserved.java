package himart;

import java.util.Date;

public class Reserved extends AbstractEvent {

    private Long id;
    private Long itemId;
    private Integer qty;
    private String status;
    private Date startDate;
    private Date endDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
    public Date getBookingFrom() {
        return startDate;
    }

    public void setBookingFrom(Date startDate) {
        this.startDate = startDate;
    }
    public Date getBookingTo() {
        return endDate;
    }

    public void setBookingTo(Date endDate) {
        this.endDate = endDate;
    }
}