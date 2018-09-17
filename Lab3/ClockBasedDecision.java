/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paystation.domain;

import java.util.Calendar;

import java.util.GregorianCalendar;

/**
 *
 * @author Pratik
 */
public class ClockBasedDecision implements WeekendDecisionStrategy{

    @Override
    public boolean isWeekend() {
        
        Calendar c = new GregorianCalendar();
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        return (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY);
        
        
        
    }
    
}
