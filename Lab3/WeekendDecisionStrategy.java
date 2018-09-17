
package paystation.domain;

/**
 *
 * @author Pratik
 */
public interface WeekendDecisionStrategy {
    
    /*
    returns true if the current day is Saturday or Sunday an false otherwise
    */
    public boolean isWeekend();
    
    
}
