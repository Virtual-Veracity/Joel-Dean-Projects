package dungeonmania.entities.enemies;

import java.util.List;

import org.json.JSONObject;

import dungeonmania.Game;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Interactable;
import dungeonmania.entities.Player;
import dungeonmania.entities.buildables.Sceptre;
import dungeonmania.entities.collectables.SunStone;
import dungeonmania.entities.collectables.Treasure;
import dungeonmania.entities.inventory.Inventory;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class Mercenary extends Enemy implements Interactable {
    public static final int DEFAULT_BRIBE_AMOUNT = 1;
    public static final int DEFAULT_BRIBE_RADIUS = 1;
    public static final int DEFAULT_MIND_CONTROL_DURATION = 1;
    public static final double DEFAULT_ATTACK = 5.0;
    public static final double DEFAULT_HEALTH = 10.0;

    private int bribeAmount = Mercenary.DEFAULT_BRIBE_AMOUNT;
    private int bribeRadius = Mercenary.DEFAULT_BRIBE_RADIUS;
    private boolean allied = false;
    private boolean mindControlled = false;
    private int mindControlDuration = Mercenary.DEFAULT_MIND_CONTROL_DURATION;
    private int currMindControlTick = 0;

    private MoveBehaviour moveFollowPlayerBehaviour = new MoveFollowPlayer();
    private MoveBehaviour moveToPlayerBehaviour = new MoveToPlayer();

    public Mercenary(Position position, double health, double attack, int bribeAmount, int bribeRadius, int duration) {
        super(position, health, attack);
        this.bribeAmount = bribeAmount;
        this.bribeRadius = bribeRadius;
        this.mindControlDuration = duration;
    }

    public boolean isAllied() {
        return allied;
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (allied) return;
        super.onOverlap(map, entity);
    }

    /**
     * check whether the current merc can be bribed
     * @param player
     * @return
     */
    private boolean canBeBribed(Player player) {
        if (hasSpectre(player.getInventory())) return true;
        return bribeRadius >= 0
            && player.countEntityOfType(Treasure.class)
            - player.countEntityOfType(SunStone.class) >= bribeAmount;
    }

    /**
     * bribe the merc
     */
    private void bribe(Player player) {
        allied = true;
        for (int i = 0; i < bribeAmount; i++) {
            player.bribe();
        }
    }

    @Override
    public void interact(Player player, Game game) {
        if (hasSpectre(player.getInventory())) {
            mindControl();
        } else {
            bribe(player);
        }
    }

    @Override
    public void move(Game game) {
        if (checkStuckSwamp()) {
            return;
        }

        if (allied && isMercAdjacent(game.getPlayerPosition())) {
            moveFollowPlayerBehaviour.move(game, this, game.getMap());
        } else {
            moveToPlayerBehaviour.move(game, this, game.getMap());
        }
    }

    @Override
    public boolean isInteractable(Player player) {
        return !allied && canBeBribed(player);
    }

    public void setAllied(boolean allied) {
        this.allied = allied;
    }

    public int getBribeAmount() {
        return bribeAmount;
    }

    private boolean isMercAdjacent(Position playerPosition) {
        List<Position> positionList = playerPosition.getCardinallyAdjacentPositions();
        positionList.add(playerPosition);
        return positionList.stream().anyMatch(p -> p.equals(getPosition()));
    }

    public boolean hasSpectre(Inventory inventory) {
        return inventory.count(Sceptre.class) != 0;
    }

    public void mindControl() {
        allied = true;
        mindControlled = true;
    }

    public void checkMindControl() {
        if (mindControlled) {
            currMindControlTick++;
            if (currMindControlTick >= mindControlDuration) {
                allied = false;
            }
        }
    }

    public int getMindControlDuration() {
        return mindControlDuration;
    }

    public int getCurrMindControlTick() {
        return currMindControlTick;
    }

    // ============= Swamp Tile Methods =====================
    // Mercenary is not stuck when following player
    @Override
    public void setEnemyStuck(int moveFactor, Player player) {
        if (allied && isMercAdjacent(player.getPosition())) {
            return;
        }

        super.setEnemyStuck(moveFactor, player);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonEntity = new JSONObject();
        jsonEntity.put("type", "mercenary");

        Position entityPosition = getPosition();
        jsonEntity.put("x", entityPosition.getX());
        jsonEntity.put("y", entityPosition.getY());

        return jsonEntity;
    }
}
