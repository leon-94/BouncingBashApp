package de.lmu.ifi.bouncingbash.app.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import java.util.HashMap;

/**
 * Created by Leon on 11.01.2016.
 */
public class Textures {
/*
    public static Texture TEX_CIRCLE = new Texture("circle.png");
    public static TextureAtlas testAtlas = new TextureAtlas("test.pack");
    public static TextureAtlas dustAtlas = new TextureAtlas("dust/dust.pack");
    public static Texture dust_single = new Texture("dust/dust_single.png");
    public static TextureAtlas ATLAS_CIRCLE = new TextureAtlas("bluecircle.pack");*/

    private static Textures t;


    private HashMap textures;
    private HashMap atlases;
    private HashMap fonts;

    public static void load(Game g) {
        t = new Textures(g);
    }

    public static Textures getTextures() {
        return t;
    }

    private Textures(Game g) {

        textures = new HashMap<String, Texture>();
        textures.put("TEX_BALL", g.TEX_BALL);
        textures.put("TEX_CIRCLE", g.TEX_CIRCLE);
        textures.put("TEX_CIRCLE2", g.TEX_CIRCLE2);
        textures.put("TEX_ARROW", g.TEX_ARROW);
        textures.put("TEX_SWITCH", g.TEX_SWITCH);
        textures.put("TEX_DUST", g.TEX_DUST);
        textures.put("TEX_DIAMONT", g.TEX_DIAMONT);

        atlases = new HashMap<String, TextureAtlas>();
//        atlases.put("ATLAS_DUST", g.ATLAS_DUST);

        fonts = new HashMap<String, BitmapFont>();
        fonts.put("FONT_CALIBRI_32", g.FONT_CALIBRI_32);
        fonts.put("FONT_CALIBRI_64", g.FONT_CALIBRI_64);
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
}
