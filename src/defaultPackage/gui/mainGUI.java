package defaultPackage.gui;

import defaultPackage.Execution;
import defaultPackage.Observer;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class mainGUI extends Application implements Observer {
    private static final double WINDOW_HEIGHT = 720;
    private static final double WINDOW_WIDTH = 420;
    private static double ZOOMFACTOR = 1;
    private Number mainStageHeight = WINDOW_HEIGHT, mainStageWidth = WINDOW_WIDTH;
    private final Group root = new Group();

    private ImageButton extractButton = new ImageButton(100,25,"Extract", .95,.82,.38,.8);
    private ImageButton packageButton = new ImageButton(100,25,"Package",.1, .3, .9, .8);
    private UploadField uploadField = new UploadField(150);
    private GridPane labelArea = new GridPane();
    private Label locationLabel = new Label();
    private Label errorLabel = new Label("Cannot recognize file type, unable to auto execute.");
    private Label successfulLabel = new Label("Process success!");
    //private MenuButton menuButton = new MenuButton();
    //MenuNode mn = new MenuNode(420,720);

    private Execution execution = new Execution();
    private String address = "";

    private void setZoomFactor(Number mainStageWidth, Number mainStageHeight) {
        double width = mainStageWidth.doubleValue() / WINDOW_WIDTH, height = mainStageHeight.doubleValue() / WINDOW_HEIGHT;
        if (width < height) ZOOMFACTOR = Math.round(width * 100.0) / 100.0;
        else ZOOMFACTOR = Math.round(height * 100.0) / 100.0;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        execution.reg(this);
        primaryStage.setTitle("icon maker");
        primaryStage.setMinHeight(720*0.7);
        primaryStage.setMinWidth(420*0.7);
        Scene scene = new Scene(root,WINDOW_WIDTH,WINDOW_HEIGHT);
        scene.setFill(Color.WHITESMOKE.deriveColor(
                0, 1, 1, 0.5
        ));

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
        labelArea.setVgap(5);
        labelArea.setAlignment(Pos.CENTER);
        GridPane.setHalignment(locationLabel, HPos.CENTER);
        GridPane.setHalignment(errorLabel, HPos.CENTER);
        GridPane.setHalignment(successfulLabel, HPos.CENTER);
        //GridPane.setValignment(labelArea, VPos.CENTER);
        locationLabel.setTextFill(new Color(.6,.6,.6,1));
        locationLabel.setFont(Font.font("Menlo", 12));
        errorLabel.setTextFill(new Color(1,.3,.3,1));
        errorLabel.setFont(Font.font("Menlo", 10));
        successfulLabel.setTextFill(new Color(.3,1,.3,1));
        successfulLabel.setFont(Font.font("Menlo", 10));

        packageButton.setDisable(true);
        extractButton.setDisable(true);

        extractButton.setRadius(25);
        packageButton.setRadius(25);

        //mn.setLayoutX(WINDOW_WIDTH);mn.setLayoutY(WINDOW_HEIGHT);
        root.getChildren().addAll(extractButton,packageButton, uploadField, labelArea);
        primaryStage.initStyle(StageStyle.DECORATED);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    //X: width, Y: height
    private void resize(){
        packageButton.setLocation(mainStageWidth.doubleValue()/2-100-(30*ZOOMFACTOR),mainStageHeight.doubleValue()-200*ZOOMFACTOR);
        extractButton.setLocation(mainStageWidth.doubleValue()/2+(30*ZOOMFACTOR),mainStageHeight.doubleValue()-200*ZOOMFACTOR);
        uploadField.setLocation((mainStageWidth.doubleValue()-uploadField.getSize())/2,mainStageHeight.doubleValue()/2-uploadField.getSize()-100*ZOOMFACTOR);
        labelArea.setMinWidth(mainStageWidth.doubleValue());
        labelArea.setLayoutY(uploadField.getLayoutY()+uploadField.getSize()+50);
    }

    private void listener(){
        extractButton.setOnMouseEntered(event -> extractButton.setOver(1.0));
        extractButton.setOnMouseExited(event -> extractButton.setOver(0.8));
        extractButton.setOnMouseClicked(event -> execution.execute(address,false));

        packageButton.setOnMouseEntered(event -> packageButton.setOver(1.0));
        packageButton.setOnMouseExited(event -> packageButton.setOver(0.8));
        packageButton.setOnMouseClicked(event -> execution.execute(address,true));

        uploadField.setOnMouseEntered(event -> {uploadField.setExit(false);uploadField.onDrag(false);});
        uploadField.setOnMouseExited(event -> {uploadField.setExit(true);uploadField.onDrag(true);});
        uploadField.setOnDragOver(event -> {
            if (event.getGestureSource() != uploadField) {
                event.acceptTransferModes(TransferMode.ANY);
                labelArea.getChildren().clear();
            }
        });
        uploadField.setOnDragDropped(event -> {
            packageButton.setDisable(false);
            extractButton.setDisable(false);
            Dragboard db = event.getDragboard();
            address = db.getFiles().get(0).toString();
            String showAdd = "";
            for (int i = address.length()-1; i >=0 ; i--) {
                if(address.charAt(i)=='/')break;
                showAdd=address.charAt(i)+showAdd;
            }
            locationLabel.setText(showAdd);

            labelArea.add(locationLabel,0,0);
            if(!execution.autoRegconize(address)){
                errorLabel.setText("Cannot recognize file type, unable to auto execute.");
                labelArea.add(errorLabel,0,1);
                System.out.println("Cannot recognize!");
            }
        });


    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void update(String context) {
        labelArea.getChildren().remove(errorLabel);
        if(context.contains(":")){
            errorLabel.setText("Error"+context);
            labelArea.add(errorLabel,0,1);
        }
        else
            labelArea.add(successfulLabel,0,1);
        packageButton.setDisable(true);
        extractButton.setDisable(true);
    }
}
