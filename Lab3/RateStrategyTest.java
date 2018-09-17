/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paystation.domain;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Pratik
 */
public class RateStrategyTest {
    
    public RateStrategyTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of calculateTime method, of class RateStrategy.
     */
    @Test
    public void testCalculateTimeLin() {
        RateStrategy instance = new LinearRate();
        int result = instance.calculateTime(150);
        assertEquals(60, result);
       
    }
    @Test
    public void testCalculateTimePro() {
        RateStrategy instance = new ProgressiveRate();
        int result = instance.calculateTime(150);
        assertEquals(60, result);
          result = instance.calculateTime(350);
        assertEquals(120, result);
         result = instance.calculateTime(650);
        assertEquals(180, result);
          result = instance.calculateTime(950);
        assertEquals(240, result);
       
    }
    @Test
    public void testCalculateTimeAltWhenWeekend() {
        RateStrategy weekend=new ProgressiveRate();
        RateStrategy weekday= new LinearRate();
        WeekendDecisionStrategy decision= new BooleanDecision(true);
        RateStrategy instance = new AlternatingRate(weekend,weekday,decision);
        int result = instance.calculateTime(150);
        assertEquals(60, result);
          result = instance.calculateTime(350);
        assertEquals(120, result);
         result = instance.calculateTime(650);
        assertEquals(180, result);
          result = instance.calculateTime(950);
        assertEquals(240, result);
       
    }
    @Test
    public void testCalculateTimeAltWhenWeekday() {
        RateStrategy weekend=new ProgressiveRate();
        RateStrategy weekday= new LinearRate();
        WeekendDecisionStrategy decision= new BooleanDecision(false);
        RateStrategy instance = new AlternatingRate(weekend,weekday,decision);
        int result = instance.calculateTime(150);
        assertEquals(60, result);
          result = instance.calculateTime(350);
        assertEquals(140, result);
         result = instance.calculateTime(650);
        assertEquals(260, result);
          result = instance.calculateTime(950);
        assertEquals(380, result);  
    }@Test
    public void testCalculateTimeAltClock() {
        //Since day i run this is weekday it should be linear
        RateStrategy weekend=new ProgressiveRate();
        RateStrategy weekday= new LinearRate();
        WeekendDecisionStrategy decision= new ClockBasedDecision();
        RateStrategy instance = new AlternatingRate(weekend,weekday,decision);
        int result = instance.calculateTime(150);
        assertEquals(60, result);
          result = instance.calculateTime(350);
        assertEquals(140, result);
         result = instance.calculateTime(650);
        assertEquals(260, result);
          result = instance.calculateTime(950);
        assertEquals(380, result);
    }
   
    
}
