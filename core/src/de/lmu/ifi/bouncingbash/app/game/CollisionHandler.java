package de.lmu.ifi.bouncingbash.app.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.lmu.ifi.bouncingbash.app.game.models.EffectType;
import de.lmu.ifi.bouncingbash.app.game.models.Entity;
import de.lmu.ifi.bouncingbash.app.game.models.GameModel;
import de.lmu.ifi.bouncingbash.app.game.models.Item;
import de.lmu.ifi.bouncingbash.app.game.models.Platform;
import de.lmu.ifi.bouncingbash.app.game.views.BallView;
import de.lmu.ifi.bouncingbash.app.game.views.BodyView;
import de.lmu.ifi.bouncingbash.app.game.views.CustomUserData;
import de.lmu.ifi.bouncingbash.app.game.views.ItemView;
import de.lmu.ifi.bouncingbash.app.game.views.PlatformView;

/**
 * Created by Michi on 15.01.2016.
 */
//enthält die kollisionsabfragen und die Reaktionen auf eine Kollision
public class CollisionHandler {
    private ArrayList<BodyView> views;
    private World world;
    private GameModel gameModel;
    //platzhalter
    private Body body1,body2;
    private BodyView ballView,platformView,itemView ;
    public CollisionHandler(ArrayList<BodyView> views,World world, GameModel gameModel)
    {
        this.views = views;
        this.world = world;
        this.gameModel = gameModel;
        for(BodyView view : views)
        {
            if(ItemView.class.isInstance(view))
            {
                itemView = (ItemView)view;
            }
            if(PlatformView.class.isInstance(view))
            {
                platformView = (PlatformView)view;
            }
            if(BallView.class.isInstance(view))
            {
                ballView = (BallView)view;
            }
        }
    }
    public void collision() {
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Body fA = contact.getFixtureA().getBody();
                Body fB = contact.getFixtureB().getBody();
                /**Ball Platform Collision**/
                for(Map.Entry<Entity,Body> entry : platformView.getBodys().entrySet())
                {
                    Body platformBody = entry.getValue();

                    for(Map.Entry<Entity,Body> entry2: ballView.getBodys().entrySet()) {
                        Body ballBody = entry2.getValue();

                        if ((fA == platformBody && fB == ballBody) ||
                                (fA == ballBody && fB == platformBody)) {

                            System.out.println("CONTACT ball body platform body");
                            System.out.println("" + fA + " " + fB);
                        }
                    }
                }
                /**Ball item Collision**/
                for(Map.Entry<Entity,Body> entry : itemView.getBodys().entrySet())
                {
                    Body itemBody = entry.getValue();

                    for(Map.Entry<Entity,Body> entry2: ballView.getBodys().entrySet()) {
                        Body ballBody = entry2.getValue();
                        if ((fA == itemBody && fB == ballBody) ||
                                (fA == ballBody && fB == itemBody)) {
                            CustomUserData data = (CustomUserData) itemBody.getUserData();
                            data.setIsFlaggedForDelete(true);

                            gameModel.getPlayer1().getBall().setSpeed(10);
                            System.out.println("CONTACT ball body item body");
                            System.out.println("" + fA + " " + fB);
                        }
                    }
                }
                /**Ball  ball Collision**/
                for(Map.Entry<Entity,Body> entry : ballView.getBodys().entrySet())
                {
                    Body ballBody = entry.getValue();
                    for(Map.Entry<Entity,Body> entry2: ballView.getBodys().entrySet()) {
                        Body ballBody2 = entry2.getValue();
                        if ((fA == ballBody2 && fB == ballBody) ||
                                (fA == ballBody && fB == ballBody)) {

                            System.out.println("CONTACT ball body ball body");
                            System.out.println("" + fA + " " + fB);
                        }
                    }
                }
            }

            @Override
            public void endContact(Contact contact) {
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
            }
        });
    }
}
