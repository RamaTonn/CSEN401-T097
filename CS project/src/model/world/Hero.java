package model.world;
import java.util.*;

import model.effects.*;


public class Hero extends Champion {

	public Hero(String name, int maxHP, int maxMana, int actions, int speed, int attackRange, int attackDamage) {
		super(name, maxHP, maxMana, actions, speed, attackRange, attackDamage);

	}
	public void useLeaderAbility(ArrayList<Champion> targets){
		for(Champion c : targets){
			for(Effect f: c.getAppliedEffects()){
				if(f.getType().equals(EffectType.DEBUFF)){
					f.remove(c);
				}
				Embrace e=new Embrace(2);
				e.apply(c);
			}
					}
	}
	
}
