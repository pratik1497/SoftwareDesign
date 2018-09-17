
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
    //Used to hold total amount in paystation
    private int total;               
    //Contains mapping of each coinvalue to number of coins of that coinvalue
    private Map<Integer,Integer> coins; 
    
    //A constuctor to intialize map so it can grab storage space
    PayStationImpl()
    {
        coins=new HashMap<>();
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
        /*
        If the map already contains the coin value then just increment index
        else add coinValue to map with value 1
        */
        if(coins.containsKey(coinValue))
        { 
          coins.put(coinValue, coins.get(coinValue)+1);
        }
        else
            coins.put(coinValue, 1);
        insertedSoFar += coinValue;
        timeBought = insertedSoFar / 5 * 2;
    }

    @Override
    public int readDisplay() {
        return timeBought;
    }

    @Override
    public Receipt buy() {
        Receipt r = new ReceiptImpl(timeBought);
        /*
        *Since buy is the only way the paystation actually gains money
        *buy is only place where total is increment so it can later be emptied
        */
        total += insertedSoFar;
        reset();
        return r;
    }
    @Override
    public int empty()
    {
        /*
        *Creates temporary integer to hold total amount so 
        * total can be reset back to zero
        */
        int hold=total;
        total=0;
        return hold;
    }

    @Override
    public Map<Integer,Integer> cancel() {
        /*
        *Creates a temporary Map so that we can reset map to be empty 
        *and still return original mapping
        */
        Map<Integer,Integer> returnMapping= new HashMap<>();
        returnMapping.putAll(coins);
        reset();
       return returnMapping;
        
    }
    
    private void reset() {
        /*
        *Since reset is only called by buy and cancel and the mapping
        *should be emptied only by buy or cancel, Reset also clears mapping
        */
        timeBought = insertedSoFar = 0;
        coins.clear();
    }
}
