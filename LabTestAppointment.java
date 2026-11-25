public class LabTestAppointment extends BaseRecord {

    public static final double LAB_PROCESSING_FEE = 250.0;

    private String testType;

    public LabTestAppointment(String recordId, String name, double baseRate,
                              String status, String testType) {
        super(recordId, name, baseRate, status);
        this.testType = testType;
    }

    public LabTestAppointment(String recordId, String name, double baseRate,
                              String testType) {
        super(recordId, name, baseRate);
        this.testType = testType;
    }

    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    @Override
    public String getCategoryType() {
        return "Lab Test";
    }

    @Override
    public double calculateTotalCost() {
        return super.calculateTotalCost() + LAB_PROCESSING_FEE;
    }

    @Override
    public String getSummaryDetails() {
        return super.getSummaryDetails() + " | Test: " + testType;
    }
}
