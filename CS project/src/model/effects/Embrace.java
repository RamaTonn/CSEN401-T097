package model.effects;

import model.world.Champion;



public class Embrace extends Effect {
	

	public Embrace(int duration) {
		super("Embrace", duration, EffectType.BUFF);
	}
	public void apply (Champion c){
		c.setCurrentHP(c.getCurrentHP()+(int)(c.getMaxHP()*0.2));
	    c.setMana(c.getMana()+(int)(c.getMana()*0.2));
	    c.setSpeed( (int)(c.getSpeed()+ (c.getSpeed()*0.2)));
	    c.setAttackDamage((int)(c.getAttackDamage()+ (c.getAttackDamage()*0.2)));
	    c.getAppliedEffects().add(this);
	}
}
