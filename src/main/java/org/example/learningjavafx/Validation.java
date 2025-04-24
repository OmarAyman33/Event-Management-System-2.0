package org.example.learningjavafx;

public class Validation {
    public static boolean username(String username){
        /*
        * requirements:
        * username must be between 4 and 15 characters long
        * username must start with a letter
        * username can only include letters,digits and underscores
        * */
        if(username.length()<4 || username.length()>15 || username == null)
            return false;
        // make sure username starts with a letter
        if(!Character.isLetter(username.charAt(0)))
            return false;
        // Only allow letters, digits, and underscores
        for (int i = 0; i < username.length(); i++) {
            char c = username.charAt(i);
            if (!Character.isLetterOrDigit(c) && c != '_') {
                return false;
            }
        }
        return true;
    }

    public static boolean password(String password){
         /*
         * requirements:
         * password must be at least 8 characters long
         * password must contain at least one uppercase letter, one lowercase letter and one digit
         *  */
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        if (password == null || password.length() < 8) {
            return false;
        }
        for (int i = 0; i < password.length(); i++) {
            if (Character.isUpperCase(password.charAt(i))) hasUpper = true;
            else if (Character.isLowerCase(password.charAt(i))) hasLower = true;
            else if (Character.isDigit(password.charAt(i))) hasDigit = true;
        }
        return hasUpper && hasLower && hasDigit;
    }
}
