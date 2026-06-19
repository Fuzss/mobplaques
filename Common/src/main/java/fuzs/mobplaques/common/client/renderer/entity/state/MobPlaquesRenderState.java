package fuzs.mobplaques.common.client.renderer.entity.state;

import fuzs.mobplaques.common.client.gui.plaque.HealthPlaqueRenderer;
import net.minecraft.client.gui.Hud;
import net.minecraft.resources.Identifier;

public class MobPlaquesRenderState {
    public int health;
    public int maxHealth;
    public int absorption;
    public Identifier sprite = HealthPlaqueRenderer.getSprite(Hud.HeartType.NORMAL);
    public int armor;
    public int toughness;
    public int airSupply;
    public int maxAirSupply;
}
