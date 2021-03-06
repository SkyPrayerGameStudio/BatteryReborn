package b.b.monsters;

import b.b.core.Config;
import b.b.core.World;

public class Gun {
    private Monster mon;
    private World world;
    protected double lastShotTime;
    private double shotInterval;
    private double bulletSpeed;
    /* instance that has immune (usualy shooter) */
    private Object owner;
    public double xShift;
    public double yShift;

    public Gun(Monster m, World w, double shotInterval, double bulletSpeed,
               Object owner) {
        xShift = 0;
        yShift = 0;
        mon = m;
        world = w;
        lastShotTime = -9999999;
        this.shotInterval = shotInterval;
        this.bulletSpeed = bulletSpeed;
        this.owner = owner;
    }

    public final void shoot(int dir) {
        if (mon.time() - lastShotTime > shotInterval) {
            double x = mon.x + xShift;
            double y = mon.y + yShift;
            double shift = Config.Monsters.Bullet.startShift +
                    world.gfx.getSprite("bullet0").halfHeight;
            if (dir == 0) {
                y = mon.yStart() - shift;
            } else if (dir == 1) {
                x = mon.xEnd() + shift;
            } else if (dir == 2) {
                y = mon.yEnd() + shift;
            } else {
                x = mon.xStart() - shift;
            }
            Bullet bullet = new Bullet(bulletSpeed, x, y, dir, world, owner);
            world.objectsToAdd.add(bullet);
            lastShotTime = mon.time();
        }
    }
}
