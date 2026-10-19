public class Patient{
    public String id;
    public Doctor myDoctor;
    public Patient next;
    public Patient prev;

    public Patient(String id, Doctor d){
        this.id = id;
        this.myDoctor = d;
    }
}