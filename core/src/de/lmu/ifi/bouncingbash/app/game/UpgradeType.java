package de.lmu.ifi.bouncingbash.app.game;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by Michael on 11.12.2015.
 */
public enum UpgradeType {
    SPEEDUP("speedUp",10),FIREUP("fireUp",10);
    private String name;
    private int length;
    private static final List<UpgradeType> VALUES =
            Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();

    UpgradeType(String name, int length)
    {
        this.name=name;
        this.length = length;
    }
    public String getName() {
        return name;
    }

    public int getLength() {
        return length;
    }

    public static UpgradeType randomUpgrade()  {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }
}
