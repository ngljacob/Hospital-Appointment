public abstract class BaseRecord implements Billable {
    private String recordId;
    private String name;
    private double baseRate;
    private String status;

    public BaseRecord(String recordId, String name, double baseRate, String status) {
        this.recordId = recordId;
        this.name = name;
        this.baseRate = baseRate;
        this.status = status;
    }

    public BaseRecord(String recordId, String name, double baseRate) {
        this(recordId, name, baseRate, "Active");
    }

    public abstract String getCategoryType();

    @Override
    public double calculateTotalCost() {
        return this.baseRate;
    }

    @Override
    public String getSummaryDetails() {
        return String.format("[%s] %s (%s) - ₱%.2f", recordId, name, getCategoryType(), calculateTotalCost());
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBaseRate() {
        return baseRate;
    }

    public void setBaseRate(double baseRate) {
        this.baseRate = baseRate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
