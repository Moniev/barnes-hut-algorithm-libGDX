package pl.moniev.core.MainEngine;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import pl.moniev.core.Body.Body;
import pl.moniev.core.Vector.Vector;


public class QuadTree {

    public final QuadTreeNode root;
    public List<Body> bodies;

    public QuadTree(QuadTreeNode root) {
        this.root = root;
        this.bodies = new ArrayList<>();
    }

    public void addBody(Body body) {
        root.addBody(body);
        bodies.add(body);
    }

    public void updateBodies(Float dt) {
       bodies.parallelStream().forEach(body -> {
            synchronized (body) { 
                body.velocity = body.velocity.add(body.acceleration.multiply(dt));
                body.position = body.position.add(body.velocity.multiply(dt));
                body.acceleration = new Vector(0, 0);
            }
        });
    }

    public void updateGravity() { 
        rebuild();

        bodies.parallelStream().forEach(body -> {
            root.accelerate(root, body, 1f, 10f);
        });
    }

    public void resolveCollisions() {
        root.resolveCollisions();
    }

    public void rebuild() {
        root.clear();
        bodies.parallelStream().forEach(body -> {
        synchronized (root) {
            root.addBody(body, true);
        }
       });
       root.propagate();
    }

    public void renderBodies(SpriteBatch spriteBatch) {
        for (Body body : bodies) {
            body.sprite.setPosition(body.position.x - body.radius, body.position.y - body.radius);
            body.sprite.draw(spriteBatch);
        }
    }

    public void renderTree(ShapeRenderer shapeRenderer) {
        root.renderTree(root, shapeRenderer);
    }
}

