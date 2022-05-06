package model.effects;

import model.world.Champion;

public class SpeedUp extends Effect{

	public SpeedUp(int duration) {
		super("SpeedUp",duration,EffectType.BUFF);
	}
	public void apply (Champion c){
		c.setSpeed( (int)(c.getSpeed()+ (c.getSpeed()*0.15)));
		c.setMaxActionPointsPerTurn(c.getMaxActionPointsPerTurn()+1);
		c.setCurrentActionPoints(c.getCurrentActionPoints()+1);
		c.getAppliedEffects().add(this);
	}
}
