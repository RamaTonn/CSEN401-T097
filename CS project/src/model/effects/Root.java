package model.effects;

import model.world.Champion;
import model.world.Condition;

public class Root extends Effect {

	public Root(int duration) {
		super("Root", duration, EffectType.DEBUFF);

	}

	public void apply(Champion c) {
		if (c.getCondition() == Condition.ACTIVE ) {
			c.setCondition(Condition.ROOTED);
		}
	}

	public void remove1(Champion c) {
		c.getAppliedEffects().remove(this);
		for (Effect e : c.getAppliedEffects()) {
			if (e instanceof Root || e instanceof Stun) {
				return;
			}
		}
		if (c.getCondition() == Condition.ROOTED) {
			c.setCondition(Condition.ACTIVE);
		}
	}
	public void remove(Champion c) {
		boolean flag1 = false;
		boolean flag2 = false;
		for(Effect e :c.getAppliedEffects()) {
			if(e.getName().equals("Stun") && e.getDuration()==0  ) {
				flag1 =true;
				c.getAppliedEffects().remove(0);
			}
			else if( e.getName().equals("Stun") && e.getDuration()!=0)
				flag2=true;
			}
		if(flag2 ==false && flag1==true) 
		if(!(c.getCondition().equals(Condition.ACTIVE)))
			c.setCondition(Condition.ACTIVE);
		for(int i=0;i<c.getAppliedEffects().size();i++) {
			Effect e=c.getAppliedEffects().get(i);
			if(e.getName()=="Stun" && e.getDuration()==0) {
				c.getAppliedEffects().remove(i);
			}
		}

		}
}
