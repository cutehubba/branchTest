package App;

import Set.DutyIntervalSet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DutyRosterApp extends DutyIntervalSet {
    private final Set<DutyInterval> intervals = new CopyOnWriteArraySet<>();
    private long StartTime;//排班开始时间   格式为"yyyy-MM-dd"
    private long EndTime;//排班结束时间   格式为"yyyy-MM-dd"
    /**
     * 员工集合中的员工没有被编入排班表时可以随意删除，
     * 被编入排班表之后必须先删除排班表的内容再来删除员工集合中的员工40
     * */
    private final Set<Employee> employees = new HashSet<>();//员工的集合  (标签的集合)

    public DutyRosterApp() {}
    /**
     * 功能一:
     * 设定排班开始日期和结束日期，具体到年月日
     * @return
     * */
    public void setDate(Date StartTime, Date EndTime){
        this.EndTime = EndTime.getTime();
        this.StartTime = StartTime.getTime();
    }

    /**
     * 根据员工名字找到员工
     * @return 找到了返回员工类，未找到返回null
     * */
    public Employee findEmployee(String Name) {

        for(Employee temp:employees)
        {
            if(temp.getName().equals(Name)) {
                return temp;
            }
        }
        return null;

    }
    /**
     * 功能二：
     * 增加一名员工，放入员工集合中，如果员工已经存在，则返回false，否则返回true
     * */
    public boolean addEmployee(String Name,String Duty,String PhoneNumber) {
        Employee one = new Employee(Name,Duty,PhoneNumber);

        for(Employee temp:this.employees)
        {
            if(temp.getName().equals(Name))
            {System.out.println("员工已存在");
                return false;}
        }
        employees.add(one);
        return true;

    }
    /**
     * 功能三：
     * 删除一名员工，如果员工在排班表中，则不可删除，返回false，如果员工不在排班表中，则删除后返回true
     * */
    public boolean deleteEmployee(String Name) {

        for(DutyInterval temp:this.intervals)
        {
            if(temp.getLabel().getName().equals(Name)) {
                System.out.println("员工已被编排在排班表中");
                return false;
            }
        }
        Iterator<Employee> it = this.employees.iterator();
        while(it.hasNext()) {
            Employee temp = it.next();
            if(temp.getName().equals(Name)) {
                it.remove();
                return true;
            }
        }
        return false;
    }



    /**
     * 排班系统在添加时需要检查是否标签已经出现过以及是否时间段发生重叠
     * 还需要检查  排班表是否已经满了  满了不能再排班  未满的话可以排班
     *
     * 功能四：手动选择一名员工，放入到某个时间段中，生成一条排班表信息。
     *
     * */

    public void insert(String start, String end, Employee label) throws ParseException {
        /*
         * 检查是否已经被排班
         * */
        for(Employee temp:labels()) {
            if(temp.getName().equals(label.getName())){
                System.out.println("该员工已经值过班\n");
                return ;
            }
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = sdf.parse(start);//想插入的排班信息的开始时间
        Date date2 = sdf.parse(end);//想插入的排班信息的 结束时间
        /*
         * 检查是否有重叠
         * */
        for(DutyInterval temp:intervals)
        {

            Date date3 = sdf.parse(temp.getStart());//排班表中某个时间段的开始时间
            Date date4 = sdf.parse(temp.getEnd());//排班表中某个时间段的结束时间
            if(date1.getTime()>=date4.getTime()||date2.getTime()<=date3.getTime()) {
                continue;
            }
            else {
                System.out.println("排班表时间有重叠\n");
                return ;
            }
        }
        /**
         * 检查排班表是否已经满
         * */
        if(isfull()) {
            System.out.println("排班表已经满了\n");
            return ;
        }



        DutyInterval one = new DutyInterval(label,start,end);
        intervals.add(one);

        return ;
    }
    /**
     * 功能五：根据员工人数和起止日期随机生成排班表
     * */
    public void randomMake() throws ParseException {

        /*
         * 计算从开始日期到结束日期之间的天数days，
         * 当days不是0时，循环
         * 从员工集合中取出一个员工，
         * 生成一个小于days大于等于0的随机数random，表示这个员工需要值班的天数
         * days = days - random
         * date = date + random
         * */
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = new Date(this.StartTime);//排班的开始日期
        Date date2 = new Date(this.EndTime);//排班的结束日期
        int days = (int) ((date2.getTime()-date1.getTime())/(1000*60*60*24) + 1);
        Set<Employee> TempEmployees = new HashSet<>();
        TempEmployees.addAll(this.employees);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date1);
        while(days!=0) {
            Employee  temp = TempEmployees.iterator().next();//temp为员工集合中第一个元素
            TempEmployees.remove(temp);
            //创建Random类对象
            Random random = new Random();

            //产生随机数
            int number = random.nextInt(days)+1;//nexInt生成一个1到days范围内的随机数
            String start = sdf.format(cal.getTime());
            cal.add(Calendar.DAY_OF_MONTH, number-1);
            String end =  sdf.format(cal.getTime());
            insert(start,end,temp);//生成一条排班信息
            days = days - number;
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

    }

    /**
     * 功能七：可视化展示排班表
     * */
    public void display() throws ParseException {
        /*
         * 遍历从开始日期起的每一天
         * */
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        Date date5 = new Date(this.StartTime);//排班开始日期
        Date date6 = new Date(this.EndTime);//排班结束日期
        cal.setTime(date5);
        while(cal.getTime().getTime()<=date6.getTime()) {
            boolean flag = false;
            for(DutyInterval temp : intervals) {
                Date date3 = sdf.parse(temp.getStart());//排班表中某个时间段的开始时间
                Date date4 = sdf.parse(temp.getEnd());//排班表中某个时间段的结束时间
                if(cal.getTime().getTime()>=date3.getTime()&&cal.getTime().getTime()<=date4.getTime()) {//这一天在当前时间段中
                    String day = sdf.format(cal.getTime());
                    System.out.println(day+"负责的员工为"+temp.getLabel().getName()+"\n");
                    flag = true;
                }
            }
            //遍历所有时间段也没有包含这一天，则说明这一天不在排班表中，将它打印输出
            if(!flag) {
                String day = sdf.format(cal.getTime());
                System.out.println(day+"不在排班表中\n");
            }

            cal.add(Calendar.DAY_OF_MONTH, 1);

        }

    }

    /**
     * 检查排班表是否已经满，将未安排的日期全部输出
     * @return true代表排班表已经满了  false代表未满
     * */
    public boolean isfull() throws ParseException {
        /*
         * 遍历从开始日期起的每一天
         * */
        boolean back = true;//false代表排班表未满，true代表已经满
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        Date date5 = new Date(this.StartTime);//排班开始日期
        Date date6 = new Date(this.EndTime);//排班结束日期
        cal.setTime(date5);
        while(cal.getTime().getTime()<=date6.getTime()) {
            boolean flag = false;
            for(DutyInterval temp : intervals) {
                Date date3 = sdf.parse(temp.getStart());//排班表中某个时间段的开始时间
                Date date4 = sdf.parse(temp.getEnd());//排班表中某个时间段的结束时间
                if(cal.getTime().getTime()>=date3.getTime()&&cal.getTime().getTime()<=date4.getTime()) {//这一天在当前时间段中
                    flag = true;
                }
            }
            //遍历所有时间段也没有包含这一天，则说明这一天不在排班表中，将它打印输出
            if(!flag) {
                back = false;
            }

            cal.add(Calendar.DAY_OF_MONTH, 1);

        }
        return back;
    }

    /**
     * 获得当前所有时间段的标签集合
     * */
    public Set<Employee> labels() {
        Set<Employee> labels = new HashSet<>();

        for(DutyInterval temp :this.intervals)
        {
            labels.add(temp.getLabel());
        }

        return labels;
    }
    /**
     * 打印功能菜单
     * */
    public static void menu() {
        System.out.println("欢迎进入排班表管理系统\n");
        System.out.println("系统为您提供了以下几项功能\n");
        System.out.println("功能0：退出排班表系统\n");
        System.out.println("功能1：设定排班开始日期和结束日期\n");
        System.out.println("功能2：增加一名员工信息\n");
        System.out.println("功能3：删除一名员工信息\n");
        System.out.println("功能4：手动选择一名员工，生成一条排班表信息\n");
        System.out.println("功能5：根据员工人数和起止日期随机生成排班表\n");
        System.out.println("功能6：根据本地文本文件信息生成排班表\n");
        System.out.println("功能7：展示排班表\n");

    }
    public static void main(String args[]) throws ParseException, FileNotFoundException {
        DutyRosterApp app = new DutyRosterApp();
        SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");
        menu();
        while(true) {
            System.out.println("请输入您的操作数字\n");
            @SuppressWarnings("resource")
            Scanner in = new Scanner(System.in);
            String input = in.nextLine();
            switch(input){

                case "0":System.exit(0);

                case "1":
                    System.out.println("请输入排班开始日期,格式为2024-01-01\n");
                    String s1 = in.nextLine();
                    System.out.println("请输入排班结束日期,格式为2024-01-01\n");
                    String e1 = in.nextLine();
                    app.setDate(SDF.parse(s1), SDF.parse(e1));
                    System.out.println("设置成功");
                    break;

                case "2":
                    System.out.println("请输入员工姓名\n");
                    String n1 = in.nextLine();
                    System.out.println("请输入员工职位\n");
                    String d1 = in.nextLine();
                    System.out.println("请输入员工手机号码\n");
                    String p1 = in.nextLine();
                    app.addEmployee(n1, d1, p1);
                    System.out.println("添加成功\n");
                    break;
                case "3":
                    System.out.println("请输入员工姓名\n");
                    String a1 = in.nextLine();
                    if(app.deleteEmployee(a1)) {
                        System.out.println("删除成功\n");
                    }
                    else System.out.println("删除失败\n");
                    break;

                case "4":
                    System.out.println("请输入员工姓名\n");
                    String b1 = in.nextLine();
                    Employee c1 = app.findEmployee(b1);
                    System.out.println("请输入排班开始时间,格式2024-01-01\n");
                    String s2 = in.nextLine();
                    System.out.println("请输入排班结束时间,格式2024-01-01\n");
                    String e2 = in.nextLine();
                    app.insert(s2, e2, c1);
                    System.out.println("排班信息生成成功\n");
                    break;
                case "5":
                    app.randomMake();
                    System.out.println("排班信息生成成功\n");
                    break;
                case "6":
                    Scanner sc = null;
                    sc = new Scanner(new FileInputStream(new File("src/text/test1.txt")));
                    String line;
                    String pattern;
                    while(sc.hasNext())
                    {
                        line = sc.nextLine();
                        if(line.contains("Employee")) {
                            String name;
                            String jobTitle;
                            String phone;
                            pattern = "　([a-zA-Z]+)\\{((?:[a-zA-Z]+| )+)\\,((?:[0-9]|\\-)+)\\}";
                            Pattern r = Pattern.compile(pattern);
                            while(sc.hasNext())
                            {
                                line = sc.nextLine();
                                if(line.equals("}")) break;
                                Matcher m = r.matcher(line);
                                if(m.find())
                                {
                                    name = m.group(1);
                                    jobTitle = m.group(2);
                                    phone = m.group(3);
                                    app.addEmployee(name,jobTitle,phone);
                                }
                            }

                        }
                        else if(line.contains("Period"))
                        {
                            pattern = "Period\\{((?:[0-9]|\\-)+)\\,((?:[0-9]|\\-)+)\\}";
                            Pattern r = Pattern.compile(pattern);
                            Matcher m = r.matcher(line);
                            if(m.find())
                            {
                                Date S = null;
                                try {
                                    S = SDF.parse(m.group(1));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                Date E = null;
                                try {
                                    E = SDF.parse(m.group(2));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                app.setDate(S,E);
                            }

                        }
                        else if(line.contains("Roster"))
                        {
                            String name;
                            Date S = null;
                            Date E = null;
                            pattern = "　([a-zA-Z]+)\\{((?:[0-9]|\\-)+)\\,((?:[0-9]|\\-)+)\\}";
                            Pattern r = Pattern.compile(pattern);
                            while(sc.hasNext())
                            {
                                line = sc.nextLine();
                                if(line.equals("}")) break;
                                Matcher m = r.matcher(line);
                                if(m.find())
                                {
                                    name = m.group(1);
                                    try {
                                        S = (Date) SDF.parse(m.group(2));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        E = (Date) SDF.parse(m.group(3));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    String start = SDF.format(S);
                                    String end = SDF.format(E);
                                    app.insert(start, end,app.findEmployee(name));
                                }
                            }
                        }
                    }
                    System.out.println("排班信息生成成功\n");
                    break;
                case "7":
                    app.display();
                    break;
            }
        }

    }
    /**
     * Interval在不同类中有不同形式
     *
     * */
    class DutyInterval{
        private final Employee Label ;
        private final String StartTime ;
        private final String EndTime;


        public DutyInterval(Employee Label ,String StartTime, String EndTime){
            this.Label = Label ;
            this.EndTime = EndTime;
            this.StartTime = StartTime;
        }
        public Employee getLabel() {
            return this.Label;
        }
        public String getStart() {return this.StartTime;}
        public String getEnd() {
            return this.EndTime;
        }
    }

}
