package model.effects;

import model.abilities.Ability;
import model.abilities.AreaOfEffect;
import model.abilities.DamagingAbility;
import model.world.Champion;
import model.world.Condition;
import exceptions.*;

public class Disarm extends Effect {
	public Disarm(int duration) {
		super("Disarm", duration, EffectType.DEBUFF);

	}

	public void apply(Champion c) {
			//c.getAppliedEffects().add(this);
			c.getAbilities().add(new DamagingAbility("Punch", 0, 1, 1, AreaOfEffect.SINGLETARGET, 1, 50));
}
	

	public void remove(Champion c) {

		c.getAppliedEffects().remove(this);

		for (int i = 0; i < c.getAbilities().size() - 1; i++) {
			Ability a = c.getAbilities().get(i);
			if (a.getName().equals("Punch")) {
				c.getAbilities().remove(i);
			}
		}
	}
}
