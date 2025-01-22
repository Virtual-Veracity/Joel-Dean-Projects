package dungeonmania;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.map.GameMap;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.ResponseBuilder;
import dungeonmania.util.Direction;
import dungeonmania.util.FileLoader;
import dungeonmania.saveGameCreator;
import dungeonmania.entities.Player;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class DungeonManiaController {
    private Game game = null;
    private String config;

    public String getSkin() {
        return "default";
    }

    public String getLocalisation() {
        return "en_US";
    }

    /**
     * /dungeons
     */
    public static List<String> dungeons() {
        return FileLoader.listFileNamesInResourceDirectory("dungeons");
    }

    /**
     * /configs
     */
    public static List<String> configs() {
        return FileLoader.listFileNamesInResourceDirectory("configs");
    }

    /**
     * /game/new
     */
    public DungeonResponse newGame(String dungeonName, String configName) throws IllegalArgumentException {
        if (!dungeons().contains(dungeonName)) {
            throw new IllegalArgumentException(dungeonName + " is not a dungeon that exists");
        }

        if (!configs().contains(configName)) {
            throw new IllegalArgumentException(configName + " is not a configuration that exists");
        }

        try {
            GameBuilder builder = new GameBuilder();
            config = configName;
            game = builder.setConfigName(configName).setDungeonName(dungeonName).buildGame();
            return ResponseBuilder.getDungeonResponse(game);
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * /game/dungeonResponseModel
     */
    public DungeonResponse getDungeonResponseModel() {
        return null;
    }

    /**
     * /game/tick/item
     */
    public DungeonResponse tick(String itemUsedId) throws IllegalArgumentException, InvalidActionException {
        return ResponseBuilder.getDungeonResponse(game.tick(itemUsedId));
    }

    /**
     * /game/tick/movement
     */
    public DungeonResponse tick(Direction movementDirection) {
        return ResponseBuilder.getDungeonResponse(game.tick(movementDirection));
    }

    /**
     * /game/build
     */
    public DungeonResponse build(String buildable) throws IllegalArgumentException, InvalidActionException {
        List<String> validBuildables = List.of("bow", "shield", "midnight_armour", "sceptre");
        if (!validBuildables.contains(buildable)) {
            throw new IllegalArgumentException("Only bow, shield, midnight_armour and sceptre can be built");
        }

        return ResponseBuilder.getDungeonResponse(game.build(buildable));
    }

    /**
     * /game/interact
     */
    public DungeonResponse interact(String entityId) throws IllegalArgumentException, InvalidActionException {
        return ResponseBuilder.getDungeonResponse(game.interact(entityId));
    }

    /**
     * /game/save
     */
    public DungeonResponse saveGame(String name) throws IllegalArgumentException {
        try {
            checkSaveFolder();
            // 2. Create a folder with the name passed in for this save 
            String filePath = new File("").getAbsolutePath();
            Path save = Paths.get(filePath + "\\saveGames\\" + name);
            Files.createFile(save);
            
            // 3. Within that folder save the appropraite JSON objects
            // 3.a. Get all entities and positions 
            GameMap map = game.getMap();
            saveGameCreator.storeEntities(map.getEntities(), name);
            saveGameCreator.storeConfig(config, name);

            // 3.b. Get inventory
            //      Store in JSON array
            Player player = map.getPlayer();
            saveGameCreator.storeInventory(player.getInventory(), name);
        
            // 3.c. Get Interactables
            saveGameCreator.storeInteractables(game.getMap(), name);

        } catch (Exception e) {
        }
        return null;
    }

    /**
     * /game/load
     */
    public DungeonResponse loadGame(String name) throws IllegalArgumentException {
        try {
            checkSaveFolder();
            // 2. Use name to find save file
            String filePath = new File("").getAbsolutePath();
            String savePath = filePath + "\\name";
            File saveFolder = new File(savePath);

            // 3. Use config and dungeon found to create a new game
            // 4. Update player Inventory
        } catch(Exception e) {
        }
        return null;
    }

    /**
     * /games/all
     */
    public List<String> allGames() {
        return new ArrayList<>();
    }

    private void checkSaveFolder() throws IOException {
        // 1. If Save game folder doesn't exist make it
        String filePath = new File("").getAbsolutePath();
        Path path = Paths.get(filePath + "\\saveGames");
        if (!Files.exists(path)) {
            Path save = Paths.get(filePath + "\\saveGames");
            Files.createDirectory(save);
        } 
    }

    /**
     * /game/new/generate
     */
    public DungeonResponse generateDungeon(
            int xStart, int yStart, int xEnd, int yEnd, String configName) throws IllegalArgumentException {
        return null;
    }

    /**
     * /game/rewind
     */
    public DungeonResponse rewind(int ticks) throws IllegalArgumentException {
        return null;
    }

}
