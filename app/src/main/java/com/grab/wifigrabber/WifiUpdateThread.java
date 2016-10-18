package com.grab.wifigrabber;

/**
 * Vlákno které v daném intervalu obnovuje seznam wifi sítí.
 */
public class WifiUpdateThread implements Runnable
{
    MainActivity activity;
    boolean running;
    int interval;
    /**
     * Konstruktor
     * @param activity
     * @param interval interval po kterém se sítě obnovují, zadávaný v milisekundách
     */
    public WifiUpdateThread(MainActivity activity,int interval)
    {
        this.activity = activity;
        this.interval = interval;
        running = true;
    }

    @Override
    public void run()
    {
        while(running)
        {
            activity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    activity.refreshWifiInfo();
                }
            });

            try
            {
                Thread.sleep(interval);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void stop()
    {
        running = false;
    }
}
