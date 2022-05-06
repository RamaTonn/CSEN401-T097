package model.effects;

import model.world.Champion;
import model.world.Condition;

public class Root extends Effect {

	public Root( int duration) {
		super("Root", duration, EffectType.DEBUFF);
		
	}
	public void apply (Champion c){
		if(!(c.getCondition().equals(Condition.INACTIVE))){
		c.setCondition(Condition.ROOTED);
	}
		c.getAppliedEffects().add(this);

}}
