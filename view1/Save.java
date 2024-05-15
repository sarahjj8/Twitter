package view;


import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class Save {
    private static final String HEADER_IMAGE_FOLDER = "C:\\Users\\DELL INSPIRON\\Desktop\\ap final\\src\\view\\media\\header\\";
    private static final String AVATAR_IMAGE_FOLDER = "C:\\Users\\DELL INSPIRON\\Desktop\\ap final\\src\\view\\media\\avatar\\";
    private static final String TWEET_IMAGE_FOLDER = "C:\\Users\\DELL INSPIRON\\Desktop\\ap final\\src\\view\\media\\tweet images\\";
    private static final String TWEET_VIDEO_FOLDER = "C:\\Users\\DELL INSPIRON\\Desktop\\ap final\\src\\view\\media\\tweet videos\\";

//    private static final String HEADER_IMAGE_FOLDER = "C:\\Users\\DELL INSPIRON\\Desktop\\AP-Workshop\\AP project\\src\\view\\media\\header\\";
    //    private static final String AVATAR_IMAGE_FOLDER = "C:\\Users\\DELL INSPIRON\\Desktop\\AP-Workshop\\AP project\\src\\view\\media\\avatar\\";
    //    private static final String TWEET_IMAGE_FOLDER = "C:\\Users\\DELL INSPIRON\\Desktop\\AP-Workshop\\AP project\\src\\view\\media\\tweet images\\";
//    private static final String TWEET_VIDEO_FOLDER = "C:\\Users\\DELL INSPIRON\\Desktop\\AP-Workshop\\AP project\\src\\view\\media\\tweets videos\\";


    /**
     * Return the absolute file path if the image was successfully saved
     * If an image with the same name exists, it will replace it
     */
    public static String saveImageToFile(Image image, String imageName, String type) throws IOException {
        String filePath = null;
        switch (type){
            case "avatar":
                filePath = AVATAR_IMAGE_FOLDER + imageName + ".png";
                break;
            case "header":
                filePath = HEADER_IMAGE_FOLDER + imageName + ".png";
                break;
            case "tweet images":
                filePath = TWEET_IMAGE_FOLDER + imageName + ".png";
                break;
            case "tweet videos":
                filePath = TWEET_VIDEO_FOLDER + imageName + ".mp4";
                break;
        }
        File imageFile = new File(filePath);

        // Create the parent folder if it doesn't exist
        File parentFolder = imageFile.getParentFile();
        if (!parentFolder.exists()) {
            parentFolder.mkdirs();
        }
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        boolean success = ImageIO.write(bufferedImage, "png", imageFile);

        if (success) {
            return imageFile.getAbsolutePath();
        } else {
            // Throw an exception if there was an error saving the image
            throw new IOException("Failed to save image to file: " + imageFile.getAbsolutePath());
        }
    }

//    /**
//     * Return the absolute file path if the image was successfully saved
//     * If an image with the same name exists, it will replace it
//     */
//    public static String saveImageToFile(Image image, String imageName, String type) throws IOException {
//        String filePath = null;
//        switch (type){
//            case "avatar":
//                filePath = AVATAR_IMAGE_FOLDER + imageName + ".png";
//                break;
//            case "header":
//                filePath = HEADER_IMAGE_FOLDER + imageName + ".png";
//                break;
//            case "tweet images":
//                filePath = TWEET_IMAGE_FOLDER + imageName + ".png";
//                break;
//        }
//        File imageFile = new File(filePath);
//
//        // Create the parent folder if it doesn't exist
//        File parentFolder = imageFile.getParentFile();
//        if (!parentFolder.exists()) {
//            parentFolder.mkdirs();
//        }
//        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
//        boolean success = ImageIO.write(bufferedImage, "png", imageFile);
//
//        if (success) {
//            return imageFile.getAbsolutePath();
//        } else {
//            throw new IOException("Failed to save image to file: " + imageFile.getAbsolutePath());
//        }
//    }

    public static void resizeVideoFrame(MediaView mediaView, double maxWidth, double maxHeight) {
        double videoWidth = mediaView.getMediaPlayer().getMedia().getWidth();
        double videoHeight = mediaView.getMediaPlayer().getMedia().getHeight();

        double scaleFactor = Math.min(maxWidth / videoWidth, maxHeight / videoHeight);

        mediaView.setFitWidth(videoWidth * scaleFactor);
        mediaView.setFitHeight(videoHeight * scaleFactor);
    }

    /**
     * This method can be used to check if the video is longer than the limit time or not
     */
    public static boolean checkVideoDuration(MediaPlayer mediaPlayer, int limit) {
        Duration duration = mediaPlayer.getTotalDuration();
        Duration maxDuration = Duration.seconds(limit);
        return duration.greaterThan(maxDuration);
    }

    public static String saveVideoToFile(File videoFile, String videoName, String type) throws IOException {
        String filePath = null;
        switch (type){
            case "tweet videos":
                filePath = TWEET_VIDEO_FOLDER + videoName + ".mp4";
                break;
        }
        File outputFile = new File(filePath);

        // Create the parent folder if it doesn't exist
        File parentFolder = outputFile.getParentFile();
        if (!parentFolder.exists()) {
            parentFolder.mkdirs();
        }

        // Copy the video file to the output file
        try (InputStream in = new FileInputStream(videoFile);
             OutputStream out = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        } catch (IOException e) {
            throw new IOException("Failed to save video to file: " + outputFile.getAbsolutePath(), e);
        }

        return outputFile.getAbsolutePath();
    }

    public static Image resizeImage(Image image, int desiredWidth, int desiredHeight) {
        // Get the original dimensions of the image
        int originalWidth = (int) image.getWidth();
        int originalHeight = (int) image.getHeight();

        // Calculate the aspect ratio
        double aspectRatio = (double) originalWidth / originalHeight;

        // Calculate the new dimensions while preserving the aspect ratio
        int newWidth;
        int newHeight;

        if (aspectRatio > 1) {
            newWidth = desiredWidth;
            newHeight = (int) (desiredWidth / aspectRatio);
        } else {
            newHeight = desiredHeight;
            newWidth = (int) (desiredHeight * aspectRatio);
        }

        // Create a new writable image with the new dimensions
        WritableImage resizedImage = new WritableImage(desiredWidth, desiredHeight);

        // Create an image view and set the original image
        ImageView imageView = new ImageView(image);

        // Set the preserve ratio property to true to maintain the aspect ratio
        imageView.setPreserveRatio(true);

        // Set the fit width and height to the new dimensions
        imageView.setFitWidth(newWidth);
        imageView.setFitHeight(newHeight);

        // Create a pane and add the image view to it
        Pane pane = new Pane();
        pane.getChildren().add(imageView);

        // Set the background color to white (or any other color you prefer)
        pane.setStyle("-fx-background-color: white");

        // Calculate the position to center the image in the pane
        double x = (desiredWidth - newWidth) / 2.0;
        double y = (desiredHeight - newHeight) / 2.0;

        // Set the translation of the image view to center it in the pane
        imageView.setTranslateX(x);
        imageView.setTranslateY(y);

        // Render the pane to the resized image
        pane.snapshot(null, resizedImage);

        return resizedImage;
    }

    public static boolean isImageFile(File file) {
        String fileName = file.getName();
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return extension.equals("jpg") || extension.equals("jpeg") || extension.equals("png") || extension.equals("bmp") || extension.equals("gif");
    }

    public static boolean isVideoFile(File file) {
        String fileName = file.getName();
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return extension.equals("mp4") || extension.equals("avi") || extension.equals("mov") || extension.equals("wmv") || extension.equals("flv");
    }

    public static String generateUniqueName() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
        String formattedNow = now.format(formatter);
        String randomString = UUID.randomUUID().toString().substring(0, 6);
        String extension = ".png"; // change this to the appropriate file extension
        return formattedNow + "-" + randomString;
    }

    public static int extractId(String message) {
        Pattern pattern = Pattern.compile("\\b(\\w+) (\\d+) posted successfully");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(2));
        } else {
            throw new IllegalArgumentException("Invalid message format: " + message);
        }
    }

    public static String escapeBackslashes(String input) {
        return input.replace("\\", "\\\\");
    }

    public static String saveVideoToFile(File videoFile, String filePath) throws IOException {
        // Create the file object
        File outputFile = new File(filePath);

        // Create the parent folder if it doesn't exist
        File parentFolder = outputFile.getParentFile();
        if (!parentFolder.exists()) {
            parentFolder.mkdirs();
        }

        // Copy the video file to the output file
        java.nio.file.Files.copy(videoFile.toPath(), outputFile.toPath());

        // Return the absolute file path of the saved file
        return outputFile.getAbsolutePath();
    }

    public static String saveFile(Object file, String filePath) throws IOException {
        // Create the file object
        File outputFile = new File(filePath);

        // Create the parent folder if it doesn't exist
        File parentFolder = outputFile.getParentFile();
        if (!parentFolder.exists()) {
            parentFolder.mkdirs();
        }

        // Save the file to disk
        if (file instanceof Image) {
            // Convert the image to a BufferedImage
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage((Image) file, null);

            // Write the image to the file
            boolean success = ImageIO.write(bufferedImage, "png", outputFile);

            if (!success) {
                // Throw an exception if there was an error saving the image
                throw new IOException("Failed to save image to file: " + outputFile.getAbsolutePath());
            }
        } else if (file instanceof File) {
            // Copy the video file to the output file
            File inputFile = (File) file;
            java.nio.file.Files.copy(inputFile.toPath(), outputFile.toPath());
        } else {
            throw new IllegalArgumentException("Unsupported file type: " + file.getClass().getName());
        }

        // Return the absolute file path of the saved file
        return outputFile.getAbsolutePath();
    }
}