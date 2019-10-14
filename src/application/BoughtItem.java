package application;

import java.time.LocalDate;

public class BoughtItem extends Item{
	
	int quant;
	LocalDate buyingDate;
	String supplierName="";
	
	public BoughtItem() {
		super();
	}
	
	public BoughtItem(String barcode,String name,double buyPrice,double sellPrice,int quant,LocalDate buyingDate,String supplierName) {
		this.barcode=barcode;
		this.name=name;
		this.buyPrice=buyPrice;
		this.sellPrice=sellPrice;
		this.quant=quant;
		this.buyingDate=buyingDate;
		this.supplierName=supplierName;
		
	}

	public int getQuant() {
		return quant;
	}

	public void setQuant(int quant) {
		this.quant = quant;
	}

	public LocalDate getBuyingDate() {
		return buyingDate;
	}

	public void setBuyingDate(LocalDate buyingDate) {
		this.buyingDate = buyingDate;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}
	
	
	
}
