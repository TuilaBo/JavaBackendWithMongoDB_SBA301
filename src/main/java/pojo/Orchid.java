package pojo;

import jakarta.persistence.*;

@Entity
@Table(name = "orchid")
public class Orchid {

    @Id
    private String id;

    private Boolean isNatural;

    private String orchidDescription;

    private String orchidName;

    private String orchidUrl;

    private double price;

    @Column(name = "category_id")
    private Long categoryId;

    public Orchid() {
    }

    public Orchid(String id, Boolean isNatural, String orchidDescription, String orchidName, String orchidUrl, double price, Long categoryId) {
        this.id = id;
        this.isNatural = isNatural;
        this.orchidDescription = orchidDescription;
        this.orchidName = orchidName;
        this.orchidUrl = orchidUrl;
        this.price = price;
        this.categoryId = categoryId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getNatural() {
        return isNatural;
    }

    public void setNatural(Boolean natural) {
        isNatural = natural;
    }

    public String getOrchidDescription() {
        return orchidDescription;
    }

    public void setOrchidDescription(String orchidDescription) {
        this.orchidDescription = orchidDescription;
    }

    public String getOrchidName() {
        return orchidName;
    }

    public void setOrchidName(String orchidName) {
        this.orchidName = orchidName;
    }

    public String getOrchidUrl() {
        return orchidUrl;
    }

    public void setOrchidUrl(String orchidUrl) {
        this.orchidUrl = orchidUrl;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}
