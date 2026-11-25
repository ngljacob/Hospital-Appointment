import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class AuthService {

    private static final int ITERATIONS = 120_000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;

    private final SecureRandom random = new SecureRandom();
    private final Path store = Paths.get(System.getProperty("user.home"), ".takecare", "users.txt");

    public String signUp(String username, char[] password) throws AuthException {
        String name = normalize(username);
        validateUsername(name);
        validatePassword(password);
        ensureStore();

        try {
            List<String> lines = new ArrayList<>(Files.readAllLines(store, StandardCharsets.UTF_8));
            for (String line : lines) {
                if (line.startsWith(name + "|")) {
                    throw new AuthException("That username is already taken.");
                }
            }
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);
            byte[] hash = hash(password, salt);
            lines.add(name + "|" + Base64.getEncoder().encodeToString(salt)
                    + "|" + Base64.getEncoder().encodeToString(hash));
            Files.write(store, lines, StandardCharsets.UTF_8);
            return name;
        } catch (IOException e) {
            throw new AuthException("Could not save your account. Please try again.");
        }
    }

    public String signIn(String username, char[] password) throws AuthException {
        String name = normalize(username);
        validateUsername(name);
        ensureStore();

        try {
            for (String line : Files.readAllLines(store, StandardCharsets.UTF_8)) {
                String[] parts = line.split("\\|", -1);
                if (parts.length != 3 || !parts[0].equals(name)) {
                    continue;
                }
                byte[] salt = Base64.getDecoder().decode(parts[1]);
                byte[] expected = Base64.getDecoder().decode(parts[2]);
                byte[] actual = hash(password, salt);
                if (MessageDigest.isEqual(expected, actual)) {
                    return name;
                }
                throw new AuthException("Incorrect password.");
            }
        } catch (IOException e) {
            throw new AuthException("Could not read your account. Please try again.");
        } catch (IllegalArgumentException e) {
            throw new AuthException("Stored account data is invalid.");
        }
        throw new AuthException("No account found for that username.");
    }

    public void changePassword(String username, char[] current, char[] next) throws AuthException {
        signIn(username, current);
        String name = normalize(username);
        validatePassword(next);

        try {
            List<String> lines = new ArrayList<>(Files.readAllLines(store, StandardCharsets.UTF_8));
            for (int i = 0; i < lines.size(); i++) {
                String[] parts = lines.get(i).split("\\|", -1);
                if (parts.length != 3 || !parts[0].equals(name)) {
                    continue;
                }
                byte[] salt = new byte[SALT_LENGTH];
                random.nextBytes(salt);
                byte[] hash = hash(next, salt);
                lines.set(i, name + "|" + Base64.getEncoder().encodeToString(salt)
                        + "|" + Base64.getEncoder().encodeToString(hash));
                Files.write(store, lines, StandardCharsets.UTF_8);
                return;
            }
        } catch (IOException e) {
            throw new AuthException("Could not update your password. Please try again.");
        }
        throw new AuthException("No account found for that username.");
    }

    private byte[] hash(char[] password, byte[] salt) throws AuthException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return factory.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new AuthException("Security provider is unavailable.");
        } finally {
            spec.clearPassword();
        }
    }

    private void ensureStore() throws AuthException {
        try {
            Files.createDirectories(store.getParent());
            if (!Files.exists(store)) {
                Files.createFile(store);
            }
        } catch (IOException e) {
            throw new AuthException("Could not prepare account storage.");
        }
    }

    private String normalize(String username) throws AuthException {
        if (username == null || username.trim().isEmpty()) {
            throw new AuthException("Username is required.");
        }
        return username.trim();
    }

    private void validateUsername(String username) throws AuthException {
        if (username.length() < 3 || username.length() > 20
                || !username.matches("[A-Za-z0-9_.-]+")) {
            throw new AuthException("Use 3-20 letters, numbers, dots, dashes, or underscores.");
        }
    }

    private void validatePassword(char[] password) throws AuthException {
        if (password == null || password.length < 6) {
            throw new AuthException("Password must be at least 6 characters.");
        }
    }
}
