package view;

import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import model.entity.Profile;

public class MenuComponent {
    private Profile profile;
    private CompleteProfileComponent completeProfileComponent;
    private ContextMenu contextMenu;
    private MenuItem reportItem, followItem, blockItem, muteItem;
    private boolean isFollowed;

    public MenuComponent(Profile profile, CompleteProfileComponent completeProfileComponent, boolean isFollowed){
        this.profile = profile;
        this.completeProfileComponent = completeProfileComponent;
        this.isFollowed = isFollowed;

        contextMenu = new ContextMenu();
        blockItem = new MenuItem("Block " + "@" + profile.getUsername());
        if(isFollowed)
            followItem = new MenuItem("Follow " + "@" + profile.getUsername());
        else
            followItem = new MenuItem("Unfollow " + "@" + profile.getUsername());
        reportItem = new MenuItem("Report " + "@" + profile.getUsername());
        muteItem = new MenuItem("Mute " + "@" + profile.getUsername());

        contextMenu.getItems().addAll(followItem, blockItem, reportItem, muteItem);

        setConfigure();
        setActions();
    }

    public ContextMenu getContextMenu() {
        return contextMenu;
    }

    private void setConfigure(){
        contextMenu.setPrefWidth(120);

        contextMenu.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc;");
        contextMenu.setOpacity(0.9);

        followItem.setStyle("-fx-font-weight: bold;");
        blockItem.setStyle("-fx-font-weight: bold;");
        reportItem.setStyle("-fx-font-weight: bold;");
        muteItem.setStyle("-fx-font-weight: bold;");
    }

    private void setActions(){
        followItem.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> followItem.setStyle("-fx-background-color: #e6e6e6;"));
        followItem.addEventHandler(MouseEvent.MOUSE_EXITED, e -> followItem.setStyle("-fx-background-color: transparent;"));
        blockItem.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> blockItem.setStyle("-fx-background-color: #e6e6e6;"));
        blockItem.addEventHandler(MouseEvent.MOUSE_EXITED, e -> blockItem.setStyle("-fx-background-color: transparent;"));
        reportItem.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> reportItem.setStyle("-fx-background-color: #e6e6e6;"));
        reportItem.addEventHandler(MouseEvent.MOUSE_EXITED, e -> reportItem.setStyle("-fx-background-color: transparent;"));
        muteItem.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> muteItem.setStyle("-fx-background-color: #e6e6e6;"));
        muteItem.addEventHandler(MouseEvent.MOUSE_EXITED, e -> muteItem.setStyle("-fx-background-color: transparent;"));
        blockItem.setOnAction(e -> {
            completeProfileComponent.block();
        });

        followItem.setOnAction(e -> {
            completeProfileComponent.follow();
            updateFollowItem();
        });
    }

    public void updateFollowItem(){
        if(isFollowed){
            isFollowed = false;
            followItem.setText("Follow " + "@" + profile.getUsername());
        } else {
            isFollowed = true;
            followItem.setText("Unfollow " + "@" + profile.getUsername());
        }
    }
}
