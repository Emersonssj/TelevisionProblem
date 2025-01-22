package adapters;

public class SO extends Thread{
    public int timeDuration;

    public SO(int timeDuration){
        this.timeDuration = timeDuration;
    }

    public void verifyDeadlock(){

    }

    @Override
    public void run(){
        try {
            while (true) {
                Thread.sleep(1000);
                verifyDeadlock();
            }
        } catch (InterruptedException e) {}
    }

}
