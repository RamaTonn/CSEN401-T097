package model.effects;

import model.world.*;

public class Shield extends Effect {

	public Shield( int duration) {
		super("Shield", duration, EffectType.BUFF);
		
	}
	public void apply(Champion c){
		remove(c);
		c.setSpeed((int)(c.getSpeed()+(c.getSpeed()*0.02)));
		c.getAppliedEffects().add(this);
	}
}
