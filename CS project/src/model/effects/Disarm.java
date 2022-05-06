package model.effects;

import model.abilities.AreaOfEffect;
import model.abilities.DamagingAbility;
import model.world.Champion;
import model.world.Condition;
import exceptions.*;
public class Disarm extends Effect {
	public Disarm( int duration) {
		super("Disarm", duration, EffectType.DEBUFF);
		
	}
	public void apply (Champion c)throws AbilityUseException,NotEnoughResourcesException{
		if(c.getCurrentActionPoints()==0){
			throw new NotEnoughResourcesException();
		}
		if(c.getCondition().equals(Condition.INACTIVE) || c.getCondition().equals(Condition.KNOCKEDOUT) ){
			throw new AbilityUseException();
		}
		
		c.getAbilities().add(new DamagingAbility("Punch",0,1,1,AreaOfEffect.SINGLETARGET,1,50));
		c.getAppliedEffects().add(this);
	}
	
}
