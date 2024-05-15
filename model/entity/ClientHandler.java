package model.entity;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;

import java.io.*;
import java.util.*;
import java.net.Socket;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ClientHandler extends Thread{
    private final Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        PrintWriter writer = null;
        BufferedReader reader = null;
        HttpResponse httpResponse = null;
        try {
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new PrintWriter(clientSocket.getOutputStream(), true);
            String requestLine = reader.readLine();
            httpResponse = new HttpResponse(writer);
            if (requestLine != null && !requestLine.isEmpty()) {
                HashMap<String, String> requestParts = HttpRequestUtils.parseRequest(requestLine);
                String method = requestParts.get("method");
                String path = requestParts.get("path");
                if (path == null || method == null)
                    throw new BadRequestException();
                switch (method) {
                    case "GET":
                        doGet(path, requestParts, httpResponse);
                        break;
                    case "POST":
                        doPost(path, httpResponse, reader);
                        break;
                    case "DELETE":
                        doDelete(path, requestParts, httpResponse);
                        break;
                    case "PATCH":
                        doPatch(path, httpResponse, reader);
                        break;
                    default:
                        httpResponse.writeResponse(400, "Invalid request: The specified resource does not exist.");
                        break;
                }
            }
//            } else {
//                throw new model.entity.BadRequestException();
//            }
        } catch (BadRequestException | NumberFormatException | NullPointerException |
                 JsonSyntaxException | MalformedJsonException e) {
            if(httpResponse != null) {
                httpResponse.writeResponse(400, "The request could not be processed. Please provide valid data.");
                httpResponse.setGotResponse(true);
            }
            e.printStackTrace();
        } catch (IOException e) {
            if (httpResponse != null){
                httpResponse.writeResponse(500, "Oops! Something went wrong on our server. We apologize for the inconvenience. We will try to fix this as soon as possible.");
                httpResponse.setGotResponse(true);
            }
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
            if (httpResponse != null){
                httpResponse.writeResponse(500, "Error in performing the database operation. We apologize for the inconvenience. We will try to fix this as soon as possible.");
                httpResponse.setGotResponse(true);
            }
            e.printStackTrace();
        } catch (Exception e) {
            if (httpResponse != null) {
                httpResponse.writeResponse(500, "An unexpected error occurred. We apologize for the inconvenience. We will try to fix this as soon as possible.");
                httpResponse.setGotResponse(true);
            }
            e.printStackTrace();
        }finally {
            if(writer != null)
                writer.close();
            try {
                if(reader != null)
                    reader.close();
                if (clientSocket != null)
                    clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void doGet(String path, HashMap<String, String> requestParts, HttpResponse httpResponse)
            throws SQLException, BadRequestException{
        switch (path){
            case "/images":
                handleGetImages(requestParts, httpResponse);
                break;
            case "/videos":
                handleGetVideos(requestParts, httpResponse);
                break;
            case "/directs":
                handleGetDirects(requestParts, httpResponse);
                break;
            case "/hasDirectWith":
                handleGetHasDirectWith(requestParts, httpResponse);
                break;
            case "/profiles":
                handleGetProfiles(requestParts, httpResponse);
                break;
            case "/profile":
                handleGetProfile(requestParts, httpResponse);
                break;
            case "/likes":
                handleGetlikes(requestParts, httpResponse);
                break;
            case "/timeline":
                handleGetTimeline(requestParts, httpResponse);
                break;
            case "/profileTweets":
                handleGetProfileTweets(requestParts, httpResponse);
                break;
            case "/tweet":
                handleGetTweet(requestParts, httpResponse);
                break;
            case "/replies":
                handleGetReplies(requestParts, httpResponse);
                break;
            case "/retweets":
                handleGetRetweets(requestParts, httpResponse);
                break;
            case "/quotes":
                handleGetQuotes(requestParts, httpResponse);
                break;
            case "/retweetsAndQuotes":
                handleGetQuotesAndRetweets(requestParts, httpResponse);
                break;
            case "/hashtags":
                handleGetHashtags(requestParts, httpResponse);
                break;
            case "/searchedHashtags":
                handleGetSearchedHashtags(requestParts, httpResponse);
                break;
            case "/countHashtags":
                handleGetCountHashtags(requestParts, httpResponse);
                break;
            case "/countTotalHashtags":
                handleGetCountTotalHashtags(requestParts, httpResponse);
                break;
            case "/isLikedBy":
                handleGetIsLikesBy(requestParts, httpResponse);
                break;
            case "/isFollowedBy":
                handleGetIsFollowedBy(requestParts, httpResponse);
                break;
            case "/hasVotedBefore":
                handleGetHasVotedBefore(requestParts, httpResponse);
                break;
            default:
                httpResponse.writeResponse(400, "Invalid request: The specified resource does not exist.");
                break;
        }
    }

    private void doPost(String path, HttpResponse httpResponse, BufferedReader reader)
            throws SQLException, BadRequestException, IOException{
        String body = HttpRequestUtils.readRequestBody(reader);
        switch (path){
            case "/media":
                handlePostMedia(httpResponse, body);
                break;
            case "/tweet":
                handleTweetRequest(httpResponse, body);
                break;
            case "/retweet":
                handleRetweetRequest(httpResponse, body);
                break;
            case "/poll":
                handlePollRequest(httpResponse, body);
                break;
            case "/reply":
                handleReplyRequest(httpResponse, body);
                break;
            case "/like":
                handleLikeRequest(httpResponse, body);
                break;
            case "/block":
                handleBlockRequest(httpResponse, body);
                break;
            case "/follow":
                handleFollowRequest(httpResponse, body);
                break;
            case "/direct":
                handleDirectRequest(httpResponse, body);
                break;
            case "/hashtag":
                handleHashtagRequest(httpResponse, body);
                break;
            case "/vote":
                handleVoteRequest(httpResponse, body);
                break;
            case "/voteWithChoiceText":
                handleVoteWithChoiceTextRequest(httpResponse, body);
                break;
            case "/signin":
                handleSignIn(httpResponse, body);
                break;
            case "/signup":
                handleSignUpRequest(httpResponse, body);
                break;
            default:
                httpResponse.writeResponse(400, "Invalid request: The specified resource does not exist.");
                break;
        }
    }
    private void doDelete(String path, HashMap<String, String> requestParts, HttpResponse httpResponse)
            throws SQLException, BadRequestException{
        switch (path){
            case "/unfollow":
                String followerUsername = requestParts.get("follower username");
                String followedUsername = requestParts.get("followed username");
                Database.getProfileDatabaseInstance().unFollow(followerUsername, followedUsername, httpResponse);
                break;
            case "/unlike":
                String username = requestParts.get("username");
                String tweetId = requestParts.get("tweet id");
                Database.getTweetDatabaseInstance().unLike(Integer.parseInt(tweetId), username, httpResponse);
                break;
            case "/unblock":
                username = requestParts.get("username");
                String blockedUsername = requestParts.get("blocked username");
                Database.getProfileDatabaseInstance().unBlock(username, blockedUsername, httpResponse);
                break;
            default:
                httpResponse.writeResponse(400, "Invalid request: The specified resource does not exist.");
                break;
        }
    }

    private void doPatch(String path, HttpResponse httpResponse, BufferedReader reader)
            throws SQLException, BadRequestException, IOException{
        String body = HttpRequestUtils.readRequestBody(reader);
        switch (path){
            case "/bio":
                handleBioRequest(httpResponse, body);
                break;
            case "/avatar":
                handleAvatarRequest(httpResponse, body);
                break;
            case "/header":
                handleHeaderRequest(httpResponse, body);
                break;
            case "/location":
                handleLocationRequest(httpResponse, body);
                break;
            case "/websiteAddress":
                handleWebsiteAddressRequest(httpResponse, body);
                break;
            default:
                httpResponse.writeResponse(400, "Invalid request: The specified resource does not exist.");
                break;
        }
    }

    private void handlePostMedia(HttpResponse httpResponse, String body)
            throws SQLException, BadRequestException{
        Media media = JsonHelper.fromJson(body, Media.class);
        if(media.getTweetId() == 0)
            throw new BadRequestException();
        Database.getTweetDatabaseInstance().addMedia(media.getTweetId(), media.getType(), media.getUrl(), httpResponse);
    }

    private void handleTweetRequest(HttpResponse httpResponse, String body)
            throws SQLException{
        Tweet tweet = JsonHelper.fromJsonWithAdapter(body, Tweet.class);
        JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
        String username = jsonObject.get("username").getAsString();
        tweet.setProfile(username);
        Database.getTweetDatabaseInstance().addTweet(tweet, "default", httpResponse);
    }

    private void handleRetweetRequest(HttpResponse httpResponse, String body)
            throws SQLException, BadRequestException{
        Retweet retweet = JsonHelper.fromJsonWithAdapter(body, Retweet.class);
        JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
        String username = jsonObject.get("username").getAsString();
        retweet.setProfile(username);
        if(retweet.getAboutTweet() == 0)
            throw new BadRequestException();
        Database.getTweetDatabaseInstance().addTweet(retweet, "retweet", retweet.getAboutTweet(), httpResponse);
    }

    private void handlePollRequest(HttpResponse httpResponse, String body)
            throws SQLException{
        Poll poll = JsonHelper.fromJsonWithAdapter(body, Poll.class);
        Database.getTweetDatabaseInstance().addPoll(poll, httpResponse);
    }

    private void handleReplyRequest(HttpResponse httpResponse, String body)
            throws SQLException, BadRequestException{
        Reply reply = JsonHelper.fromJsonWithAdapter(body, Reply.class);
        JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
        String username = jsonObject.get("username").getAsString();
        reply.setProfile(username);
        if(reply.getAboutTweet() == 0)
            throw new BadRequestException();
        Database.getTweetDatabaseInstance().addTweet(reply, "reply", reply.getAboutTweet(), httpResponse);
    }

    private void handleLikeRequest(HttpResponse httpResponse, String body)
            throws SQLException, BadRequestException{
        Like like = JsonHelper.fromJsonWithAdapter(body, Like.class);
        if(like.getTweetId() == 0)
            throw new BadRequestException();
        Database.getTweetDatabaseInstance().like(like.getTweetId(), like.getUsername(), httpResponse);
    }

    private void handleFollowRequest(HttpResponse httpResponse, String body)
            throws SQLException{
        ArrayList<String> parameters = JsonHelper.jsonArrayToArrayList(body);
        String followerUsername = parameters.get(0);
        String followedUsername = parameters.get(1);
        Database.getProfileDatabaseInstance().follow(followerUsername, followedUsername, httpResponse);
    }

    private void handleBlockRequest(HttpResponse httpResponse, String body)
            throws SQLException{
        ArrayList<String> parameters = JsonHelper.jsonArrayToArrayList(body);
        String username = parameters.get(0);
        String blockedUsername = parameters.get(1);
        Database.getProfileDatabaseInstance().block(username, blockedUsername, httpResponse);
    }

    private void handleDirectRequest(HttpResponse httpResponse, String body)
            throws SQLException{
        Direct direct = JsonHelper.fromJsonWithAdapter(body, Direct.class);
        Database.getDirectDatabaseInstance().direct(direct.getSenderUsername(), direct.getReceiverUsername(),direct.getText(), httpResponse);
    }

    private void handleHashtagRequest(HttpResponse httpResponse, String body)
            throws SQLException, BadRequestException{
        Hashtag hashtag = JsonHelper.fromJsonWithAdapter(body, Hashtag.class);
        if(hashtag.getTweetId() == 0)
            throw new BadRequestException();
        Database.getTweetDatabaseInstance().addHashtag(hashtag.getTweetId(), hashtag.getHashtagName(), httpResponse);
    }

    private void handleVoteRequest(HttpResponse httpResponse, String body)
            throws SQLException, BadRequestException{
        Vote vote = JsonHelper.fromJsonWithAdapter(body, Vote.class);
        if(vote.getPollId() == 0 || vote.getChoiceId() == 0)
            throw new BadRequestException();
        Database.getTweetDatabaseInstance().vote(vote.getPollId(), vote.getChoiceId(), vote.getUsername(), httpResponse);
    }

    private void handleVoteWithChoiceTextRequest(HttpResponse httpResponse, String body)
            throws SQLException, BadRequestException{
        JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
        String username = jsonObject.get("username").getAsString();
        int pollId = jsonObject.get("pollId").getAsInt();
        String choiceText = jsonObject.get("choice text").getAsString();
        Database.getTweetDatabaseInstance().voteWithChoiceText(pollId, choiceText, username, httpResponse);
    }

    private void handleSignUpRequest(HttpResponse httpResponse, String body) throws SQLException {
        JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
        String username = getString(jsonObject, "username");
        String name = getString(jsonObject, "name");
        String lastname = getString(jsonObject, "lastname");
        String email = getStringOrNull(jsonObject, "email");
        String phoneNumber = getStringOrNull(jsonObject, "phoneNumber");
        String birthDate = getString(jsonObject, "birthDate");
        String country = getString(jsonObject, "country");
        String password = getString(jsonObject, "password");
        String re_enteredPass = getString(jsonObject, "re_enteredPass");
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        try {
            Date birthDateD = dateFormat.parse(birthDate);
            User user = new User(name, lastname, email, phoneNumber, birthDateD, country);
            user.SignUp(username, password, re_enteredPass, httpResponse);
        } catch (SignUpException e) {
            httpResponse.writeResponse(400, e.getMessage());
        } catch (ParseException e) {
            httpResponse.writeResponse(400, "The request could not be processed. Please provide valid birthdate.");
        }
    }

    private String getString(JsonObject jsonObject, String propertyName) {
        JsonElement element = jsonObject.get(propertyName);
        if (element == null || element.isJsonNull()) {
            throw new NullPointerException("The " + propertyName + " field is missing or null.");
        }
        return element.getAsString();
    }

    private String getStringOrNull(JsonObject jsonObject, String propertyName) {
        JsonElement element = jsonObject.get(propertyName);
        return element != null && !element.isJsonNull() ? element.getAsString() : null;
    }

//    private void handleSignUpRequest(HttpResponse httpResponse, String body)
//            throws SQLException {
//        JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
//        String username = jsonObject.get("username").getAsString();
//        String name = jsonObject.get("name").getAsString();
//        String lastname = jsonObject.get("lastName").getAsString();
//        String email = jsonObject.has("email") ? jsonObject.get("email").getAsString() : null;
//        String phoneNumber = jsonObject.has("phoneNumber") ? jsonObject.get("phoneNumber").getAsString() : null;
//        String birthDate = jsonObject.get("birthDate").getAsString();
//        String country = jsonObject.get("country").getAsString();
//        String password = jsonObject.get("password").getAsString();
//        String re_enteredPass = jsonObject.get("re_enteredPass").getAsString();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        try {
//            Date birthDateD = dateFormat.parse(birthDate);
//            User user = new User(name, lastname, email, phoneNumber, birthDateD, country);
//            user.SignUp(username, password, re_enteredPass, httpResponse);
//        } catch (SignUpException e) {
//            httpResponse.writeResponse(400, e.getMessage());
//        } catch (ParseException e) {
//            httpResponse.writeResponse(400, "The request could not be processed. Please provide valid birthdate.");
//        }
//    }

    private void handleSignIn(HttpResponse httpResponse,String body) throws SQLException{
        try {
            JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
            String username = jsonObject.get("username").getAsString();
            String password = jsonObject.get("password").getAsString();
            User.SignIn(username,password, httpResponse);
        }catch (SignInException e) {
            return;
        }
    }

    private void handleBioRequest(HttpResponse httpResponse, String body)
            throws SQLException{
        JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
        String username = jsonObject.get("username").getAsString();
        String bio =  jsonObject.get("bio").getAsString();
        Database.getProfileDatabaseInstance().updateBio(username, bio, httpResponse);
    }

    private void handleAvatarRequest(HttpResponse httpResponse, String body)
            throws SQLException{
        body = body.replace("\\", "\\\\");
        JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
        String username = jsonObject.get("username").getAsString();
        String avatar =  jsonObject.get("avatar").getAsString();
        Database.getProfileDatabaseInstance().updateAvatar(username, avatar, httpResponse);
    }

    private void handleHeaderRequest(HttpResponse httpResponse, String body)
            throws SQLException{
        body = body.replace("\\", "\\\\");
        JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
        String username = jsonObject.get("username").getAsString();
        String header =  jsonObject.get("header").getAsString();
        Database.getProfileDatabaseInstance().updateHeader(username, header, httpResponse);
    }

    private void handleLocationRequest(HttpResponse httpResponse, String body)
            throws SQLException{
        JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
        String username = jsonObject.get("username").getAsString();
        String location =  jsonObject.get("location").getAsString();
        Database.getProfileDatabaseInstance().updateLocation(username, location, httpResponse);
    }

    private void handleWebsiteAddressRequest(HttpResponse httpResponse, String body)
            throws SQLException{
        JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
        String username = jsonObject.get("username").getAsString();
        String websiteAddress =  jsonObject.get("website address").getAsString();
        Database.getProfileDatabaseInstance().updateWebAddress(username, websiteAddress, httpResponse);
    }

    private void handleGetImages(HashMap<String, String> requestParts, HttpResponse httpResponse)
            throws SQLException{
        String tweetId = requestParts.get("tweet id");
        Database.getTweetDatabaseInstance().getTweetImages(Integer.parseInt(tweetId), httpResponse);
    }

    private void handleGetVideos(HashMap<String, String> requestParts, HttpResponse httpResponse)
            throws SQLException{
        String tweetId = requestParts.get("tweet id");
        Database.getTweetDatabaseInstance().getTweetVideos(Integer.parseInt(tweetId), httpResponse);
    }

    private void handleGetDirects(HashMap<String, String> requestParts, HttpResponse httpResponse)
            throws SQLException{
        String username = requestParts.get("username");
        String toUsername = requestParts.get("to username");
        Database.getDirectDatabaseInstance().getDirects(username, toUsername, httpResponse);
    }

    private void handleGetHasDirectWith(HashMap<String, String> requestParts, HttpResponse httpResponse)
            throws SQLException{
        String username = requestParts.get("username");
        Database.getDirectDatabaseInstance().hasDirectWith(username, httpResponse);
    }

    /**
     * with considering blocked profiles
     */
    private void handleGetProfiles(HashMap<String, String> requestParts, HttpResponse httpResponse)
            throws SQLException{
        String str = requestParts.get("string to search for");
        String username = requestParts.get("username");
        Database.getProfileDatabaseInstance().searchProfile(username, str, httpResponse);
    }

    private void handleGetSearchedHashtags(HashMap<String, String> requestParts, HttpResponse httpResponse)
            throws SQLException{
        String str = requestParts.get("string to search for");
        Database.getTweetDatabaseInstance().searchHashtag(str, httpResponse);
    }

    private void handleGetCountHashtags(HashMap<String, String> requestParts, HttpResponse httpResponse)
            throws SQLException{
        String str = requestParts.get("hashtag name");
        Database.getTweetDatabaseInstance().countHashtags(str, httpResponse);
    }

    private void handleGetCountTotalHashtags(HashMap<String, String> requestParts, HttpResponse httpResponse)
            throws SQLException{
        Database.getTweetDatabaseInstance().countTotalHashtags(httpResponse);
    }

    private void handleGetProfile(HashMap<String, String> requestParts, HttpResponse httpResponse)
            throws SQLException{
        String username = requestParts.get("username");
        Database.getProfileDatabaseInstance().getProfile(username, httpResponse);
    }

    private void handleGetlikes(HashMap<String, String> requestParts, HttpResponse httpResponse)
            throws SQLException{
        String tweetId = requestParts.get("tweet id");
        Database.getTweetDatabaseInstance().getLike(Integer.parseInt(tweetId), httpResponse);
    }

    private void handleGetIsLikesBy(HashMap<String, String> requestParts, HttpResponse httpResponse)
            throws SQLException{
        String tweetId = requestParts.get("tweet id");
        String username = requestParts.get("username");
        Database.getTweetDatabaseInstance().isLikedBy(Integer.parseInt(tweetId), username, httpResponse);
    }

    private void handleGetIsFollowedBy(HashMap<String, String> requestParts, HttpResponse httpResponse)
            throws SQLException{
        String followerUsername = requestParts.get("follower username");
        String followedUsername = requestParts.get("followed username");
        Database.getProfileDatabaseInstance().isFollowedBy(followerUsername, followedUsername, httpResponse);
    }

    private void handleGetHasVotedBefore(HashMap<String, String> requestParts, HttpResponse httpResponse)
            throws SQLException{
        String pollId = requestParts.get("poll id");
        String username = requestParts.get("username");
        Database.getTweetDatabaseInstance().hasVotedBefore(username, Integer.parseInt(pollId), httpResponse);
    }

    private void handleGetTimeline(HashMap<String, String> requestParts, HttpResponse httpResponse)
            throws SQLException{
        String username = requestParts.get("username");
        Database.getTweetDatabaseInstance().timeLine(username, httpResponse);
    }

    private void handleGetProfileTweets(HashMap<String, String> requestParts, HttpResponse httpResponse)
            throws SQLException{
        String username = requestParts.get("username");
        Database.getTweetDatabaseInstance().getProfileTweets(username, httpResponse);
    }

    private void handleGetTweet(HashMap<String, String> requestParts, HttpResponse httpResponse)
            throws SQLException{
        String tweetId = requestParts.get("tweet id");
        Database.getTweetDatabaseInstance().getTweet(Integer.parseInt(tweetId), httpResponse);
    }

    private void handleGetReplies(HashMap<String, String> requestParts, HttpResponse httpResponse)
            throws SQLException{
        String tweetId = requestParts.get("tweet id");
        String username = requestParts.get("username");
        Database.getTweetDatabaseInstance().getReplies(Integer.parseInt(tweetId), username, httpResponse);
    }

    private void handleGetRetweets(HashMap<String, String> requestParts, HttpResponse httpResponse)
            throws SQLException{
        String tweetId = requestParts.get("tweet id");
        String username = requestParts.get("username");
        Database.getTweetDatabaseInstance().getRetweets(Integer.parseInt(tweetId), username, "retweet", httpResponse);
    }

    private void handleGetQuotes(HashMap<String, String> requestParts, HttpResponse httpResponse)
            throws SQLException{
        String tweetId = requestParts.get("tweet id");
        String username = requestParts.get("username");
        Database.getTweetDatabaseInstance().getRetweets(Integer.parseInt(tweetId), username, "quote", httpResponse);
    }

    private void handleGetQuotesAndRetweets(HashMap<String, String> requestParts, HttpResponse httpResponse)
            throws SQLException{
        String tweetId = requestParts.get("tweet id");
        String username = requestParts.get("username");
        Database.getTweetDatabaseInstance().getRetweetsAndQuotes(Integer.parseInt(tweetId), username, httpResponse);
    }

    private void handleGetHashtags(HashMap<String, String> requestParts, HttpResponse httpResponse)
            throws SQLException{
        String hashtagName = requestParts.get("hashtag name");
        String username = requestParts.get("username");
        Database.getTweetDatabaseInstance().findTweetOfHashtag(username, hashtagName, httpResponse);
    }
}