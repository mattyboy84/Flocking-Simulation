package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Main extends Application {
    //https://natureofcode.com/book/chapter-6-autonomous-agents/
    //https://www.red3d.com/cwr/boids/


ArrayList<Boid> boids = new ArrayList<>();
    Group group = new Group();
    Scene scene = new Scene(group, 1920, 1080);


    @Override
    public void start(Stage stage) {
        int n=150;
        Slider alignSlider =new Slider(0,1000,150);
        alignSlider.relocate(100,100);
        Slider cohesionSlider =new Slider(0,1000,100);
        cohesionSlider.relocate(100,200);
        Slider separationSlider =new Slider(0,1000,100);
        separationSlider.relocate(100,300);
        group.getChildren().addAll(alignSlider,separationSlider,cohesionSlider);
        //
        for (int i = 0; i <n ; i++) {
            boids.add(new Boid(group,boids,alignSlider,separationSlider,cohesionSlider));
        }
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()){
                case R:
                    for (Boid boid : boids) {
                        group.getChildren().remove(boid.getCircle());
                    }
                    boids.clear();
                    for (int i = 0; i <n ; i++) {
                        boids.add(new Boid(group,boids,alignSlider,separationSlider,cohesionSlider));
                    }
                    break;
            }
        });
        //
        stage.setFullScreen(true);
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
