package dungeonmania.entities;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.stream.Collectors;

import org.json.JSONObject;

import dungeonmania.Game;
import dungeonmania.battles.BattleStatistics;
import dungeonmania.battles.Battleable;
import dungeonmania.entities.collectables.Bomb;
import dungeonmania.entities.collectables.SunStone;
import dungeonmania.entities.collectables.potions.InvincibilityPotion;
import dungeonmania.entities.collectables.potions.Potion;
import dungeonmania.entities.enemies.Assassin;
import dungeonmania.entities.enemies.Enemy;
import dungeonmania.entities.enemies.Mercenary;
import dungeonmania.entities.inventory.Inventory;
import dungeonmania.entities.inventory.InventoryItem;
import dungeonmania.entities.playerState.BaseState;
import dungeonmania.entities.playerState.PlayerState;
import dungeonmania.map.GameMap;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class Player extends Entity implements Battleable {
    public static final double DEFAULT_ATTACK = 5.0;
    public static final double DEFAULT_HEALTH = 5.0;
    private BattleStatistics battleStatistics;
    private Inventory inventory;
    private Queue<Potion> queue = new LinkedList<>();
    private Potion inEffective = null;
    private int nextTrigger = 0;
    private GameMap map;

    private PlayerState state;

    public Player(Position position, double health, double attack, GameMap map) {
        super(position);
        battleStatistics = new BattleStatistics(
                health,
                attack,
                0,
                BattleStatistics.DEFAULT_DAMAGE_MAGNIFIER,
                BattleStatistics.DEFAULT_PLAYER_DAMAGE_REDUCER);
        inventory = new Inventory();
        state = new BaseState(this);
        this.map = map;
    }

    public boolean hasWeapon() {
        return inventory.hasWeapon();
    }

    public BattleItem getWeapon() {
        return inventory.getWeapon();
    }

    public List<String> getBuildables(GameMap map) {
        return inventory.getBuildables(map);
    }

    public boolean build(String entity, EntityFactory factory, GameMap map) {
        InventoryItem item = inventory.checkBuildCriteria(entity, factory, map);
        if (item == null) return false;
        return inventory.add(item);
    }

    public void move(GameMap map, Direction direction) {
        this.setFacing(direction);
        map.moveTo(this, Position.translateBy(this.getPosition(), direction));
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (entity instanceof Enemy) {
            battle(map.getGame(), (Enemy) entity);
        }
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return true;
    }

    public Entity getEntity(String itemUsedId) {
        return inventory.getEntity(itemUsedId);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Potion getEffectivePotion() {
        return inEffective;
    }

    public <T extends InventoryItem> void use(Class<T> itemType) {
        T item = inventory.getFirst(itemType);
        if (item != null) inventory.remove(item);
    }

    public void use(Bomb bomb, GameMap map) {
        inventory.remove(bomb);
        bomb.onPutDown(map, getPosition());
    }

    public void triggerNext(int currentTick) {
        if (queue.isEmpty()) {
            inEffective = null;
            state.transitionBase();
            return;
        }
        inEffective = queue.remove();
        if (inEffective instanceof InvincibilityPotion) {
            state.transitionInvincible();
        } else {
            state.transitionInvisible();
        }
        nextTrigger = currentTick + inEffective.getDuration();
    }

    public void changeState(PlayerState playerState) {
        state = playerState;
    }

    public void use(Potion potion, int tick) {
        inventory.remove(potion);
        queue.add(potion);
        if (inEffective == null) {
            triggerNext(tick);
        }
    }

    public void onTick(int tick) {
        if (inEffective == null || tick == nextTrigger) {
            triggerNext(tick);
        }
    }

    public void remove(InventoryItem item) {
        inventory.remove(item);
    }

    @Override
    public BattleStatistics getBattleStatistics() {
        return battleStatistics;
    }

    public <T extends InventoryItem> int countEntityOfType(Class<T> itemType) {
        return inventory.count(itemType);
    }

    public BattleStatistics applyBuff(BattleStatistics origin) {
        if (state.isInvincible()) {
            return BattleStatistics.applyBuff(origin, new BattleStatistics(
                0,
                0,
                0,
                1,
                1,
                true,
                true));
        } else if (state.isInvisible()) {
            return BattleStatistics.applyBuff(origin, new BattleStatistics(
                0,
                0,
                0,
                1,
                1,
                false,
                false));
        }
        return origin;
    }

    public void useWeapon(Game game) {
        inventory.useWeapon(game);
    }

    public double getHealth() {
        return battleStatistics.getHealth();
    }

    public void setHealth(double health) {
        battleStatistics.setHealth(health);
    }

    public void battle(Game game, Enemy enemy) {
        if (enemy instanceof Mercenary) {
            if (enemy instanceof Assassin) {
                if (((Assassin) enemy).isAllied()) return;
            } else {
                if (((Mercenary) enemy).isAllied()) return;
            }
        }
        game.battle(this, enemy);
    }

    public void bribe() {
        List<Entity> items = inventory.getEntities().stream()
                                                    .filter(it -> !Objects.equals(it.getClass(), SunStone.class))
                                                    .collect(Collectors.toList());
        Entity curr = items.get(0);
        if (curr != null && curr instanceof InventoryItem) {
            inventory.remove((InventoryItem) curr);
        }
    }

    public GameMap getMap() {
        return this.map;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonEntity = new JSONObject();
        jsonEntity.put("type", "player");

        Position entityPosition = getPosition();
        jsonEntity.put("x", entityPosition.getX());
        jsonEntity.put("y", entityPosition.getY());

        return jsonEntity;
    }
}
