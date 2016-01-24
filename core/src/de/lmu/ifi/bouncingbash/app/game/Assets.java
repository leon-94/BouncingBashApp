package de.lmu.ifi.bouncingbash.app.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import java.util.HashMap;

/**
 * Created by Leon on 11.01.2016.
 */
public class Assets {

    public Texture TEX_BALL;
    public Texture TEX_CIRCLE;
    public Texture TEX_CIRCLE2;
    public Texture TEX_DIAMONT;
    public Texture TEX_ARROW;
    public Texture TEX_SWITCH;
    public Texture TEX_DUST;
    public Texture TEX_BACKGROUND0;
    public Texture TEX_BACKGROUND1;
    public Texture TEX_TITLE;
    public Texture TEX_YOUWON;
    public Texture TEX_YOULOST;
    public Texture TEX_DOT;
    public Texture TEX_FIRE_UP;
    public Texture TEX_SPEED_UP;

    public BitmapFont FONT_CALIBRI_32;
    public BitmapFont FONT_CALIBRI_64;

//    public TextureAtlas ATLAS_DUST;

    private static Assets a;


    private HashMap textures;
    private HashMap atlases;
    private HashMap fonts;

    public static void init() {
        a = new Assets();
    }

    public static Assets getAssets() {
        if(a == null) Gdx.app.log("Assets", "textures not loaded");
        return a;
    }

    private Assets() {
        textures = new HashMap<String, Texture>();
        atlases = new HashMap<String, TextureAtlas>();
        fonts = new HashMap<String, BitmapFont>();
    }

    public void loadAssets() {

        // textures
        TEX_CIRCLE = new Texture("circle.png");
        textures.put("TEX_CIRCLE", TEX_CIRCLE);
        TEX_CIRCLE2 = new Texture("circle2.png");
        textures.put("TEX_CIRCLE2", TEX_CIRCLE2);
        TEX_ARROW = new Texture("arrow.png");
        textures.put("TEX_ARROW", TEX_ARROW);
        TEX_SWITCH = new Texture("switch.png");
        textures.put("TEX_SWITCH", TEX_SWITCH);
        TEX_DUST = new Texture("dust.png");
        textures.put("TEX_DUST", TEX_DUST);
        TEX_DIAMONT = new Texture("diamont.png");
        textures.put("TEX_DIAMONT", TEX_DIAMONT);
        TEX_BACKGROUND0 = new Texture("background0.png");
        textures.put("TEX_BACKGROUND0", TEX_BACKGROUND0);
        TEX_BACKGROUND1 = new Texture("background1.png");
        textures.put("TEX_BACKGROUND1", TEX_BACKGROUND1);
        TEX_FIRE_UP = new Texture("fireUp.png");
        textures.put("TEX_FIRE_UP", TEX_FIRE_UP);
        TEX_SPEED_UP = new Texture("speedUp.png");
        textures.put("TEX_SPEED_UP", TEX_SPEED_UP);

        Pixmap p = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        p.setColor(Color.WHITE);
        p.fill();
        TEX_DOT = new Texture(p);
        textures.put("TEX_DOT", TEX_DOT);
        p.dispose();

        // atlases
//        ATLAS_DUST = new TextureAtlas("dust/dust.pack");
//        atlases.put("ATLAS_DUST", g.ATLAS_DUST);


        // fonts
        FONT_CALIBRI_32 = new BitmapFont(Gdx.files.internal("fonts/calibri_32.fnt"));
        FONT_CALIBRI_32.setColor(Color.WHITE);
        FONT_CALIBRI_32.setColor(Color.BLACK);
        fonts.put("FONT_CALIBRI_32", FONT_CALIBRI_32);

        FONT_CALIBRI_64 = new BitmapFont(Gdx.files.internal("fonts/calibri_64.fnt"));
        FONT_CALIBRI_64.setColor(Color.WHITE);
        FONT_CALIBRI_64.setColor(Color.BLACK);
        FONT_CALIBRI_64.getData().setScale(2f);
        fonts.put("FONT_CALIBRI_64", FONT_CALIBRI_64);
    }

    public Texture getTexture(String s) {
        return (Texture)textures.get(s);
    }
    public TextureAtlas getTextureAtlas(String s) {
        return (TextureAtlas)atlases.get(s);
    }
    public BitmapFont getFont(String s) {
        return (BitmapFont)fonts.get(s);
    }

    public void loadLoadingScreenAssets() {
        TEX_BACKGROUND1 = new Texture("background1.png");
        textures.put("TEX_BACKGROUND1", TEX_BACKGROUND1);
        TEX_TITLE = new Texture("title.png");
        textures.put("TEX_TITLE", TEX_TITLE);
        TEX_BALL = new Texture("ball_gear.png");
        textures.put("TEX_BALL", TEX_BALL);
    }

    public void disposeLoadingScreen() {
        TEX_BACKGROUND1.dispose();
        textures.remove("TEX_BACKGROUND1");
        TEX_TITLE.dispose();
        textures.remove("TEX_TITLE");
    }

    public void loadEndScreenAssets() {
        TEX_YOUWON = new Texture("youwon.png");
        textures.put("TEX_YOUWON", TEX_YOUWON);
        TEX_YOULOST = new Texture("youlost.png");
        textures.put("TEX_YOULOST", TEX_YOULOST);
        TEX_BACKGROUND1 = new Texture("background1.png");
        textures.put("TEX_BACKGROUND1", TEX_BACKGROUND1);
        TEX_TITLE = new Texture("title.png");
        textures.put("TEX_TITLE", TEX_TITLE);
    }

    public void dispose() {
        for(Object t : textures.values()) {
            ((Texture) t).dispose();
        }
    }
}
