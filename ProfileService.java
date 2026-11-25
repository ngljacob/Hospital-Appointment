import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public final class ProfileService {

    public static class Details {
        public String fullName = "";
        public String email = "";
        public String phone = "";
    }

    private final Path store = Paths.get(System.getProperty("user.home"), ".takecare", "profiles.txt");

    public Details load(String username) {
        Details details = new Details();
        if (username == null) {
            return details;
        }
        try {
            Files.createDirectories(store.getParent());
            if (!Files.exists(store)) {
                return details;
            }
            for (String line : Files.readAllLines(store, StandardCharsets.UTF_8)) {
                String[] parts = line.split("\\|", -1);
                if (parts.length == 4 && parts[0].equals(username)) {
                    details.fullName = decode(parts[1]);
                    details.email = decode(parts[2]);
                    details.phone = decode(parts[3]);
                    return details;
                }
            }
        } catch (IOException | IllegalArgumentException ignored) {
            return details;
        }
        return details;
    }

    public void save(String username, Details details) throws IOException {
        Files.createDirectories(store.getParent());
        List<String> lines = new ArrayList<>();
        if (Files.exists(store)) {
            lines.addAll(Files.readAllLines(store, StandardCharsets.UTF_8));
        }
        String record = username + "|" + encode(details.fullName)
                + "|" + encode(details.email) + "|" + encode(details.phone);
        boolean updated = false;
        for (int i = 0; i < lines.size(); i++) {
            String[] parts = lines.get(i).split("\\|", -1);
            if (parts.length == 4 && parts[0].equals(username)) {
                lines.set(i, record);
                updated = true;
                break;
            }
        }
        if (!updated) {
            lines.add(record);
        }
        Files.write(store, lines, StandardCharsets.UTF_8);
    }

    private static String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private static String decode(String value) {
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }
}
