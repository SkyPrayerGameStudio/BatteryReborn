package b.gfx;

import java.util.*;

public class BufGfx {
  public static final int transp=0xffffffff;

  private static final double shadow=0.87;

  public int[] b;
  public int w;
  public int h;

  public BufGfx(int w, int h) {
    b=new int[h*w];
    this.w=w;
    this.h=h;
  }

  public BufGfx(int[] b, int w, int h) {
    this.b=b;
    this.w=w;
    this.h=h;
  }

  public BufGfx(Sprite sprite) {
    b=sprite.b;
    w=sprite.w;
    h=sprite.h;
  }

  public BufGfx(Sprite sprite, boolean anotherbufferplease) {
    w=sprite.w;
    h=sprite.h;
    if (anotherbufferplease) {
      b=new int[w*h];
      System.arraycopy(sprite.b, 0, b, 0, h*w);
    } else {
      b=sprite.b;
    }
  }

  public BufGfx(Sprite sprite, int[] buf) {
    w=sprite.w;
    h=sprite.h;
    b=buf;
    System.arraycopy(sprite.b, 0, b, 0, h*w);
  }

  public Effects effects() {
    return new Effects(this);
  }

  public final void replaceColor(int what, int with) {
    for (int i=w*h-1; i>=0; i--) {
      if (b[i]==what) b[i]=with;
    }
  }

  public final void draw(Sprite sprite, int x, int y) {
    draw(sprite.b, sprite.w, sprite.h, x, y);
  }

  public final void drawRangeCheck(BufGfx buf, int x, int y) {
    drawRangeCheck(buf.b, buf.w, buf.h, x, y);
  }

  public final void drawTransp(Sprite sprite, int x, int y) {
    drawTransp(sprite.b, sprite.w, sprite.h, x, y);
  }

  public final void drawTranspShadow(Sprite sprite, int x, int y) {
    drawTranspShadow(sprite.b, sprite.w, sprite.h, x, y);
  }

  public final void drawTranspRangeCheck(Sprite sprite, int x, int y) {
    drawTranspRangeCheck(sprite.b, sprite.w, sprite.h, x, y);
  }

  /**
   * @param solid 1-not transp.; 0-absolutely transp.
   */
  public final void drawTranspTrRangeCheck(Sprite sprite, int x, int y,
      double solid) {
    drawTranspTrRangeCheck(sprite.b, sprite.w, sprite.h, x, y, solid);
  }

  public final void drawBlackRangeCheck(Sprite sprite, int x, int y) {
    drawBlackRangeCheck(sprite.b, sprite.w, sprite.h, x, y);
  }

  public final void drawTranspShadowRangeCheck(Sprite sprite, int x, int y) {
    drawTranspShadowRangeCheck(sprite.b, sprite.w, sprite.h, x, y);
  }

  public final void drawTranspWhite(Sprite sprite, int x, int y) {
    drawTranspWhite(sprite.b, sprite.w, sprite.h, x, y);
  }

  public final void drawTranspWhiteRangeCheck(Sprite sprite, int x, int y) {
    drawTranspWhiteRangeCheck(sprite.b, sprite.w, sprite.h, x, y);
  }

  public final void drawTranspBlackRangeCheck(Sprite sprite, int x, int y) {
    drawTranspBlackRangeCheck(sprite.b, sprite.w, sprite.h, x, y);
  }

  public final void drawTranspWhiteRangeCheck(BufGfx buf, int x, int y) {
    drawTranspWhiteRangeCheck(buf.b, buf.w, buf.h, x, y);
  }

  public final void drawRangeCheck(Sprite sprite, int x, int y) {
    drawRangeCheck(sprite.b, sprite.w, sprite.h, x, y);
  }

  public final void flipHorizontal() {
    int xBorder=w/2;
    for (int y=0; y<h; y++) {
      int offset=y*w;
      int offset2=y*w+w-1;
      for (int x=0; x<xBorder; x++) {
        int c=b[offset];
        b[offset++]=b[offset2];
        b[offset2--]=c;
      }
    }
  }

