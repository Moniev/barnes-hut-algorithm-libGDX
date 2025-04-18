package pl.moniev.core.Body;

import com.badlogic.gdx.graphics.g2d.Sprite;

import pl.moniev.core.Vector.Vector;

public class Body {
    public final int id;
    public Vector position, acceleration, velocity;
    public final Float mass;
    public final Float radius;
    public final Sprite sprite;


    public Body(int id, Vector position, Vector velocity, Float mass, Float radius, Sprite sprite) {
        this.id = id;
        this.position = position;
        this.acceleration = new Vector(0, 0);
        this.velocity = velocity;
        this.mass = mass;
        this.radius = radius;
        this.sprite = sprite;
    }


}
