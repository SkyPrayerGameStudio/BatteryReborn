/* refactoring0 */
package b.b.core.loader;

import java.util.*;
import b.b.*;
import b.b.monsters.*;
import b.b.core.*;
import b.b.core.objs.*;
import b.b.gfx.*;
import b.b.monsters.bosses.*;
import b.b.monsters.items.*;
import b.gfx.*;
import b.util.*;

public class WorldLoader {
  private Battery btr;
  int lvl;
  private Gfx g;
  private World world;
  private int w;
  private int h;
  private WorldSquare[][] map;

  /**
   * @param lvl - number of map
   */
  public WorldLoader(World world, boolean oldPlayer) {
    lvl=world.trueLevel();
    this.world=world;
    g=world.g;
    btr=g.btr;
    loadPart1(oldPlayer);
    loadMain();
    specialMonsters();
    new BrickManager(map, world, g.getSprite("lvl"+lvl)).prepare();
    btr.timeWhenLevelLoaded=btr.time.time;
    btr.timeWhenLevelCompleted=0;
    U77.dropRandom();
    btr.kbd.clear(btr);
    btr.logger.log("lvl "+lvl);
    System.gc();
  }

  private void loadPart1(boolean oldPlayer) {
    Sprite sprite=g.getSprite("lvl"+lvl);
    w=world.width=sprite.w;
    h=world.height=sprite.h;
    map=new WorldSquare[h][w];
    for (int y=0; y<h; y++) {
      for (int x=0; x<w; x++) {
        map[y][x] = new WorldSquare(x, y, btr);
      }
    }
    world.setMap(map);
    world.objsToAdd=new ArrayList<Monster>();
    world.activeObjs=new ArrayList<Monster>();
    world.objsToRemove=new ArrayList<Monster>();
    world.objsToAddInTime=new ArrayList<Monster>();
    world.notMonsters = new ArrayList<ChanSquare>();
    if (btr.player==null || btr.player.life<=0) {
      Screen scr=btr.screen;
      int[] buf=scr.b;
      scr.init(scr.w, scr.h);
      scr.b=buf;
      if (!oldPlayer) {
        g.btr.player=new Player(btr.kbd, world, 0, 3, 0, new PlayerExtras());
        g.btr.logger.log("newgame "+U77.sprecision(g.btr.time.time));
      } else {
        Player p = g.btr.player;
        g.btr.player=new Player(btr.kbd, world, p.getScores(), p.lifes,
            p.getCoins(), p.extras);
        g.btr.logger.log("newlife "+U77.sprecision(g.btr.time.time));
      }
    }
  }

  private void loadMain() {
    Random random = new Random(77);
    int offset=0;
    Sprite sprite=g.getSprite("lvl"+lvl);
    for (int y=0; y<h; y++) {
      for (int x=0; x<w; x++) {
        int c=sprite.b[offset++];
        List<Drawable> list = map[y][x].objs;
        if (c==0xff0000ff) {
          list.add(new Water(x, y, world));
        } else if (c==0xffff0000 || c==0xff808040) {
          list.add(new Square(g.getSprite("brickbig"), x, y, world, true, 1));
        } else if (c==0xffc0c0c0) {
          list.add(new Square(g.getSprite("ground"), x, y, world, false, 0));
        } else if (c==0xff808080) {
          list.add(new Square(g.getSprite("warfloor"), x, y, world, false, 0));
        } else if (c==0xffc0e6c0) {
          list.add(new Square(g.getSprite("bck0"), x, y, world, false, 0));
        } else if (c==0xff8000ff) {
          Sprite spr=g.getSprite("grass");
          list.add(new Square(spr, x, y, world, false, 0));
        } else if (c==0xff008080) {
          throw new RuntimeException("corner should not be defined on image "+
              "map for a while x:"+x+" y:"+y);
        } else if (c==0xff004040) {
          list.add(new Square(g.getSprite("trap|"), x, y, world, false, 0));
        } else if (c==0xff008282) {
          list.add(new Square(g.getSprite("trap-"), x, y, world, false, 0));
        } else if (c==0xffa2a2a2) {
          list.add(new Square(g.getSprite("bck4_"+random.nextInt(4)), x, y,
              world, false, 0));
        } else if (c==0xff804000) {
          int part=getLandingPart(x, y);
          if (part==0) {
            list.add(new LandingGround(x, y, world));
          } else if (part==1) {
            list.add(getLG(map[y][x-1].objs));
          } else if (part==2) {
            list.add(getLG(map[y-1][x-1].objs));
          } else {
            list.add(getLG(map[y-1][x].objs));
          }
        } else if (c==0xffff0080) {
          list.add(background(x, y));
          firstaid(x, y);
        } else if (c==0xff008000) {
          Drawable sq=background(x, y);
          list.add(sq);
          heli(x, y);
        } else if (c==0xff00ffff) {
          list.add(background(x, y));
          enplane(x, y);
        } else if (c==0xff00ff80) {
          list.add(background(x, y));
          tank(x, y);
        } else if (c==0xff800080) {
          list.add(background(x, y));
          if (lvl == 3 || lvl == 103 || lvl == 203) {
            boss1(x, y);
          } else {
            boss2(x, y);
          }
        } else if (c==0xffff8040) {
System.out.println("the first cannon");
          cannon(list, x, y, 1);
        } else if (c==0xffff8041) {
System.out.println("the second cannon");
          cannon(list, x, y, 3);
        } else if (c!=0xff000000) {
          throw new RuntimeException("WorldLoader.load lvl (true number):"+lvl+
              " xy("+x+","+y+") color:"+C.string(c)+" should not be");
        }
      }
    }
  }