  public final void flipVertical() {
    int yBorder=h/2;
    int offset=0;
    for (int y=0; y<yBorder; y++) {
      int offset2=(h-y-1)*w;
      for (int x=0; x<w; x++) {
        int c=b[offset];
        b[offset++]=b[offset2];
        b[offset2++]=c;
      }
    }
  }

  public final void rot90() {
    if (w==h) {
      int p[]=new int[h*w];
      int offset=0;
      for(int y=0; y<h; y++) {
        for(int x=0; x<w; x++) {
          p[x*w+w-y-1]=b[offset++];
        }
      }
      System.arraycopy(p, 0, b, 0, h*w);
    } else {
      rot90NotSquare(null);
    }
  }

  /**
   * Changes b (reference)
   */
  public final void rot90NotSquare(Sprite s) {
    int p[]=new int[h*w];
    int offset=0;
    for(int y=0; y<h; y++) {
      for(int x=0; x<w; x++) {
        p[x*h+y]=b[offset++];
      }
    }
    System.arraycopy(p, 0, b, 0, h*w);
    int exW=w;
    w=h;
    h=exW;
    if (s!=null) s.setWH(w, h);
  }

  public final void drawRangeCheck(int[] p, int width, int height, int x, int y) {
    int xStart=x<0?0:x;
    int yStart=y<0?0:y;
    int xBorder=x + width;
    int yBorder=y + height;
    int xxBorder=xBorder>w?w:xBorder;
    int yyBorder=yBorder>h?h:yBorder;
    int pixel=0;
    for (int yy=y; yy<yBorder; yy++) {
      int offset=yy*w+x;
      for (int xx=x; xx<xBorder; xx++) {
        if (xx>=xStart && xx<xxBorder && yy>=yStart && yy<yyBorder) {
          b[offset++]=p[pixel++];
        } else {
          offset++;
          pixel++;
        }
      }
    }
  }

  public final void draw(int[] buf, int w, int h, int x, int y) {
    int xxBorder=x + w;
    int yyBorder=y + h;
    int pixel=0;
    for (int yy=y; yy<yyBorder; yy++) {
      int offset=yy*this.w+x;
      for (int xx=x; xx<xxBorder; xx++) b[offset++]=buf[pixel++];
    }
  }

  public final void drawTransp(int[] p, int width, int height, int x, int y) {
    int xBorder=x + width;
    int yBorder=y + height;
    int pixel=0;
    for (int yy=y; yy<yBorder; yy++) {
      int offset=yy*w+x;
      for (int xx=x; xx<xBorder; xx++) {
        int c=p[pixel++];
        if (c != transp) {
          b[offset++]=c;
        } else {
          offset++;
        }
      }
    }
  }  

  public final void drawTranspShadow(int[] p, int width, int height, int x, int y) {
    int xBorder=x + width;
    int yBorder=y + height;
    int pixel=0;
    for (int yy=y; yy<yBorder; yy++) {
      int offset=yy*w+x;
      for (int xx=x; xx<xBorder; xx++) {
        int c=p[pixel++];
        if (c != transp) {
          b[offset++]=c;
        } else {
          b[offset]=C.dark(b[offset++], shadow);
        }
      }
    }
  }  

  public final void rectShadow(int x, int y, int width, int height,
      double shadow) {
    int xBorder=x + width;
    int yBorder=y + height;
    for (int yy=y; yy<yBorder; yy++) {
      int offset=yy*w+x;
      for (int xx=x; xx<xBorder; xx++) {
        b[offset]=C.dark(b[offset], shadow);
        offset++;
      }
    }
  }  

  public final void drawTranspRangeCheck(int[] p, int width, int height, int x,
      int y) {
    int xStart=x<0?0:x;
    int yStart=y<0?0:y;
    int xBorder=x + width;
    int yBorder=y + height;
    int xxBorder=xBorder>w?w:xBorder;
    int yyBorder=yBorder>h?h:yBorder;
    int pixel=0;
    for (int yy=y; yy<yBorder; yy++) {
      int offset=yy*w+x;
      for (int xx=x; xx<xBorder; xx++) {
        int c=p[pixel++];
        if ((c != transp) && (xx>=xStart && xx<xxBorder && yy>=yStart &&
            yy<yyBorder)) {
          b[offset++]=c;
        } else {
          offset++;
        }
      }
    }
  }  

