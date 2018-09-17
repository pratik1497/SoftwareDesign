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
public class BooleanDecision implements WeekendDecisionStrategy{
private boolean weekend;

BooleanDecision(boolean weekend)
{
    this.weekend=weekend;
}

    
    @Override
    public boolean isWeekend() {
        return weekend;
    }
    
    
}
