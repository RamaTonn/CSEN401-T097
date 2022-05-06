package model.abilities;

import java.util.ArrayList;

import model.world.Damageable;



public class DamagingAbility extends Ability {
	
	private int damageAmount;
	public DamagingAbility(String name, int cost, int baseCoolDown, int castRadius, AreaOfEffect area,int required,int damageAmount) {
		super(name, cost, baseCoolDown, castRadius, area,required);
		this.damageAmount=damageAmount;
	}
	public int getDamageAmount() {
		return damageAmount;
	}
	public void setDamageAmount(int damageAmount) {
		this.damageAmount = damageAmount;
	}
	public void execute(ArrayList<Damageable> targets) {
		for(Damageable c: targets){
			c.setCurrentHP(c.getCurrentHP()-this.damageAmount);
		}

}
}