  public final void drawTranspTrRangeCheck(int[] p, int width, int height,
      int x, int y, double solid) {
    int xStart=x<0?0:x;
    int yStart=y<0?0:y;
    int xBorder=x + width;
    int yBorder=y + height;
    int xxBorder=xBorder>w?w:xBorder;
    int yyBorder=yBorder>h?h:yBorder;
    int pixel=0;
    for (int yy=y; yy<yBorder; yy++) {
      int offset=yy*w+x;
      for (int xx=x; xx<xBorder; xx++) {
        int c=p[pixel++];
        if ((c != transp) && (xx>=xStart && xx<xxBorder && yy>=yStart &&
            yy<yyBorder)) {
          b[offset]=C.mix(b[offset], c, solid);
        }
        offset++;
      }
    }
  }  

  public final void drawBlackRangeCheck(BufGfx buf, int x, int y) {
    drawBlackRangeCheck(buf.b, buf.w, buf.h, x, y);
  }
  
  public final void drawBlackRangeCheck(int[] p, int width, int height, int x,
      int y) {
    int xStart=x<0?0:x;
    int yStart=y<0?0:y;
    int xBorder=x + width;
    int yBorder=y + height;
    int xxBorder=xBorder>w?w:xBorder;
    int yyBorder=yBorder>h?h:yBorder;
    int pixel=0;
    for (int yy=y; yy<yBorder; yy++) {
      int offset=yy*w+x;
      for (int xx=x; xx<xBorder; xx++) {
        int c=p[pixel++];
        if ((c==0xff000000) && (xx>=xStart && xx<xxBorder && yy>=yStart &&
            yy<yyBorder)) {
          b[offset++]=c;
        } else {
          offset++;
        }
      }
    }
  }  

  public final void drawTranspShadowRangeCheck(int[] p, int width, int height, int x,
      int y) {
    int xStart=x<0?0:x;
    int yStart=y<0?0:y;
    int xBorder=x + width;
    int yBorder=y + height;
    int xxBorder=xBorder>w?w:xBorder;
    int yyBorder=yBorder>h?h:yBorder;
    int pixel=0;
    for (int yy=y; yy<yBorder; yy++) {
      int offset=yy*w+x;
      for (int xx=x; xx<xBorder; xx++) {
        int c=p[pixel++];
        if (xx>=xStart && xx<xxBorder && yy>=yStart && yy<yyBorder) {
          if (c != transp) { 
            b[offset++]=c;
          } else {
            b[offset]=C.dark(b[offset++], shadow);
          }
        } else {
          offset++;
        }
      }
    }
  }

  public final void drawTranspWhite(int[] p, int width, int height, int x, int y) {
    int xBorder=x + width;
    int yBorder=y + height;
    int pixel=0;
    for (int yy=y; yy<yBorder; yy++) {
      int offset=yy*w+x;
      for (int xx=x; xx<xBorder; xx++) {
        int c=p[pixel++];
        if (c != transp) {
          if (c==0xff000000) {
            b[offset++]=0xff000000;
          } else {
            b[offset++]=0xffffffff;
          }
        } else {
          offset++;
        }
      }
    }
  }  

  public final void drawTranspWhiteRangeCheck(int[] p, int width, int height, int x,
      int y) {
    int xStart=x<0?0:x;
    int yStart=y<0?0:y;
    int xBorder=x + width;
    int yBorder=y + height;
    int xxBorder=xBorder>w?w:xBorder;
    int yyBorder=yBorder>h?h:yBorder;
    int pixel=0;
    for (int yy=y; yy<yBorder; yy++) {
      int offset=yy*w+x;
      for (int xx=x; xx<xBorder; xx++) {
        int c=p[pixel++];
        if (xx>=xStart && xx<xxBorder && yy>=yStart && yy<yyBorder) {
          if (c != transp) {
            if (c==0xff000000) {
              b[offset++]=0xff000000;
            } else {
              b[offset++]=0xffffffff;
            }
          } else {
            offset++;
          }
        } else {
          offset++;
        }
      }
    }
  }  

