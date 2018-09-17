package edu.temple.cis.c3238.banksim;

/**
 * @author Cay Horstmann
 * @author Modified by Paul Wolfgang
 * @author Modified by Charles Wang
 */

public class Bank {

    public static final int NTEST = 10;
    private final Account[] accounts;
    private long ntransacts = 0;
    private final int initialBalance;
    private final int numAccounts;
    private boolean open;
    private boolean flagger;// a flag boolean to indicate if sum thread active or not 
    private int counter; //counter of how many transfer threads active
    
    

    public Bank(int numAccounts, int initialBalance) {
        open=true; //Opens banks
        flagger=false;
        this.initialBalance = initialBalance;
        this.numAccounts = numAccounts;
        accounts = new Account[numAccounts];
        for (int i = 0; i < accounts.length; i++) {
            accounts[i] = new Account(this, i, initialBalance);
        }
        ntransacts = 0;
        counter=0;
    }
    public synchronized void sumWait() throws InterruptedException {
        while(flagger){
            wait();
        }
        counter++;
    }
    public synchronized void counterdec() {
        counter--;
        notifyAll();
    }

    public void transfer(int from, int to, int amount) throws InterruptedException {
//        accounts[from].waitForAvailableFunds(amount);       
       sumWait();
        if(!open)
        {
            counterdec();
            return;
        }
        
        if (accounts[from].withdraw(amount)) {
            accounts[to].deposit(amount);
            System.out.printf("%s %s%n", 
                    Thread.currentThread().toString(), accounts[to].toString());
        }

        counterdec();
        ntransacts++;
        if(shouldTest()&& !flagger)
        {
            flagger=true;
            new Sum(this).start();
        }
        
    }

    public synchronized void test() throws InterruptedException {
        //If there are transfer threads still running wait until no active 
        while(counter>0)
        { wait();}
        
        if(!open)
        {
            flagger=false;
            return;
        }
            int sum = 0;
            for (Account account : accounts) {
                sum += account.getBalance();
            }
            System.out.println(Thread.currentThread().toString() + 
                    " Sum: " + sum);
            if (sum != numAccounts * initialBalance) {
                System.out.println(Thread.currentThread().toString() + 
                        " Money was gained or lost");
                System.exit(1);
            } else {
                System.out.println(Thread.currentThread().toString() + 
                        " The bank is in balance");
            } 
            flagger=false;
            notifyAll(); //wakes any threads 
   
    }

    public int size() {
        return accounts.length;
    }
    public boolean isOpen() {
        return open;
    }

    public void closeBank() {
        synchronized (this) {
            open = false;
            System.out.println("Bank is closed");
        }
    }
    
    public boolean shouldTest() {
        return ntransacts % NTEST == 0;
    }

}
