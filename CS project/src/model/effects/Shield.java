package model.effects;

import model.world.*;

public class Shield extends Effect {

	public Shield(int duration) {
		super("Shield", duration, EffectType.BUFF);

	}

	public void apply(Champion c) {
		//remove(c);
		c.setSpeed((int)(c.getSpeed() * 1.02));
	}

	public void remove(Champion c) {
		c.setSpeed((int)(c.getSpeed() / 1.02));
		c.getAppliedEffects().remove(this);
	}
}
