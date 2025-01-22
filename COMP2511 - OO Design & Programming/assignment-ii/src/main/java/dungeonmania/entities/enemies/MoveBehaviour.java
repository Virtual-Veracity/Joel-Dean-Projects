package dungeonmania.entities.enemies;

import dungeonmania.Game;
import dungeonmania.map.GameMap;

interface MoveBehaviour {
    public void move(Game game, Enemy enemy, GameMap map);
}
