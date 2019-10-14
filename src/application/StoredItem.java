package application;

import java.time.LocalDate;

public class StoredItem extends Item{
	
	int quant;
	double totalProfit;
	
	public StoredItem() {
		
	}

	public StoredItem(String barcode, String name, double buyPrice, double sellPrice, LocalDate expDate,int quant) {
		super(barcode, name, buyPrice, sellPrice, expDate);
		this.quant=quant;
		this.totalProfit=quant*(sellPrice-buyPrice);
	}
	
	public int getQuant() {
		return quant;
	}

	public void setQuant(int quant) {
		this.quant = quant;
	}
	
	public double getTotalProfit() {
		return this.totalProfit=quant*(sellPrice-buyPrice);
	}
		
	
}
