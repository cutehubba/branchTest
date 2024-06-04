package App;

public class Employee {
    private final String Name;
    private final String Duty;
    private final String PhoneNumber;

    public Employee(String Name, String Duty, String PhoneNumber){
        this.Duty = Duty ;
        this.Name = Name ;
        this.PhoneNumber = PhoneNumber;

    }
    public String getName() {

        return this.Name;
    }
    public String getDuty() {

        return this.Duty;
    }
    public String getPhoneNumber() {
        return this.PhoneNumber;

    }
}
