package b.b.monsters;

import b.b.core.Config;
import b.b.core.Square;
import b.b.core.World;
import b.b.gfx.Gfx;
import b.gfx.Sprite;

public class Tank extends Monster {
    protected TankAI ai;
    protected Gun gun;

    private TankGfx tankGfx;
    private Gfx gfx;

    public Tank(double x, double y, World world, int direction) {
        super(world, x, y, world.gfx.getSprite("tank_base"),
                Config.Monsters.Tank.life * Config.Damages.bullet, ZLayer.FOUR);
        ai = new TankAI(this, world, direction);
        Sprite caterpillar = world.gfx.getSprite("caterpillar");
        gun = new Gun(this, world, 1, Config.Monsters.Tank.bulletSpeed,
                null);
        tankGfx = new TankGfx(this, caterpillar);
        gfx = world.gfx;
    }

    protected void move() {
        ai.move();
    }

    public void draw() {
        tankGfx.draw(gfx.battery.world);
    }

    protected boolean onSquare(Square square) {
        removeFrom(square);
        ai.direction = TankAI.opposite(ai.direction);
        return true;
    }

    protected boolean onMonster(Monster monster) {
        if ((monster instanceof Helicopter) || (monster instanceof EnPlane)) {
            return false;
        }
        if (!(monster instanceof Tank)) {
            return super.onMonster(monster);
        }
        removeFrom(monster);
        ai.direction = TankAI.opposite(ai.direction);
        return true;
    }
}
