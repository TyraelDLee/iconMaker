package defaultPackage.gui;

import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class UploadField extends StackPane {
    private Rectangle hr = new Rectangle();
    private Rectangle vt = new Rectangle();
    private double size = 50;

    UploadField(){
        this.hr.setHeight(2);
        this.vt.setHeight(2);
        this.hr.setRotate(90);

        this.getChildren().addAll(hr,vt);
    }

    UploadField(double size){
        this.setAlignment(Pos.CENTER);
        this.hr.setWidth(size-15);
        this.hr.setHeight(3);
        this.vt.setWidth(3);
        this.vt.setHeight(size-15);
        this.hr.setFill(new Color(.32,.75,.89,1));
        this.vt.setFill(new Color(.32,.75,.89,1));

        this.size = size;
        this.getChildren().addAll(hr,vt);
    }

    /**
     * Set the Button location by taken X and Y.
     * The anchor at top left.
     *
     * @param X the point at x-axis
     * @param Y the point at y-axis
     * */
    void setLocation(double X, double Y) {
        this.setLayoutX(X);
        this.setLayoutY(Y);
    }

    void setSize(double size){
        this.size = size;
        this.hr.setWidth(size);
        this.hr.setHeight(2);
        this.vt.setWidth(2);
        this.vt.setHeight(size);
    }

    public double getSize(){
        return this.size;
    }

    void onDrag(boolean exit){
//        RotateTransition rt = new RotateTransition(Duration.millis(1000), arc);
//        rt.setFromAngle(0);
//        rt.setToAngle(360);
//        rt.setAutoReverse(false);
//        rt.setCycleCount(Timeline.INDEFINITE);
//        if(exit)rt.pause();
//        else rt.play();
    }
}
