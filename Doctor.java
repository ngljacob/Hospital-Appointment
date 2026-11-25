import java.awt.Color;

public class Doctor {

    private final String name;
    private final String clinic;
    private final String specialty;
    private final String hours;
    private final String status;
    private final String initials;
    private final String about;
    private final double fee;
    private final int patients;
    private final int surgeries;
    private final int experienceYears;
    private final double rating;
    private final Color accent;
    private final String photoPath;
    private final double photoOffsetY;

    public Doctor(String name, String clinic, String specialty, String hours, String status,
                  String initials, String about, double fee, int patients, int surgeries,
                  int experienceYears, double rating, Color accent) {
        this(name, clinic, specialty, hours, status, initials, about, fee, patients, surgeries,
                experienceYears, rating, accent, null, 0);
    }

    public Doctor(String name, String clinic, String specialty, String hours, String status,
                  String initials, String about, double fee, int patients, int surgeries,
                  int experienceYears, double rating, Color accent, String photoPath) {
        this(name, clinic, specialty, hours, status, initials, about, fee, patients, surgeries,
                experienceYears, rating, accent, photoPath, 0);
    }

    public Doctor(String name, String clinic, String specialty, String hours, String status,
                  String initials, String about, double fee, int patients, int surgeries,
                  int experienceYears, double rating, Color accent, String photoPath,
                  double photoOffsetY) {
        this.name = name;
        this.clinic = clinic;
        this.specialty = specialty;
        this.hours = hours;
        this.status = status;
        this.initials = initials;
        this.about = about;
        this.fee = fee;
        this.patients = patients;
        this.surgeries = surgeries;
        this.experienceYears = experienceYears;
        this.rating = rating;
        this.accent = accent;
        this.photoPath = photoPath;
        this.photoOffsetY = photoOffsetY;
    }

    public String getName() {
        return name;
    }

    public String getClinic() {
        return clinic;
    }

    public String getSpecialty() {
        return specialty;
    }

    public String getHours() {
        return hours;
    }

    public String getStatus() {
        return status;
    }

    public String getInitials() {
        return initials;
    }

    public String getAbout() {
        return about;
    }

    public double getFee() {
        return fee;
    }

    public int getPatients() {
        return patients;
    }

    public int getSurgeries() {
        return surgeries;
    }

    public int getExperienceYears() {
        return experienceYears;
    }

    public double getRating() {
        return rating;
    }

    public Color getAccent() {
        return accent;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public double getPhotoOffsetY() {
        return photoOffsetY;
    }
}
