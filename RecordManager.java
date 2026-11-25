import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RecordManager {

    private final ArrayList<BaseRecord> records = new ArrayList<>();
    private final HashMap<String, BaseRecord> index = new HashMap<>();
    private int validationChecks = 0;

    public void addRecord(BaseRecord record) throws InvalidRecordException {
        if (record == null) {
            throw new InvalidRecordException("Cannot add an empty appointment.");
        }
        if (index.containsKey(record.getRecordId())) {
            throw new InvalidRecordException(
                    "Appointment ID '" + record.getRecordId() + "' already exists.");
        }
        records.add(record);
        index.put(record.getRecordId(), record);
    }

    public BaseRecord getRecordById(String id) throws InvalidRecordException {
        BaseRecord found = index.get(id);
        if (found == null) {
            throw new InvalidRecordException("No appointment found with ID '" + id + "'.");
        }
        return found;
    }

    public void updateRecord(BaseRecord updated) throws InvalidRecordException {
        if (updated == null) {
            throw new InvalidRecordException("Cannot update with an empty appointment.");
        }
        BaseRecord old = getRecordById(updated.getRecordId());
        records.set(records.indexOf(old), updated);
        index.put(updated.getRecordId(), updated);
    }

    public void deleteRecord(String id) throws InvalidRecordException {
        BaseRecord old = getRecordById(id);
        records.remove(old);
        index.remove(id);
    }

    public List<BaseRecord> getAllRecords() {
        return Collections.unmodifiableList(new ArrayList<>(records));
    }

    public BaseRecord buildRecord(String type, String id, String name, String rateText,
                                  String status, String extra) throws InvalidRecordException {
        try {
            if (isBlank(id)) {
                throw new InvalidRecordException("Appointment ID is required.");
            }
            if (isBlank(name)) {
                throw new InvalidRecordException("Patient name is required.");
            }
            double rate = parseRate(rateText);
            if (isBlank(extra)) {
                throw new InvalidRecordException(
                        "Consultation".equals(type) ? "Doctor name is required."
                                                    : "Test type is required.");
            }
            id = id.trim();
            name = name.trim();
            extra = extra.trim();

            if ("Consultation".equals(type)) {
                return new ConsultationAppointment(id, name, rate, status, extra);
            } else if ("Lab Test".equals(type)) {
                return new LabTestAppointment(id, name, rate, status, extra);
            }
            throw new InvalidRecordException("Please choose an appointment type.");
        } finally {
            validationChecks++;
        }
    }

    public double parseRate(String text) throws InvalidRecordException {
        double rate;
        try {
            if (isBlank(text)) {
                throw new InvalidRecordException("Fee is required.");
            }
            rate = Double.parseDouble(text.trim());
        } catch (NumberFormatException e) {
            throw new InvalidRecordException("Fee must be a number (e.g. 500 or 500.50).");
        } finally {
            validationChecks++;
        }
        if (rate < 0) {
            throw new InvalidRecordException("Fee cannot be negative.");
        }
        return rate;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public int getValidationChecks() {
        return validationChecks;
    }

    public int getTotalRecords() {
        return records.size();
    }

    public int getActiveCount() {
        int count = 0;
        for (BaseRecord r : records) {
            if ("Active".equalsIgnoreCase(r.getStatus())) {
                count++;
            }
        }
        return count;
    }

    public double getTotalRevenue() {
        double total = 0;
        for (BaseRecord r : records) {
            if (!"Cancelled".equalsIgnoreCase(r.getStatus())) {
                total += r.calculateTotalCost();
            }
        }
        return total;
    }

    public Map<String, Integer> getCountByCategory() {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (BaseRecord r : records) {
            counts.merge(r.getCategoryType(), 1, Integer::sum);
        }
        return counts;
    }
}
