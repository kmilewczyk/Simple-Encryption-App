package pl.milewczyk.karol;


import java.util.regex.Pattern;

final public class ValidityChecker {


    private ValidityChecker() {}

    private final static String CORRECT_USER_NAME_REGEX = "[\\S&&[^\\,]]*";
    private final static String CORRECT_PASSWORD_REGEX = ".*";

    public static ValidityAnswer validUsername(String name){
        if (name.equals("")){
            return new ValidityAnswer("Pole nazwy użytkownika jest puste", false);
        }
        else if (!Pattern.matches(CORRECT_USER_NAME_REGEX, name)){
            return new ValidityAnswer("Pole nazwy użytkownika nie może zawierać białych znaków i przecinka", false);
        }
        return new ValidityAnswer(null, true);
    }

    public static ValidityAnswer validPassword(String password, String repeatedPassword){
        if (!password.equals(repeatedPassword)){
            return new ValidityAnswer("Hasła nie są identyczne", false);
        } else if (password.equals("")){
            return new ValidityAnswer("Pole hasła jest puste", false);
        } else if (!Pattern.matches(CORRECT_PASSWORD_REGEX, password)){
            return new ValidityAnswer("Hasło zawiera zabronione znaki", false);
        }

        return new ValidityAnswer(null, true);
    }

}
