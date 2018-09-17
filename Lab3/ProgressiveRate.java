
package paystation.domain;

/**
 *
 * @author Pratik
 */
public class ProgressiveRate implements RateStrategy{
    
 @Override
public int calculateTime(int amount)
    {
        int time=0;
        if(amount >= 150+200) //From 2nd hour onwards
        {
            amount -= 350;
            time= 120 + amount/5;
        }
        else if(amount >=150) // from 1st to 2nd hr
        {
            amount-=150;
            time= 60 + amount * 3/10;
        }
        else{ // up to 1st hr
            time=amount * 2/5;
        }
        return time;
    }
    
}
