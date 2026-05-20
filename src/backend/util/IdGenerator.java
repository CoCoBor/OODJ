package src.backend.util;

       
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class IdGenerator {
    private static final Pattern ID_PATTERN = Pattern.compile("^([A-Z]+)(\\d+)$");
    private static final int DEFAULT_PADDING = 3;

    private IdGenerator() {
    }

    public static String nextId(String prefix, Collection<String> existingIds) {
        String normalizedPrefix = normalizePrefix(prefix);
        long maxNumber = 0L;
        int padding = DEFAULT_PADDING;

        if (existingIds != null) {
            for (String id : existingIds) {
                if (id == null) {
                    continue;
                }

                Matcher matcher = ID_PATTERN.matcher(id.trim().toUpperCase());
                if (!matcher.matches()) {
                    continue;
                }

                String idPrefix = matcher.group(1);
                if (!normalizedPrefix.equals(idPrefix)) {
                    continue;
                }

                String numericPart = matcher.group(2);
                padding = Math.max(padding, numericPart.length());

                try {
                    long currentNumber = Long.parseLong(numericPart);
                    if (currentNumber > maxNumber) {
                        maxNumber = currentNumber;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }

        if (maxNumber == Long.MAX_VALUE) {
            throw new IllegalStateException("Cannot generate next ID because numeric range is exhausted for prefix: " + normalizedPrefix);
        }

        long nextNumber = maxNumber + 1;
        return normalizedPrefix + String.format("%0" + padding + "d", nextNumber);
    }

    public static String nextUserId(Collection<String> existingIds) {
        return nextId("U", existingIds);
    }

    public static String nextAppointmentId(Collection<String> existingIds) {
        return nextId("A", existingIds);
    }

    public static String nextPaymentId(Collection<String> existingIds) {
        return nextId("P", existingIds);
    }

    public static String nextFeedbackId(Collection<String> existingIds) {
        return nextId("F", existingIds);
    }

    public static String nextPriceId(Collection<String> existingIds) {
        return nextId("PR", existingIds);
    }

    private static String normalizePrefix(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("prefix cannot be null");
        }

        String normalizedPrefix = prefix.trim().toUpperCase();
        if (normalizedPrefix.isEmpty() || !normalizedPrefix.matches("[A-Z]+")) {
            throw new IllegalArgumentException("prefix must contain only letters");
        }

        return normalizedPrefix;
    }
}