  private final int getLandingPart(int x, int y) {
    if (x==0 || !contains(map[y][x-1].objs, "landingg")) {
      /*left*/
      if (y==0 || !contains(map[y-1][x].objs, "landingg")) {
        /*up*/
        return 0;
      } else return 3;
    } else {
      /*right*/
      if (y==0 || !contains(map[y-1][x].objs, "landingg")) {
        /*up*/
        return 1;
      } else return 2;
    }
  }

  private final LandingGround getLG(List<Drawable> list) {
    for (Drawable d: list) if (d instanceof LandingGround) {
      return (LandingGround)d;
    }
    throw new RuntimeException("have not found LG in this list");
  }

  private final void specialMonsters() {
/*  if (lvl==1) {
      heli(w, -2, 72);
    }*/
  }

  private final Drawable background(int x, int y) {
    /* warfloor ground bck0 grass trap| water trap- bck4*/
    int[] wcg=new int[8];
    for (int i=0; i<8; i++) wcg[i]=0;
    if (x>0) {
      background(map[y-1][x-1].objs, wcg);
      background(map[y][x-1].objs, wcg);
    } else if (x<h-1) {
      background(map[y-1][x+1].objs, wcg);
    }
    background(map[y-1][x].objs, wcg);
    int max=U77.maxIndex(wcg);
    if (max==0) {
      return new Square(g.getSprite("warfloor"), x, y, world, false, 0);
    } else if (max==1) {
      return new Square(g.getSprite("ground"), x, y, world, false, 0);
    } else if (max==2) {
      return new Square(g.getSprite("bck0"), x, y, world, false, 0);
    } else if (max==3) {
      return new Square(g.getSprite("grass"), x, y, world, false, 0);
    } else if (max==4) {
      return new Square(g.getSprite("trap|"), x, y, world, false, 0);
    } else if (max==5) {
      return new Water(x, y, world);
    } else if (max==6) {
      return new Square(g.getSprite("trap-"), x, y, world, false, 0);
    } else if (max==7) {
      return new Square(g.getSprite("bck4_0"), x, y, world, false, 0);
    } else {
      throw new RuntimeException("Should not be: unknown background lvl:" +
          lvl + " x:" + x + "y:" + y);
    }
  }

  private static final boolean contains(List<Drawable> list, String starts) {
    for (Drawable d: list) {
      if (d instanceof Square) {
        Square sq=(Square)d;
        if (sq.sprite.name().startsWith(starts)) return true;
      }
    }
    return false;
  }

  private static final void background(List<Drawable> square, int[]wcg) {
    for (Drawable d: square) {
      if (d instanceof Square) {
        Square s=(Square)d;
        if (s.sprite.name().startsWith("warfloor")) {
          wcg[0]++;
        } else if (s.sprite.name().startsWith("ground")) {
          wcg[1]++;
        } else if (s.sprite.name().startsWith("bck0")) {
          wcg[2]++;
        } else if (s.sprite.name().startsWith("grass")) {
          wcg[3]++;
        } else if (s.sprite.name().equals("trap|")) {
          wcg[4]++;
        } else if (s.sprite.name().startsWith("wat")) {
          wcg[5]++;
        } else if (s.sprite.name().equals("trap-")) {
          wcg[6]++;
        } else if (s.sprite.name().startsWith("bck4")) {
          wcg[7]++;
        }
      }
    }
  }

  private final void enplane(int x, int y) {
    EnPlane enplane=new EnPlane(squareCenter(x), squareCenter(y), world);
    world.objsToAddInTime.add(enplane);
  }

  private final void heli(int x, int y) {
    Heli heli=new Heli(squareCenter(x), squareCenter(y), world, 1);
    world.objsToAddInTime.add(heli);
  }

  private final void tank(int x, int y) {
    Tank tank=new Tank(squareCenter(x), squareCenter(y), world, 2);
    world.objsToAddInTime.add(tank);
  }

  private final void cannon(List list, int x, int y, int dir) {
System.out.println("cannon "+dir);
    list.add(background(x, y));
    list.add(new Cannon(dir, x, y, world));
  }

  private final void boss1(int x, int y) {
    Boss1AI boss=new Boss1AI(squareCenter(x), squareCenter(y), world);
    world.objsToAddInTime.add(boss);
  }

  private final void boss2(int x, int y) {
    Boss2AI boss = new Boss2AI(squareCenter(x), squareCenter(y), world);
    world.objsToAddInTime.add(boss);
  }

  private final void firstaid(int x, int y) {
    FirstAid fa=new FirstAid(squareCenter(x), squareCenter(y), world);
    world.objsToAddInTime.add(fa);
  }

  private static final double squareCenter(int square) {
    return (double)square*Config.squareSize+Config.hSquareSize;
  }
}