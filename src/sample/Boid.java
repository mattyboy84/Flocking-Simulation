package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Boid {
    int WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
    int HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
    Timeline timeline;
    Vecc2f location;
    Vecc2f velocity = new Vecc2f();
    Vecc2f acceleration = new Vecc2f((float) (0), (float) 0);
    Circle circle;
    Random random = new Random();
    int r;
    float maxForce;
    float maxSpeed;

    public Boid(Group group, ArrayList<Boid> boids, Slider alignSlider, Slider separationSlider, Slider cohesionSlider) {
        this.maxSpeed = 4;
        this.maxForce = (float) 0.5;
        this.r = 8;
        this.circle = new Circle(this.r, Color.BLACK);
        this.location = new Vecc2f(random.nextInt(WIDTH - (this.r * 2)), random.nextInt(HEIGHT - (this.r * 2)));
        this.velocity.random2D(1);
        this.circle.relocate(location.x, location.y);
        //
        int FPS = 60;
        this.timeline = new Timeline((new KeyFrame(Duration.seconds((float) 1 / FPS), event -> {
            flock(boids,alignSlider,separationSlider,cohesionSlider);
            //
            this.location.add(this.velocity);
            //
            this.velocity.add(this.acceleration);
            this.velocity.limit(this.maxSpeed);
            this.acceleration.set(0,0);
            //
            this.circle.relocate(location.x, location.y);
            //
            /*
            if (this.location.y < 0 || (this.location.y + (2 * this.r)) > 1080) {
                this.velocity.y = (this.velocity.y * -1);
            }
            if (this.location.x < 0 || (this.location.x + (2 * this.r)) > 1920) {
                this.velocity.x = (this.velocity.x * -1);
            }
            */
            if (this.location.x < 0) {
                this.location.x = 1920;
            }
            if (this.location.x > 1920) {
                this.location.x = 0;
            }
            if (this.location.y < 0) {
                this.location.y = 1080;
            }
            if (this.location.y > 1080) {
                this.location.y = 0;
            }
            //
        })));
        this.timeline.setCycleCount(Timeline.INDEFINITE);
        this.timeline.play();
        group.getChildren().add(this.circle);
    }

    public Vecc2f align(ArrayList<Boid> boids, Slider alignSlider) {
        int perceptionRadius = (int) alignSlider.getValue();

        Vecc2f steering = new Vecc2f();
        int total = 0;
        for (Boid boid : boids) {
            if ((this.location.distance(boid.getLocation()) < perceptionRadius) && (this.circle != boid.getCircle())) {
                steering.add(boid.getVelocity());
                total++;
            }
        }
        if (total > 0) {
            steering.div(total);
            steering.setMag(this.maxSpeed);
            steering.sub(this.velocity);
            steering.limit(this.maxForce);
        }
        return steering;
    }

    public Vecc2f cohesion(ArrayList<Boid> boids, Slider cohesionSlider) {
        int perceptionRadius = (int) cohesionSlider.getValue();

        Vecc2f steering = new Vecc2f();
        int total = 0;
        for (Boid boid : boids) {
            if ((this.location.distance(boid.getLocation()) < perceptionRadius) && (this.circle != boid.getCircle())) {
                steering.add(boid.getLocation());
                total++;
            }
        }
        if (total > 0) {
            steering.div(total);
            steering.sub(this.location);
            steering.setMag(this.maxSpeed);
            steering.sub(this.velocity);
            steering.limit(this.maxForce);
        }
        return steering;
    }

    public Vecc2f seperation(ArrayList<Boid> boids, Slider separationSlider) {
        int perceptionRadius = (int) separationSlider.getValue();
        Vecc2f steering = new Vecc2f();
        int total = 0;
        for (Boid boid : boids) {
            float d = (this.location.distance(boid.getLocation()));
            if ((d < perceptionRadius) && (this.circle != boid.getCircle())) {
                Vecc2f difference = new Vecc2f().sub(this.location, boid.getLocation());
                //difference.sub(this.location,boids.get(i).getLocation());
                difference.div((d * d));
                steering.add(difference);
                total++;
            }
        }
        if (total > 0) {
            steering.div(total);
            steering.setMag(this.maxSpeed);
            steering.sub(this.velocity);
            steering.limit(this.maxForce);
        }
        return steering;
    }

    public void flock(ArrayList<Boid> boids, Slider alignSlider, Slider separationSlider, Slider cohesionSlider) {
        this.acceleration.set(0, 0);
        //
        Vecc2f alignment = align(boids,alignSlider);
        Vecc2f cohesion = cohesion(boids,cohesionSlider);
        Vecc2f separation = seperation(boids,separationSlider);
        //
        alignment.mult((float)1);
        cohesion.mult((float) 1.3);
        separation.mult((float) 1.6);
        //
        this.acceleration.add(alignment);
        this.acceleration.add(cohesion);
        this.acceleration.add(separation);
        //
    }

    public Vecc2f getLocation() {
        return location;
    }

    public Vecc2f getVelocity() {
        return velocity;
    }

    public Circle getCircle() {
        return circle;
    }
}
