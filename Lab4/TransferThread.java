package edu.temple.cis.c3238.banksim;


/**
 * @author Cay Horstmann
 * @author Modified by Paul Wolfgang
 * @author Modified by Charles Wang
 */
class TransferThread extends Thread {

    private final Bank bank;
    private final int fromAccount;
    private final int maxAmount;
    private boolean running;

    public TransferThread(Bank b, int from, int max) {
        bank = b;
        fromAccount = from;
        maxAmount = max;
        running=true;
    }

    @Override
    public void run() {
        int i=0;   
       while(i < 10000 && running) {
            try{    
                int toAccount = (int) (bank.size() * Math.random());
                int amount = (int) (maxAmount * Math.random());
                bank.transfer(fromAccount, toAccount, amount);
            } catch (InterruptedException ex) {
                running=false;
                System.out.println("Closed Thread");
                //System.exit(0);
            }
            i++;
        }
    }
    
    public void stopRun()
    {
        running=false;
    }
    
}
