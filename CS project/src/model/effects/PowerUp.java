package model.effects;

import java.util.ArrayList;

import model.abilities.*;
import model.world.Champion;

public class PowerUp extends Effect {

	public PowerUp(int duration) {
		super("PowerUp", duration, EffectType.BUFF);

	}

	public void apply(Champion c) {
		for (Ability a : c.getAbilities()) {
			if (a instanceof HealingAbility) {
				HealingAbility h = (HealingAbility) a;
				h.setHealAmount((int) (h.getHealAmount() * 1.2));
			} else if (a instanceof DamagingAbility) {
				DamagingAbility h = (DamagingAbility) a;
				h.setDamageAmount((int) (h.getDamageAmount() * 1.2));
			}
		}

	}

	public void remove(Champion c) {
		c.getAppliedEffects().remove(this);
		for (Ability a : c.getAbilities()) {
			if (a instanceof HealingAbility) {
				HealingAbility h = (HealingAbility) a;
				h.setHealAmount((int) (h.getHealAmount() / 1.2));
			} else if (a instanceof DamagingAbility) {
				DamagingAbility h = (DamagingAbility) a;
				h.setDamageAmount((int) (h.getDamageAmount() / 0.2));
			}
		}
	}
}
