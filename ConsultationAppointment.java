public class ConsultationAppointment extends BaseRecord {

    public static final double FACILITY_FEE = 100.0;

    private String doctorName;

    public ConsultationAppointment(String recordId, String name, double baseRate,
                                   String status, String doctorName) {
        super(recordId, name, baseRate, status);
        this.doctorName = doctorName;
    }

    public ConsultationAppointment(String recordId, String name, double baseRate,
                                   String doctorName) {
        super(recordId, name, baseRate);
        this.doctorName = doctorName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    @Override
    public String getCategoryType() {
        return "Consultation";
    }

    @Override
    public double calculateTotalCost() {
        return super.calculateTotalCost() + FACILITY_FEE;
    }

    @Override
    public String getSummaryDetails() {
        return super.getSummaryDetails() + " | Doctor: Dr. " + doctorName;
    }
}
