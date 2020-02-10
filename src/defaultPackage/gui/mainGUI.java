package defaultPackage.gui;

import defaultPackage.Excution;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class mainGUI extends Application {
    private static final double WINDOW_HEIGHT = 720;
    private static final double WINDOW_WIDTH = 420;
    private static double ZOOMFACTOR = 1;
    private Number mainStageHeight = WINDOW_HEIGHT, mainStageWidth = WINDOW_WIDTH;
    private final Group root = new Group();

    private ImageButton extractButton = new ImageButton(100,25,"Extract", .95,.82,.38,.8);
    private ImageButton packageButton = new ImageButton(100,25,"Package",.1, .3, .9, .8);
    private UploadField uploadField = new UploadField(150);
    private Label locationLable = new Label();

    private Excution excution = new Excution();
    private String address = "";

    private void setZoomFactor(Number mainStageWidth, Number mainStageHeight) {
        double width = mainStageWidth.doubleValue() / WINDOW_WIDTH, height = mainStageHeight.doubleValue() / WINDOW_HEIGHT;
        if (width < height) ZOOMFACTOR = Math.round(width * 100.0) / 100.0;
        else ZOOMFACTOR = Math.round(height * 100.0) / 100.0;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("icon maker");
        primaryStage.setMinHeight(720*0.7);
        primaryStage.setMinWidth(420*0.7);
        Scene scene = new Scene(root,WINDOW_WIDTH,WINDOW_HEIGHT);
        scene.heightProperty().addListener((observable, oldValue, newValue) -> {
            mainStageHeight = newValue;
            setZoomFactor(mainStageWidth, mainStageHeight);
            resize();
        });
        scene.widthProperty().addListener((observable, oldValue, newValue) -> {
            mainStageWidth = newValue;
            setZoomFactor(mainStageWidth, mainStageHeight);
            resize();
        });
        resize();
        listener();
        extractButton.setRadius(25);
        packageButton.setRadius(25);
        root.getChildren().addAll(extractButton,packageButton, uploadField);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    //X: width, Y: height
    private void resize(){
        packageButton.setLocation(mainStageWidth.doubleValue()/2-100-(30*ZOOMFACTOR),mainStageHeight.doubleValue()-200*ZOOMFACTOR);
        extractButton.setLocation(mainStageWidth.doubleValue()/2+(30*ZOOMFACTOR),mainStageHeight.doubleValue()-200*ZOOMFACTOR);
        uploadField.setLocation((mainStageWidth.doubleValue()-uploadField.getSize())/2,mainStageHeight.doubleValue()/2-uploadField.getSize()-100*ZOOMFACTOR);
    }

    private void listener(){
        extractButton.setOnMouseEntered(event -> extractButton.setOver(1.0));
        extractButton.setOnMouseExited(event -> extractButton.setOver(0.8));
        extractButton.setOnMouseClicked(event -> excution.execute(address,false));

        packageButton.setOnMouseEntered(event -> packageButton.setOver(1.0));
        packageButton.setOnMouseExited(event -> packageButton.setOver(0.8));
        packageButton.setOnMouseClicked(event -> excution.execute(address,true));

        uploadField.setOnMouseEntered(event -> uploadField.onDrag(false));
        uploadField.setOnMouseExited(event -> uploadField.onDrag(true));
        uploadField.setOnDragOver(event -> {
            if (event.getGestureSource() != uploadField) {
                event.acceptTransferModes(TransferMode.ANY);
            }
        });
        uploadField.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            address = db.getFiles().get(0).toString();
            if(!excution.autoRegconize(address))
                System.out.println("Cannot recognize!");
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

}
