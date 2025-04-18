package pl.moniev.core.MainEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import pl.moniev.core.Body.Body;
import pl.moniev.core.Vector.Vector;

public class Engine {
    public final int BODIESLIMIT;             // Maximum number of particles allowed in the simulation.
    public final int SIZE;                    // Size of the simulation space.
    public final int SUBSTEPS;                // Number of sub-steps for each simulation update.
    public int bodies;
    public int bodiesLimit;
    public float mTime;                       // Time elapsed in the simulation.
    public final float x, y;                  // Width and height of the simulation space
    public final Vector center;               // Center of the simulation at (x/2, y/2)
    public final Vector lowerPoint, upperPoint; 
    public QuadTree quadTree;
    public QuadTreeNode root;
    public Texture centralTexture;            // Texture for central body
    public Texture particleTexture;           // Texture for disc particles
    public SpriteBatch spriteBatch;

    public Engine(int bodies, int bodiesLimit, SpriteBatch spriteBatch, int size, int substeps, float x, float y) {
        this.x = x;
        this.y = y;
        this.bodies = bodies;
        this.BODIESLIMIT = bodiesLimit;
        this.SIZE = size;
        this.SUBSTEPS = substeps;
        this.center = new Vector(x / 2, y / 2); 
        this.lowerPoint = new Vector(0, 0);    
        this.upperPoint = new Vector(x, y);     
        this.root = new QuadTreeNode(bodiesLimit, lowerPoint, upperPoint);
        this.quadTree = new QuadTree(root);
        this.spriteBatch = spriteBatch;
        initializeTextures();
    }

    private void initializeTextures() {
        centralTexture = createCircularTexture(32, Color.YELLOW);
        particleTexture = createCircularTexture(16, Color.WHITE);
    }

    private Texture createCircularTexture(int size, Color color) {
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fillCircle(size / 2, size / 2, size / 2);
        Texture texture = new Texture(pixmap);
        pixmap.dispose(); 
        return texture;
    }

    public void update() {
        Float dt = 1f / SUBSTEPS;
        for (int i = 0; i < SUBSTEPS; i++) {
            quadTree.updateBodies(dt);
            quadTree.resolveCollisions();
            quadTree.updateGravity();
        }
    } 

    public void addBodies(List<Body> bodies) {
        for (Body body : bodies) {
            quadTree.addBody(body);
        }
    }

    public List<Body> createDisk() {
        Random random = new Random();
        random.setSeed(0);
    
        Float innerRadius = x / 2f; 
        Float outerRadius = (float) Math.sqrt(bodies);
    
        List<Body> disk = new ArrayList<>();
    
        Float centralMass = 1e4f;
        Sprite centralSprite = new Sprite(centralTexture);
        centralSprite.setSize(10.0f, 10.0f);
        centralSprite.setOriginCenter();
        centralSprite.setPosition(center.x, center.y);
        Body main = new Body(
            0, 
            new Vector(center),
            new Vector(0, 0),
            centralMass,
            5.0f,
            centralSprite
        );
        disk.add(main); 
    
        for (int i = 1; i < bodies; i++) {
            Vector position;
            double a;
            while (true) {
                a = random.nextDouble() * 2.0 * Math.PI;
                Float sinA = (float) Math.sin(a);
                Float cosA = (float) Math.cos(a);
    
                Float t = innerRadius / outerRadius;
                double r = random.nextDouble() * (1.0 - t * t) + t * t;
                double radiusPos = outerRadius * Math.sqrt(r);
    
                Float posX = center.x + cosA * (float) radiusPos;
                Float posY = center.y + sinA * (float) radiusPos;
    
                if (posX >= 0 && posX <= x && posY >= 0 && posY <= y) {
                    position = new Vector(posX, posY);
                    break;
                }
            }
    
            Vector velocity = new Vector((float) Math.sin(a), -(float) Math.cos(a));
            Float radius = (float) Math.pow(1f, 1f / 3);
    
            Sprite sprite = new Sprite(particleTexture);
            sprite.setSize(radius * 2.0f, radius * 2.0f); 
            sprite.setOriginCenter();
            sprite.setPosition(position.x - radius, position.y - radius);
    
            Body body = new Body(
                i,
                position,
                velocity,
                0.2f,
                radius,
                sprite
            );
            disk.add(body);
        }
    
        Float totalMass = 0f; 
        for (Body body : disk) {
            totalMass += body.mass;
            if (body.position.x == center.x && body.position.y == center.y) {
                continue;
            } 
            
            Float magnitude = body.position.distance(center);
            Float v = (float)Math.sqrt(totalMass / magnitude);
            body.velocity = body.velocity.multiply(v);
        }
    
        return disk;
    }

    public void renderBodies() {
        quadTree.renderBodies(spriteBatch);
    }

    public void renderTree(ShapeRenderer shapeRenderer) {
        quadTree.renderTree(shapeRenderer);
    }

    public void dispose() {
        if (centralTexture != null) centralTexture.dispose();
        if (particleTexture != null) particleTexture.dispose();
    }
}