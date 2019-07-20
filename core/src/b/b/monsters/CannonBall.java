package b.b.monsters;

import b.b.core.*;
import b.b.core.objs.*;
import b.b.monsters.items.*;
import b.gfx.*;

public class CannonBall extends Monster {
  private int dir;

  public CannonBall(double x, double y, int dir, World world,
      Screen screen) {
    super(world, x, y, world.g.getSprite("cannon_ball"), 0.000001);
    lvl=2;
    this.dir=dir;
    double speed=Config.Monsters.Cannon.ballSpeed;
    mover=new Mover(this, speed, speed, speed);
    mover.setSpeed(speed, dir);
  }

  public void draw() {
    world.g.b.drawTranspRangeCheck(sprite, xScreenStart(), yScreenStart());
  }

  protected void move() {
    mover.move(dir);
    mover.move();
  }

  protected void justDied() {
    world.removeFromMap(this);
    world.objsToRemove.add(this);
  }

  protected boolean onSquare(Square o) {
    if (o instanceof Water || o instanceof Cannon) {
      return false;
    }
    dmg(1, false);
    return false;
  }

  protected boolean onBullet(Bullet b) {
    dmg(1, true);
    b.dmg(1, true);
    return false;
  }

  protected boolean onMonster(Monster m) {
    dmg(1, true);
    m.dmg(Config.Damages.cannonBall, true);
    return false;
  }

  protected boolean onItem(Item i) {
    return false;
  }

  protected boolean checkScreenCollision() {
    if (outOfScreen()) dmg(1, false);
    return false;
  }
}