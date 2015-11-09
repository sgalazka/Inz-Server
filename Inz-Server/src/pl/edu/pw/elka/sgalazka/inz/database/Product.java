package pl.edu.pw.elka.sgalazka.inz.database;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created by ga³¹zka on 2015-10-15.
 */
@Entity
@Table(name = "PRODUCT")
public class Product implements Serializable {


    @Id
    @Column(name = "id")
    private int code;
    @Column(name = "barcode")
    private String barcode;
    @Column(name = "quantity")
    private int quantity;
    @Column(name = "name")
    private String name;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

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
}
