package model.entity;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.*;
import java.util.*;

public class User {
    private String name;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Date birthDate;
    private String country;
    private Date dateCreated;

    public User(String name, String lastName, String email, String phoneNumber, Date birthDate, String country) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.country = country;
    }

    public User(String name, String lastName) {
        this.name = name;
        this.lastName = lastName;
    }

    public User(String name, String lastName, Date dateCreated) {
        this.name = name;
        this.lastName = lastName;
        this.dateCreated = dateCreated;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public String getCountry() {
        return country;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    private static boolean checkEmail(String email) {
        String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}";
        //Compile regular expression to get the pattern
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    //the methods listed below,check the validity of params!
    private void validateUsername(String username) throws SignUpException {
        if (username == null || username.trim().isEmpty()) {
            throw new SignUpException("Username cannot be empty.");
        }
    }


    private void validateName(String name) throws SignUpException {
        if (name == null || name.trim().isEmpty()) {
            throw new SignUpException(" firstname cannot be empty.");
        }
    }


    private void validateLastname(String lastName) throws SignUpException {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new SignUpException("lastname cannot be empty");
        }


    }

    //another aspect we have to check is the fact that email is unique.
    //it is checked in the former lines
    private void validateEmail(String email) throws SignUpException {
        if (email == null || email.trim().isEmpty()) {
            throw new SignUpException("Email cannot be empty.");
        }

        if (!checkEmail(email)) {
            throw new SignUpException("This is not a correct format of email.");
        }
    }


    //at this part,we are checking the validity of the given phone number's format!
    public static boolean isValidPhoneNumber(String phoneNumber) {
        // Regular expression pattern for phone number format
        String regex = "^(\\+\\d{1,3})?[-.\\s]?\\(?\\d{1,3}\\)?[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,9}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();


    }

    private void validatePhoneNumber(String phoneNumber) throws SignUpException {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new SignUpException("Phone number cannot be empty.");
        }

        //uniqueness is checked by a method in the database!

        if (!isValidPhoneNumber(phoneNumber)) {
            throw new SignUpException("This is not a correct format of phone number.");
        }
    }

    public static boolean isValidPassword(String password) {
        // Check if the password length is at least 8 characters
        if (password.length() < 8) {
            return false;
        }
        // Check if the password contains at least one uppercase letter
        boolean hasUppercase = false;
        // Check if the password contains at least one lowercase letter
        boolean hasLowercase = false;
//we move throughout all letters to know whether they have at least one capital and one small letter or not!
        for (char ch : password.toCharArray()) {
            if (Character.isUpperCase(ch)) {
                hasUppercase = true;
            } else if (Character.isLowerCase(ch)) {
                hasLowercase = true;
            }
            // Break the loop early if both conditions are satisfied
            if (hasUppercase && hasLowercase) {
                break;
            }
        }
        // Return true if both conditions are satisfied, if not:false
        return hasUppercase && hasLowercase;
    }


    private void validatePassword(String password) throws SignUpException {
        if (password == null || password.isEmpty()) {
            throw new SignUpException("Password cannot be empty.");
        }
        if (!checkPassword(password)) {
            throw new SignUpException("Your password must be at least 8 characters long and " +
                    "contain at least one uppercase and one lowercase letter");
        }
    }

    private static boolean checkPassword(String password) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z]).{8,}$";
        //Compile regular expression to get the pattern
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    /**
     * This field is for making SignUp method multi-thread. So if When one thread is executing SignUp method for an object,
     * all other threads that invoke this method for another objects block until the first thread is done with the object.
     */
    private static final Object lock = new Object();

    //Sign Up:
    public void SignUp(String username, String password, String re_enteredPass, HttpResponse httpResponse) throws SignUpException, SQLException {
        synchronized (lock){
            //after receiving the parameters from the client we will pass them to the chosen method inside the server
//checking the validity of our params:
//username checking:
            boolean userValidity = false;
            if (Database.getProfileDatabaseInstance().isValidUsername(username)) {
                userValidity = true;
            }
            if (!userValidity) {
                httpResponse.writeResponse(409, "This username already exists");
                return;
            }
            validateUsername(username);
//name checking:
            validateName(name);
//lastname checking:
            validateLastname(lastName);
//email and phone number checking:
            if ((email == null || email.trim().isEmpty()) && (phoneNumber == null || phoneNumber.trim().isEmpty())) {
                throw new SignUpException("You must provide at least one of email or phone number.");
            } else if ((email != null && !email.trim().isEmpty()) && (phoneNumber == null || phoneNumber.trim().isEmpty())) {
                validateEmail(email);
                if(!Database.getProfileDatabaseInstance().isUniqueEmail(email)){
                    httpResponse.writeResponse(409, "This email already exists");
                    return;
                }
                if (!checkEmail(email)) {
                    throw new SignUpException("This is not a correct format of an email!");
                }
            } else if ((email == null || email.trim().isEmpty()) && (phoneNumber != null && !phoneNumber.trim().isEmpty())) {
                validatePhoneNumber(phoneNumber);
                isValidPhoneNumber(phoneNumber);
                if(!Database.getProfileDatabaseInstance().isUniquePhoneNumber(phoneNumber)){
                    httpResponse.writeResponse(409, "This phone number already exists");
                    return;
                }
            } else {
                validatePhoneNumber(phoneNumber);
                isValidPhoneNumber(phoneNumber);
                if(!Database.getProfileDatabaseInstance().isUniquePhoneNumber(phoneNumber)){
                    httpResponse.writeResponse(409, "This phone number already exists");
                    return;
                }
                validateEmail(email);
                if(!Database.getProfileDatabaseInstance().isUniqueEmail(email)){
                    httpResponse.writeResponse(409, "This email already exists");
                    return;
                }
                if (!checkEmail(email)) {
                    throw new SignUpException("This is not a correct format of an email!");
                }
            }
//password checking:
            validatePassword(password);
            if (!password.equals(re_enteredPass)) {
                throw new SignUpException("The entered password doesn't match the original one!");
            }
//here,lastModified has the same meaning as the dateCreated field!
            //setting the params.
            Database.getProfileDatabaseInstance().addProfile(username, this, password, httpResponse);
        }
    }

    public static void SignIn(String username, String password, HttpResponse httpResponse) throws SQLException, SignInException {
        if (!Database.getProfileDatabaseInstance().usernameExists(username, null)) {
            httpResponse.writeResponse(404, "This username doesn't exist!");
            throw new SignInException("This username doesn't exist!");
        } else {
            if (!Database.getProfileDatabaseInstance().checkPassword(username, password)) {
                httpResponse.writeResponse(401, "The entered password doesn't match this username!");
                throw new SignInException("The entered password doesn't match this username!");
            }
        }
        httpResponse.writeResponse(200,"User signed in successfully!");
        Database.getProfileDatabaseInstance().updateLoggedIn(username, "true");
    }

    public static Date toDate(String dateString){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = dateFormat.parse(dateString);
            return date;
        } catch (ParseException e) {
            return null;
        }
    }

    public static String getMonthYearString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy");
        return dateFormat.format(date);
    }
}

