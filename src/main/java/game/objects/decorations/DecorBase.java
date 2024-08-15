package game.objects.decorations;

import game.config.Registry;
import game.objects.AbstractDecor;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class DecorBase {
    public static AbstractDecor get(int id, Point2D p) {
        return switch (id) {
            case 100 -> new Dirt(100, new BufferedImage[]{Registry.sComb.getSprites("decos")[0]}, p, 0.00075f, -1);
            case 101 -> new Rock(101, new BufferedImage[]{Registry.sComb.getSprites("decos")[1]}, p, 0.00075f, -1);
            case 102 -> new Dirt(102, new BufferedImage[]{Registry.sComb.getSprites("decos")[2]}, p, 0.0005f, -1);
            case 103 -> new Tree(103, new BufferedImage[]{Registry.sComb.getSprites("decos")[3]}, p, 0.001f, -1);
            case 104 -> new Rock(104, new BufferedImage[]{Registry.sComb.getSprites("decos")[4]}, p, 0.0005f, -1);
            case 105 -> new Grave(105, new BufferedImage[]{Registry.sComb.getSprites("decos")[5]}, p, 0.00075f, -1);
            case 106 -> new Grave(106, new BufferedImage[]{Registry.sComb.getSprites("decos")[6]}, p, 0.00075f, -1);
            case 107 -> new Bush(107, new BufferedImage[]{Registry.sComb.getSprites("decos")[7]}, p, 0.00075f, -1);
            case 108 -> new Flower(108, new BufferedImage[]{Registry.sComb.getSprites("decos")[8]}, p, 0.0004f, -1);
            case 109 -> new Mushroom(109, new BufferedImage[]{Registry.sComb.getSprites("decos")[9]}, p, 0.00025f, -1);
            case 110 -> new Mushroom(110, new BufferedImage[]{Registry.sComb.getSprites("decos")[10]}, p, 0.00025f, -1);
            case 111 ->
                    new WavingGrass(111, new BufferedImage[]{Registry.sComb.getSprites("grass")[0], Registry.sComb.getSprites("grass")[1],
                            Registry.sComb.getSprites("grass")[2]}, p, 0.0004f, 300);
            case 112 ->
                    new RotatingFlowers(112, new BufferedImage[]{Registry.sComb.getSprites("grass")[3], Registry.sComb.getSprites("grass")[4],
                            Registry.sComb.getSprites("grass")[5]}, p, 0.0003f, 100);
            case 113 -> new Bush(113, new BufferedImage[]{Registry.sComb.getSprites("decos_2")[0]}, p, 0.0004f, -1);
            case 114 -> new Mushroom(114, new BufferedImage[]{Registry.sComb.getSprites("decos_2")[1]}, p, 0.0002f, -1);
            case 115 -> new Flower(115, new BufferedImage[]{Registry.sComb.getSprites("decos_2")[2]}, p, 0.00025f, -1);
            case 116 -> new Rock(116, new BufferedImage[]{Registry.sComb.getSprites("decos_2")[3]}, p, 0.0002f, -1);
            case 117 -> new Rock(117, new BufferedImage[]{Registry.sComb.getSprites("decos_2")[4]}, p, 0.0002f, -1);
//			case 118: 	return new SomeStones(118, new BufferedImage[]{GameMenu.comb.getSprites("decos_2")[5]}, p, 0.1f, 500, parentDimension);
            case 199 ->
                    new DefencePlace(199, new BufferedImage[]{Registry.sComb.getSprites("defPlace")[0]}, p, 0.001f, -1);
            default -> null;
        };
    }

    public static AbstractDecor[] getAll() {
        ArrayList<AbstractDecor> decorsListing = new ArrayList<>();
        AbstractDecor tempDeco;
        int ind = 100;
        while ((tempDeco = get(ind, new Point2D.Double(0, 0))) != null) {
            decorsListing.add(tempDeco);
            ind++;
        }

        return decorsListing.toArray(new AbstractDecor[0]);
    }
}
