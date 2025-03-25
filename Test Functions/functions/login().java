public boolean loginUser(String username, String password) {
    try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] userData = line.split(",");
            if (userData[1].equals(username) && userData[4].equals(password)) {
                this.lastLogin = LocalDateTime.now();
                System.out.println("Login successful!");
                return true;
            }
        }
    } catch (IOException e) {
        System.out.println("Error logging in: " + e.getMessage());
    }
    System.out.println("Invalid credentials.");
    return false;
}
