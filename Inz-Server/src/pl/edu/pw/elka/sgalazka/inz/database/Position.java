package pl.edu.pw.elka.sgalazka.inz.database;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;




@Entity
@Table(name = "POSITION")
public class Position implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getVat() {
		return vat;
	}
	public void setVat(double vat) {
		this.vat = vat;
	}
	@Id
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CODE")
	private int code;
	@Column(name = "NAME")
	private String name;
	@Column(name = "VAT")
	private double vat;
}
