package application;

import java.time.LocalDate;

public class Item {
	
	String name="";
	String barcode="";
	double buyPrice;
	double sellPrice;
	LocalDate expDate;	
	
	//// for the tableview
	
	double totalBP;
	double totalSP;
	int quant;
	
	public Item() {
		
	}
	
	public Item(String barcode, String name, double buyPrice, double sellPrice, LocalDate expDate) {
		super();
		this.barcode = barcode;
		this.name = name;
		this.buyPrice = buyPrice;
		this.sellPrice = sellPrice;
		this.expDate = expDate;
	}
	
	
	public Item(String barcode, String name,int quant, double buyPrice, double sellPrice, LocalDate expDate,double totalBP,double totalSP) {
		super();
		this.barcode = barcode;
		this.name = name;
		this.quant=quant;
		this.buyPrice = buyPrice;
		this.sellPrice = sellPrice;
		this.expDate = expDate;
		this.totalBP=totalBP;
		this.totalSP=totalSP;
	}
	
	
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getBuyPrice() {
		return buyPrice;
	}
	public void setBuyPrice(double buyPrice) {
		this.buyPrice = buyPrice;
	}
	public double getSellPrice() {
		return sellPrice;
	}
	public void setSellPrice(double sellPrice) {
		this.sellPrice = sellPrice;
	}
	public LocalDate getExpDate() {
		return expDate;
	}
	public void setExpDate(LocalDate expDate) {
		this.expDate = expDate;
	}


	public double getTotalBP() {
		return totalBP;
	}

	public void setTotalBP(double totalBP) {
		this.totalBP = totalBP;
	}

	public double getTotalSP() {
		return totalSP;
	}

	public void setTotalSP(double totalSP) {
		this.totalSP = totalSP;
	}

	public int getQuant() {
		return quant;
	}

	public void setQuant(int quant) {
		this.quant = quant;
	}
	
	public void update() {
		this.quant++;
		this.totalSP=sellPrice*quant;
	}
	
}


/*class BoughtItem extends Item{
	
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
	
	
	
}*/