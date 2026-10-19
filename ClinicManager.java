public class ClinicManager {
    public static final String MIN_ID = "";
    public static final String MAX_ID = "\uFFFF\uFFFF\uFFFF\uFFFF";
    private RatuvTree doctorsById;
    private RatuvTree patientsById;
    private RatuvTree doctorsByLoad;

    private static final Doctor MIN_DOC_SENTINEL = new Doctor(MIN_ID);
    private static final Doctor MAX_DOC_SENTINEL = new Doctor(MAX_ID);
    static {
        MIN_DOC_SENTINEL.patientCount = Integer.MIN_VALUE;
        MAX_DOC_SENTINEL.patientCount = Integer.MAX_VALUE;
    }

    public ClinicManager(){
        RatuvKeyHandler idHandler = new RatuvKeyHandler(){
            public int compare(Object o1, Object o2){
                String s1 = extractId(o1);
                String s2 = extractId(o2);
                return s1.compareTo(s2);
            }
            private String extractId(Object o){
                if (o instanceof String){
                    return (String)o;
                }
                if (o instanceof Doctor){
                    return ((Doctor)o).id;
                }
                if (o instanceof Patient){
                    return ((Patient)o).id;
                }
                return "";
            }
            public long getLoad(Object o){
                return 0;
            }
            public Object getMaxKey(){
                return MAX_ID;
            }
            public Object getMinKey(){
                return MIN_ID;
            }
        };
        RatuvKeyHandler loadHandler = new RatuvKeyHandler(){
            public int compare(Object o1, Object o2){
                Doctor d1 = (Doctor) o1;
                Doctor d2 = (Doctor) o2;
                int loadCompare = Integer.compare(d1.patientCount, d2.patientCount);
                if (loadCompare != 0){
                    return loadCompare;
                }
                return d1.id.compareTo(d2.id);
            }
            public long getLoad(Object o){
                return ((Doctor)o).patientCount;
            }
            public Object getMaxKey(){
                return MAX_DOC_SENTINEL;
            }
            public Object getMinKey(){
                return MIN_DOC_SENTINEL;
            }
        };
        doctorsById = new RatuvTree(idHandler);
        patientsById = new RatuvTree(idHandler);
        doctorsByLoad = new RatuvTree(loadHandler);
    }

    public void doctorEnter(String doctorId){
        if (doctorsById.search(doctorId) != null){
            throw new IllegalArgumentException();
        }
        Doctor d = new Doctor(doctorId);
        doctorsById.insert(d);
        doctorsByLoad.insert(d);
    }

    public void doctorLeave(String doctorId){
        Doctor d = (Doctor) doctorsById.search(doctorId);
        if (d == null || d.patientCount > 0){
            throw new IllegalArgumentException();
        }
        doctorsById.delete(d);
        doctorsByLoad.delete(d);
    }

    public void patientEnter(String doctorId, String patientId){
        if (patientsById.search(patientId) != null){
            throw new IllegalArgumentException();
        }
        Doctor d = (Doctor) doctorsById.search(doctorId);
        if (d == null){
            throw new IllegalArgumentException();
        }
        doctorsByLoad.delete(d);
        Patient p = new Patient(patientId, d);
        d.addPatient(p);
        doctorsByLoad.insert(d);
        patientsById.insert(p);
    }

    public String nextPatientLeave(String doctorId){
        Doctor d = (Doctor) doctorsById.search(doctorId);
        if (d == null || d.firstPatient == null){
            throw new IllegalArgumentException();
        }
        Patient p = d.firstPatient;
        doctorsByLoad.delete(d);
        d.removePatient(p);
        doctorsByLoad.insert(d);
        patientsById.delete(p);
        return p.id;
    }

    public void patientLeaveEarly(String patientId){
        Patient p = (Patient) patientsById.search(patientId);
        if (p == null){
            throw new IllegalArgumentException();
        }
        Doctor d = p.myDoctor;
        doctorsByLoad.delete(d);
        d.removePatient(p);
        doctorsByLoad.insert(d);
        patientsById.delete(p);
    }

    public int numPatients(String doctorId){
        Doctor d = (Doctor) doctorsById.search(doctorId);
        if (d == null){
            throw new IllegalArgumentException();
        }
        return d.patientCount;
    }

    public String nextPatient(String doctorId){
        Doctor d = (Doctor) doctorsById.search(doctorId);
        if (d == null || d.firstPatient == null){
            throw new IllegalArgumentException();
        }
        return d.firstPatient.id;
    }

    public String waitingForDoctor(String patientId){
        Patient p = (Patient) patientsById.search(patientId);
        if (p == null){
            throw new IllegalArgumentException();
        }
        return p.myDoctor.id;
    }
    public int numDoctorsWithLoadInRange(int low, int high){
        if (low > high){
            return 0;
        }
        Doctor maxD = new Doctor(MAX_ID);
        maxD.patientCount = high;
        Doctor minD = new Doctor(MIN_ID);
        minD.patientCount = low;
        return doctorsByLoad.getRank(maxD) - doctorsByLoad.getRank(minD);
    }

    public int averageLoadWithinRange(int low, int high){
        int count = numDoctorsWithLoadInRange(low, high);
        if (count == 0) return 0;
        Doctor maxD = new Doctor(MAX_ID); maxD.patientCount = high;
        Doctor minD = new Doctor(MIN_ID);
        minD.patientCount = low;
        long totalLoad = doctorsByLoad.getSumLoad(maxD) - doctorsByLoad.getSumLoad(minD);
        return (int)(totalLoad / count);
    }
}