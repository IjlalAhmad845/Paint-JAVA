package sample;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML AnchorPane anchorPane;//Main paint area
    @FXML Pane pane,pane2;//panes for color and polygon preview
    @FXML Label coordonatelabel,anglelabel;//for realtime mouse coordinates and angle of polygons
    @FXML Button clearbtn,undobtn;//clear and undo buttons
    @FXML ToggleButton linebtn,polybtn,circlebtn;//selection buttons between line,polygon and circle
    @FXML ToggleButton cyan,teal,lime,pink,red,yellow,olive,green,blue,black;//color palette
    @FXML Slider strokeslider,angleslider;//width and angle sliders
    @FXML Line strokeview;//for preview of width of line strokes
    @FXML ColorPicker colorpicker;
    @FXML Spinner<Integer> spinner;//for changing sides of polygons



    int staticX,staticY;//for storing initial mouse coordinates
    boolean counter=true;//counter to do so
    int i=0,j=0,k=0,l=0;//main variables for the increment of arraylists (for jumping to next index)
    int strokewidth=1;//initial stroke width
    String color="#000000";//initial color BLACK
    int angle=0;//initial angle 0

    double pi=3.14;
    int polygonside=0;//iterator for storing coordinates of vertex of polygons

    ArrayList<Line> linelist =new ArrayList<>();//dynamic list for lines
    ArrayList<Ellipse> circlelist=new ArrayList<>();//dynamic list for circles
    ArrayList<Polygon> polylist=new ArrayList<>();//dynamic list for polygons

    ArrayList<String> memory=new ArrayList<>();//dynamic list for determining the sequence of lines,circles and polygons

    ArrayList<Double> points=new ArrayList<>();//dynamic list for storing vertices of varying sides of polygons


    public void mouseevent(MouseEvent event){//THIS IS THE MAIN METHOD CALLED WHEN MOUSE IS HOVERING OVER PAINT AREA
    //CONDITIONS FOR MAKING SURE THAT LEFT CLICK IS CLICKED AND WHICH TOGGLE IS SELECTED
        if(event.getButton().toString().equals("PRIMARY") && linebtn.isSelected()){//lines are selected
            if(counter==true) {//for storing initial coordinates of mouse ,THIS RUNS ONLY ONE TIME
                staticX = (int) event.getX();
                staticY = (int) event.getY();
                counter = false;//to make this condition run only very fisrt time
                Line line = new Line();//new line is created
                linelist.add(line);//line is added to the list
            }

            //now basic line coordinates are defined within the line list by using "i" as iterator

            linelist.get(i).setStartX(staticX); linelist.get(i).setStartY(staticY);
            linelist.get(i).setEndX(event.getX()); linelist.get(i).setEndY(event.getY());

            //some more properties of lines
            linelist.get(i).setStrokeWidth(strokewidth);
            linelist.get(i).setStrokeLineCap(StrokeLineCap.ROUND);
            linelist.get(i).setStroke(Paint.valueOf(color));
            linelist.get(i).setSmooth(true);

            //THE FOLLOWING SYNTAX IS REMOVING AND ADDING LINES IN REAL TIME AND USER CAN DEFINE WHERE HE WANTS THE LINE
            anchorPane.getChildren().removeAll(linelist.get(i));//removing all the previous lines from paint area
            anchorPane.getChildren().addAll(linelist.get(i));// adding new lines
        }


        if(event.getButton().toString().equals("PRIMARY") && circlebtn.isSelected()){//circles are selected
            if(counter==true){//same concept as above
                staticX= (int) event.getX();
                staticY= (int) event.getY();
                counter=false;
                Ellipse ellipse=new Ellipse();//creating new ellipse
                circlelist.add(ellipse);
            }
                //now basic Circle coordinates are defined within the circle list by using "j" as iterator

            if(event.getX()>=staticX)//condition for checking mouse is going right or left
                //circle will be created right side of initial mouse coordinates
                    circlelist.get(j).setCenterX(staticX+Math.abs(event.getX()-staticX)/2);
            ////circle will be created left side of initial mouse coordinates
            else    circlelist.get(j).setCenterX(staticX-Math.abs(event.getX()-staticX)/2);

            if(event.getY()>=staticY)//condition for checking mouse is going upward or downward
                //circle will be created downward side of initial mouse coordinates
                    circlelist.get(j).setCenterY(staticY+Math.abs(event.getY()-staticY)/2);
                //circle will be created upward side of initial mouse coordinates
            else    circlelist.get(j).setCenterY(staticY-Math.abs(event.getY()-staticY)/2);

            //setting X radius and Y radius (non negative value between initial and final coordinates of the mouse)
            circlelist.get(j).setRadiusX(Math.abs(event.getX()-staticX)/2);
            circlelist.get(j).setRadiusY(Math.abs(event.getY()-staticY)/2);

            //some more properties of circle
            circlelist.get(j).setFill(Color.TRANSPARENT);
            circlelist.get(j).setStrokeWidth(strokewidth);
            circlelist.get(j).setStroke(Paint.valueOf(color));
            circlelist.get(j).setSmooth(true);

            //THE FOLLOWING SYNTAX IS REMOVING AND ADDING CIRCLE IN REAL TIME AND USER CAN DEFINE WHERE HE WANTS THE CIRCLE
            anchorPane.getChildren().removeAll(circlelist.get(j));//removing all the previous circles from paint area
            anchorPane.getChildren().addAll(circlelist.get(j));// adding new circles
        }


        if(event.getButton().toString().equals("PRIMARY") && polybtn.isSelected()){//polygons are selected
            if(counter==true){//same concept as above
                staticX= (int) event.getX();
                staticY= (int) event.getY();
                counter=false;
                Polygon polygon=new Polygon(); //new polygon is created
                
                polylist.add(polygon);//added to polygon list
            }

            //now basic polygon coordinates are defined within the polygon list by using "k" as iterator

            if(event.getX()>=staticX)//same concept as above for checking mouse final location w.r.t. initial location
                polylist.get(k).setLayoutX(staticX+Math.abs(event.getX()-staticX)/2);
            else    polylist.get(k).setLayoutX(staticX-Math.abs(event.getX()-staticX)/2);

            if(event.getY()>=staticY)
                polylist.get(k).setLayoutY(staticY+Math.abs(event.getY()-staticY)/2);
            else    polylist.get(k).setLayoutY(staticY-Math.abs(event.getY()-staticY)/2);
            
            points.clear();//making sure points list is empty
            polygonside=0;//making sure iterator is 0 for storing coordinates of vertices of polygons

            /* HERE IS THE MAGIC FOR CREATING N-SIDED POLYGON
             * Iterating loop between 0 to 360 for circle
             * and extracting polygons vertices from circles BY USING PARAMETRIC (TRIGONOMETRIC) CIRCLE EQUATION
             * because all the regular polygons are CON-CYCLIC
             * sine and cosine will gave us desired coordinates
             * just stop the loop where the polygon vertices lies and fill the points list by using "polygonside" as iterator
             */
            for(int m=0;m<360;m++){
                //extracting values at desired locations set by spinner ,cutting 360 in equal (spinner value) parts
                //modulo for checking when remainder is 0 then 360 will cut
                if(m%(360/spinner.getValue())==0){//e.g.- for rectangle ,360 is cut into 4 parts of 90 degrees (set by spinner)
                    //Setting x coordinate using sine and rotated the polygon w.r.t. spinner so that base will be horizontal
                    //and again rotated by user defined angle (ALL ANGLES ARE CONVERTED IN RADIANS)
                    //multiplying non-negative value in between initial and final mouse coordinates for scaling the polygon
                    points.add(polygonside,(Math.abs(event.getX()-staticX)/2)*Math.sin(m*pi/180+pi/spinner.getValue()+angle*pi/180));
                    //Setting y coordinate using cosine and rest is same as above
                    points.add(polygonside+1,(Math.abs(event.getY()-staticY)/2)*Math.cos(m*pi/180+pi/spinner.getValue()+angle*pi/180));
                    polygonside+=2;//increment 2 for both next x and y coordinates
                }

            }

            polylist.get(k).getPoints().setAll(points);//setting points to polygon
            points.clear();//clearing the points list after use

            //some more properties for polygons
            polylist.get(k).setFill(Color.TRANSPARENT);
            polylist.get(k).setStrokeWidth(strokewidth);
            polylist.get(k).setStroke(Paint.valueOf(color));
            polylist.get(k).setStrokeType(StrokeType.OUTSIDE);
            polylist.get(k).setStrokeLineJoin(StrokeLineJoin.ROUND);
            polylist.get(k).setSmooth(true);

            //THE FOLLOWING SYNTAX IS REMOVING AND ADDING POLYGONS IN REAL TIME AND USER CAN DEFINE WHERE HE WANTS THE POLYGON
            anchorPane.getChildren().removeAll(polylist.get(k));//removing all the previous polygons from paint area
            anchorPane.getChildren().addAll(polylist.get(k));// adding new polygons
        }



        coordonatelabel.setText(event.getX()+","+event.getY());//showing real time mouse coordinates

    }
    //method for increment all the list iterators after mouse is released or  successful print of a line,circle and polygon
    public void mousereleased(MouseEvent event){
        if(event.getButton().toString().equals("PRIMARY") && linebtn.isSelected()){//when line is printed
            i++;l++;//HERE L UNIVERSAL ITERATOR FOR NET INCREMENT OF ITERATORS
            counter=true;//resetting counter for next line
            memory.add("i");//memory for determining the sequence of lines,circles and polygons

        }
        else if(event.getButton().toString().equals("PRIMARY") && circlebtn.isSelected()){//when circle is printed
            j++;l++;//HERE L UNIVERSAL ITERATOR FOR NET INCREMENT OF ITERATORS
            counter=true;//resetting counter for next circle
            memory.add("j");

        }
        else if(event.getButton().toString().equals("PRIMARY") && polybtn.isSelected()){//when polygon is printed
            k++;l++;//HERE L UNIVERSAL ITERATOR FOR NET INCREMENT OF ITERATORS
            counter=true;//resetting counter for next polygon
            memory.add("k");

        }
    }

    public void activatespinner(){//THIS METHOD IS FOR TOGGLE BUTTONS (LINES, CIRCLES AND POLYGONS)
if(polybtn.isSelected()){//when polygon is selected
    setPane2();//calling method for initial polygon figure
    spinner.setDisable(false);//all these tools are disabled at the start of the program so enabling them here
    angleslider.setDisable(false);//angle slider for setting user defined angle of polygons
    anglelabel.setDisable(false);//displaying the angle set by user
}
 else if(linebtn.isSelected()){//when line is selected
    Line line=new Line();//created new line for the preview at pane2
    line.setStartX(10);line.setStartY(10);//setting basic line coordinates
    line.setEndX(65);line.setEndY(48);
    line.setStrokeWidth(2);
    line.setSmooth(true);
    //displaying line at pane2
    pane2.getChildren().clear();//but first clearing it
    pane2.getChildren().addAll(line);
}
 else if(circlebtn.isSelected()){//when circle is selected
  Circle circle=new Circle();//created new circle for the preview at pane2
    circle.setCenterX(37.5);circle.setCenterY(29);//setting basic circle coordinates
    circle.setRadius(20);
    circle.setFill(Color.TRANSPARENT);//basic properties of circle
    circle.setStroke(Color.BLACK);
    circle.setStrokeWidth(2);
    circle.setSmooth(true);

    pane2.getChildren().clear();//same thing as above
    pane2.getChildren().addAll(circle);
}

 if(!polybtn.isSelected()){//when polygon is not selected then again disabling all the polygon tools
    spinner.setDisable(true);
    angleslider.setDisable(true);
    anglelabel.setDisable(true);

}

    }
    public void setPane2() {//THIS IS FOR THE PREVIEW OF POLYGONS

        Polygon polygon=new Polygon();//created new polygons

        polygon.setLayoutX(37.5);polygon.setLayoutY(29);//basic properties of polygons
        polygon.setStrokeLineCap(StrokeLineCap.ROUND);
        polygon.setFill(Color.TRANSPARENT);
        polygon.setStroke(Color.BLACK);
        polygon.setStrokeWidth(2);
        polygon.setSmooth(true);
        //setting angle defined by user (here is the other way because polygon here is only for preview not for printing)
        polygon.setRotate(angle);


        points.clear();//making sure that points list is empty
        polygonside=0;
        for(int m=0;m<360;m++){//SAME CONCEPT FOR CREATING POLYGONS FROM CIRCLES
            if(m%(360/spinner.getValue())==0){
                points.add(polygonside,20*Math.sin(m*pi/180+pi/spinner.getValue()));//here radius is fixed because
                points.add(polygonside+1,20*Math.cos(m*pi/180+pi/spinner.getValue()));//here is only for preview
                polygonside+=2;//incremented 2 for next both x and y coordinates
            }

        }

        polygon.getPoints().setAll(points);//setting points to polygon
        pane2.getChildren().clear();//clearing the preview window
        pane2.getChildren().setAll(polygon);//setting the new polygon for preview

    }

    public void setAngle() {//THIS METHOD IS FOR CUSTOM USER DEFINED ANGLE FOR POLYGONS
        //here slider value is divided for creating discrete steps by converting DOUBLE value to int
        angle= (int) angleslider.getValue()/15;
        angle*=15;//and again multiplied to recover the original value with discrete steps
        anglelabel.setText(String.valueOf(angle));//setting label for showing the angle defined by user
        setPane2();//updating pane2 as the angle changes

    }

    public void setStrokeslider(){//THIS METHOD IS FOR SETTING STROKE WIDTH DEFINED BY USER

      strokeview.setStrokeWidth(1+strokeslider.getValue());//getting width and setting to preview
      strokewidth=1+(int)strokeslider.getValue();//setting value to the variable
    }

    public void setColorpicker(){//THIS METHOD IS FOR CHOOSING DIFF TYPES OF COLORS FROM THE COLOR PALETTE
        if(colorpicker.isFocused())color=colorpicker.getValue().toString();//custom color from palette
        //otherwise choosing from program color palette and setting the same value to the custom color palette
        else if(cyan    .isSelected()){color=String.valueOf(Color.CYAN);    colorpicker.setValue(Color.CYAN);}
        else if(teal    .isSelected()){color=String.valueOf(Color.TEAL);    colorpicker.setValue(Color.TEAL);}
        else if(lime    .isSelected()){color=String.valueOf(Color.LIME);    colorpicker.setValue(Color.LIME);}
        else if(pink    .isSelected()){color=String.valueOf(Color.MAGENTA); colorpicker.setValue(Color.MAGENTA);}
        else if(red     .isSelected()){color=String.valueOf(Color.RED);     colorpicker.setValue(Color.RED);}
        else if(yellow  .isSelected()){color=String.valueOf(Color.YELLOW);  colorpicker.setValue(Color.YELLOW);}
        else if(olive   .isSelected()){color=String.valueOf(Color.OLIVE);   colorpicker.setValue(Color.OLIVE);}
        else if(green   .isSelected()){color=String.valueOf(Color.GREEN);   colorpicker.setValue(Color.GREEN);}
        else if(blue    .isSelected()){color=String.valueOf(Color.BLUE);    colorpicker.setValue(Color.BLUE);}
        else if(black   .isSelected()){color=String.valueOf(Color.BLACK);   colorpicker.setValue(Color.BLACK);}

        //showing the chosen color at the preview window (pane1)
        pane.setBackground(new Background(new BackgroundFill(Paint.valueOf(color), new CornerRadii(5), null)));

    }

    public void setClearbtn(){//FOR CLEARING EVERYTHING FROM THE SCREEN
        anchorPane.getChildren().removeAll(linelist);
        anchorPane.getChildren().removeAll(circlelist);
        anchorPane.getChildren().removeAll(polylist);
        linelist.clear();//setting al the values to default
        circlelist.clear();
        polylist.clear();
        memory.clear();
        i=j=k=l=0;
    }



    public void setUndobtn() {//UNDO BUTTON (MIND BENDING)
            //HERE IS THE USE OF UNIVERSAL ITERATOR FOR UNDO ALL THE THINGS WHICH COME NEWEST
        if(l>0){//only when something is printed
            //checking the last index of memory for what is printed lastly and removing it from its respective list
           if(memory.get(l-1).equals("i")){linelist.remove(linelist.size()-1);i--;}//reverting all the iterators
           else if(memory.get(l-1).equals("j")){circlelist.remove(circlelist.size()-1);j--;}//to their respective lists
           else if(memory.get(l-1).equals("k")){polylist.remove(polylist.size()-1);k--;}
           //also removing it from the memory
           memory.remove(l-1);

           //ABOVE MENTION CODE IS NOT SO IMPORTANT FOR UNDO THINGS FROM THE SCREEN ,THAT CODE IS JUST FOR CLEARING THE MEMORY
            //ASSIGNED TO THEIR RESPECTIVE LISTS
            //HERE IT IS REMOVED FROM THE SCREEN BY REMOVING LAST PRINT
           anchorPane.getChildren().remove(l);
            l--;//reverting back the universal iterator for further undoing(if user wanted to do so)
            //and then the next last printed figure will be removed from the screen

        }

    }


    //THIS METHOD IS CALLED AT THE EXECUTION OF THE PROGRAM TO SET INITIAL THINGS TO THE PROGRAM
    @Override
    public void initialize(URL location, ResourceBundle resources) {
colorpicker.setValue(Color.BLACK);//setting black color to the colorpicker
strokeview.setStrokeLineCap(StrokeLineCap.ROUND);//setting rounded cap to the preview stroke tool

        //setting initial black color to the preview window of chosen color
        pane.setBackground(new Background(new BackgroundFill(Paint.valueOf("#000000"), new CornerRadii(5), null)));
        //seitting limits to the spinner for the polygons
      spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(3,100,4));
        activatespinner();//calling the function to print initial preview of line at pane2
    }
}
