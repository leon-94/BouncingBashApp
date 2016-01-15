package de.lmu.ifi.bouncingbash.app.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;
import java.util.Map;

import de.lmu.ifi.bouncingbash.app.game.models.Platform;
import de.lmu.ifi.bouncingbash.app.game.views.BallView;
import de.lmu.ifi.bouncingbash.app.game.views.BodyView;
import de.lmu.ifi.bouncingbash.app.game.views.ItemView;
import de.lmu.ifi.bouncingbash.app.game.views.PlatformView;

/**
 * Created by Michi on 15.01.2016.
 */
//enthält die kollisionsabfragen und die Reaktionen auf eine Kollision
public class CollisionHandler {
    private ArrayList<BodyView> views;
    private World world;
    //platzhalter
    private Body body1,body2;
    private BodyView ballView,platformView,itemView ;
    public CollisionHandler(ArrayList<BodyView> views,World world)
    {
        this.views = views;
        this.world = world;
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
                for(Map.Entry<Sprite,Body> entry : platformView.getBodys().entrySet())
                {
                    Body platformBody = entry.getValue();
                    for(Map.Entry<Sprite,Body> entry2: ballView.getBodys().entrySet()) {
                        Body ballBody = entry.getValue();
                        if ((fA == platformBody && fB == ballBody) ||
                                (fA == ballBody && fB == platformBody)) {

                            System.out.println("CONTACT ball body platform body");
                            System.out.println("" + fA + " " + fB);
                        }
                    }
                }
                /**Ball item Collision**/
                for(Map.Entry<Sprite,Body> entry : itemView.getBodys().entrySet())
                {
                    Body itemBody = entry.getValue();
                    for(Map.Entry<Sprite,Body> entry2: ballView.getBodys().entrySet()) {
                        Body ballBody = entry.getValue();
                        if ((fA == itemBody && fB == ballBody) ||
                                (fA == ballBody && fB == itemBody)) {

                            System.out.println("CONTACT ball body item body");
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
