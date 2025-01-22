package dungeonmania.goals;

import dungeonmania.Game;
import dungeonmania.map.GameMap;

public interface Goal {

    /**
     * @return true if the goal has been achieved, false otherwise
     */
    public boolean achieved(Game game, GameMap map);

    public String toString(Game game);

}
