import java.lang.*;
import java.util.*;
import java.time.LocalDateTime;

enum StockSymbol {
    TEA,
    POP,
    ALE,
    GIN,
    JOE};

enum StockType {
    Common,
    Preferred};
enum BuyOrSell {
    Buy,
    Sell};

class Stock
{
    public final StockSymbol symbol;
    public final StockType type;
    public final double lastDividend;
    public final double fixedDividend;
    public final double parValue;
    public Stock(StockSymbol symbol, StockType type, double lastDividend, double fixedDividend, double parValue)
    {
        this.symbol = symbol;
        this.type = type;
        this.lastDividend = lastDividend;
        this.fixedDividend = fixedDividend;
        this.parValue = parValue;
    }
}

class Trade 
{
    public final LocalDateTime timestamp;
    public final StockSymbol   symbol;
    public final BuyOrSell     buysell;
    public final double        quantity;
    public final double        price;
    
    public Trade(StockSymbol symbol, BuyOrSell buysell, double price, double quantity, LocalDateTime timestamp)
    {
        this.timestamp = timestamp;
        this.symbol    = symbol;
        this.buysell   = buysell;
        this.price     = price;
        this.quantity  = quantity;           
    }
    public String toString()
    {
        return "TRADE "+symbol.name()+" "+buysell.name()+" quantity " + quantity  + " at price "+price+" at time "+ timestamp;
    }
}
public class StockTrading
{
    static HashMap<StockSymbol, Stock> stockMap = new HashMap<StockSymbol, Stock>();
    static ArrayList<Trade> trades = new ArrayList<Trade>();
    
    public static void initReferenceData()
    {
        Stock stock1 = new Stock(StockSymbol.TEA, StockType.Common, 0, -1 , 100);
        
        stockMap.put(StockSymbol.TEA, stock1);
        
        Stock stock2 = new Stock(StockSymbol.POP, StockType.Common, 8, -1, 100);
        
        stockMap.put(StockSymbol.POP, stock2);
        
        Stock stock3 = new Stock(StockSymbol.ALE, StockType.Common, 23, -1, 60);
        
        stockMap.put(StockSymbol.ALE, stock3);
        
        Stock stock4 = new Stock(StockSymbol.GIN, StockType.Preferred, 8, 0.02, 100);
        
        stockMap.put(StockSymbol.GIN, stock4);
        
        Stock stock5 = new Stock(StockSymbol.JOE, StockType.Common, 13 , -1, 250);
        
        stockMap.put(StockSymbol.JOE, stock5);
        
    }
    //Task a 1 - calculating the dividend yield
    static double calculateDividendYield(StockSymbol symbol, double price)
    {
         Stock stock = stockMap.get(symbol);
         if (stock.type == StockType.Common)
         {
             return stock.lastDividend/price;
         }
         else
         {
             return stock.parValue*stock.fixedDividend/price;
         }  
    }
    
    //Task a 2 - calculating PE Ratio
    static double calculatePERatio(StockSymbol symbol, double price) 
    {
        double dividend = calculateDividendYield(symbol, price);
        return price/dividend;
    }
    
    // Task a 3 - record trades
    static void recordTrade(StockSymbol symbol, BuyOrSell buysell, double price, double quantity)
    {
        Trade trade = new Trade(symbol, buysell, price, quantity, LocalDateTime.now());
        trades.add(trade);
    }
    
    static void printTrades()
    {
        for(Trade trade : trades)
        {
            System.out.println(trade.toString());
        }
    }

    static void clearTrades()
    {
        trades.clear();
    }

    // Task a 4 - Calculate volume weighted stock price
    
    static Double volumeWeightedStockPrice(StockSymbol symbol)
    {
        double weightedSum = 0.0;
        double totalWeight = 0.0;
        LocalDateTime currentTime = LocalDateTime.now();
        for(Trade trade : trades)
        {
            if (trade.symbol==symbol && trade.timestamp.isAfter(currentTime.minusMinutes(15)))
            {
                weightedSum += trade.price*trade.quantity;
                totalWeight += trade.quantity;
            }
        }
        if (totalWeight == 0.0)
        {
            return null;
        } else {
            return new Double(weightedSum/totalWeight);
        }
    }

    // Task b - Calculate GBCE All Share Index

    static double GBCEMarketIndex()
    {
        double marketIndex = 1;
        double count = 0;

        for (StockSymbol symbol: StockSymbol.values())
        {
            Double price = volumeWeightedStockPrice(symbol);
            if (price != null)
            {
                marketIndex = marketIndex * price.doubleValue();
                count = count + 1;
            }
        }

        marketIndex = Math.pow(marketIndex, 1.0/count);
        return marketIndex;
    }


     
    public static void main(String [] args)
    {
        initReferenceData();
        test1();
        test2();
        test3();
        test4();
    }
    
    public static void addSampleTrades()
    {
        recordTrade(StockSymbol.TEA, BuyOrSell.Buy, 10, 100);
        recordTrade(StockSymbol.GIN, BuyOrSell.Buy, 15, 150);
        recordTrade(StockSymbol.TEA, BuyOrSell.Buy, 13, 130);
        recordTrade(StockSymbol.TEA, BuyOrSell.Buy, 12, 200);
        recordTrade(StockSymbol.ALE, BuyOrSell.Buy, 11, 110);
        recordTrade(StockSymbol.GIN, BuyOrSell.Buy, 12, 170);
        recordTrade(StockSymbol.JOE, BuyOrSell.Buy, 13, 140);
        recordTrade(StockSymbol.POP, BuyOrSell.Buy, 12, 210);
        recordTrade(StockSymbol.ALE, BuyOrSell.Buy, 14, 180);
        recordTrade(StockSymbol.GIN, BuyOrSell.Buy, 16, 160);
        recordTrade(StockSymbol.JOE, BuyOrSell.Buy, 13, 150);
        recordTrade(StockSymbol.POP, BuyOrSell.Buy, 15, 230);
     
    }

        static void test1()
    {
        System.out.println("TEST1");
        double dividendYield = calculateDividendYield(StockSymbol.TEA, 10);
        System.out.println("dividendYield for TEA at price 10="+dividendYield);
         dividendYield = calculateDividendYield(StockSymbol.GIN, 20);
        System.out.println("dividendYield for GIN at price 20="+dividendYield);
    }

    static void test2()
    {
        System.out.println("TEST2");
        double peRatio = calculatePERatio(StockSymbol.TEA, 10);
        System.out.println("PE ratio for TEA at price 10="+peRatio);
        
        peRatio = calculatePERatio(StockSymbol.GIN, 20);
        System.out.println("PE ratio for GIN at price 20="+peRatio);
        
        peRatio = calculatePERatio(StockSymbol.POP, 25);
        System.out.println("PE ratio for POP at price 25="+peRatio);
    }
    
    public static void test3(){
        System.out.println("TEST3"); 
        clearTrades();
        addSampleTrades();
        printTrades();
        Double price = volumeWeightedStockPrice(StockSymbol.TEA);
        if(price != null)
        {
            System.out.println("Volume Weighted Stock Price of TEA="+price.doubleValue());
        }
    }

    public static void test4(){
        System.out.println("TEST4");
        clearTrades();
        addSampleTrades();
        printTrades();
        double marketIndex = GBCEMarketIndex();
        System.out.println("marketIndex="+marketIndex);
        
    }
    

}

