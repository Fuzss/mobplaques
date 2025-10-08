package fuzs.mobplaques.client.renderer.entity.state;

import fuzs.mobplaques.client.gui.plaque.HealthPlaqueRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.resources.ResourceLocation;

public class MobPlaquesRenderState {
    public int health;
    public int maxHealth;
    public int absorption;
    public ResourceLocation sprite = HealthPlaqueRenderer.getSprite(Gui.HeartType.NORMAL);
    public int armor;
    public int toughness;
    public int airSupply;
    public int maxAirSupply;
}
