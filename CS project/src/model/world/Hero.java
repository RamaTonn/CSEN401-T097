package model.world;

import java.util.*;
import model.effects.*;

public class Hero extends Champion {

	public Hero(String name, int maxHP, int maxMana, int actions, int speed, int attackRange, int attackDamage) {
		super(name, maxHP, maxMana, actions, speed, attackRange, attackDamage);

	}
	
	public void useLeaderAbility(ArrayList<Champion> targets) {
		for (Champion c : targets) {					
			
			for (int i = 0; i < c.getAppliedEffects().size(); i++) {
				Effect f = c.getAppliedEffects().get(i);
				if (f.getType() == EffectType.DEBUFF) {
					f.remove(c);
				}
			}
			
			Embrace e = new Embrace(2);
			e.apply(c);
			c.getAppliedEffects().add(e);
		}
	}
}
