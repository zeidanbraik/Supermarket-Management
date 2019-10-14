package application;
 
import java.time.LocalDate;
 
public class SoldItems extends Item{
	Integer quant;
    LocalDate sellingDate;
    Double totalsellPrice;
    Double profit;
    
    
    public SoldItems() {
       
    }
   
    public SoldItems(String name,int quant,double sellPrice,LocalDate sellingDate, double BuyPrice) {
        this.name=name;
        this.quant=quant;
        this.sellPrice=sellPrice;
        this.sellingDate=sellingDate;
        this.buyPrice=BuyPrice;
        this.totalsellPrice=(this.sellPrice)*this.quant;
        this.profit=(this.sellPrice-this.buyPrice)*this.quant;
    }
   
public double getProfit() {
    return profit;
}
 
   
public double getTotalSellPrice() {
   
    return totalsellPrice;
}

public int getQuant() {
	return quant;
}

public void setQuant(Integer quant) {
	this.quant = quant;
}

public LocalDate getSellingDate() {
	return sellingDate;
}

public void setSellingDate(LocalDate sellingDate) {
	this.sellingDate = sellingDate;
}

public Double getTotalsellPrice() {
	return totalsellPrice;
}

public void setTotalsellPrice(Double totalsellPrice) {
	this.totalsellPrice = totalsellPrice;
}

public void setProfit(Double profit) {
	this.profit = profit;
}

public void update() {
	  this.profit=(this.sellPrice-this.buyPrice)*this.quant;
}

   
}
