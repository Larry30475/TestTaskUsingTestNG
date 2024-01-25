import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Objects;

public class HttpClientFunctions {
    public static java.net.http.HttpResponse<String> getResponseBody(String url, String method, String requestBody){
        java.net.http.HttpClient httpClient = java.net.http.HttpClient.newHttpClient();
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("accept", "*/*")
                .header("Content-Type", "application/json");

        if (!Objects.equals(method, "")) {
            requestBuilder.method(method, HttpRequest.BodyPublishers.ofString(requestBody));
        }

        HttpRequest request = requestBuilder.build();

        try {
            java.net.http.HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<String> getIds(String url){
        String response = getResponseBody(url, "", "").body();

        ArrayList<String> onlyIds = new ArrayList<String>();

        Pattern pattern = Pattern.compile("\"id\":(\\d+)");
        Matcher matcher = pattern.matcher(response);

        while (matcher.find()) {
            onlyIds.add(matcher.group(1));
        }

        return onlyIds;
    }

    public static boolean checkRole(String id, String login){
        try {
            String url = "http://3.68.165.45/player/get";
            String jsonBody = "{ \"playerId\": " + id + "}";

            String response = getResponseBody(url, "POST", jsonBody).body();

            Pattern patternRole = Pattern.compile("\"role\":\"(\\w+)\"");
            Matcher matcherRole = patternRole.matcher(response);

            Pattern patternLogin = Pattern.compile("\"login\":\"(\\w+)\"");
            Matcher matcherLogin = patternLogin.matcher(response);

            while (matcherRole.find()){
                if(!Objects.equals(matcherRole.group(1), "user")){
                    while (matcherLogin.find()){
                        if(Objects.equals(matcherLogin.group(1), login)){
                            return true;
                        }
                    }
                }
            }

            return false;

        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static boolean checkUps(int age, String gender, String login, String password, String role, String screenName) throws Exception {
        String getAll = getResponseBody("http://3.68.165.45/player/get/all", "", "").body();

        ArrayList<String> onlyLogins = new ArrayList<String>();

        Pattern patternLogin = Pattern.compile("\"login\":\"(\\w+)\"");
        Matcher matcherLogin = patternLogin.matcher(getAll);

        while (matcherLogin.find()){
            onlyLogins.add(matcherLogin.group(1));
        }

        ArrayList<String> onlyScreenNames = new ArrayList<String>();

        Pattern patternScreenNames = Pattern.compile("\"login\":\"(\\w+)\"");
        Matcher matcherScreenNames = patternScreenNames.matcher(getAll);

        while (matcherScreenNames.find()){
            onlyScreenNames.add(matcherScreenNames.group(1));
        }

        String passwordPattern = "^(?=.*[a-zA-Z])(?=.*\\d).{7,15}$";

        Pattern patternPassword = Pattern.compile(passwordPattern);
        Matcher matcherPassword = patternPassword.matcher(password);

        if (age > 16 && age < 60){
            if(Objects.equals(gender, "male") || Objects.equals(gender, "female")){
                if(!onlyLogins.contains(login)){
                    if(matcherPassword.matches()){
                        if(Objects.equals(role, "admin") || Objects.equals(role, "user")){
                            if(!onlyScreenNames.contains(screenName)){
                                return true;
                            }
                            else{
                                throw new Exception("Screen name already exists");
                            }
                        }
                        else{
                            throw new Exception("The created user role is neither admin nor user");
                        }
                    }
                    else{
                        throw new Exception("Password must contain latin letters and numbers (min 7 max 15 characters)");
                    }
                }
                else{
                    throw new Exception("Login already exists");
                }
            }
            else{
                throw new Exception("User should be male or female");
            }
        }
        else{
            throw new Exception("User should be older than 16 and younger than 60 years old");
        }
    }

    public static int createUser(String editor, int age, String gender, String login, String password, String role, String screenName) throws Exception {
        if(checkUps(age, gender, login, password, role, screenName)){
            String createUserUrl = "http://3.68.165.45/player/create/" + editor + "?age=" + age + "&gender=" + gender + "&login=" + login + "&password=" + password + "&role=" + role + "&screenName=" + screenName;
            return getResponseBody(createUserUrl, "GET", "").statusCode();
        }
        return 403;
    }

    public static boolean checkIfSupervisor(String id, String login){
        try {
            String url = "http://3.68.165.45/player/get";
            String jsonBody = "{ \"playerId\": " + id + "}";

            String response = getResponseBody(url, "POST", jsonBody).body();

            Pattern patternRole = Pattern.compile("\"role\":\"(\\w+)\"");
            Matcher matcherRole = patternRole.matcher(response);

            Pattern patternLogin = Pattern.compile("\"login\":\"(\\w+)\"");
            Matcher matcherLogin = patternLogin.matcher(response);

            while (matcherRole.find()){
                if(Objects.equals(matcherRole.group(1), "supervisor")){
                    while (matcherLogin.find()){
                        if(Objects.equals(matcherLogin.group(1), login)){
                            return true;
                        }
                    }
                }
            }

            return false;

        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static int deleteUser(String idToDelete, String editor) throws Exception {
        if(!checkIfSupervisor(idToDelete, editor)) {
            ArrayList<String> ids = getIds("http://3.68.165.45/player/get/all");
            if (ids.contains(idToDelete)) {
                String url = "http://3.68.165.45/player/delete/" + editor;
                String jsonBody = "{ \"playerId\": " + idToDelete + "}";
                return getResponseBody(url, "DELETE", jsonBody).statusCode();
            } else {
                throw new Exception("User with such ID does not exist");
            }
        } else {
            throw new Exception("Supervisor cannot be deleted");
        }
    }

    public static int checkId(String id) throws Exception{
        ArrayList<String> ids = getIds("http://3.68.165.45/player/get/all");
        if (ids.contains(id)) {
            String url = "http://3.68.165.45/player/get";
            String jsonBody = "{ \"playerId\": " + id + "}";
            return getResponseBody(url, "POST", jsonBody).statusCode();
        }
        else{
            throw new Exception("User with such ID does not exist");
        }
    }

    public static int updateUser(String editor, String idToUpdate, int age, String gender, String login, String password, String role, String screenName) throws Exception {
        ArrayList<String> ids = getIds("http://3.68.165.45/player/get/all");
        if(ids.contains(idToUpdate)){
            if(checkUps(age, gender, login, password, role, screenName)){
                String url = "http://3.68.165.45/player/update/" + editor + "/" + idToUpdate;
                String jsonBody = "{" +
                        "\"age\": " + age + "," +
                        "\"gender\": \"" + gender + "\"," +
                        "\"login\": \"" + login + "\"," +
                        "\"password\": \"" + password + "\"," +
                        "\"role\": \"" + role + "\"," +
                        "\"screenName\": \"" + screenName + "\"" +
                        "}";
                String responseString = getResponseBody(url, "PATCH", jsonBody).body();
                return getResponseBody(url, "PATCH", jsonBody).statusCode();
            }
        }
        else{
            throw new Exception("User with such ID does not exist");
        }
        return 403;
    }
}
