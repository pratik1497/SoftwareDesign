
package paystation.domain;

/**
 *
 * @author Pratik
 */


public interface RateStrategy {
/**
 * 
 * @param coinValues
 * Calculates the time equivalence to the amount given
 * @return time in minutes 
 */    
    public int calculateTime(int coinValues);
    
    
}