  public final void drawTranspBlackRangeCheck(int[] p, int width, int height,
      int x, int y) {
    int xStart=x<0?0:x;
    int yStart=y<0?0:y;
    int xBorder=x + width;
    int yBorder=y + height;
    int xxBorder=xBorder>w?w:xBorder;
    int yyBorder=yBorder>h?h:yBorder;
    int pixel=0;
    for (int yy=y; yy<yBorder; yy++) {
      int offset=yy*w+x;
      for (int xx=x; xx<xBorder; xx++) {
        int c=p[pixel++];
        if (xx>=xStart && xx<xxBorder && yy>=yStart && yy<yyBorder) {
          if (c != transp) {
            b[offset++]=0xff000000;
          } else {
            offset++;
          }
        } else {
          offset++;
        }
      }
    }
  }  

  public final void drawAtCenter(Sprite s) {
    draw(s.b, s.w, s.h, (w-s.w)/2, (h-s.h)/2);
  }

  public final void line(int x0, int y0, int x1, int y1, int c) {
    int dx=x1 - x0;
    int dy=y1 - y0;
    b[y0*w+x0]=c;
    if (Math.abs(dx) > Math.abs(dy)) {
      float m=(float)dy / (float)dx;
      float bb=y0 - m*x0;
      dx=(dx < 0) ? -1 : 1;
      while (x0 != x1) {
        x0 += dx;
        b[Math.round(m*x0 + bb)*w+x0]=c;
      }
    } else if (dy != 0) {
      float m=(float)dx / (float)dy;
      float bb=x0 - m*y0;
      dy=(dy < 0) ? -1 : 1;
      while (y0 != y1) {
        y0 += dy;
        b[y0*w + Math.round(m*y0 + bb)]=c;
      }
    }
  }

  public final void rect(int startX, int startY, int width, int height, int c) {
    hline(startX, startY, width, c);
    hline(startX, startY+height-1, width, c);
    vline(startX, startY, height, c);
    vline(startX+width-1, startY, height, c);
  }

  public final void filledRect(int startX, int startY, int width, int height, int c) {
    int offset=startY*w+startX;
    int yBorder=startY+height;
    for (int y=startY; y<yBorder; y++) {
      Arrays.fill(b, offset, offset+width, c);
      offset += w;
    }
  }

  public final void circleRangeCheck(int x, int y, int radius, int color) {
    int i=0;
    int j=radius;
    while (i<=j) {
      pixel(x+i,y-j,color) ;
      pixel(x+j,y-i,color) ;
      pixel(x+i,y+j,color) ;
      pixel(x+j,y+i,color) ;
      pixel(x-i,y-j,color) ;
      pixel(x-j,y-i,color) ;
      pixel(x-i,y+j,color) ;
      pixel(x-j,y+i,color) ;
      i++ ;
      j=(int)(Math.sqrt(radius*radius - i*i) + 0.5) ;
    }
  }

  public final void hline(int x, int y, int width, int c) {
    int start=y*w+x;
    Arrays.fill(b, start, start+width, c);
  }

  public final void vline(int x, int y, int height, int c) {
    int yBorder=y+height;
    int offset=y*w+x;
    for (int i=y; i<yBorder; i++) {
      b[offset]=c;
      offset += w;
    }
  }

  public final void vlineRangeCheck(int x, int y, int height, int c) {
    if (x>=0 && x<w) {
      if (y<0) {
        height += y;
        y=0;
      }
      if (y<h) {
        int yBorder=y+height;
        if (yBorder<=h) {
          int offset=y*w+x;
          for (int i=y; i<yBorder; i++) {
            b[offset]=c;
            offset += w;
          }
        }
      }
    }
  }

  /**
   * Width range check
   */
  public final void pixel(int x, int y, int color) {
    if (x>=0 && x<w && y>=0 && y<h) {
      b[y*w+x]=color;
    }
  }
}