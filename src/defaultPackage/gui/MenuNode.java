package defaultPackage.gui;

import javafx.geometry.HPos;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;



public class MenuNode extends StackPane {
    private double width, height;
    private ShowPane root = new ShowPane();
    private GridPane iconroot = new GridPane();
    private int[] sizes = new int[]{16,32,128,256,512};

    class Icon extends GridPane {
        private int size = 50;
        private boolean x2b;
        private Label showText = new Label();
        private String sizeText = "";
        private String text = "NxN";
        private String x2 = "@2x";
        private Canvas icon = new Canvas(size,size);
        private GraphicsContext gc = icon.getGraphicsContext2D();

        Icon(){
            this.x2b = false;
            render();
        }

        Icon(int size, String sizeText, boolean x2){
            this.size = size;
            this.x2b = x2;
            this.sizeText = sizeText;
            this.setHgap(5);
            this.icon.setWidth(size);
            this.icon.setHeight(size);
            render();
            GridPane.setHalignment(icon, HPos.CENTER);
            GridPane.setHalignment(showText, HPos.CENTER);
        }

        private void render(){
            gc.setStroke(new Color(.5,.5,.5,1));
            gc.setLineWidth(5);
            gc.strokeRoundRect(5,5,size-10,size-10,10,10);
            gc.strokeLine((icon.getHeight())/2,size/2f-30,(icon.getHeight())/2,size/2f+30);
            gc.strokeLine(size/2f-30,(icon.getHeight())/2, size/2f+30 ,(icon.getHeight())/2);
            showText.setText(text.replace("N",this.sizeText)+(x2b?x2:""));
            this.add(icon,0,0);
            this.add(showText,0,1);
        }
    }

    MenuNode(){}

    MenuNode(double width, double height){
        this.setBackground(new Background(new BackgroundFill( Color.rgb(0,0,0),null,null)));
        resize(width,height);
        init();
    }

    private void init(){
        this.root.setTheme(ShowPane.TRANSPARENT);

        this.iconroot.setVgap(20);
        this.iconroot.setHgap(25);
        int i = 0;
        for(int size : sizes){
            iconroot.add(new Icon(100,size+"",false),0,i);
            iconroot.add(new Icon(100,size+"",true),1,i);
            i++;
        }
        this.root.setContent(iconroot);
        this.getChildren().addAll(this.root);
    }

    public void resize(double width, double height){
        this.width = width;
        this.height = height;
        //this.setMinSize(width,height);
        this.root.setSize(width, height);
    }
}
