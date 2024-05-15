package model.entity;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JsonHelper {
    private static final Gson gson = new Gson();

    //     Helper method to serialize object to JSON
    public static String toJson(Object obj) {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    public static ArrayList<String> jsonArrayToArrayList(String jsonArrayString) {
        Gson gson = new Gson();
        Type listType = new com.google.gson.reflect.TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(jsonArrayString, listType);
    }

    public static <T> ArrayList<T> parseJsonToList(String jsonString, Class<T> elementType) {
        Gson gson = new Gson();
        TypeToken<ArrayList<T>> token = new TypeToken<ArrayList<T>>() {};
        return gson.fromJson(jsonString, token.getType());
    }

    /**
     * This method must be used for classes that has a LocalDateTime field(such as tweet and its subclasses)
     */
    public static <T> T fromJsonWithAdapter(String json, Class<T> clazz) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .create();
        return gson.fromJson(json, clazz);
    }

    /**
     * This generic method gets a json string as an input and returns arrayList of that generic type
     * This method must be used for classes that has a LocalDateTime field(such as tweet and its subclasses) as well
     */
    public static <T> ArrayList<T> parseJsonToListWithAdapter(String json, Class<T> elementType) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .create();

        Type listType = TypeToken.getParameterized(ArrayList.class, elementType).getType();
        return gson.fromJson(json, listType);
    }

    public static ArrayList<Tweet> parseJsonToTweetListWithAdapter(String json){
        ArrayList<Tweet> tweets = new ArrayList<>();
        ArrayList<String> jsonStrings = JsonHelper.getJsonArrayFromString(json);
        Tweet tweet = null;
        for(String jsonString : jsonStrings){
            JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
            String type = jsonObject.get("type").getAsString();
            switch (type){
                case "default":
                    tweet = JsonHelper.fromJsonWithAdapter(jsonString, Tweet.class);
                    break;
                case "reply":
                    tweet = JsonHelper.fromJsonWithAdapter(jsonString, Reply.class);
                    break;
                case "poll":
                    tweet = JsonHelper.fromJsonWithAdapter(jsonString, Poll.class);
                    break;
                case "retweet":
                case "quote":
                    tweet = JsonHelper.fromJsonWithAdapter(jsonString, Retweet.class);
                    break;
            }
            tweets.add(tweet);
        }
        return tweets;
    }

    public static ArrayList<String> getJsonArrayFromString(String jsonString) {
        ArrayList<String> jsonArray = new ArrayList<>();
        JsonElement element = JsonParser.parseString(jsonString);
        if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            for (JsonElement jsonElement : array) {
                jsonArray.add(jsonElement.toString());
            }
        }
        return jsonArray;
    }

    public static HashMap<LocalDate, Integer> hashmapDeserializer(String jsonString) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateAdapter());
        Gson gson = gsonBuilder.create();
        Type type = new TypeToken<HashMap<LocalDate, Integer>>(){}.getType();
        return gson.fromJson(jsonString, type);
    }

    public static Gson createCustomGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .create();
    }

    public static String toJsonWithAdapter(Object object) {
        Gson gson = createCustomGson();
        return gson.toJson(object);
    }
}
