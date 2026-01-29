package org.windett.azerusBlizzerus.rpg.entity.data;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RpgMobData {

    private final LivingEntity entity;
    private final Map<Attribute, Float> attributes = new HashMap<>();


    public RpgMobData(LivingEntity entity) {
        this.entity = entity;

        initBaseAttributes();
    }

    public void initBaseAttributes() {
        this.attributes.put(Attribute.MAX_HEALTH, 10.0F);
        this.attributes.put(Attribute.MOVEMENT_SPEED, 0.25F);
        this.attributes.put(Attribute.FOLLOW_RANGE, 15F);
        this.attributes.put(Attribute.SCALE, 1.0F);

        applyAttributes();
    }

    public void applyAttributes() {
        for (Attribute attribute : this.attributes.keySet()) {
            Objects.requireNonNull(entity.getAttribute(attribute)).setBaseValue(this.attributes.get(attribute));
        }
    }

    public void addOrChangeAttribute(Attribute attribute, float value) {
        this.attributes.put(attribute, value);
        entity.getAttribute(attribute).setBaseValue(this.attributes.get(attribute));
    }
}
