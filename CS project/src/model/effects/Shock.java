package model.effects;

import model.world.Champion;
import model.world.Condition;

public class Shock extends Effect {

	public Shock(int duration) {
		super("Shock", duration, EffectType.DEBUFF);

	}

	public void apply(Champion c) {
		c.setSpeed((int) (c.getSpeed() * 0.9));
		
		c.setAttackDamage((int) (c.getAttackDamage() * 0.9));
		
		c.setMaxActionPointsPerTurn(c.getMaxActionPointsPerTurn() - 1);
		
		c.setCurrentActionPoints(c.getCurrentActionPoints() - 1);
		
		c.getAppliedEffects().add(this);
	}

	public void remove(Champion c) {
		c.getAppliedEffects().remove(this);
		
		c.setSpeed((int) (c.getSpeed() / 0.9));
		
		c.setAttackDamage((int) (c.getAttackDamage() / 0.9));
		
		c.setMaxActionPointsPerTurn(c.getMaxActionPointsPerTurn() + 1);
		
		c.setCurrentActionPoints(c.getCurrentActionPoints() + 1);
	}
}
