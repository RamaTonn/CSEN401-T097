package model.abilities;

import java.util.ArrayList;

import model.effects.Shield;
import model.world.Champion;
import model.world.Damageable;

public class DamagingAbility extends Ability {

	private int damageAmount;

	public DamagingAbility(String name, int cost, int baseCoolDown, int castRadius, AreaOfEffect area, int required,
			int damageAmount) {
		super(name, cost, baseCoolDown, castRadius, area, required);
		this.damageAmount = damageAmount;
	}

	public int getDamageAmount() {
		return damageAmount;
	}

	public void setDamageAmount(int damageAmount) {
		this.damageAmount = damageAmount;
	}

	
	//This method should take into consideration the type of the caster and the target
	public void execute(ArrayList<Damageable> targets) {

		for (Damageable c : targets) {
			c.setCurrentHP(c.getCurrentHP() - this.damageAmount);
			if(c instanceof Champion) {
				Champion x = (Champion) c;
				for(int i = 0; i < x.getAppliedEffects().size();i++) {
					if(x.getAppliedEffects().get(i) instanceof Shield) {
						x.getAppliedEffects().get(i).remove(x);
					}
				}
			}
		}
		
		this.setCurrentCooldown(this.getBaseCooldown());

	}

}
