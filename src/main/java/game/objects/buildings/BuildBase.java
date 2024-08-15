package game.objects.buildings;

import game.config.Registry;
import game.objects.AbstractBuilding;

import java.awt.geom.Point2D;
import java.util.ArrayList;


public class BuildBase {
    public static AbstractBuilding get(int ID, Point2D p) {
        return switch (ID) {
            case 200 -> new HomeBase(200, p, Registry.cache.getBufferedImage("buildHome"));
            case 201 -> new RedDefenceTower(201, p, Registry.cache.getBufferedImage("redDefenceTower"));
            case 202 -> new WhiteDefenceTower(202, p, Registry.cache.getBufferedImage("whiteDefenceTower"));
            case 203 -> new GreenDefenceTower(203, p, Registry.cache.getBufferedImage("greenDefenceTower"));
            case 204 -> new MageDefenceTower(204, p, Registry.cache.getBufferedImage("mageDefenceTower"));
            default -> null;
        };
    }

    public static AbstractBuilding[] getAll() {
        ArrayList<AbstractBuilding> buildingsListing = new ArrayList<>();

        AbstractBuilding tempBuild;
        int ind = 200;
        while ((tempBuild = get(ind, new Point2D.Double(0, 0))) != null) {
//			log.info("prepare for getting build '" + tempBuild.getName() + "'...");
            buildingsListing.add(tempBuild);
            ind++;
        }

        return buildingsListing.toArray(new AbstractBuilding[0]);
    }
}
