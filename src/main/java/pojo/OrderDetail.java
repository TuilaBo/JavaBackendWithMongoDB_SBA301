package pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "order_detail")
public class OrderDetail {

    @Id
    private String id;

    private String orchidId;

    private double price;

    private int quantity;

    private String orderId;

    public OrderDetail() {
    }
    public OrderDetail(String id, String orchidId, double price, int quantity, String orderId) {
        this.id = id;
        this.orchidId = orchidId;
        this.price = price;
        this.quantity = quantity;
        this.orderId = orderId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrchidId() {
        return orchidId;
    }

    public void setOrchidId(String orchidId) {
        this.orchidId = orchidId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
