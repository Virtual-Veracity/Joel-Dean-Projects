package dungeonmania.entities.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import dungeonmania.Game;
import dungeonmania.entities.BattleItem;
import dungeonmania.entities.Entity;
import dungeonmania.entities.EntityFactory;
import dungeonmania.entities.buildables.Bow;
import dungeonmania.entities.collectables.Arrow;
import dungeonmania.entities.collectables.Key;
import dungeonmania.entities.collectables.SunStone;
import dungeonmania.entities.collectables.Sword;
import dungeonmania.entities.collectables.Treasure;
import dungeonmania.entities.collectables.Wood;
import dungeonmania.map.GameMap;

public class Inventory {
    private List<InventoryItem> items = new ArrayList<>();

    public boolean add(InventoryItem item) {
        items.add(item);
        return true;
    }

    public void remove(InventoryItem item) {
        items.remove(item);
    }

    public List<String> getBuildables(GameMap map) {

        int wood = count(Wood.class);
        int arrows = count(Arrow.class);
        int treasure = count(Treasure.class);
        int keys = count(Key.class);
        int sunStones = count(SunStone.class);
        int swords = count(Sword.class);
        treasure -= sunStones;
        List<String> result = new ArrayList<>();

        if (canBuildBow(wood, arrows)) {
            result.add("bow");
        }
        if (canBuildShield(wood, treasure, keys, sunStones)) {
            result.add("shield");
        }
        if (canBuildSceptre(wood, arrows, treasure, keys, sunStones)) {
            result.add("sceptre");
        }
        if (canBuildMidnightArmour(map, swords, sunStones)) {
            result.add("midnight_armour");
        }
        return result;
    }

    public InventoryItem checkBuildCriteria(String entity, EntityFactory factory, GameMap map) {
        List<Wood> wood = getEntities(Wood.class);
        List<Arrow> arrows = getEntities(Arrow.class);
        List<Treasure> treasure = getEntities(Treasure.class);
        List<Key> keys = getEntities(Key.class);
        List<SunStone> sunStones = getEntities(SunStone.class);
        List<Sword> swords = getEntities(Sword.class);
        treasure.removeAll(sunStones);

        int woodSize = wood.size();
        int arrowSize = arrows.size();
        int treasureSize = treasure.size();
        int keySize = keys.size();
        int sunStoneSize = sunStones.size();
        int swordSize = swords.size();

        if (Objects.equals(entity, "bow")
            && canBuildBow(woodSize, arrowSize)) {
            items.remove(wood.get(0));
            items.remove(arrows.get(0));
            items.remove(arrows.get(1));
            items.remove(arrows.get(2));
            return factory.buildBow();

        } else if (Objects.equals(entity, "shield")
                   && canBuildShield(woodSize, treasureSize, keySize, sunStoneSize)) {
            items.remove(wood.get(0));
            items.remove(wood.get(1));
            if (treasureSize >= 1) {
                items.remove(treasure.get(0));
            } else if (keySize >= 1) {
                items.remove(keys.get(0));
            }
            return factory.buildShield();
        } else if (Objects.equals(entity, "sceptre")
                   && canBuildSceptre(woodSize, arrowSize, treasureSize, keySize, sunStoneSize)) {
            if (woodSize >= 1) {
                items.remove(wood.get(0));
            } else {
                items.remove(arrows.get(0));
                items.remove(arrows.get(1));
            }
            if (treasureSize >= 1) {
                items.remove(treasure.get(0));
            } else if (keySize >= 1) {
                items.remove(keys.get(0));
            }

            if (sunStoneSize >= 1) {
                items.remove(sunStones.get(0));
            }
            return factory.buildSceptre();
        } else if (Objects.equals(entity, "midnight_armour")
                   && canBuildMidnightArmour(map, swordSize, sunStoneSize)) {
            items.remove(swords.get(0));
            items.remove(sunStones.get(0));
            return factory.buildMidnightArmour();
        }
        return null;
    }

    public <T extends InventoryItem> T getFirst(Class<T> itemType) {
        for (InventoryItem item : items)
            if (itemType.isInstance(item)) return itemType.cast(item);
        return null;
    }

    public <T extends InventoryItem> int count(Class<T> itemType) {
        int count = 0;
        for (InventoryItem item : items)
            if (itemType.isInstance(item)) count++;
        return count;
    }

    public Entity getEntity(String itemUsedId) {
        for (InventoryItem item : items)
            if (((Entity) item).getId().equals(itemUsedId)) return (Entity) item;
        return null;
    }

    public List<Entity> getEntities() {
        return items.stream().map(Entity.class::cast).collect(Collectors.toList());
    }

    public <T> List<T> getEntities(Class<T> clz) {
        return items.stream().filter(clz::isInstance).map(clz::cast).collect(Collectors.toList());
    }

    public boolean hasWeapon() {
        return getFirst(Sword.class) != null || getFirst(Bow.class) != null;
    }

    public BattleItem getWeapon() {
        BattleItem weapon = getFirst(Sword.class);
        if (weapon == null)
            return getFirst(Bow.class);
        return weapon;
    }

    public void useWeapon(Game game) {
        getWeapon().use(game);
    }

    public boolean canBuildBow(int wood, int arrows) {
        return wood >= 1 && arrows >= 3;
    }

    public boolean canBuildShield(int wood, int treasure, int keys, int sunStones) {
        return wood >= 2 && (treasure >= 1 || keys >= 1 || sunStones >= 1);
    }

    public boolean canBuildSceptre(int wood, int arrows, int treasure, int keys, int sunStones) {
        return (wood >= 1 || arrows >= 2) && (treasure >= 1 || keys >= 1 || sunStones >= 2) && sunStones >= 1;
    }

    private boolean canBuildMidnightArmour(GameMap map, int swords, int sunStones) {
        return !map.containsZombies() && swords >= 1 && sunStones >= 1;
    }

    public List<InventoryItem> getItems() {
        return items;
    }
}
