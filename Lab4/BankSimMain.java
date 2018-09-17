package edu.temple.cis.c3238.banksim;

import java.util.Random;

/**
 * @author Cay Horstmann
 * @author Modified by Paul Wolfgang
 * @author Modified by Charles Wang
 */
public class BankSimMain {

    public static final int NACCOUNTS = 10;
    public static final int INITIAL_BALANCE = 10000;

    public static void main(String[] args) {
        Bank b = new Bank(NACCOUNTS, INITIAL_BALANCE);
        Thread[] threads = new Thread[NACCOUNTS];
        Random rn = new Random();
        

        for (int i = 0; i < NACCOUNTS; i++) {
            threads[i] = new TransferThread(b, i, INITIAL_BALANCE); 
            threads[i].start();
            threads[i].setPriority(rn.nextInt(10)+1);
        }
        
        boolean open=true;
        while(open)
        {
         //Checks to see if a thread died or not and if one did close bank 
         for (int i = 0; i < NACCOUNTS; i++) 
         {
            if(!threads[i].isAlive())
            { open=false; }  
         } 
         //If A Thread died interrupt all other threads and wait for them to stop
         if(!open)
         {
          for (int i = 0; i < NACCOUNTS; i++) {
            try{
                threads[i].interrupt();
                threads[i].join();
            }catch(InterruptedException exception)
            {}
          }   
              
           b.closeBank();
         }
        }
        
         
    }
}


