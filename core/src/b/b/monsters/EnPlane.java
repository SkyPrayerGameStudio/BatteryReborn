package b.b.monsters;

import b.b.core.Config;
import b.b.core.Square;
import b.b.core.World;
import b.b.core.objs.Water;
import b.util.Utils;

public class EnPlane extends Monster {
    private double angle;

    public EnPlane(double x, double y, World world) {
        super(world, x, y, world.gfx.getSprite("enplane"),
                Config.Monsters.EnPlane.life * Config.Damages.bullet, ZLayer.FIVE);
        final double speed = Config.Monsters.EnPlane.speed;
        mover = new Mover(this, Config.Monsters.EnPlane.shiftSpeed, 0, speed);
        mover.setSpeed(speed, 2);
        angle = Utils.rnd(Utils.dpi);
    }

    protected void move() {
        angle = Utils.angle(angle + Config.Monsters.EnPlane.turnSpeed);
        if (angle < Math.PI) mover.move(3);
        else mover.move(1);
        mover.move(2);
        mover.move();
    }

    public void draw() {
        super.draw();
    }

    protected boolean onMonster(Monster m) {
        if (m instanceof Tank) return false;
        if (m instanceof EnPlane) {
            removeFrom(m);
            return true;
        }
        return super.onMonster(m);
    }

    protected boolean onSquare(Square o) {
        if (o instanceof Water) return false;
        removeFrom(o);
        return true;
    }
}
