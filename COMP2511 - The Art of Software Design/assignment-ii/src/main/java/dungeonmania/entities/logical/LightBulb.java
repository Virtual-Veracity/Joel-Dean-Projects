package dungeonmania.entities.logical;

import org.json.JSONObject;

import dungeonmania.util.Position;

public class LightBulb extends LogicalEntity {
    private boolean isOn;

    public LightBulb(Position position, String logic) {
        super(position, logic);
        isOn = false;
    }

    public void turnOn() {
        isOn = true;
    }

    public void turnOff() {
        isOn = false;
    }

    public boolean isOn() {
        return isOn;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonEntity = new JSONObject();
        jsonEntity.put("type", "light_bulb_off");
        jsonEntity.put("logic", getLogic());

        Position entityPosition = getPosition();
        jsonEntity.put("x", entityPosition.getX());
        jsonEntity.put("y", entityPosition.getY());

        return jsonEntity;
    }
}
