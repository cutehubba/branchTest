package App;

public class Course{
    /*
     * 课程 ID、课程名称、教师名字、地点、周学时数、已经安排的学时数（默认为0）
     *
     * */
    private final int CourseID;
    private final int Maxhours;
    private final String CourseName;
    private final String TeacherName;
    private final String Place;
    private  int Usedhours;

    Course(int CourseID,int Maxhours,String CourseName,String TeacherName,String  Place,int Usedhours){
        this.CourseID = CourseID ;
        this.CourseName = CourseName;
        this.TeacherName = TeacherName;
        this.Place = Place;
        this.Maxhours = Maxhours;
        this.Usedhours = Usedhours;
    }
    public void setUsedhours(int hour) {

        this.Usedhours = this.Usedhours + hour;

    }
    public int getUsedhours() {

        return this.Usedhours;

    }
    public int getMaxhours() {

        return this.Maxhours;

    }
    public String getCourseName() {

        return this.CourseName;
    }
    public String getTeacherName() {

        return this.TeacherName;
    }
    public String getPlace() {

        return this.Place;
    }
    public int getCourseID() {
        return this.CourseID;
    }
}
