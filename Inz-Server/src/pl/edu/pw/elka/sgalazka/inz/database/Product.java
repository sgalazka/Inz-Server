package pl.edu.pw.elka.sgalazka.inz.database;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by ga��zka on 2015-10-15.
 */
@Entity
@Table(name = "PRODUCT")
@SequenceGenerator(name = "seq", initialValue = 1, allocationSize = 1)
public class Product implements Serializable {

    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seq")
    @Id
    long id;
    @Column(name = "barcode", unique = true)
    private String barcode;
    @Column(name = "quantity")
    private int quantity;
    @Column(name = "name")
    private String name;
    @Column(name = "vat")
    private String vat;
    @Column(name = "packaging")
    private int packaging;
    @Column(name = "price")
    private int price;


    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVat() {
        return vat;
    }

    public void setVat(String vat) {
        this.vat = vat;
    }

    public int getPackaging() {
        return packaging;
    }

    public void setPackaging(int packaging) {
        this.packaging = packaging;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
