/**
 * Testcases for the Pay Station system.
 *
 * This source code is from the book "Flexible, Reliable Software: Using
 * Patterns and Agile Development" published 2010 by CRC Press. Author: Henrik B
 * Christensen Computer Science Department Aarhus University
 *
 * This source code is provided WITHOUT ANY WARRANTY either expressed or
 * implied. You may study, use, modify, and distribute it for non-commercial
 * purposes. For any commercial use, see http://www.baerbak.com/
 */
package paystation.domain;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import java.util.Map;
import java.util.HashMap;

public class PayStationImplTest {

    PayStation ps;

    @Before
    public void setup() {
        ps = new PayStationImpl(new LinearRate());
    }

    /**
     * Entering 5 cents should make the display report 2 minutes parking time.
     */
    @Test
    public void shouldDisplay2MinFor5Cents()
            throws IllegalCoinException {
        ps.addPayment(5);
        assertEquals("Should display 2 min for 5 cents",
                2, ps.readDisplay());
    }

    /**
     * Entering 25 cents should make the display report 10 minutes parking time.
     */
    @Test
    public void shouldDisplay10MinFor25Cents() throws IllegalCoinException {
        ps.addPayment(25);
        assertEquals("Should display 10 min for 25 cents",
                10, ps.readDisplay());
    }

    /**
     * Verify that illegal coin values are rejected.
     */
    @Test(expected = IllegalCoinException.class)
    public void shouldRejectIllegalCoin() throws IllegalCoinException {
        ps.addPayment(17);
    }

    /**
     * Entering 10 and 25 cents should be valid and return 14 minutes parking
     */
    @Test
    public void shouldDisplay14MinFor10And25Cents()
            throws IllegalCoinException {
        ps.addPayment(10);
        ps.addPayment(25);
        assertEquals("Should display 14 min for 10+25 cents",
                14, ps.readDisplay());
    }

    /**
     * Buy should return a valid receipt of the proper amount of parking time
     */
    @Test
    public void shouldReturnCorrectReceiptWhenBuy()
            throws IllegalCoinException {
        ps.addPayment(5);
        ps.addPayment(10);
        ps.addPayment(25);
        Receipt receipt;
        receipt = ps.buy();
        assertNotNull("Receipt reference cannot be null",
                receipt);
        assertEquals("Receipt value must be 16 min.",
                16, receipt.value());
    }

    /**
     * Buy for 100 cents and verify the receipt
     */
    @Test
    public void shouldReturnReceiptWhenBuy100c()
            throws IllegalCoinException {
        ps.addPayment(10);
        ps.addPayment(10);
        ps.addPayment(10);
        ps.addPayment(10);
        ps.addPayment(10);
        ps.addPayment(25);
        ps.addPayment(25);

        Receipt receipt;
        receipt = ps.buy();
        assertEquals(40, receipt.value());
    }

    /**
     * Verify that the pay station is cleared after a buy scenario
     */
    @Test
    public void shouldClearAfterBuy()
            throws IllegalCoinException {
        ps.addPayment(25);
        ps.buy(); // I do not care about the result
        // verify that the display reads 0
        assertEquals("Display should have been cleared",
                0, ps.readDisplay());
        // verify that a following buy scenario behaves properly
        ps.addPayment(10);
        ps.addPayment(25);
        assertEquals("Next add payment should display correct time",
                14, ps.readDisplay());
        Receipt r = ps.buy();
        assertEquals("Next buy should return valid receipt",
                14, r.value());
        assertEquals("Again, display should be cleared",
                0, ps.readDisplay());
    }

    /**
     * Verify that cancel clears the pay station
     */
    @Test
    public void shouldClearAfterCancel()
            throws IllegalCoinException {
        ps.addPayment(10);
        ps.cancel();
        assertEquals("Cancel should clear display",
                0, ps.readDisplay());
        ps.addPayment(25);
        assertEquals("Insert after cancel should work",
                10, ps.readDisplay());
    }
    
