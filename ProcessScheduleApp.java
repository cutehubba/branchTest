package App;

import Set.ProcessIntervalSet;

import java.text.ParseException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

public class ProcessScheduleApp extends ProcessIntervalSet {
    private final Set<ProcessInterval> intervals = new HashSet<>();

    private final Set<Process> processes = new CopyOnWriteArraySet<>();//尚未结束运行的进程的集合  (标签的集合)


    /**
     * 功能一：增加一个进程，包括进程的各种信息
     * */
    public boolean addProcess(int ID,String Name,long MinRunTime,long MaxRunTime,long UsedRunTime) {
        Process one = new Process(ID,Name,MinRunTime,MaxRunTime,0);
        for(Process temp : processes) {

            if(temp.getID()==ID) {
                System.out.println("进程已存在");
                return false;
            }

        }
        processes.add(one);
        return true;

    }

    /**
     * 功能二：
     * 当前时刻（设定为 0）启动模拟调度，
     * 随机选择某个尚未执行结束的进程在 CPU 上执行（执行过程中其他进程不能被执行），
     * 并在该进程最大时间之前的任意时刻停止执行，
     * 如果本次及其之前的累积执行时间已落到[最短执行时间，最长执行时间]的区间内，则该进程被设定为“执行结束”。
     * 重复上述过程，直到所有进程都达到“执行结束”状态。
     * 在每次选择时，也可“不执行任何进程”，并在后续随机选定的时间点再次进行进程选择。
     * */
    public void simulateRunRandom() {
        long NowTime = 0;

        while(!processes.isEmpty()) {
            Iterator<Process> it = this.processes.iterator();
            while(it.hasNext()) {
                Process temp = (Process) it.next();
                int RunTime = (int)(temp.getMaxRunTime() - temp.getUsedRunTime());//本次剩余最大可执行时间
                //创建Random类对象
                Random random = new Random();
                //产生随机数
                int number = random.nextInt(RunTime);//nexInt生成一个0到RunTime-1的随机数
                //number 为本次执行时间
                if(number!=0) {
                    temp.setUsedRunTime(number);
                    Iterator<Process> it1  = this.processes.iterator();
                    while(it1.hasNext()) {
                        Process temp0 = (Process) it1.next();
                        if(temp0.getUsedRunTime()<=temp0.getMaxRunTime()&&temp0.getUsedRunTime()>=temp0.getMinRunTime()) {
                            processes.remove(temp0);
                        }

                    }
                    ProcessInterval one = new ProcessInterval(temp,NowTime,NowTime+number);
                    this.intervals.add(one);
                    NowTime+=number;
                    NowTime+=random.nextInt(1000);
                }
            }

        }
    }


    /**
     * 当进程执行结束时，将进程从进程集合中删除掉
     * */
    public void  removeProcess() {
        //遍历进程集合，将所有已经运行完毕的进程删除
        Iterator<Process> it  = this.processes.iterator();
        while(it.hasNext()) {
            Process temp = (Process) it.next();
            if(temp.getUsedRunTime()<=temp.getMaxRunTime()&&temp.getUsedRunTime()>=temp.getMinRunTime()) {
                processes.remove(temp);
            }

        }


    }

