package dev.darkblade.mod.input_engine.server.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerComboEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final String comboId;

    public PlayerComboEvent(Player player, String comboId) {
        this.player = player;
        this.comboId = comboId;
    }

    public Player getPlayer() {
        return player;
    }

    public String getComboId() {
        return comboId;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
