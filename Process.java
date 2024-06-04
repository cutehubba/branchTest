package App;

public class Process{
    /*
     * 进程 ID、进程名称、最短执行时间、最长执行时间
     * */
    private final int ID;
    private final String Name ;
    private final long MinRunTime;
    private final long MaxRunTime;
    private long UsedRunTime;//进程已经运行的时间

    Process(int ID,String Name,long MinRunTime,long MaxRunTime,long UsedRunTime){
        this.ID = ID ;
        this.Name = Name ;
        this.MinRunTime = MinRunTime;
        this.MaxRunTime = MaxRunTime;
        this.UsedRunTime = UsedRunTime;
    }


    public int getID() {
        return ID;
    }


    public String getName() {
        return Name;
    }


    public long getMinRunTime() {
        return MinRunTime;
    }


    public long getMaxRunTime() {
        return MaxRunTime;
    }

    public long getUsedRunTime() {

        return UsedRunTime;

    }

    public void setUsedRunTime(long time) {

        this.UsedRunTime = this.UsedRunTime + time;

    }
}

