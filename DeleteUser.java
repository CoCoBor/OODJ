import java.io.*;

public class DeleteUser {
    public void deleteUserFromFile(String tp) throws Exception {

    java.util.List<String> lines = new java.util.ArrayList<>();

    int managerCount = 0;

    boolean deletingManager = false;

    String filePath = "text/users_id.txt";

    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

        String line;

        while ((line = br.readLine()) != null) {

            String[] parts = line.split(",");

            if (parts.length == 5) {

                String fileTpNum = parts[1].trim();
                String fileRole = parts[4].trim();

                // Count total managers
                //Prevent to delete last manager
                if (fileRole.equalsIgnoreCase("manager")) {
                    managerCount++;
                }

                // Check if the user being deleted is a manager
                //Because if the last manager is delected, the system will be unmanageable
                if (fileTpNum.equals(tp) && fileRole.equalsIgnoreCase("manager")) {
                    deletingManager = true;
                }

                // Keep all users except the deleted one
                if (!fileTpNum.equals(tp)) {
                    lines.add(line);
                }
            }
        }

    } catch (IOException e) {
        e.printStackTrace();
    }

    // ONLY block if deleting the LAST manager
    if (deletingManager && managerCount <= 1) {

        throw new Exception("Cannot delete the last manager!");
    }

    // Rewrite file
    try (BufferedWriter bw =
            new BufferedWriter(new FileWriter(filePath))) {

        for (String line : lines) {
            bw.write(line);
            bw.newLine();
        }

    } catch (IOException e) {
        e.printStackTrace();
    }
}
}


