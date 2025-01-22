package dungeonmania;

import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

import dungeonmania.map.GameMap;
import dungeonmania.entities.Entity;
import dungeonmania.entities.inventory.*;
import dungeonmania.entities.Door;
import dungeonmania.entities.Switch;
import dungeonmania.entities.logical.LogicalConductor;
import dungeonmania.entities.logical.LogicalEntity;

public class saveGameCreator {

    public static void storeEntities(List<Entity> entityList, String saveFile) throws IOException {
        JSONArray jsonArray = new JSONArray();

        for (Entity entity : entityList) {
            JSONObject json = entity.toJSON();
            jsonArray.put(json);
        }

        File file = new File("savedGames\\" + saveFile + "\\d_dungeonEntities.json");
        file.createNewFile();

        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(jsonArray.toString());
        fileWriter.close();
    }

    public static void storeConfig(String config, String saveFile) throws IOException {
        String filePath = new File("").getAbsolutePath();
        File sourceFile = new File(filePath.concat("\\..\\..\\test\\resources\\configs\\" + config));
        File destFile = new File(filePath.concat("\\saveGames\\" + saveFile + "\\" + config));
        Files.copy(sourceFile.toPath(), destFile.toPath());
    }

    public static void storeInventory(Inventory inventory, String saveName) throws IOException {
        JSONArray jsonArray = new JSONArray();

        for (InventoryItem item : inventory.getItems()) {
            JSONObject json = item.toInvJSON();
            jsonArray.put(json);
        }

        String filePath = new File("").getAbsolutePath();
        File file = new File(filePath + "savedGames\\" + saveName + "\\inventory.json");
        file.createNewFile();

        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(jsonArray.toString());
        fileWriter.close();
    } 

    public static void storeInteractables(GameMap map, String saveName) throws IOException {
        JSONArray jsonArray = new JSONArray();

        for (Door door : map.getEntities(Door.class)) {
            String open = "false";
            if (door.isOpen() == true) {
                open = "true";
            }
            jsonArray.put(new JSONObject("door", open));
        }

        for (LogicalConductor conductor : map.getEntities(LogicalConductor.class)) {
            String logic = "false";
            if (conductor.isActive() == true) {
                logic = "true";
            }
            jsonArray.put(new JSONObject("conductor", logic));
        }

        for (LogicalEntity logical : map.getEntities(LogicalEntity.class)) {
            jsonArray.put(new JSONObject("logical", logical.getLogic()));
        }

        String filePath = new File("").getAbsolutePath();
        File file = new File(filePath + "savedGames\\" + saveName + "\\interactables.json");
        file.createNewFile();

        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(jsonArray.toString());
        fileWriter.close();
    }
}
