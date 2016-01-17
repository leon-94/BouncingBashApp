package de.lmu.ifi.bouncingbash.app.game;

import com.badlogic.gdx.graphics.Texture;
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

    public static void load(Game g) {
        t = new Textures(g);
    }

    public static Textures getTextures() {
        return t;
    }

    private Textures(Game g) {

        textures = new HashMap<String, Texture>();
        textures.put("TEX_CIRCLE", g.TEX_CIRCLE);
        textures.put("TEX_ARROW", g.TEX_ARROW);
        textures.put("TEX_SWITCH", g.TEX_SWITCH);
        textures.put("TEX_DUST", g.TEX_DUST);

        atlases = new HashMap<String, TextureAtlas>();
//        atlases.put("ATLAS_DUST", g.ATLAS_DUST);
    }

    public Texture getTexture(String s) {
        return (Texture)textures.get(s);
    }
    public TextureAtlas getTextureAtlas(String s) {
        return (TextureAtlas)atlases.get(s);
    }
}
