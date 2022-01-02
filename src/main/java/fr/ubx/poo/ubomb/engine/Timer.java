package fr.ubx.poo.ubomb.engine;

public class Timer {
    public long timer;
    public Timer(long now){
        this.timer=now;
    }
    public void setTime(long now){
        this.timer= now;
    }
    public long Time(long now){
        return now-timer;
    }
}