    /**
     * Verify that empty returns total amount in pay station
     */
    @Test
    public void shouldReturnTotalAmountAfterEmpty() throws IllegalCoinException
    {
        ps.addPayment(25);
        ps.addPayment(5);
        ps.buy();
        ps.addPayment(25);
        ps.addPayment(10);
        ps.buy();
        int result=ps.empty();
        assertEquals("Should display 65 cents as it is total after 2 buys",
                65, result);
    }
    /**
     * Verify that canceled entry does not add to amount returned in empty
     */
    @Test
    public void shouldNotAddCanceledEntryToTotal() throws IllegalCoinException
    {
        ps.addPayment(25);
        ps.addPayment(5);
        ps.buy();
        ps.addPayment(25);
        ps.addPayment(10);
        ps.cancel();
        int result=ps.empty();
        assertEquals("Should display 30 cents",
                30, result);
    }
    /**
     * Verify that empty sets total to zero
     */
    @Test
    public void shouldSetTotalAmountToZeroAfterEmpty() throws IllegalCoinException
    {
        ps.addPayment(25);
        ps.addPayment(5);
        ps.buy();
        ps.empty();//Call empty to set to zero and call again to see if it is zero
        int result=ps.empty();
        assertEquals("Empty Twice should display 0",
                0, result);
    }
    
    /**
     * Verify that cancel returns a map for one coin
     */
    @Test
    public void shouldReturnCoinMapForOneCoin() throws IllegalCoinException
    {
       Map<Integer,Integer> results=ps.cancel();
       assertNotNull("Mapping cannot be null",
                results);
        ps.addPayment(25);
        results=ps.cancel();
        Map<Integer,Integer> expected=new HashMap<>();
        expected.put(25, 1);
        assertEquals("Cancel should return map of 25 and 1",
                expected, results);
    }
    
    /**
     * Verify that cancel returns a map for mixture of coins
     */
    @Test
    public void shouldReturnCoinMapForMixtureOfCoins() throws IllegalCoinException
    {
        ps.addPayment(25);
        ps.addPayment(5);
        ps.addPayment(25);
        ps.addPayment(10);
        ps.addPayment(10);
        Map<Integer,Integer> results=ps.cancel();
        Map<Integer,Integer> expected=new HashMap<>();
        expected.put(25, 2);
        expected.put(5,1);
        expected.put(10,2);
        assertEquals("Cancel should return map of 25-2 5-1 10-2",
                expected, results);
    }
    /**
     * Verify that cancel returns a map that does not contain key for a 
     * coin not entered
     */
    @Test
    public void shouldReturnCoinMapWhenOneKeyNotEntered() 
            throws IllegalCoinException
    {
        ps.addPayment(25);
        ps.addPayment(10);
        ps.addPayment(10);
        ps.addPayment(10);
        Map<Integer,Integer> results=ps.cancel();
        Map<Integer,Integer> expected=new HashMap<>();
        expected.put(25, 1);
        expected.put(10, 3);
        assertEquals("Cancel should return map containing no 5",
                expected, results);
    }
    /**
     * Verify that cancel clears map
     */
    @Test
    public void shouldMakeSureCancelClearsMap() throws IllegalCoinException
    {
        ps.addPayment(25);
       Map<Integer,Integer> results=ps.cancel();
       results=ps.cancel();
       Map<Integer,Integer> expected=new HashMap<>();
        assertEquals("Cancel should return map that is empty as first cancel "
                + "should have cleared", expected, results);
    }
    /**
     * Verify that buys clears map
     */
    @Test
    public void shouldMakeSureBuyClearsMap() throws IllegalCoinException
    {
        ps.addPayment(25);
        ps.buy();
       Map<Integer,Integer> results=ps.cancel();
       Map<Integer,Integer> expected=new HashMap<>();
        assertEquals("Cancel should return map that is empty as buy "
                + "should have cleared", expected, results);
    }
    
    
    
    
}
