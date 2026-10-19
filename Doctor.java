public class Doctor{
    public String id;
    public int patientCount;
    public Patient firstPatient;
    public Patient lastPatient;

    public Doctor(String id){
        this.id = id;
        this.patientCount = 0;
    }
    public void addPatient(Patient p){
        if (lastPatient == null){
            firstPatient = p;
            lastPatient = p;
        }
        else{
            lastPatient.next = p;
            p.prev = lastPatient;
            lastPatient = p;
        }
        patientCount++;
    }
    public void removePatient(Patient p){
        if (p.prev != null){
            p.prev.next = p.next;
        }
        else{
            firstPatient = p.next;
        }
        if (p.next != null){
            p.next.prev = p.prev;
        }
        else{
            lastPatient = p.prev;
        }
        p.next = null;
        p.prev = null;
        patientCount--;
    }
}
