package b.b.monsters;

import b.b.core.Config;
import b.b.core.Square;
import b.b.core.World;
import b.b.core.objs.Water;
import b.gfx.Sprite;
import b.util.U77;

public class Heli extends Monster {
    /**
     * Direction: 0-up, 1-right, 2-down, 3-left
     */
    private int dir;
    private Sprite flipped;

    public Heli(double x, double y, World world, int direction) {
        super(world, x, y, world.g.getSprite("heli"),
                Config.Monsters.Heli.life * Config.Damages.bullet);
        lvl = 5;
        dir = direction;
        flipped = world.g.getSprite("heliLeft");
        final double speed = Config.Monsters.Heli.maxSpeed;
        mover = new Mover(this, speed, 0, 0);
        mover.setSpeed(speed, dir);
    }

    protected void move() {
        mover.move(dir);
        mover.move();
    }

    public void draw() {
        super.draw();
        Sprite blades = world.g.getSprite("heli_blades" +
                U77.rem((int) (world.g.battery.time.time *
                        Config.Monsters.Heli.bladesK), 4));
        int shift = (int) blades.hh;
        if (dir == 3) {
            world.g.bufGfx.drawTranspTrRangeCheck(blades, xScreenStart(),
                    yScreenStart(), 0.7);
        } else {
            world.g.bufGfx.drawTranspTrRangeCheck(blades, xScreenBorder() - blades.w,
                    yScreenStart(), 0.7);
        }
    }

    protected boolean onSquare(Square o) {
        if (o instanceof Water) return false;
        removeFrom(o);
        mover.stop();
        if (dir == 1) dir = 3;
        else dir = 1;
        Sprite s = sprite;
        sprite = flipped;
        flipped = s;
        return true;
    }

    protected boolean checkScreenCollision() {
        if (super.checkScreenCollision()) return true;
        if (xEnd() < 0) {
            if (dir != 1) {
                world.objsToRemove.add(this);
            }
        } else if (xStart() > screen.xEnd()) {
            if (dir != 3) {
                world.objsToRemove.add(this);
            }
        }
        return false;
    }

    protected boolean onMonster(Monster m) {
        if (m instanceof Tank) return false;
        if (!(m instanceof Heli)) return super.onMonster(m);
        removeFrom(m);
        mover.stop();
        m.mover.stop();
        if (dir == 1) dir = 3;
        else dir = 1;
        Sprite s = sprite;
        sprite = flipped;
        flipped = s;
        return true;
    }
}
