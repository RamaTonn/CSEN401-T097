package model.effects;

import model.world.Champion;

public class Shock extends Effect {

	public Shock(int duration) {
		super("Shock", duration, EffectType.DEBUFF);
		
	}
	public void apply (Champion c){
		c.setSpeed( (int)(c.getSpeed()- (c.getSpeed()*0.10)));
	    c.setAttackDamage((int)(c.getAttackDamage()-c.getAttackDamage()*10));
	    c.setMaxActionPointsPerTurn(c.getMaxActionPointsPerTurn()-1);
		c.setCurrentActionPoints(c.getCurrentActionPoints()-1);
		c.getAppliedEffects().add(this);
	}
}
