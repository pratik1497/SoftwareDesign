
package paystation.domain;

import java.util.Map;
import java.util.Scanner;
/**
 *
 * @author Pratik
 */
public class SimulatePaystation {
   
    public static void main(String [ ] args) throws IllegalCoinException
{   
     RateStrategy lin=new LinearRate();
     RateStrategy pro=new ProgressiveRate();
     WeekendDecisionStrategy decision= new ClockBasedDecision();
     RateStrategy alt=new AlternatingRate(pro,lin,decision);
     //Intialize PayStation with linear rate intially
     PayStation ps = new PayStationImpl(lin);
     Scanner sc=new Scanner(System.in);
     
     boolean bought=false;
     
     while(!bought)
     {
         int value=0;
       //Displays the Five Options For User to choose from  
       System.out.println("1) Deposit Coins\n2) Display\n3) Buy Ticket\n"
               + "4) Cancel\n5) Change Rate Strategy\n");
       //If-Else to make sure scanner picks up number and not string 
       if(sc.hasNextInt())
          value=sc.nextInt();
       else
        {
            sc.next();
            System.out.println("Wrong Input");
            //Skips rest of code and retrys
            continue;
        }
       
       switch (value) {
           //Deposit Coins Case
            case 1: 
                System.out.print("Enter Coin Value: ");
                int coin=sc.nextInt();
                //Trys to add payment as if coin payment not 25,10, or 5 will throw Exception
                try{
                    ps.addPayment(coin);
                  
                }catch ( IllegalCoinException  e)
                {System.out.println("Invalid Coin Type: " + coin);}
                break;
            
            //Display time bought so far   
            case 2: 
                System.out.println("The time bought is "+ps.readDisplay()+" minutes");
                break; 
                
            //Buys the transcation and stops payStation   
            case 3:
                Receipt ticket= ps.buy();
                System.out.printf("You have bought %d minutes\n",ticket.value());
                bought=true;
                break;
                
            /*
            Displays the CoinValue and the number of each coin type returned
            also shows the total amount returned    
            */    
            case 4:           
                System.out.println("\nCoinValue  NumberOfCoins");
                Map<Integer,Integer> returned=ps.cancel();
                int total=0;
                //Finds total amount returnted and displayes each key(coinValue) to number of each coin
                for(int key:returned.keySet())
                { int numberCoins=returned.get(key);
                    total += key*numberCoins;
                    System.out.println(key + "\t\t" + numberCoins);
                }
                System.out.printf("\nThe total value of the coins is %d cents\n", total);
                break;
                
            //Changes the paystation's rate strategy to either linear, progressive, or alternating    
            case 5: 
                System.out.println("1) Linear Rate \n2) Progressive Rate \n3) Alternating Rate\n ");
                int rate=sc.nextInt();
                switch(rate){
                    case 1: 
                        ps.changeRate(lin); 
                        System.out.println("PayStation now using Linear Rate Strategy");
                        break;
                    case 2: 
                        ps.changeRate(pro);
                        System.out.println("PayStation now using Progressive Rate Strategy");
                        break;
                    case 3: 
                        ps.changeRate(alt);
                        System.out.println("PayStation now using Alternating Rate Strategy");
                        break;
                    
                    default:System.out.println("Invalid Number: " + rate);
                        
                }
                    
                break;
            
            default:
                System.out.println("Invalid Number: " + value);
        }
         System.out.println("");
     }
}
    
}
