package b.b.monsters.items;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

import b.b.core.World;
import b.b.monsters.Monster;

public class Explosion extends Item implements DrawableLibGDX {
    private static final double duration = 370;
    private static final int count = 30;

    private List<ExplosionParticle> list;
    private double time;
    protected int secondaryExplosions;

    public Explosion(double x, double y, World world, Monster monster) {
        this(x, y, world, monster, 1);
    }

    public Explosion(double x, double y, World world, Monster monster, int secondaryExplosions) {
        this(x, y, world, monster.getSpeed(), secondaryExplosions);
    }

    public Explosion(double x, double y, World world, Vector2 initialSpeed, int secondaryExplosions) {
        super(world, x, y, world.gfx.getSprite("expl"), ZLayer.SEVEN);
        this.secondaryExplosions = secondaryExplosions - 1;
        list = new ArrayList<ExplosionParticle>();
        double xSpeed = initialSpeed.x;
        double ySpeed = initialSpeed.y;
        for (int i = 0; i < count; i++) {
            list.add(new ExplosionParticle((float) x, (float) y, world, (float) xSpeed, (float) ySpeed, this));
        }
        time = time();
        setWH(1, 1);
    }

    public void draw() {
    }

    protected void move() {
        for (ExplosionParticle e : list) {
            e.move();
        }
        if (time + duration < time()) {
            dmg(1, null);
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        for (DrawableLibGDX e : list) {
            e.draw(batch);
        }
    }
}
