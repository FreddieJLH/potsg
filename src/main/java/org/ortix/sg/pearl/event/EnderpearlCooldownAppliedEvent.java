package org.ortix.sg.pearl.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@AllArgsConstructor
@Getter @Setter
public class EnderpearlCooldownAppliedEvent extends Event {

	@Getter
	private static HandlerList handlerList = new HandlerList();

	private final Player player;
	private long timeToApply;

	@Override
	public HandlerList getHandlers() {
		return handlerList;
	}

}
