public boolean registerUser() {
    if (!validateEmail(email) || !validatePhoneNumber(phoneNumber)) {
        System.out.println("Invalid email or phone number.");
        return false;
    }
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
        writer.write(userID + "," + username + "," + email + "," + phoneNumber + "," + passwordHash + "\n");
        System.out.println("User registered successfully.");
        return true;
    } catch (IOException e) {
        System.out.println("Error registering user: " + e.getMessage());
        return false;
    }
}

private boolean validateEmail(String email) {
    String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
    return Pattern.matches(emailRegex, email);
}

private boolean validatePhoneNumber(String phoneNumber) {
    return phoneNumber.matches("\\d{10}");
}
}