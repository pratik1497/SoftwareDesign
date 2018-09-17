/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paystation.domain;

/**
 *
 * @author Pratik
 */
public class AlternatingRate implements RateStrategy{
    private RateStrategy weekend;
    private RateStrategy weekday;
    private RateStrategy current;
    private WeekendDecisionStrategy decision;
    
    public AlternatingRate(RateStrategy weekendStr,RateStrategy weekdayStr,WeekendDecisionStrategy decisionStr)
    {
        weekend=weekendStr;
        weekday= weekdayStr;
        current=null;
        decision= decisionStr;
    }

    
    @Override
public int calculateTime(int amount)
    {
      boolean isWeekend=decision.isWeekend();
      if(isWeekend)
      {
          current=weekend;
      }
      else
      {
          current=weekday;
      }
      return current.calculateTime(amount);
    }
    
}
