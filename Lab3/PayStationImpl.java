
package paystation.domain;
import java.util.Map;
import java.util.HashMap;
/**
 * Implementation of the pay station.
 *
 * Responsibilities:
 *
 * 1) Accept payment; 
 * 2) Calculate parking time based on payment; 
 * 3) Know earning, parking time bought; 
 * 4) Issue receipts; 
 * 5) Handle buy and cancel events.
 *
 * This source code is from the book "Flexible, Reliable Software: Using
 * Patterns and Agile Development" published 2010 by CRC Press. Author: Henrik B
 * Christensen Computer Science Department Aarhus University
 *
 * This source code is provided WITHOUT ANY WARRANTY either expressed or
 * implied. You may study, use, modify, and distribute it for non-commercial
 * purposes. For any commercial use, see http://www.baerbak.com/
 */
public class PayStationImpl implements PayStation {
    
    private int insertedSoFar;
    private int timeBought;
    private int total;
    private Map<Integer,Integer> coins;
    private RateStrategy rateStrategy;
    
    PayStationImpl(RateStrategy rateStrategy)
    {
        coins=new HashMap<>();
        this.rateStrategy=rateStrategy;
    }
    //This will allow the paystation to change the rateStrategy at runtime
    @Override
    public void changeRate(RateStrategy newStra)
    {
        this.rateStrategy=newStra;
    }

    @Override
    public void addPayment(int coinValue)
            throws IllegalCoinException {
        switch (coinValue) {
            case 5: break;
            case 10:  break; 
            case 25: break;
            
            default:
                throw new IllegalCoinException("Invalid coin: " + coinValue);
        }
        if(coins.containsKey(coinValue))
        { 
          coins.put(coinValue, coins.get(coinValue)+1);
        }
        else
            coins.put(coinValue, 1);
        insertedSoFar += coinValue;
        timeBought = rateStrategy.calculateTime(insertedSoFar);
    }

    @Override
    public int readDisplay() {
        
        updateTime();
        return timeBought;
    }

    @Override
    public Receipt buy() {
        updateTime();
        Receipt r = new ReceiptImpl(timeBought);
        total += insertedSoFar;
        reset();
        return r;
    }
    @Override
    public int empty()
    {
        int hold=total;
        total=0;
        return hold;
    }

    @Override
    public Map<Integer,Integer> cancel() {
        Map<Integer,Integer> returnMapping= new HashMap<>();
        returnMapping.putAll(coins);
        reset();
       return returnMapping;
        
    }
    private void updateTime()
    {
        timeBought=rateStrategy.calculateTime(insertedSoFar);
    }
    
    private void reset() {
        timeBought = insertedSoFar = 0;
        coins.clear();
    }
}