    /**
     * 功能三：
     * 实现“最短进程优先”的模拟策略：每次选择进程的时候，优先选择距离其最大执行时间差距最小的进程。
     * */
    public void simulateRunMin() {
        long NowTime = 0;

        while(!processes.isEmpty()) {
            //遍历进程集合找出距离其最大执行时间差距最小的进程
            Process minTemp = processes.iterator().next();//minTemp为集合中第一个元素
            long minTime = minTemp.getMaxRunTime() - minTemp.getUsedRunTime();
            for(Process temp:this.processes)
            {
                long tempTime = temp.getMaxRunTime()-temp.getUsedRunTime();
                if(minTime<tempTime) {
                    minTime = tempTime;
                    minTemp = temp;
                }

            }
            /**
             * 循环结束，此时minTemp为距离其最大执行时间差距最小的进程，
             * 此时为它分配运行时间，创建一条进程运行时间段信息
             * */
            //创建Random类对象
            Random random = new Random();
            //产生随机数
            int number = random.nextInt((int)minTime);//nexInt生成一个0到minTime-1的随机数
            //number 为本次执行时间
            if(number!=0) {
                minTemp.setUsedRunTime(number);//这个时候修改minTemp中数据时，会同时修改集合中数据
                removeProcess();
                ProcessInterval one = new ProcessInterval(minTemp,NowTime,NowTime+number);
                this.intervals.add(one);
                NowTime+=number;
            }
        }

    }
    /**
     * 功能四：
     * 可视化展示当前时刻之前的进程调度结果，以及当前时刻正在执行的进程。
     * */
    public void display() {

        //遍历集合找出开始时间最小的时间段
        Set<ProcessInterval> TempIntervals = new HashSet<>();
        TempIntervals.addAll(this.intervals);

        while(!TempIntervals.isEmpty()) {
            ProcessInterval minInterval = TempIntervals.iterator().next();
            for(ProcessInterval temp :TempIntervals )
            {
                if(temp.getStart()<minInterval.getStart()) {
                    minInterval = temp;
                }
            }
            System.out.println(minInterval.getStart()+"至"+minInterval.getEnd()+"秒执行了"+minInterval.getLabel().getID()+"进程");
            TempIntervals.remove(minInterval);
        }
    }


    /**
     * 打印功能菜单
     * */
    public static void menu() {
        System.out.println("欢迎进入进程管理系统\n");
        System.out.println("系统为您提供了以下几项功能\n");
        System.out.println("功能0：退出进程管理系统\n");
        System.out.println("功能1：增加一个进程\n");
        System.out.println("功能2：执行进程随机模拟调度\n");
        System.out.println("功能3：“最短进程优先”模拟调度\n");
        System.out.println("功能4：展示进程调度结果\n");

    }

    public static void main(String args[]) throws ParseException {
        ProcessScheduleApp app = new ProcessScheduleApp();
        menu();
        while(true) {
            System.out.println("请输入您的操作数字\n");
            @SuppressWarnings("resource")
            Scanner in = new Scanner(System.in);
            String input = in.nextLine();
            switch(input){
                case "0":System.exit(0);
                case "1":
                    System.out.println("请输入进程ID\n");
                    String str1 = in.nextLine();
                    int ID = Integer.valueOf(str1).intValue();
                    System.out.println("请输入进程名\n");
                    String str2 = in.nextLine();
                    System.out.println("请输入进程最短执行时间\n");
                    String str3 = in.nextLine();
                    long MinRunTime = Integer.valueOf(str3).intValue();
                    System.out.println("请输入进程最大执行时间\n");
                    String str4 = in.nextLine();
                    long MaxRunTime = Integer.valueOf(str4).intValue();
                    app.addProcess(ID,str2,MinRunTime,MaxRunTime,0);
                    System.out.println("添加成功\n");
                    break;
                case "2":
                    app.simulateRunRandom();
                    System.out.println("模拟调度已完成\n");
                    break;
                case "3":
                    app.simulateRunMin();
                    System.out.println("模拟调度已完成\n");
                    break;
                case "4":
                    app.display();
                    break;
            }

        }

    }

    class ProcessInterval{
        private final Process Label;
        private final long StartTime;
        private final long EndTime;

        public ProcessInterval(Process Label ,long StartTime, long EndTime){
            this.Label = Label ;
            this.EndTime = EndTime;
            this.StartTime = StartTime;

        }

        public Process getLabel() {
            return this.Label;
        }
        public long getStart() {
            return this.StartTime;

        }
        public long getEnd() {
            return this.EndTime;
        }
    }
}
